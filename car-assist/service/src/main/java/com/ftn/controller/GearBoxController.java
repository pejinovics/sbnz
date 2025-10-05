package com.ftn.controller;

import com.ftn.service.GearBoxService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GearBoxController {

    private final GearBoxService gearBoxService;

    public GearBoxController(GearBoxService gearBoxService) {
        this.gearBoxService = gearBoxService;
    }

    @GetMapping("/api/simulate/gearbox")
    public String simulate() {
        gearBoxService.simulateGearBox();
        return "GearBox simulation started";
    }
}
