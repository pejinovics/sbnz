package com.ftn.service;

import com.ftn.model.DriveSystem;
import com.ftn.model.SurroundSystem;
import com.ftn.model.events.CurrentSpeedEvent;
import com.ftn.utils.LoadingUtility;
import com.ftn.utils.WebSocketRuleNotifier;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class BrakeAssistService {

    private final WebSocketRuleNotifier notifier;
    private volatile boolean running = false;
    private Thread simulationThreadOwnCar;
    private Thread simulationThreadFrontCar;
    private KieSession kieSession;

    @Autowired
    public BrakeAssistService(WebSocketRuleNotifier notifier) {
        this.notifier = notifier;
    }

    public void simulateBrakeAssist() {
        if (running) {
            System.out.println("Simulation already running...");
            return;
        }

        running = true;
        try {
            KieServices kieServices = KieServices.Factory.get();
            KieContainer kieContainer = kieServices.getKieClasspathContainer();

            KieSessionConfiguration configuration = KieServices.Factory.get().newKieSessionConfiguration();
            configuration.setOption(ClockTypeOption.get("realtime"));

            kieSession = kieContainer.newKieSession("brakeAssistKSession", configuration);
            notifier.attach(kieSession);

            Properties properties = LoadingUtility.loadSystemProperties();

            kieSession.setGlobal("minFrontVehicleDistance", Double.valueOf(properties.getProperty("braking.minFrontVehicleDistance")));
            kieSession.setGlobal("minBrakingSpeed", Double.valueOf(properties.getProperty("braking.minBrakingSpeed")));
            kieSession.setGlobal("dangerTTC", Double.valueOf(properties.getProperty("braking.dangerTTC")));
            kieSession.setGlobal("maxDangerTTC", Double.valueOf(properties.getProperty("braking.maxDangerTTC")));

            SurroundSystem surroundSystem = new SurroundSystem(Double.parseDouble(properties.getProperty("surround_system.left_line_distance")),
                    Double.parseDouble(properties.getProperty("surround_system.right_line_distance")),
                    Boolean.parseBoolean(properties.getProperty("surround_system.line_visible")),
                    Double.parseDouble(properties.getProperty("surround_system.front_vehicle_distance")),
                    1.0);
            DriveSystem driveSystem = new DriveSystem(0.0,
                    Boolean.parseBoolean(properties.getProperty("drive_system.brake_pressed")),
                    Boolean.parseBoolean(properties.getProperty("surround_system.left_turn_signal")),
                    Boolean.parseBoolean(properties.getProperty("surround_system.right_turn_signal")));

            FactHandle surroundSystemHandle =  kieSession.insert(surroundSystem);
            FactHandle driveSystemHandle = kieSession.insert(driveSystem);

            startSimulationThreads();

            new Thread(() -> kieSession.fireUntilHalt(), "DroolsFireThread").start();

            List<Double> frontCarDistances = LoadingUtility.loadDataFromCSV("testCases/frontCarDistance1.csv");
            int size = frontCarDistances.size();
            for (int i = size; ; i++) {
                surroundSystem.setFrontVehicleDistance(frontCarDistances.get(i % size));
                kieSession.update(surroundSystemHandle, surroundSystem);
                Thread.sleep(20000);
            }


        } catch (Throwable t) {
            t.printStackTrace();
            running = false;
        }
    }

    private void startSimulationThreads() {
        simulationThreadOwnCar = new Thread(() -> {
            List<Double> ownCarSpeed = LoadingUtility.loadDataFromCSV("testCases/speedValues/values_65_to_70.csv");
            int size = ownCarSpeed.size();
            int i = size;
            while (running) {
                try {
                    System.out.println("INSERTING OWN SPEED");
                    kieSession.insert(new CurrentSpeedEvent(ownCarSpeed.get(i % size), new Date()));
                    Thread.sleep(2000);
                    i++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "OwnCarThread");

        simulationThreadFrontCar = new Thread(() -> {
            List<Double> frontCarSpeed = LoadingUtility.loadDataFromCSV("testCases/speedValues/values_30_to_40.csv");
            int size = frontCarSpeed.size();
            int i = size;
            while (running) {
                try {
                    System.out.println("INSERTING FRONT CAR SPEED : 30KMH");
                    kieSession.insert(new CurrentSpeedEvent(frontCarSpeed.get(i % size), new Date(), true));
                    Thread.sleep(2000);
                    i++;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "FrontCarThread");

        simulationThreadOwnCar.start();
        simulationThreadFrontCar.start();
    }

    public void stopSimulation() {
        running = false;
        if (simulationThreadOwnCar != null) simulationThreadOwnCar.interrupt();
        if (simulationThreadFrontCar != null) simulationThreadFrontCar.interrupt();
        if (kieSession != null) {
            kieSession.halt();
            kieSession.dispose();
        }
        System.out.println("Brake Assist simulation stopped.");
    }
}
