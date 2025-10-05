package com.ftn.controller;

import com.ftn.service.AirConditionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AirConditionController {

    private final AirConditionService airConditionService;

    public AirConditionController(AirConditionService airConditionService) {
        this.airConditionService = airConditionService;
    }

    @GetMapping("/api/simulate/aircondition")
    public String simulate() {
        airConditionService.simulateAirCondition();
        return "AirCondition simulation started";
    }
}

