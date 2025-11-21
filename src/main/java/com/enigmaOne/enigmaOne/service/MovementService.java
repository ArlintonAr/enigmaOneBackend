package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.Config.GenerateNanoIdCongif;
import com.enigmaOne.enigmaOne.Config.CustomUserDetails;
import com.enigmaOne.enigmaOne.persistence.entity.DetailEntryMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.Movement;
import com.enigmaOne.enigmaOne.persistence.entity.Stock;
import com.enigmaOne.enigmaOne.persistence.repository.DetailEntryMaterialRepository;
import com.enigmaOne.enigmaOne.persistence.repository.DetailExitMaterialRepository;
import com.enigmaOne.enigmaOne.persistence.repository.MovementRepository;
import com.enigmaOne.enigmaOne.service.dto.EmployeeResponseDTO;
import com.enigmaOne.enigmaOne.service.dto.MovementResponseDTO;
import com.enigmaOne.enigmaOne.service.mapper.MovementMapper;
import com.enigmaOne.enigmaOne.persistence.types.ReturnableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovementService implements MovementServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(MovementService.class);

    @Autowired
    private  MovementRepository movementRepository;

    @Autowired
    private MovementMapper movementMapper;

    @Autowired
    private GenerateNanoIdCongif generateNanoIdCongif;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private StockService stockService;

    @Autowired
    private DetailEntryMaterialRepository detailEntryMaterialRepository;

    @Autowired
    private DetailExitMaterialRepository detailExitMaterialRepository;

    @Override
    public List<MovementResponseDTO> getAllMovements() {
    List<MovementResponseDTO> movements =  this.movementRepository.findAll()
                .stream()
                .map(this.movementMapper::toMovementDTOResponse)
                .collect(Collectors.toList());
      return  movements;
    }


    @Override
    public List<MovementResponseDTO> getMovementsByMaterialRequesterFirstName(String firstName) {
        List<MovementResponseDTO> movements = this.movementRepository.findMovementByMaterialRequerterFirstName(firstName)
                .stream()
                .map(this.movementMapper::toMovementDTOResponse)
                .collect(Collectors.toList());

        return  movements;
    }

    @Override
    public List<MovementResponseDTO>  getMovementByTransactionCode(String transactionCode) {
        List<MovementResponseDTO> movements = this.movementRepository.findMovementByTransactionCode(transactionCode)
                .stream()
                .map(this.movementMapper::toMovementDTOResponse)
                .collect(Collectors.toList());

        return movements;
    }


    @Override
    public Movement getMovementById(Long id) {
        return this.movementRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public boolean createMovement(Movement movement) {
        try {

            // Guardar referencias a los detalles recibidos y evitar que JPA los persista
            // automáticamente al guardar el movimiento (porque los detalles requieren
            // transactionCode no nulo y se asigna después de generar el código del movimiento).
            List<DetailEntryMaterial> incomingEntryDetails = movement.getDetailEntryMaterials();
            List<DetailExitMaterial> incomingExitDetails = movement.getDetailExitMaterials();

            // CALCULAR cantidad total del movimiento a partir de los detalles (derivado)
            Integer totalQuantity = calculateTotalQuantity(movement);
            movement.setQuantity(totalQuantity);

            // Evitar que los detalles sean persistidos junto con el movement en el primer save
            movement.setDetailEntryMaterials(null);
            movement.setDetailExitMaterials(null);

            //Generacion automatica del codigo de transaccion
            String nanoId = this.generateNanoIdCongif.generateNanoId();
            if (this.existsMovementByTransactionCode(nanoId)) {
                nanoId = this.generateNanoIdCongif.generateNanoId();
            }
            movement.setTransactionCode(nanoId);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Agregar el id del usuario que crea la orden de forma segura
            if (authentication == null) {
                // No hay autenticación en el contexto; dejar employeeId null (o lanzar según política de la app)
                movement.setEmployeeId(null);
            } else {
                Object principal = authentication.getPrincipal();
                if (principal instanceof CustomUserDetails) {
                    movement.setEmployeeId(((CustomUserDetails) principal).getId());
                } else {
                    try {
                        if (principal != null && !"anonymousUser".equals(principal.toString())) {
                            movement.setEmployeeId(Long.parseLong(principal.toString()));
                        } else {
                            movement.setEmployeeId(null);
                        }
                    } catch (Exception ex) {
                        // si no se puede parsear, dejar null y validar más abajo
                        movement.setEmployeeId(null);
                    }
                }
            }

            //Agregar el nombre y apellido de la persona que requiere el movimiento
            if (movement.getMaterialRequesterId() != null){
                EmployeeResponseDTO employee = this.employeeService.getEmployeeById(movement.getMaterialRequesterId());
                if (employee != null) {
                    movement.setMaterialRequerterFirstName(employee.getFirstName());
                    movement.setMaterialRequerterLastName(employee.getLastName());
                }
            }

            // Guardar movement primero para obtener un id que podamos referenciar desde los detalles
            Movement savedMovement = this.movementRepository.save(movement);

            // Preparar nombres para propagar a los detalles (requerente y autorizado)
            EmployeeResponseDTO authorizer = null;
            if (savedMovement.getEmployeeId() != null) {
                authorizer = this.employeeService.getEmployeeById(savedMovement.getEmployeeId());
            }
            String requesterFullName = "";
            if (savedMovement.getMaterialRequerterFirstName() != null || savedMovement.getMaterialRequerterLastName() != null) {
                requesterFullName = (savedMovement.getMaterialRequerterFirstName() != null ? savedMovement.getMaterialRequerterFirstName() : "")
                        + " " + (savedMovement.getMaterialRequerterLastName() != null ? savedMovement.getMaterialRequerterLastName() : "");
                requesterFullName = requesterFullName.trim();
            }
            String authorizerFullName = null;
            if (authorizer != null) {
                authorizerFullName = (authorizer.getFirstName() != null ? authorizer.getFirstName() : "") +
                        " " + (authorizer.getLastName() != null ? authorizer.getLastName() : "");
                authorizerFullName = authorizerFullName.trim();
            }

             // Procesar detalles de salida (si existen)
             if (incomingExitDetails != null && !incomingExitDetails.isEmpty()){
                 processIncomingExitDetails(incomingExitDetails, savedMovement, requesterFullName, authorizerFullName);
             }

             // Procesar detalles de entrada (si existen)
             if (incomingEntryDetails != null && !incomingEntryDetails.isEmpty()){
                 processIncomingEntryDetails(incomingEntryDetails, savedMovement, requesterFullName, authorizerFullName);
             }

            return true;
        }catch (Exception e){
            // imprimir stack trace para diagnosticar en logs y regresar false para el controller
            logger.error("Error creando movement: {}", e.getMessage(), e);
             return  false;
         }
     }

     // Helpers para reducir duplicación y asegurar destinationMaterial
     private void processIncomingExitDetails(List<DetailExitMaterial> details, Movement savedMovement, String requesterFullName, String authorizerFullName) {
         for (DetailExitMaterial detail : details) {
             prepareExitDetail(detail, savedMovement, requesterFullName, authorizerFullName);
             applyStockChangeAndSaveExit(detail, savedMovement);
         }
     }

     private void processIncomingEntryDetails(List<DetailEntryMaterial> details, Movement savedMovement, String requesterFullName, String authorizerFullName) {
         for (DetailEntryMaterial detail : details) {
             prepareEntryDetail(detail, savedMovement, requesterFullName, authorizerFullName);
             applyStockChangeAndSaveEntry(detail);
         }
     }

     private void prepareExitDetail(DetailExitMaterial detail, Movement savedMovement, String requesterFullName, String authorizerFullName) {
         detail.setMovementId(savedMovement.getId());
         if (requesterFullName != null && !requesterFullName.isEmpty()) detail.setEmployeeNameRequerter(requesterFullName);
         if (authorizerFullName != null && !authorizerFullName.isEmpty()) detail.setEmployeeNameAuthorized(authorizerFullName);
         detail.setTransactionCode(savedMovement.getTransactionCode());

         // destinationMaterial viene del DTO y ya fue seteado en el controller; no sobreescribir
     }

     private void prepareEntryDetail(DetailEntryMaterial detail, Movement savedMovement, String requesterFullName, String authorizerFullName) {
         detail.setMovementId(savedMovement.getId());
         if (requesterFullName != null && !requesterFullName.isEmpty()) detail.setEmployeeNameRequerter(requesterFullName);
         if (authorizerFullName != null && !authorizerFullName.isEmpty()) detail.setEmployeeNameAuthorized(authorizerFullName);
         detail.setTransactionCode(savedMovement.getTransactionCode());

         // destinationMaterial viene del DTO y ya fue seteado en el controller; no sobreescribir
     }

     private void applyStockChangeAndSaveExit(DetailExitMaterial detail, Movement savedMovement) {
         if (detail.getStockId() == null) throw new IllegalArgumentException("Detalle debe incluir stockId");
         Stock stockForUpdated = this.stockService.getStockById(detail.getStockId());
         if (stockForUpdated == null) throw new IllegalArgumentException("Stock no encontrado id=" + detail.getStockId());
         if (stockForUpdated.getQuantity() == null) throw new IllegalStateException("Stock encontrado con cantidad nula id=" + stockForUpdated.getId());
         if (detail.getQuantity() == null) throw new IllegalArgumentException("Detalle debe incluir quantity");

         int newQuantity;
         if (savedMovement.getReturnable() != null && savedMovement.getReturnable().equals(ReturnableType.SALIDA)) {
             newQuantity = stockForUpdated.getQuantity() - detail.getQuantity();
         } else {
             newQuantity = stockForUpdated.getQuantity() + detail.getQuantity();
         }

         Stock stockUpdated = new Stock();
         stockUpdated.setQuantity(newQuantity);
         boolean updated = this.stockService.updateStock(stockUpdated, stockForUpdated.getId(), null, null);
         if (!updated) throw new RuntimeException("No se pudo actualizar stock id=" + stockForUpdated.getId());
         this.detailExitMaterialRepository.save(detail);
     }

     private void applyStockChangeAndSaveEntry(DetailEntryMaterial detail) {
         if (detail.getStockId() == null) throw new IllegalArgumentException("Detalle debe incluir stockId");
         Stock stockForUpdated = this.stockService.getStockById(detail.getStockId());
         if (stockForUpdated == null) throw new IllegalArgumentException("Stock no encontrado id=" + detail.getStockId());
         if (stockForUpdated.getQuantity() == null) throw new IllegalStateException("Stock encontrado con cantidad nula id=" + stockForUpdated.getId());
         if (detail.getQuantity() == null) throw new IllegalArgumentException("Detalle debe incluir quantity");

         int newQuantity = stockForUpdated.getQuantity() + detail.getQuantity();
         Stock stockUpdated = new Stock();
         stockUpdated.setQuantity(newQuantity);
         boolean updated = this.stockService.updateStock(stockUpdated, stockForUpdated.getId(), null, null);
         if (!updated) throw new RuntimeException("No se pudo actualizar stock id=" + stockForUpdated.getId());
         this.detailEntryMaterialRepository.save(detail);
     }

     @Override
     @Transactional
     public boolean updateMovement(Long id, Movement movement) {
         try {

             Movement findMovement = this.getMovementById(id);
             movement.setId(findMovement.getId());

             this.movementMapper.updateMovementFromDto(movement,findMovement);

            // Recalcular cantidad total: si el request incluye detalles los usamos; si no, usamos los detalles guardados
            Integer total;
            if ((movement.getDetailEntryMaterials() != null && !movement.getDetailEntryMaterials().isEmpty()) ||
                    (movement.getDetailExitMaterials() != null && !movement.getDetailExitMaterials().isEmpty())) {
                total = calculateTotalQuantity(movement);
            } else {
                total = calculateTotalQuantity(findMovement);
            }
            findMovement.setQuantity(total);

            // Propagar automáticamente los nombres del solicitante y del autorizador a los detalles (entry/exit)
            EmployeeResponseDTO authorizer = null;
            if (findMovement.getEmployeeId() != null) {
                authorizer = this.employeeService.getEmployeeById(findMovement.getEmployeeId());
            }
            String requesterFullName = "";
            if (findMovement.getMaterialRequerterFirstName() != null || findMovement.getMaterialRequerterLastName() != null) {
                requesterFullName = (findMovement.getMaterialRequerterFirstName() != null ? findMovement.getMaterialRequerterFirstName() : "")
                        + " " + (findMovement.getMaterialRequerterLastName() != null ? findMovement.getMaterialRequerterLastName() : "");
                requesterFullName = requesterFullName.trim();
            } else if (findMovement.getMaterialRequesterId() != null) {
                EmployeeResponseDTO req = this.employeeService.getEmployeeById(findMovement.getMaterialRequesterId());
                if (req != null) requesterFullName = (req.getFirstName() != null ? req.getFirstName() : "") + " " + (req.getLastName() != null ? req.getLastName() : "");
            }
            String authorizerFullName = null;
            if (authorizer != null) {
                authorizerFullName = (authorizer.getFirstName() != null ? authorizer.getFirstName() : "") +
                        " " + (authorizer.getLastName() != null ? authorizer.getLastName() : "");
                authorizerFullName = authorizerFullName.trim();
            }

            // Detalles de salida
            if (findMovement.getDetailExitMaterials() != null) {
                for (DetailExitMaterial d : findMovement.getDetailExitMaterials()) {
                    if (d == null) continue;
                    if (requesterFullName != null && !requesterFullName.isEmpty()) d.setEmployeeNameRequerter(requesterFullName);
                    if (authorizerFullName != null && !authorizerFullName.isEmpty()) d.setEmployeeNameAuthorized(authorizerFullName);
                    if (d.getTransactionCode() == null) d.setTransactionCode(findMovement.getTransactionCode());
                    this.detailExitMaterialRepository.save(d);
                }
            }

            // Detalles de entrada
            if (findMovement.getDetailEntryMaterials() != null) {
                for (DetailEntryMaterial d : findMovement.getDetailEntryMaterials()) {
                    if (d == null) continue;
                    if (requesterFullName != null && !requesterFullName.isEmpty()) d.setEmployeeNameRequerter(requesterFullName);
                    if (authorizerFullName != null && !authorizerFullName.isEmpty()) d.setEmployeeNameAuthorized(authorizerFullName);
                    if (d.getTransactionCode() == null) d.setTransactionCode(findMovement.getTransactionCode());
                    this.detailEntryMaterialRepository.save(d);
                }
            }

             this.movementRepository.save(findMovement);
             return true;

         }catch (Exception e){
             logger.error("Error actualizando movement: {}", e.getMessage(), e);
             return false;
         }
     }

     @Override
     public boolean deleteMovement(Long id) {
         try {
             Movement movement = this.getMovementById(id);
             this.movementRepository.delete(movement);
             return true;
         }catch (Exception e){
             logger.error("Error eliminando movement: {}", e.getMessage(), e);
             return false;
         }
     }

     @Override
     public boolean existsMovementById(Long id) {
         return this.movementRepository.existsById(id);
     }

     @Override
     public List<MovementResponseDTO> getMovementsByEmployeeId(Long employeeId) {
         List<MovementResponseDTO> movements = this.movementRepository.findMovementByEmployeeId(employeeId)
                 .stream()
                 .map(this.movementMapper::toMovementDTOResponse)
                 .collect(Collectors.toList());

         return  movements;
     }



    private boolean existsMovementByTransactionCode(String transactionCode) {
         return this.movementRepository.existsMovementByTransactionCode(transactionCode);
     }

     // Nuevo: calcula la suma de quantities de los detalles (entry + exit)
     private Integer calculateTotalQuantity(Movement movement) {
         int total = 0;
         if (movement == null) return 0;
         if (movement.getDetailExitMaterials() != null) {
             for (DetailExitMaterial d : movement.getDetailExitMaterials()) {
                 if (d != null && d.getQuantity() != null) total += d.getQuantity();
             }
         }
         if (movement.getDetailEntryMaterials() != null) {
             for (DetailEntryMaterial d : movement.getDetailEntryMaterials()) {
                 if (d != null && d.getQuantity() != null) total += d.getQuantity();
             }
         }
         return total;
     }

 }
