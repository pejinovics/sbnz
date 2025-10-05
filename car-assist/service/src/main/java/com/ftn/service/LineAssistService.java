package com.ftn.service;

import com.ftn.model.DriveSystem;
import com.ftn.model.SurroundSystem;
import com.ftn.model.events.CurrentSpeedEvent;
import com.ftn.utils.TemplateLoadingUtility;
import com.ftn.utils.WebSocketRuleNotifier;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;

import static java.lang.Math.abs;

@Service
public class LineAssistService {

    private final WebSocketRuleNotifier notifier;
    private volatile boolean running = false;
    private Thread simulationThread;

    @Autowired
    public LineAssistService(WebSocketRuleNotifier notifier) {
        this.notifier = notifier;
    }

    public void simulateLineAssist() {
        if (running) {
            System.out.println("Simulation already running...");
            return;
        }

        running = true;

        try {
            InputStream template = LineAssistService.class.getResourceAsStream("/rules/lineAssist/line-assist.drt");
            DataProvider dataProvider = TemplateLoadingUtility.loadTemplateFromCSV("templateTable/lineAssist.csv");

            DataProviderCompiler converter = new DataProviderCompiler();
            String drl = converter.compile(dataProvider, template);

            KieSession kieSession = TemplateLoadingUtility.createKieSessionFromDRLForCEP(drl);
            notifier.attach(kieSession);

            // Postavljanje global promenljivih
            kieSession.setGlobal("minDistance", 0.5);
            kieSession.setGlobal("minLineWarnSpeed", 30.0);
            kieSession.setGlobal("minLineSteerSpeed", 60.0);

            // Inicijalizacija početnih objekata
            SurroundSystem surroundSystem = new SurroundSystem(0.1, 0.7, true, 10.0, 1.0);
            DriveSystem driveSystem = new DriveSystem(0.0, false, false, false);
            surroundSystem.setLeftLineDistance(0.1);

            kieSession.insert(surroundSystem);
            kieSession.insert(driveSystem);

            // Paralelni thread za simulaciju brzine
            simulationThread = new Thread(() -> {
                try {
                    while (running) {
                        double currentSpeed = 80 * Math.sin(System.currentTimeMillis());
                        kieSession.insert(new CurrentSpeedEvent(abs(currentSpeed), new Date()));
                        kieSession.fireAllRules();
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            simulationThread.start();
        } catch (Throwable t) {
            t.printStackTrace();
            running = false;
        }
    }

    public void stopSimulation() {
        running = false;
        if (simulationThread != null) {
            simulationThread.interrupt();
            System.out.println("Line assist simulation stopped.");
        }
    }
}
