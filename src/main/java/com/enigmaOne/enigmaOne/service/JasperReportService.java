package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.MaterialOrder;
import com.enigmaOne.enigmaOne.persistence.entity.Order;
import com.enigmaOne.enigmaOne.persistence.entity.ServiceOrder;
import com.enigmaOne.enigmaOne.persistence.entity.Movement;
import com.enigmaOne.enigmaOne.persistence.repository.OrderRepository;
import com.enigmaOne.enigmaOne.persistence.repository.MovementRepository;
import com.enigmaOne.enigmaOne.service.dto.MaterialOrderDto;
import com.enigmaOne.enigmaOne.service.dto.OrderReportDto;
import com.enigmaOne.enigmaOne.service.dto.ServiceOrderDto;
import com.enigmaOne.enigmaOne.service.dto.MovementReportDto;
import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.DetailEntryMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.Stock;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import java.nio.charset.StandardCharsets;

@Service
public class JasperReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private StockService stockService;

    public byte[] generateOrderReportPdf(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }

            // Mapear a DTOs (evitar lazy exceptions)
            OrderReportDto dto = mapToDto(order);

            // Cargar JRXMLs desde classpath (leerlos primero y validar que no estén vacíos)
            try (InputStream materialsStream = getClass().getResourceAsStream("/jasper/materials_subreport.jrxml");
                 InputStream servicesStream = getClass().getResourceAsStream("/jasper/services_subreport.jrxml");
                 InputStream mainStream = getClass().getResourceAsStream("/jasper/order_report.jrxml")) {

                if (mainStream == null || materialsStream == null || servicesStream == null) {
                    throw new RuntimeException("No se encontraron los archivos JRXML en classpath:/jasper");
                }

                byte[] materialsBytes = readAllBytesOrThrow(materialsStream, "/jasper/materials_subreport.jrxml");
                byte[] servicesBytes = readAllBytesOrThrow(servicesStream, "/jasper/services_subreport.jrxml");
                byte[] mainBytes = readAllBytesOrThrow(mainStream, "/jasper/order_report.jrxml");

                JasperReport materialsSubreport = JasperCompileManager.compileReport(new ByteArrayInputStream(materialsBytes));
                JasperReport servicesSubreport = JasperCompileManager.compileReport(new ByteArrayInputStream(servicesBytes));
                JasperReport mainReport = JasperCompileManager.compileReport(new ByteArrayInputStream(mainBytes));

                Map<String, Object> params = new HashMap<>();
                params.put("orderId", dto.getId());
                params.put("type", dto.getType());
                params.put("employeeName", dto.getEmployeeName());
                params.put("departmentName", dto.getDepartmentName());
                params.put("approvalStatus", dto.getApprovalStatus());
                params.put("currentTrackingState", dto.getCurrentTrackingState());
                params.put("estimatedDateStock", dto.getEstimatedDateStock());
                params.put("createdAt", dto.getCreatedAt());
                params.put("generationDate", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

                // Subreports and datasources
                JRBeanCollectionDataSource materialsDs = new JRBeanCollectionDataSource(dto.getMaterialOrders() == null ? Collections.emptyList() : dto.getMaterialOrders());
                JRBeanCollectionDataSource servicesDs = new JRBeanCollectionDataSource(dto.getServiceOrders() == null ? Collections.emptyList() : dto.getServiceOrders());

                params.put("MaterialsDataSource", materialsDs);
                params.put("ServicesDataSource", servicesDs);
                params.put("MaterialsSubreport", materialsSubreport);
                params.put("ServicesSubreport", servicesSubreport);

                JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, params, new JREmptyDataSource());

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
                    return baos.toByteArray();
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error generating Jasper report: " + ex.getMessage(), ex);
        }
    }

    public byte[] generateMovementReportPdf(Long movementId) {
        try {
            Movement movement = movementRepository.findById(movementId).orElse(null);
            if (movement == null) throw new IllegalArgumentException("Movement not found: " + movementId);

            MovementReportDto dto = mapToMovementDto(movement);

            try (InputStream materialsStream = getClass().getResourceAsStream("/jasper/movement_materials_subreport.jrxml");
                 InputStream mainStream = getClass().getResourceAsStream("/jasper/movement_report.jrxml")) {

                if (mainStream == null || materialsStream == null) throw new RuntimeException("No se encontraron los archivos JRXML en classpath:/jasper");

                byte[] materialsBytes = readAllBytesOrThrow(materialsStream, "/jasper/movement_materials_subreport.jrxml");
                byte[] mainBytes = readAllBytesOrThrow(mainStream, "/jasper/movement_report.jrxml");

                JasperReport materialsSubreport = JasperCompileManager.compileReport(new ByteArrayInputStream(materialsBytes));
                JasperReport mainReport = JasperCompileManager.compileReport(new ByteArrayInputStream(mainBytes));

                Map<String, Object> params = new HashMap<>();
                params.put("movementId", dto.getId());
                params.put("transactionCode", dto.getTransactionCode());
                params.put("returnDate", dto.getReturnDate());
                params.put("authorizer", dto.getAuthorizer());
                params.put("requester", dto.getRequester());
                params.put("quantity", dto.getQuantity());
                params.put("createdAt", dto.getCreatedAt());
                params.put("generationDate", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

                JRBeanCollectionDataSource materialsDs = new JRBeanCollectionDataSource(dto.getMaterials() == null ? Collections.emptyList() : dto.getMaterials());
                params.put("MaterialsDataSource", materialsDs);
                params.put("MaterialsSubreport", materialsSubreport);

                JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, params, new JREmptyDataSource());

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
                    return baos.toByteArray();
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error generating Movement Jasper report: " + ex.getMessage(), ex);
        }
    }

    private OrderReportDto mapToDto(Order order) {
        OrderReportDto dto = new OrderReportDto();
        dto.setId(order.getId());
        dto.setType(order.getType() != null ? order.getType().name() : null);

        if (order.getEmployeeId() != null) {
            try {
                var emp = employeeService.getEmployeeById(order.getEmployeeId());
                if (emp != null) {
                    dto.setEmployeeName(emp.getFirstName() + " " + emp.getLastName());
                    dto.setDepartmentName(emp.getDepartmentName());
                }
            } catch (Exception ignored) {
            }
        }

        dto.setApprovalStatus(order.getApprovalStatus() != null ? order.getApprovalStatus().name() : null);
        dto.setCurrentTrackingState(order.getCurrentTrackingState() != null ? order.getCurrentTrackingState().name() : null);

        if (order.getEstimatedDateStock() != null) {
            dto.setEstimatedDateStock(new SimpleDateFormat("yyyy-MM-dd").format(order.getEstimatedDateStock()));
        }

        if (order.getCreated_at() != null) {
            dto.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(order.getCreated_at()));
        }

        List<MaterialOrderDto> materials = order.getMaterialOrders() == null ? Collections.emptyList()
                : order.getMaterialOrders().stream().map(this::mapMaterial).collect(Collectors.toList());

        List<ServiceOrderDto> services = order.getServiceOrders() == null ? Collections.emptyList()
                : order.getServiceOrders().stream().map(this::mapService).collect(Collectors.toList());

        dto.setMaterialOrders(materials);
        dto.setServiceOrders(services);
        return dto;
    }

    private MovementReportDto mapToMovementDto(Movement m) {
        MovementReportDto dto = new MovementReportDto();
        dto.setId(m.getId());
        dto.setTransactionCode(m.getTransactionCode());
        dto.setReturnDate(m.getReturnDate());
        dto.setQuantity(m.getQuantity());

        if (m.getEmployeeId() != null) {
            var emp = employeeService.getEmployeeById(m.getEmployeeId());
            if (emp != null) dto.setAuthorizer(emp.getFirstName() + " " + emp.getLastName());
        }
        if (m.getMaterialRequesterId() != null) {
            var req = employeeService.getEmployeeById(m.getMaterialRequesterId());
            if (req != null) dto.setRequester(req.getFirstName() + " " + req.getLastName());
        }

        if (m.getCreated_at() != null) dto.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(m.getCreated_at()));

        List<Map<String, Object>> materials = new ArrayList<>();

        if (m.getDetailExitMaterials() != null) {
            for (DetailExitMaterial d : m.getDetailExitMaterials()) {
                Map<String, Object> md = new HashMap<>();
                md.put("id", d.getId());
                md.put("stockId", d.getStockId());
                Stock s = d.getStock();
                if ((s == null || s.getCode() == null) && d.getStockId() != null) s = stockService.getStockById(d.getStockId());
                md.put("stockCode", s != null ? s.getCode() : null);
                md.put("quantity", d.getQuantity());
                md.put("destinationMaterial", d.getDestinationMaterial());
                md.put("type", "SALIDA");
                materials.add(md);
            }
        }

        if (m.getDetailEntryMaterials() != null) {
            for (DetailEntryMaterial d : m.getDetailEntryMaterials()) {
                Map<String, Object> md = new HashMap<>();
                md.put("id", d.getId());
                md.put("stockId", d.getStockId());
                Stock s = d.getStock();
                if ((s == null || s.getCode() == null) && d.getStockId() != null) s = stockService.getStockById(d.getStockId());
                md.put("stockCode", s != null ? s.getCode() : null);
                md.put("quantity", d.getQuantity());
                md.put("destinationMaterial", d.getDestinationMaterial());
                md.put("type", "ENTRADA");
                materials.add(md);
            }
        }

        dto.setMaterials(materials);
        return dto;
    }

    private MaterialOrderDto mapMaterial(MaterialOrder m) {
        MaterialOrderDto md = new MaterialOrderDto();
        md.setId(m.getId());
        md.setCode(m.getCode());
        md.setQuantity(m.getQuantity());
        md.setUnitOfMeasure(m.getUnitOfMeasure());
        md.setCharacteristics(m.getCharacteristics());
        md.setPhoto(m.getPhoto());
        if (m.getEstimatedDateStock() != null) md.setEstimatedDateStock(m.getEstimatedDateStock().toString());
        md.setObservations(m.getObservations());
        return md;
    }

    private ServiceOrderDto mapService(ServiceOrder s) {
        ServiceOrderDto sd = new ServiceOrderDto();
        sd.setId(s.getId());
        sd.setCode(s.getCode());
        sd.setCharacteristics(s.getCharacteristics());
        if (s.getDeliveryDate() != null) sd.setDeliveryDate(s.getDeliveryDate().toString());
        return sd;
    }

    // Helper: lee todo el stream y lanza RuntimeException si está vacío
    private byte[] readAllBytesOrThrow(InputStream is, String resourcePath) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            byte[] bytes = baos.toByteArray();
            if (bytes == null || bytes.length == 0) {
                throw new RuntimeException("JRXML vacío o corrupto: " + resourcePath);
            }
            return bytes;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo resource " + resourcePath + ": " + e.getMessage(), e);
        }
    }

}
