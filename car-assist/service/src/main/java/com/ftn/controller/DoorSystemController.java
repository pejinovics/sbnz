package com.ftn.controller;

import com.ftn.service.DoorSystemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DoorSystemController {

    private final DoorSystemService doorSystemService;

    public DoorSystemController(DoorSystemService doorSystemService) {
        this.doorSystemService = doorSystemService;
    }

    @GetMapping("/api/simulate/doorsystem")
    public String simulate() {
        doorSystemService.simulateDoorSystem();
        return "DoorSystem simulation started";
    }
}
