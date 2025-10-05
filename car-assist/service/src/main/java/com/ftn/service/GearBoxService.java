package com.ftn.service;

import com.ftn.model.GearBox;
import com.ftn.utils.WebSocketRuleNotifier;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class GearBoxService {

    private final WebSocketRuleNotifier notifier;

    @Autowired
    public GearBoxService(WebSocketRuleNotifier notifier) {
        this.notifier = notifier;
    }

    public void simulateGearBox() {
        try {
            InputStream template = GearBoxService.class.getResourceAsStream("/rules/gearBox/gear-box-template.drt");
            InputStream data = GearBoxService.class.getResourceAsStream("/templateTable/gearBox.xls");

            ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
            String drl = converter.compile(data, template, 3, 5);

            KieHelper kieHelper = new KieHelper();
            kieHelper.addContent(drl, ResourceType.DRL);

            Results results = kieHelper.verify();
            if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
                List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
                messages.forEach(msg -> System.out.println("Drools message: " + msg.getText()));
            }

            KieSession kSession = kieHelper.build().newKieSession();

            notifier.attach(kSession);

            GearBox gearBox = new GearBox(1, 1000, GearBox.State.OK);
            FactHandle handle = kSession.insert(gearBox);

            int[] rpms = {2500, 3500, 2000, 4000, 1800, 3700};

            for (int rpm : rpms) {
                gearBox.setCurrentRPM(rpm);
                kSession.update(handle, gearBox);
                kSession.fireAllRules();
                Thread.sleep(1500);
            }

            kSession.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
