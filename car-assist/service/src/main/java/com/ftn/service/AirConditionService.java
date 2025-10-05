package com.ftn.service;

import com.ftn.model.AirCondition;
import com.ftn.util.KnowledgeSessionHelper;
import com.ftn.utils.WebSocketRuleNotifier;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Service;

@Service
public class AirConditionService {

    private final WebSocketRuleNotifier ruleNotifier;

    public AirConditionService(WebSocketRuleNotifier ruleNotifier) {
        this.ruleNotifier = ruleNotifier;
    }

    public void simulateAirCondition() {
        try {
            KieContainer kc = KnowledgeSessionHelper.createRuleBase();
            KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kc, "airConditionKsession");

            ruleNotifier.attach(kSession);

            AirCondition airCondition = new AirCondition();
            airCondition.setState(AirCondition.State.OFF);
            airCondition.setMeasuredTemp(25.0);
            airCondition.setDesiredTemp(25.0);

            FactHandle handle = kSession.insert(airCondition);
            double[] temps = {22.0, 24.0, 25.0, 27.0, 26.0, 25.0};

            for (double t : temps) {
                airCondition.setMeasuredTemp(t);
                kSession.update(handle, airCondition);
                kSession.fireAllRules();
                Thread.sleep(1500);
            }

            kSession.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}