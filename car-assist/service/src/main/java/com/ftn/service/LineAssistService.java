package com.ftn.service;

import com.ftn.model.DriveSystem;
import com.ftn.model.SurroundSystem;
import com.ftn.model.events.CurrentSpeedEvent;
import com.ftn.utils.LoadingUtility;
import com.ftn.utils.WebSocketRuleNotifier;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
            DataProvider dataProvider = LoadingUtility.loadTemplateFromCSV("templateTable/lineAssist.csv");

            DataProviderCompiler converter = new DataProviderCompiler();
            String drl = converter.compile(dataProvider, template);

            KieSession kieSession = LoadingUtility.createKieSessionFromDRLForCEP(drl);
            notifier.attach(kieSession);

            // Postavljanje global promenljivih

            Properties properties = LoadingUtility.loadSystemProperties();

            kieSession.setGlobal("minDistance", Double.parseDouble(properties.getProperty("line_assist.min_distance")));
            kieSession.setGlobal("minLineWarnSpeed", Double.parseDouble(properties.getProperty("line_assist.min_line_warn_speed")));
            kieSession.setGlobal("minLineSteerSpeed", Double.parseDouble(properties.getProperty("line_assist.min_line_steer_speed")));

            // Inicijalizacija početnih objekata

            SurroundSystem surroundSystem = new SurroundSystem(Double.parseDouble(properties.getProperty("surround_system.left_line_distance")),
                    Double.parseDouble(properties.getProperty("surround_system.right_line_distance")),
                    Boolean.parseBoolean(properties.getProperty("surround_system.line_visible")),
                    Double.parseDouble(properties.getProperty("surround_system.front_vehicle_distance")),
                    1.0);
            DriveSystem driveSystem = new DriveSystem(0.0,
                    Boolean.parseBoolean(properties.getProperty("drive_system.brake_pressed")),
                    Boolean.parseBoolean(properties.getProperty("drive_system.left_turn_signal")),
                    Boolean.parseBoolean(properties.getProperty("drive_system.right_turn_signal")));
            surroundSystem.setLeftLineDistance(0.1);

            kieSession.insert(surroundSystem);
            kieSession.insert(driveSystem);

            // Paralelni thread za simulaciju brzine
            simulationThread = new Thread(() -> {
                List<Double> ownCarSpeed = LoadingUtility.loadDataFromCSV("testCases/speedValues/values_30_to_40.csv");
                try {
                    int size = ownCarSpeed.size();
                    int i = size;
                    while (running) {
                        kieSession.insert(new CurrentSpeedEvent(ownCarSpeed.get(i % size), new Date()));
                        kieSession.fireAllRules();
                        Thread.sleep(1000);
                        i++;
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
