package com.ftn.utils;

import com.ftn.dtos.RuleDTO;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.reteoo.InitialFactImpl;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.KieSession;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
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
                try {
                    String ruleName = event.getMatch().getRule().getName();
                    if ("go".equalsIgnoreCase(ruleName)) return;

                    List<Object> facts = event.getMatch().getObjects().stream()
                            .filter(obj -> !(obj instanceof InitialFactImpl))
                            .collect(Collectors.toList());

                    // --- BACKWARD: sistem zdrav / kvaru ---
                    if (handleBackwardHealthRule(ruleName)) return;

                    // --- BACKWARD: prikaz / napajanje ---
                    if (handleBackwardRelationRule(ruleName, facts)) return;

                    // --- OSTALA PRAVILA ---
                    handleStandardRule(ruleName, facts);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean handleBackwardHealthRule(String ruleName) {
        String lower = ruleName.toLowerCase();

        if (lower.contains("sistem zdrav") || lower.contains("sistem u kvaru")) {
            String system;
            if (lower.contains("hvac") || lower.contains("klima") || lower.contains("ventilacija")) {
                system = "Klima i ventilacija (HVAC)";
            } else {
                system = "Motorni sistem";
            }

            String status = lower.contains("zdrav")
                    ? system + " je zdrav"
                    : system + " je u kvaru";

            sendSocket(ruleName, List.of(Map.of("type", "Result", "details", status)));
            return true;
        }

        return false;
    }


    private boolean handleBackwardRelationRule(String ruleName, List<Object> facts) {
        String lower = ruleName.toLowerCase();
        if (!(lower.contains("prikaz sistema") ||
                lower.contains("napajanje gorivom") ||
                lower.contains("proveri napajanje gorivom") ||
                lower.contains("distribucija vazduha") ||
                lower.contains("hlađenje i kompresija") ||
                lower.contains("upravljanje"))) {
            return false;
        }


        List<Map<String, Object>> relations = new ArrayList<>();

        for (Object o : facts) {
            if (o instanceof Object[]) {
                Object[] pair = (Object[]) o;
                Map<String, Object> rel = new HashMap<>();
                rel.put("type", "Relation");

                if (lower.contains("napajanje gorivom") && pair.length >= 1) {
                    rel.put("details", pair[0] + " → Napajanje gorivom");
                } else if (pair.length >= 2) {
                    rel.put("details", pair[0] + " → " + pair[1]);
                } else {
                    rel.put("details", Arrays.toString(pair));
                }

                if (lower.contains("distribucija vazduha") && pair.length >= 1) {
                    rel.put("details", pair[0] + " → Distribucija vazduha");
                } else if (lower.contains("hlađenje i kompresija") && pair.length >= 1) {
                    rel.put("details", pair[0] + " → Hlađenje i kompresija");
                } else if (lower.contains("upravljanje") && pair.length >= 1) {
                    rel.put("details", pair[0] + " → Upravljanje");
                }
                relations.add(rel);
            }
        }

        sendSocket(ruleName, Collections.singletonList(relations));
        return true;
    }

    private void handleStandardRule(String ruleName, List<Object> facts) {
        List<Map<String, Object>> factsJson = new ArrayList<>();

        for (int i = 0; i < facts.size(); i++) {
            Object obj = facts.get(i);
            String label = (obj instanceof Number)
                    ? resolveDoubleLabel(ruleName, i)
                    : obj.getClass().getSimpleName();

            factsJson.add(Map.of(
                    "type", label,
                    "details", obj
            ));
        }

        sendSocket(ruleName, Collections.singletonList(factsJson));
    }

    private void sendSocket(String ruleName, Object data) {
        messagingTemplate.convertAndSend("/topic/rules", new RuleDTO(ruleName, (List<Object>) data));
    }

    private String resolveDoubleLabel(String ruleName, int index) {
        String name = ruleName.trim().toLowerCase();

        // ---- FUEL CONSUMPTION ----
        if (name.equals("immediate fuel consumption")) {
            if (index == 0) return "avgFuelFlow (mg/s)";
            else if (index == 1) return "avgSpeed (km/h)";
            else if (index == 2) return "calculatedConsumption (L/100km)";
            else return "value_" + (index + 1);
        }

        // ---- BRAKE ASSIST ----
        if (name.equals("calculate time to coalision")) {
            if (index == 1) return "frontCarSpeed (km/h)";
            else if (index == 2) return "ownCarSpeed (km/h)";
            else return "value_" + (index + 1);
        }

        if (name.equals("check front vehicle distance and speed")) {
            return "ownCarSpeed (km/h)";
        }
        if (name.equals("evaluate ttc danger")) {
            return "eventCount (TTC events in 3s)";
        }
        // ---- LINE ASSIST ----
        if (name.contains("turn signal off")) {
            return "currentSpeed (km/h)";
        }

        // ---- DEFAULT ----
        return "value_" + (index + 1);
    }
}
