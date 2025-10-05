package com.ftn.controller;

import com.ftn.service.HvacSystemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HvacSystemController {

    private final HvacSystemService hvacSystemService;

    public HvacSystemController(HvacSystemService hvacSystemService) {
        this.hvacSystemService = hvacSystemService;
    }

    @GetMapping("/api/simulate/hvacsystem")
    public String simulate() {
        hvacSystemService.simulateHvacSystem();
        return "HvacSystem simulation started";
    }
}
