package com.ftn.controller;

import com.ftn.service.FuelConsumptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FuelConsumptionController {

    private final FuelConsumptionService fuelConsumptionService;

    public FuelConsumptionController(FuelConsumptionService fuelConsumptionService) {
        this.fuelConsumptionService = fuelConsumptionService;
    }

    @GetMapping("/api/simulate/fuelconsumption")
    public String startSimulation() {
        fuelConsumptionService.simulateFuelConsumption();
        return "Fuel consumption simulation started";
    }

    @GetMapping("/api/simulate/fuelconsumption/stop")
    public String stopSimulation() {
        fuelConsumptionService.stopSimulation();
        return "Fuel consumption simulation stopped";
    }
}
