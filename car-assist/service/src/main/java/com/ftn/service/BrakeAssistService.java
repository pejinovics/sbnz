package com.ftn.service;

import com.ftn.model.DriveSystem;
import com.ftn.model.SurroundSystem;
import com.ftn.model.events.CurrentSpeedEvent;
import com.ftn.utils.WebSocketRuleNotifier;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

            kieSession.setGlobal("minFrontVehicleDistance", 10.0);
            kieSession.setGlobal("minBreakingSpeed", 30.0);
            kieSession.setGlobal("dangerTTC", 10.0);
            kieSession.setGlobal("maxDangerTTC", 3.0);

            SurroundSystem surroundSystem = new SurroundSystem(0.1, 0.7, true, 9.0, 1.0);
            DriveSystem driveSystem = new DriveSystem(0.0, false, false, false);
            surroundSystem.setLeftLineDistance(0.1);

            kieSession.insert(surroundSystem);
            kieSession.insert(driveSystem);

            startSimulationThreads();

            new Thread(() -> kieSession.fireUntilHalt(), "DroolsFireThread").start();

        } catch (Throwable t) {
            t.printStackTrace();
            running = false;
        }
    }

    private void startSimulationThreads() {
        simulationThreadOwnCar = new Thread(() -> {
            while (running) {
                try {
                    System.out.println("INSERTING OWN SPEED : 70KMH");
                    kieSession.insert(new CurrentSpeedEvent(70.0, new Date()));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "OwnCarThread");

        simulationThreadFrontCar = new Thread(() -> {
            while (running) {
                try {
                    System.out.println("INSERTING FRONT CAR SPEED : 30KMH");
                    kieSession.insert(new CurrentSpeedEvent(30.0, new Date(), true));
                    Thread.sleep(1000);
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
