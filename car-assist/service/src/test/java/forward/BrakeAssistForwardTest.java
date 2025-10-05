package forward;

import com.ftn.model.DriveSystem;
import com.ftn.model.SurroundSystem;
import com.ftn.model.events.CurrentSpeedEvent;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.util.Date;

import static java.lang.Math.abs;

public class BrakeAssistForwardTest {
    @Test
    public void BrakeAssistForwardTest1() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();

        KieSessionConfiguration configuration = KieServices.Factory.get().newKieSessionConfiguration();
        configuration.setOption(ClockTypeOption.get("realtime"));

        KieSession kieSession = kieContainer.newKieSession("brakeAssistKSession", configuration);

        kieSession.setGlobal("minFrontVehicleDistance", 10.0);
        kieSession.setGlobal("minBreakingSpeed", 30.0);
        kieSession.setGlobal("dangerTTC", 10.0);
        kieSession.setGlobal("maxDangerTTC", 3.0);

        doTest(kieSession);
    }

    private void doTest(KieSession kieSession) {
        new Thread(() -> {
            while (true) {
                double currentSpeed = 60 * Math.sin(System.currentTimeMillis());
                System.out.println("INSERTING OWN SPEED : 70KMH");
                kieSession.insert(new CurrentSpeedEvent(70.0, new Date()));
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while(true) {
                double currentSpeed = 60 * Math.sin(System.currentTimeMillis());
                System.out.println("INSERTING FRONT CAR SPEED : 30KMH");
                kieSession.insert(new CurrentSpeedEvent(30.0, new Date(), true));
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        SurroundSystem surroundSystem = new SurroundSystem(0.1, 0.7, true, 9.0, 1.0);
        DriveSystem driveSystem = new DriveSystem(0.0, false, false, false);
        surroundSystem.setLeftLineDistance(0.1);

        kieSession.insert(surroundSystem);
        kieSession.insert(driveSystem);

        kieSession.fireUntilHalt();

    }
}
