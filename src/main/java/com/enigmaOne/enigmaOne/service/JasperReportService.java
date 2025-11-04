package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.MaterialOrder;
import com.enigmaOne.enigmaOne.persistence.entity.Order;
import com.enigmaOne.enigmaOne.persistence.entity.ServiceOrder;
import com.enigmaOne.enigmaOne.persistence.repository.OrderRepository;
import com.enigmaOne.enigmaOne.service.dto.MaterialOrderDto;
import com.enigmaOne.enigmaOne.service.dto.OrderReportDto;
import com.enigmaOne.enigmaOne.service.dto.ServiceOrderDto;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JasperReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmployeeService employeeService;

    public byte[] generateOrderReportPdf(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }

            // Mapear a DTOs (evitar lazy exceptions)
            OrderReportDto dto = mapToDto(order);

            // Cargar JRXMLs desde classpath
            try (InputStream materialsStream = getClass().getResourceAsStream("/jasper/materials_subreport.jrxml");
                 InputStream servicesStream = getClass().getResourceAsStream("/jasper/services_subreport.jrxml");
                 InputStream mainStream = getClass().getResourceAsStream("/jasper/order_report.jrxml")) {

                if (mainStream == null || materialsStream == null || servicesStream == null) {
                    throw new RuntimeException("No se encontraron los archivos JRXML en classpath:/jasper");
                }

                JasperReport materialsSubreport = JasperCompileManager.compileReport(materialsStream);
                JasperReport servicesSubreport = JasperCompileManager.compileReport(servicesStream);
                JasperReport mainReport = JasperCompileManager.compileReport(mainStream);

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

}
