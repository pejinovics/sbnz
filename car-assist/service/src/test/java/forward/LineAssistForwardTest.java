package forward;

import com.ftn.model.DriveSystem;
import com.ftn.model.SurroundSystem;
import com.ftn.model.events.CurrentSpeedEvent;
import com.ftn.utils.TemplateLoadingUtility;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.io.InputStream;
import java.util.Date;

import static java.lang.Math.abs;

public class LineAssistForwardTest {

    @Test
    public void LineAssistTest1() {
//        KieServices kieServices = KieServices.Factory.get();
//        KieContainer kieContainer = kieServices.getKieClasspathContainer();
//
//        KieSessionConfiguration configuration = KieServices.Factory.get().newKieSessionConfiguration();
//        configuration.setOption(ClockTypeOption.get("realtime"));
//
//        KieSession kieSession = kieContainer.newKieSession("lineAssistKSession", configuration);

        InputStream template = LineAssistForwardTest.class.getResourceAsStream("/rules/lineAssist/line-assist.drt");
        DataProvider dataProvider = TemplateLoadingUtility.loadTemplateFromCSV("../kjar/src/main/resources/templateTable/lineAssist.csv");

        DataProviderCompiler converter = new DataProviderCompiler();
        String drl = converter.compile(dataProvider, template);

        KieSession kieSession = TemplateLoadingUtility.createKieSessionFromDRLForCEP(drl);


        kieSession.setGlobal("minDistance", 0.5);
        kieSession.setGlobal("minLineWarnSpeed", 30.0);
        kieSession.setGlobal("minLineSteerSpeed", 60.0);

        doTest(kieSession);
    }

    private void doTest(KieSession kieSession) {

        new Thread(() -> {
            while(true) {
               double currentSpeed = 80 * Math.sin(System.currentTimeMillis());
               System.out.println("SPEED : " + currentSpeed);
               kieSession.insert(new CurrentSpeedEvent(abs(currentSpeed), new Date()));
               try {
                   Thread.sleep(1000);
               } catch (Exception e) {
                   e.printStackTrace();
               }
            }
        }).start();

        SurroundSystem surroundSystem = new SurroundSystem(0.1, 0.7, true, 10.0, 1.0);
        DriveSystem driveSystem = new DriveSystem(0.0, false, false, false);
        surroundSystem.setLeftLineDistance(0.1);

        kieSession.insert(surroundSystem);
        kieSession.insert(driveSystem);

        kieSession.fireUntilHalt();

    }

}
