package com.ftn.controller;

import com.ftn.service.MotorSystemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MotorSystemController {

    private final MotorSystemService motorSystemService;

    public MotorSystemController(MotorSystemService motorSystemService) {
        this.motorSystemService = motorSystemService;
    }

    @GetMapping("/api/simulate/motorsystem")
    public String simulate() {
        motorSystemService.simulateMotorSystem();
        return "MotorSystem simulation started";
    }
}
