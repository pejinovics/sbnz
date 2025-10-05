package com.ftn.service;

import com.ftn.model.Component;
import com.ftn.util.KnowledgeSessionHelper;
import com.ftn.utils.WebSocketRuleNotifier;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Service
public class HvacSystemService {

    private final WebSocketRuleNotifier ruleNotifier;

    public HvacSystemService(WebSocketRuleNotifier ruleNotifier) {
        this.ruleNotifier = ruleNotifier;
    }

    public void simulateHvacSystem() {
        try {
            KieContainer kc = KnowledgeSessionHelper.createRuleBase();
            KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kc, "backwardhvacKsession");

            ruleNotifier.attach(kSession);

            insertHierarchy(kSession);

            String[] commands = {
                    "Pravilo: Sistem zdrav",
                    "Pravilo: Sistem u kvaru",
                    "Pravilo: Prikaz sistema",
                    "Pravilo: Proveri Distribuciju vazduha",
                    "Pravilo: Distribucija vazduha"
            };

            for (String cmd : commands) {
                kSession.insert(cmd);
                kSession.fireAllRules();
                Thread.sleep(1200);
            }

            kSession.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void insertHierarchy(KieSession kSession) {
        // Gornji nivo
        kSession.insert(new Component("Hlađenje i kompresija", "Klima i ventilacija (HVAC)"));
        kSession.insert(new Component("Distribucija vazduha", "Klima i ventilacija (HVAC)"));
        kSession.insert(new Component("Upravljanje", "Klima i ventilacija (HVAC)"));

        // --- Hlađenje i kompresija ---
        kSession.insert(new Component("Senzor pritiska freona", "Hlađenje i kompresija"));
        kSession.insert(new Component("Senzor temperature spolja/unutra", "Hlađenje i kompresija"));
        kSession.insert(new Component("Ventilator kondenzatora", "Hlađenje i kompresija"));

        // --- Distribucija vazduha ---
        kSession.insert(new Component("Aktuatori klapni", "Distribucija vazduha"));
        kSession.insert(new Component("Regulator ventilatora kabine", "Distribucija vazduha"));
        kSession.insert(new Component("Motor ventilatora kabine", "Distribucija vazduha"));

        // --- Upravljanje ---
        kSession.insert(new Component("HVAC kontrolni modul", "Upravljanje"));
        kSession.insert(new Component("Sun-load senzor (solarni)", "Upravljanje"));

        // --- Status komponenti ---
        kSession.insert(new Component("true", "Senzor pritiska freona"));
        kSession.insert(new Component("true", "Senzor temperature spolja/unutra"));
        kSession.insert(new Component("true", "Ventilator kondenzatora"));

        kSession.insert(new Component("true", "Aktuatori klapni"));
        kSession.insert(new Component("true", "Regulator ventilatora kabine"));
        kSession.insert(new Component("true", "Motor ventilatora kabine"));

        kSession.insert(new Component("true", "HVAC kontrolni modul"));
        kSession.insert(new Component("true", "Sun-load senzor (solarni"));
    }
}
