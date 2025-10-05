package com.ftn.controller;

import com.ftn.service.BrakeAssistService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrakeAssistController {

    private final BrakeAssistService brakeAssistService;

    public BrakeAssistController(BrakeAssistService brakeAssistService) {
        this.brakeAssistService = brakeAssistService;
    }

    @GetMapping("/api/simulate/brakeassist")
    public String simulate() {
        brakeAssistService.simulateBrakeAssist();
        return "Brake Assist simulation started";
    }

    @GetMapping("/api/simulate/brakeassist/stop")
    public String stop() {
        brakeAssistService.stopSimulation();
        return "Brake Assist simulation stopped";
    }
}
