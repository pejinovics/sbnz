package com.ftn.service;

import com.ftn.model.events.CurrentSpeedEvent;
import com.ftn.model.events.FuelFlowEvent;
import com.ftn.utils.TemplateLoadingUtility;
import com.ftn.utils.WebSocketRuleNotifier;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
                List<Double> fuelMiligrams = TemplateLoadingUtility.loadDataFromCSV("testCases/values_0_5_to_2_0.csv");
                int size = fuelMiligrams.size();
                int i = size;
                while (running) {
                    try {
                        kieSession.insert(new FuelFlowEvent(abs(fuelMiligrams.get(i % size)), new Date()));
                        Thread.sleep(2000);
                        i++;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "FuelFlowThread").start();

            new Thread(() -> {
                List<Double> ownCarSpeed = TemplateLoadingUtility.loadDataFromCSV("testCases/speedValues/values_30_to_40.csv");
                int size = ownCarSpeed.size();
                int i = size;
                while (running) {
                    try {
                        kieSession.insert(new CurrentSpeedEvent(ownCarSpeed.get(i % size), new Date(), true));
                        Thread.sleep(2000);
                         i++;
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
