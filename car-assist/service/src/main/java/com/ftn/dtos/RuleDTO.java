package com.ftn.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class RuleDTO {

    private String ruleName;
    private List<Object> facts;
    private LocalDateTime timestamp;

    public RuleDTO(String ruleName, List<Object> facts) {
        this.ruleName = ruleName;
        this.facts = facts;
        this.timestamp = LocalDateTime.now();
    }

    public String getRuleName() {
        return ruleName;
    }

    public List<Object> getFacts() {
        return facts;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

