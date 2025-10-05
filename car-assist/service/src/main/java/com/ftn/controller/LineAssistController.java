package com.ftn.controller;

import com.ftn.service.LineAssistService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineAssistController {

    private final LineAssistService lineAssistService;

    public LineAssistController(LineAssistService lineAssistService) {
        this.lineAssistService = lineAssistService;
    }

    @GetMapping("/api/simulate/lineassist")
    public String simulate() {
        lineAssistService.simulateLineAssist();
        return "Line Assist simulation started";
    }

    @GetMapping("/api/simulate/lineassist/stop")
    public String stop() {
        lineAssistService.stopSimulation();
        return "Line Assist simulation stopped";
    }
}
