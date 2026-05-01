package com.seprocom.gerencial.controller;

import com.seprocom.gerencial.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/atos")
    public List<String> getAtos() {
        return dashboardService.getAtosDisponiveis();
    }

    @GetMapping("/stats")
    public List<Map<String, Object>> getStats(
            @RequestParam String inicio,
            @RequestParam String fim,
            @RequestParam(defaultValue = "mensal") String tipo,
            @RequestParam(required = false) List<String> atos) {
        
        String dataIni = inicio.replace("-", "");
        String dataFim = fim.replace("-", "");
        
        return dashboardService.getEstatisticas(dataIni, dataFim, atos, tipo);
    }
}
