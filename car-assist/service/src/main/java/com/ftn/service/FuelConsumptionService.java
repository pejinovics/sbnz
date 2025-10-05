package com.ftn.service;

import com.ftn.model.events.CurrentSpeedEvent;
import com.ftn.model.events.FuelFlowEvent;
import com.ftn.utils.WebSocketRuleNotifier;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static java.lang.Math.abs;

@Service
public class FuelConsumptionService {

    private final WebSocketRuleNotifier notifier;
    private volatile boolean running = false;

    @Autowired
    public FuelConsumptionService(WebSocketRuleNotifier notifier) {
        this.notifier = notifier;
    }

    public void simulateFuelConsumption() {
        try {
            if (running) {
                System.out.println("Simulation already running...");
                return;
            }

            KieServices kieServices = KieServices.Factory.get();
            KieContainer kieContainer = kieServices.getKieClasspathContainer();

            KieSessionConfiguration configuration = KieServices.Factory.get().newKieSessionConfiguration();
            configuration.setOption(ClockTypeOption.get("realtime"));

            KieSession kieSession = kieContainer.newKieSession("fuelConsumptionKSession", configuration);

            notifier.attach(kieSession);
            running = true;

            new Thread(() -> {
                while (running) {
                    double fuelMiligrams = 2 * Math.sin(System.currentTimeMillis());
                    kieSession.insert(new FuelFlowEvent(abs(fuelMiligrams), new Date()));
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "FuelFlowThread").start();

            new Thread(() -> {
                long seed = 10;
                while (running) {
                    double currentSpeed = 60 * Math.sin(System.currentTimeMillis() - seed);
                    kieSession.insert(new CurrentSpeedEvent(abs(currentSpeed), new Date()));
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "SpeedThread").start();

            new Thread(() -> kieSession.fireUntilHalt(), "DroolsFireThread").start();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void stopSimulation() {
        running = false;
        System.out.println("Fuel consumption simulation stopped.");
    }
}
