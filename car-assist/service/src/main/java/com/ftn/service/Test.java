package com.ftn.service;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.ftn.model.Message;
import com.ftn.util.KnowledgeSessionHelper;


public class Test{
    public static void main(){
        try{
            // instanciranje
            KieContainer kc = KnowledgeSessionHelper.createRuleBase();
            KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kc, "k-session");
        

            Message message = new Message();
            message.setMessage("Cao stefooo");
            message.setStatus(Message.HELLO);
            kSession.insert(message);
            kSession.fireAllRules();


        }catch(Throwable t){
            t.printStackTrace();
        }
    }
}


            