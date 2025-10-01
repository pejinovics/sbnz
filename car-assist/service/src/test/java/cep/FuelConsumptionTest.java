package cep;

import com.ftn.model.events.CurrentSpeedEvent;
import com.ftn.model.events.FuelFlowEvent;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;


public class FuelConsumptionTest {

    @Test
    public void FuelConsumptionTest1() {

        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();

        KieSessionConfiguration configuration = KieServices.Factory.get().newKieSessionConfiguration();
        configuration.setOption(ClockTypeOption.get("realtime"));

        KieSession kieSession = kieContainer.newKieSession("fuelConsumptionKSession", configuration);

        doTest(kieSession);
    }

    private void doTest(KieSession kieSession) {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);


        long seed = 10;
        new Thread(() -> {
            while(true) {
                double fuelMiligrams = 2 * Math.sin(System.currentTimeMillis());
                kieSession.insert(new FuelFlowEvent(abs(fuelMiligrams), new Date()));
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while(true) {
                double currentSpeed = 60 * Math.sin(System.currentTimeMillis() - seed);
                kieSession.insert(new CurrentSpeedEvent(abs(currentSpeed), new Date()));
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        kieSession.fireUntilHalt();

    }
}



//        executorService.scheduleAtFixedRate(() -> {
//            new FuelFlowEvent(0.34, LocalDateTime.now());
//        }, 0, 1, TimeUnit.SECONDS);
//
//        executorService.scheduleAtFixedRate(() -> {
//            new CurrentSpeedEvent(60, LocalDateTime.now());
//        }, 0, 1, TimeUnit.SECONDS);

//        new Thread(kieSession::fireUntilHalt).start();
