// ...existing code...
package com.enigmaOne.enigmaOne.controller;

import com.enigmaOne.enigmaOne.service.JasperReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// imports RestController and RequestMapping removed (controlador obsoleto, no registrado)

// Este controlador quedó obsoleto: el endpoint de reporte fue movido a `OrderController`.
// He comentado las anotaciones para evitar que Spring lo registre y crear conflictos de mapeo.

/* @RestController
@RequestMapping("/orders") */
public class OrderReportController {

    private final JasperReportService jasperReportService;

    public OrderReportController(JasperReportService jasperReportService) {
        this.jasperReportService = jasperReportService;
    }

    // El método se mantiene como referencia pero no está expuesto. Usar /orders/{id}/report en OrderController.
    @GetMapping("/{id}/report")
    public ResponseEntity<byte[]> getOrderReport(@PathVariable("id") Long id) {
        byte[] pdf = jasperReportService.generateOrderReportPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "order-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
// ...existing code...

