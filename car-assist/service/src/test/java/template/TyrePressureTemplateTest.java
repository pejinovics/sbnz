package template;

import com.ftn.model.Tyre;
import com.ftn.model.TyreSeason;
import com.ftn.model.TyreSide;
import com.ftn.utils.TemplateLoadingUtility;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.io.InputStream;

public class TyrePressureTemplateTest {

    @Test
    public void TyrePressureRulesTest() {

    InputStream template = TyrePressureTemplateTest.class.getResourceAsStream("/rules/tyrePressure/tyre-pressure-template.drt");
    System.out.println(System.getProperty("user.dir"));
    DataProvider dataProvider = TemplateLoadingUtility.loadTemplateFromCSV("../kjar/src/main/resources/templateTable/tyrePressure.csv");

    DataProviderCompiler converter = new DataProviderCompiler();
    String drl = converter.compile(dataProvider, template);

    System.out.println(drl);

    KieSession kieSession = TemplateLoadingUtility.createKieSessionFromDRL(drl);

    }

    private void doTest(KieSession kieSession) {
        Tyre front_left_tyre = new Tyre(2.2, TyreSeason.WINTER, TyreSide.FRONT_LEFT);
        Tyre front_right_tyre = new Tyre(3.0, TyreSeason.WINTER, TyreSide.FRONT_RIGHT);
        Tyre rear_left_tyre = new Tyre(2.2, TyreSeason.WINTER, TyreSide.REAR_LEFT);
        Tyre rear_right_tyre = new Tyre(2.2, TyreSeason.WINTER, TyreSide.REAR_RIGHT);

        FactHandle handle = kieSession.insert(front_left_tyre);
        kieSession.insert(front_right_tyre);
        kieSession.insert(rear_left_tyre);
        kieSession.insert(rear_right_tyre);

        System.out.println("-------------------------------");
        System.out.println("Tyre pressure test");
        System.out.println("-------------------------------");

        kieSession.fireAllRules();
        front_right_tyre.setPressure(1.8);
        kieSession.fireAllRules();

        front_right_tyre.setPressure(3.4);
        kieSession.fireAllRules();

    }
}
