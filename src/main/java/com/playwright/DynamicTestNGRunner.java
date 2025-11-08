package com.playwright;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

/**
 * Programmatically creates a TestNG suite containing all test classes from test/java
 * directory and runs them. This allows running TestNG from a plain Java main method.
 */
public class DynamicTestNGRunner {

    private static List<String> findTestClasses(File directory, String packageName) {
        List<String> testClasses = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String newPackage = packageName.isEmpty() ? file.getName() 
                                                            : packageName + "." + file.getName();
                    testClasses.addAll(findTestClasses(file, newPackage));
                } else if (file.getName().endsWith(".java")) {
                    String className = file.getName().substring(0, file.getName().length() - 5);
                    testClasses.add(packageName + "." + className);
                }
            }
        }
        return testClasses;
    }

    public static void main(String[] args) {
        // Create a suite
        XmlSuite suite = new XmlSuite();
        suite.setName("DynamicSuite");
        suite.setVerbose(1);

        // Create a test
        XmlTest test = new XmlTest(suite);
        test.setName("DynamicTest");

        // Find all test classes in test/java directory
        File testDir = new File("test/java");
        List<String> testClassNames = findTestClasses(testDir, "");
        
        // Add all test classes to the suite
        List<XmlClass> classes = new ArrayList<>();
        for (String className : testClassNames) {
            // Remove leading dot if present
            if (className.startsWith(".")) {
                className = className.substring(1);
            }
            System.out.println("[DynamicTestNGRunner] Adding test class: " + className);
            classes.add(new XmlClass(className));
        }
        test.setXmlClasses(classes);

        // Create TestNG and run
        TestNG testng = new TestNG();
        testng.setXmlSuites(Collections.singletonList(suite));
        System.out.println("[DynamicTestNGRunner] Running TestNG suite: " + suite.getName());
        testng.run();
    }
}
