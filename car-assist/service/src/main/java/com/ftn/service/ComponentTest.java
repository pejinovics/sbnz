package com.ftn.service;

import com.ftn.model.Component;
import com.ftn.util.KnowledgeSessionHelper;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class ComponentTest {
    public static void main() {
        try {
            KieContainer kc = KnowledgeSessionHelper.createRuleBase();
            KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kc, "backwardKsession");

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

            kSession.insert("Pravilo: Sistem zdrav");
            kSession.fireAllRules();
            System.out.println("---");

            kSession.insert("Pravilo: Sistem u kvaru");
            kSession.fireAllRules();
            System.out.println("---");

            kSession.insert("Pravilo: Prikaz sistema");
//            System.out.println("Pravilo: Prikaz sistema");
            kSession.fireAllRules();
            System.out.println("---");

            kSession.insert("Pravilo: Proveri Napajanje gorivom");
            kSession.fireAllRules();
            System.out.println("---");

            kSession.insert("Pravilo: Napajanje gorivom");
            kSession.fireAllRules();
            System.out.println("---");


            kSession.dispose();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

