package com.playwright;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

/**
 * Programmatically creates a TestNG suite containing the LearningAutomation test
 * and runs it. This allows running TestNG from a plain Java main method.
 */
public class DynamicTestNGRunner {

    public static void main(String[] args) {
        // Create a suite
        XmlSuite suite = new XmlSuite();
        suite.setName("DynamicSuite");
        suite.setVerbose(1);

        // Create a test
        XmlTest test = new XmlTest(suite);
        test.setName("DynamicLearningTest");

        // Add the test class (fully-qualified)
        List<XmlClass> classes = new ArrayList<>();
        classes.add(new XmlClass("playwrightTrianing.LearningAutomation"));
        test.setXmlClasses(classes);

        // Create TestNG and run
        TestNG testng = new TestNG();
        testng.setXmlSuites(Collections.singletonList(suite));
        System.out.println("[DynamicTestNGRunner] Running TestNG suite: " + suite.getName());
        testng.run();
    }
}
