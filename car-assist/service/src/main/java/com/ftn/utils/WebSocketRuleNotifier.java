package com.ftn.utils;

import com.ftn.dtos.RuleDTO;
import com.ftn.model.events.CurrentSpeedEvent;
import com.ftn.model.events.FuelFlowEvent;
import com.ftn.model.events.TriggerEvent;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.reteoo.InitialFactImpl;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.KieSession;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
//                String rule = event.getMatch().getRule().getName();
//
//                List<Object> facts = event.getMatch().getObjects().stream()
//                        .filter(obj -> !(obj instanceof org.drools.core.reteoo.InitialFactImpl))
//                        .collect(Collectors.toList());
//
//                messagingTemplate.convertAndSend("/topic/rules", new RuleDTO(rule, facts));
                try {
                    String ruleName = event.getMatch().getRule().getName();

                    List<Map<String, Object>> factsJson = event.getMatch()
                            .getObjects()
                            .stream()
                            .filter(obj -> !(obj instanceof InitialFactImpl))
                            .map(obj -> Map.of(
                                    "type", obj.getClass().getSimpleName(),
                                    "details", extractFactDetails(obj)
                            ))
                            .collect(Collectors.toList());

                    messagingTemplate.convertAndSend(
                            "/topic/rules",
                            new RuleDTO(ruleName, Collections.singletonList(factsJson))
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private Object extractFactDetails(Object obj) {
        if (obj instanceof CurrentSpeedEvent) {
            CurrentSpeedEvent e = (CurrentSpeedEvent) obj;
            return Map.of(
                    "speed", e.getCurrentSpeed(),
                    "timestamp", e.getTimestamp(),
                    "isFrontVehicle", e.isCarInFront()
            );
        }

        if (obj instanceof FuelFlowEvent) {
            FuelFlowEvent e = (FuelFlowEvent) obj;
            return Map.of(
                    "flowRate", e.getFuelMiligrams(),
                    "timestamp", e.getTimestamp()
            );
        }

        if (obj instanceof TriggerEvent) {
            TriggerEvent e = (TriggerEvent) obj;
            return Map.of(
                    "type", e.getType(),
                    "timestamp", e.getTimestamp()
            );
        }

        if (obj instanceof Number) {
            return obj;
        }

        return obj;
    }


}