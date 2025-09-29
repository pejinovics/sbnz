package com.ftn.utils;

import org.drools.template.DataProvider;
import org.drools.template.objects.ArrayDataProvider;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TemplateLoadingUtility {

    public static final String DELIMITER = ";";

    public static KieSession createKieSessionFromDRL(String drl) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);

        Results results = kieHelper.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
            List<Message> messages = results.getMessages(Message.Level.ERROR, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.println("Error : " + message.getText());
            }
        }
        return kieHelper.build().newKieSession();
    }

    public static DataProvider loadTemplateFromCSV(String path) {
        List<String[]> rules = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(path))){
            while (scanner.hasNextLine()) {
                String[] new_rule = scanner.nextLine().split(TemplateLoadingUtility.DELIMITER);
                rules.add(new_rule);
            }
            String[][] rules_array = new String[rules.size() - 1][rules.get(1).length];
            for (int i = 1; i < rules.size(); i++) {
                rules_array[i - 1] = rules.get(i);
            }
            return new ArrayDataProvider(rules_array);
        }
        catch (Exception e) {
            if (e.getClass().equals(FileNotFoundException.class)) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
