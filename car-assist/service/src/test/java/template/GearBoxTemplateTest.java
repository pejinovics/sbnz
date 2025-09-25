package template;

import com.ftn.model.GearBox;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.drools.template.parser.RuleTemplate;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

public class GearBoxTemplateTest {

    @Test
    public void GearBoxRulesTest1(){

        InputStream template = GearBoxTemplateTest.class.getResourceAsStream("/rules/gearBox/gear-box-template.drt");
        InputStream data = GearBoxTemplateTest.class.getResourceAsStream("/templateTable/gearBox.xls");

        ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();

        String drl = converter.compile(data, template, 3, 5);

        System.out.println(drl);

        KieSession kieSession = this.createKieSessionFromDRL(drl);

        this.doTest(kieSession);


    }

    @Test
    public void GearBoxRulesTestArrays() {
        InputStream template = GearBoxTemplateTest.class.getResourceAsStream("/rules/gearBox/gear-box-template.drt");

        DataProvider dataProvider = new ArrayDataProvider(new String[][]{
                new String[]{"1100", "2500", "1"},
                new String[]{"1100", "2500", "2"},
                new String[]{"1100", "2500", "3"},
                new String[]{"1100", "2500", "4"},
                new String[]{"1100", "2500", "5"},
                new String[]{"1100", "2500", "6"}

        });

        DataProviderCompiler converter = new DataProviderCompiler();
        String drl = converter.compile(dataProvider, template);

//        System.out.println(drl);

        KieSession kieSession = createKieSessionFromDRL(drl);

        doTest(kieSession);
    }

    private KieSession createKieSessionFromDRL(String drl) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);

        Results results = kieHelper.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.println("Error : " + message.getText());
            }
//            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }
        return kieHelper.build().newKieSession();
    }

    private void doTest(KieSession kieSession) {
        GearBox gearBox = new GearBox(1, 1000, GearBox.State.OK);

        FactHandle handle = kieSession.insert(gearBox);
        kieSession.fireAllRules();

        gearBox.setCurrentRPM(2500);
        kieSession.update(handle, gearBox);
        kieSession.fireAllRules();

        System.out.println("----------------------");

        gearBox.setCurrentRPM(3500);
        kieSession.update(handle, gearBox);
        kieSession.fireAllRules();

        System.out.println("----------------------");

        gearBox.setCurrentRPM(2000);
        kieSession.update(handle, gearBox);
        kieSession.fireAllRules();

        System.out.println("----------------------");

        gearBox.setCurrentRPM(3500);
        kieSession.update(handle, gearBox);
        kieSession.fireAllRules();

        System.out.println("----------------------");

        gearBox.setCurrentRPM(2000);
        kieSession.update(handle, gearBox);
        kieSession.fireAllRules();

        System.out.println("----------------------");

        gearBox.setCurrentRPM(4000);
        kieSession.update(handle, gearBox);
        kieSession.fireAllRules();


        return;
    }

}
