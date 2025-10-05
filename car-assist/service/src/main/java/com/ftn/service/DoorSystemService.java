package com.ftn.service;

import com.ftn.model.DoorSystem;
import com.ftn.model.Side;
import com.ftn.util.KnowledgeSessionHelper;
import com.ftn.utils.WebSocketRuleNotifier;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Service;

@Service
public class DoorSystemService {

    private final WebSocketRuleNotifier ruleNotifier;

    public DoorSystemService(WebSocketRuleNotifier ruleNotifier) {
        this.ruleNotifier = ruleNotifier;
    }

    public void simulateDoorSystem() {
        try {
            // --- Inicijalizacija Kie sesije ---
            KieContainer kc = KnowledgeSessionHelper.createRuleBase();
            KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kc, "doorSystemKsession");

            ruleNotifier.attach(kSession);

            // --- Inicijalno stanje ---
            DoorSystem doorSystem = new DoorSystem();
            doorSystem.setKeyPressed(true);

            FactHandle handle = kSession.insert(doorSystem);

            // --- 1. Scenario: zadnja desna vrata otvorena ---
            doorSystem.setDoor(Side.REAR_RIGHT, false);
            kSession.update(handle, doorSystem);
            kSession.fireAllRules();
            Thread.sleep(1500);

            // --- 2. Scenario: zatvorena vrata, otvoren levi prednji prozor ---
            doorSystem.setDoor(Side.REAR_RIGHT, true);
            doorSystem.setWindow(Side.FRONT_LEFT, false);
            kSession.update(handle, doorSystem);
            kSession.fireAllRules();
            Thread.sleep(1500);

            // --- 3. Scenario: sve zatvoreno ---
            doorSystem.setWindow(Side.FRONT_LEFT, true);
            kSession.update(handle, doorSystem);
            kSession.fireAllRules();
            Thread.sleep(1000);

            kSession.dispose();
            System.out.println("DoorSystem simulation finished.");

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
