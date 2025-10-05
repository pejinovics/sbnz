package com.ftn.service;

import com.ftn.model.Component;
import com.ftn.util.KnowledgeSessionHelper;
import com.ftn.utils.WebSocketRuleNotifier;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Service
public class MotorSystemService {

    private final WebSocketRuleNotifier ruleNotifier;

    public MotorSystemService(WebSocketRuleNotifier ruleNotifier) {
        this.ruleNotifier = ruleNotifier;
    }

    public void simulateMotorSystem() {
        try {
            KieContainer kc = KnowledgeSessionHelper.createRuleBase();
            KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kc, "backwardKsession");

            ruleNotifier.attach(kSession);

            insertHierarchy(kSession);

            String[] commands = {
                    "Pravilo: Sistem zdrav",
                    "Pravilo: Sistem u kvaru",
                    "Pravilo: Prikaz sistema",
                    "Pravilo: Proveri Napajanje gorivom",
                    "Pravilo: Napajanje gorivom"
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
        kSession.insert(new Component("Napajanje gorivom", "Motorni sistem"));
        kSession.insert(new Component("Usis / vazduh", "Motorni sistem"));
        kSession.insert(new Component("Paljenje", "Motorni sistem"));

        kSession.insert(new Component("Senzor pritiska goriva", "Napajanje gorivom"));
        kSession.insert(new Component("Regulator pritiska", "Napajanje gorivom"));
        kSession.insert(new Component("Relej pumpe", "Napajanje gorivom"));

        kSession.insert(new Component("MAF-protok vazduha", "Usis / vazduh"));
        kSession.insert(new Component("MAP-pritisak u usisu", "Usis / vazduh"));
        kSession.insert(new Component("EGR ventil", "Usis / vazduh"));

        kSession.insert(new Component("CKP-radilica", "Paljenje"));
        kSession.insert(new Component("Senzor detonacija", "Paljenje"));

        kSession.insert(new Component("true", "CKP-radilica"));
        kSession.insert(new Component("false", "Senzor detonacija"));

        kSession.insert(new Component("true", "Senzor pritiska goriva"));
        kSession.insert(new Component("true", "Regulator pritiska"));
        kSession.insert(new Component("true", "Relej pumpe"));

        kSession.insert(new Component("true", "MAF-protok vazduha"));
        kSession.insert(new Component("true", "MAP-pritisak u usisu"));
        kSession.insert(new Component("true", "EGR ventil"));
    }
}
