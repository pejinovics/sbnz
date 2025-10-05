package com.ftn.utils;

import com.ftn.dtos.RuleDTO;
import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.KieSession;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WebSocketRuleNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketRuleNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    public void attach(KieSession kieSession) {
        kieSession.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                String rule = event.getMatch().getRule().getName();

                List<Object> facts = event.getMatch().getObjects().stream()
                        .filter(obj -> !(obj instanceof org.drools.core.reteoo.InitialFactImpl))
                        .collect(Collectors.toList());

                messagingTemplate.convertAndSend("/topic/rules", new RuleDTO(rule, facts));
            }
        });
    }

}