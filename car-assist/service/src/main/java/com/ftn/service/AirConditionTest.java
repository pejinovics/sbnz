package com.ftn.service;

import com.ftn.model.AirCondition;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.ftn.util.KnowledgeSessionHelper;
import org.kie.api.runtime.rule.FactHandle;


public class AirConditionTest{
    public static void main(){
        try{
            KieContainer kc = KnowledgeSessionHelper.createRuleBase();
            KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kc, "airConditionKsession");

            AirCondition airCondition = new AirCondition();
            airCondition.setState(AirCondition.State.OFF);
            airCondition.setMeasuredTemp(25.0);
            airCondition.setDesiredTemp(25.0);

            FactHandle handle = kSession.insert(airCondition);
            kSession.fireAllRules();

            airCondition.setMeasuredTemp(22.0);
            kSession.update(handle, airCondition);
            kSession.fireAllRules();

            airCondition.setMeasuredTemp(24.0);
            kSession.update(handle, airCondition);
            kSession.fireAllRules();

            airCondition.setMeasuredTemp(25.0);
            kSession.update(handle, airCondition);
            kSession.fireAllRules();

            airCondition.setMeasuredTemp(27.0);
            kSession.update(handle, airCondition);
            kSession.fireAllRules();

            airCondition.setMeasuredTemp(26.0);
            kSession.update(handle, airCondition);
            kSession.fireAllRules();

            airCondition.setMeasuredTemp(25.0);
            kSession.update(handle, airCondition);
            kSession.fireAllRules();

        }catch(Throwable t){
            t.printStackTrace();
        }
    }
}
