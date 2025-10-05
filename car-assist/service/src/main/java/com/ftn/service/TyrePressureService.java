package com.ftn.service;

import com.ftn.dtos.RuleDTO;
import com.ftn.model.Tyre;
import com.ftn.model.TyreSeason;
import com.ftn.model.TyreSide;
import com.ftn.utils.TemplateLoadingUtility;
import com.ftn.utils.WebSocketRuleNotifier;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class TyrePressureService {

    private final WebSocketRuleNotifier notifier;

    @Autowired
    public TyrePressureService(WebSocketRuleNotifier notifier) {
        this.notifier = notifier;
    }

    public void simulateTyrePressure() {
        try {
            InputStream template = TyrePressureService.class.getResourceAsStream("/rules/tyrePressure/tyre-pressure-template.drt");
            DataProvider dataProvider = TemplateLoadingUtility.loadTemplateFromCSV("templateTable/tyrePressure.csv");

            DataProviderCompiler converter = new DataProviderCompiler();
            String drl = converter.compile(dataProvider, template);

            KieSession kSession = TemplateLoadingUtility.createKieSessionFromDRL(drl);
            notifier.attach(kSession);

            Tyre frontLeft = new Tyre(2.2, TyreSeason.WINTER, TyreSide.FRONT_LEFT);
            Tyre frontRight = new Tyre(3.0, TyreSeason.WINTER, TyreSide.FRONT_RIGHT);
            Tyre rearLeft = new Tyre(2.2, TyreSeason.WINTER, TyreSide.REAR_LEFT);
            Tyre rearRight = new Tyre(2.2, TyreSeason.WINTER, TyreSide.REAR_RIGHT);

            FactHandle handle = kSession.insert(frontLeft);
            kSession.insert(frontRight);
            kSession.insert(rearLeft);
            kSession.insert(rearRight);

            double[] pressures = {1.8, 2.5, 3.4, 2.1};
            for (double p : pressures) {
                frontLeft.setPressure(p);
                kSession.update(handle, frontLeft);
                kSession.fireAllRules();
                Thread.sleep(1500);
            }

            kSession.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
