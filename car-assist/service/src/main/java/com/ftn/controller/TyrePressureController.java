package com.ftn.controller;

import com.ftn.service.TyrePressureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TyrePressureController {

    private final TyrePressureService tyrePressureService;

    public TyrePressureController(TyrePressureService tyrePressureService) {
        this.tyrePressureService = tyrePressureService;
    }

    @GetMapping("/api/simulate/tyrepressure")
    public String simulate() {
        tyrePressureService.simulateTyrePressure();
        return "Tyre Pressure simulation started";
    }
}
