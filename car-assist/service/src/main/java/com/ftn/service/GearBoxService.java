package com.ftn.service;

import com.ftn.model.GearBox;
import com.ftn.utils.LoadingUtility;
import com.ftn.utils.WebSocketRuleNotifier;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
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
            DataProvider dataProvider = LoadingUtility.loadTemplateFromCSV("templateTable/gearBox.csv");
            DataProviderCompiler converter = new DataProviderCompiler();
            String drl = converter.compile(dataProvider, template);

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

            int[] rpms = LoadingUtility.loadDataFromCSV("testCases/gearBoxTestData1.csv")
                    .stream()
                    .mapToInt(Double::intValue)
                    .toArray();

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
