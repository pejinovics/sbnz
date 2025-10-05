package com.ftn.service;

import com.ftn.model.Side;
import com.ftn.model.Tyre;
import com.ftn.model.TyreSeason;
import com.ftn.utils.LoadingUtility;
import com.ftn.utils.WebSocketRuleNotifier;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

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
            DataProvider dataProvider = LoadingUtility.loadTemplateFromCSV("templateTable/tyrePressure.csv");

            DataProviderCompiler converter = new DataProviderCompiler();
            String drl = converter.compile(dataProvider, template);

            KieSession kSession = LoadingUtility.createKieSessionFromDRL(drl);
            notifier.attach(kSession);

            Properties properties = LoadingUtility.loadSystemProperties();

            Tyre frontLeft = new Tyre(Double.parseDouble(properties.getProperty("front_left_tyre.pressure")),
                    TyreSeason.valueOf(properties.getProperty("front_left_tyre.season").toUpperCase()),
                    Side.valueOf(properties.getProperty("front_left_tyre.side").toUpperCase()));


            Tyre frontRight = new Tyre(Double.parseDouble(properties.getProperty("front_right_tyre.pressure")),
                    TyreSeason.valueOf(properties.getProperty("front_right_tyre.season").toUpperCase()),
                    Side.valueOf(properties.getProperty("front_right_tyre.side").toUpperCase()));

            Tyre rearLeft = new Tyre(Double.parseDouble(properties.getProperty("rear_left_tyre.pressure")),
                    TyreSeason.valueOf(properties.getProperty("rear_left_tyre.season").toUpperCase()),
                    Side.valueOf(properties.getProperty("rear_left_tyre.side").toUpperCase()));

            Tyre rearRight = new Tyre(Double.parseDouble(properties.getProperty("rear_right_tyre.pressure")),
                    TyreSeason.valueOf(properties.getProperty("rear_right_tyre.season").toUpperCase()),
                    Side.valueOf(properties.getProperty("rear_right_tyre.side").toUpperCase()));

            FactHandle handle = kSession.insert(frontLeft);
            kSession.insert(frontRight);
            kSession.insert(rearLeft);
            kSession.insert(rearRight);

            List<Double> pressures = LoadingUtility.loadDataFromCSV("testCases/tyrePressures1.csv");
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
