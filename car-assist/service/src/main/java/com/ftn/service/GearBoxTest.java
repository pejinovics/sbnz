package com.ftn.service;

import com.ftn.model.GearBox;
import com.ftn.util.KnowledgeSessionHelper;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class GearBoxTest {

    public static void main(){
        try{
            KieContainer kc = KnowledgeSessionHelper.createRuleBase();
            KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kc, "gearBoxKsession");

            GearBox gearBox = new GearBox(1, 1000, GearBox.State.OK);

            FactHandle handle = kSession.insert(gearBox);
            kSession.fireAllRules();

            gearBox.setCurrentRPM(2500);
            kSession.update(handle, gearBox);
            kSession.fireAllRules();

            gearBox.setCurrentRPM(3500);
            kSession.update(handle, gearBox);
            kSession.fireAllRules();

            gearBox.setCurrentGear(2);
            gearBox.setCurrentRPM(2500);
            kSession.update(handle, gearBox);
            kSession.fireAllRules();

            gearBox.setCurrentRPM(3500);
            kSession.update(handle, gearBox);
            kSession.fireAllRules();

            gearBox.setCurrentGear(3);
            gearBox.setCurrentRPM(2500);
            kSession.update(handle, gearBox);
            kSession.fireAllRules();

            gearBox.setCurrentRPM(1000);
            kSession.update(handle, gearBox);
            kSession.fireAllRules();


        }
        catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
