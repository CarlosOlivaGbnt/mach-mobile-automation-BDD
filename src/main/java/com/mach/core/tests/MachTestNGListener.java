package com.mach.core.tests;

import io.appium.java_client.events.api.general.ElementEventListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.testng.IConfigurationListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestNGListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

@Component
public class MachTestNGListener extends TestListenerAdapter
        implements ISuiteListener, ITestNGListener, ITestListener, IConfigurationListener, ElementEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MachTestNGListener.class);

    @Override
    public void onTestStart(ITestResult tr) {
        LOGGER.info("Test {} STARTED", tr.getName());
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        LOGGER.info("Test {} PASSED", tr.getName());
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        LOGGER.error("Test {} FAILED", tr.getName());
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        LOGGER.warn("Test {} SKIPPED", tr.getName());
    }

    @Override
    public void onStart(ISuite iSuite) {
        LOGGER.info("Suite {} STARTED", iSuite.getName());
    }

    @Override
    public void onFinish(ISuite iSuite) {
        LOGGER.info("Suite {} PASSED", iSuite.getName());
    }

    @Override
    public void onConfigurationFailure(ITestResult itr) {
        LOGGER.error("Configuration {} FAILED", itr.getName());
    }

    @Override
    public void onConfigurationSuccess(ITestResult itr) {
        LOGGER.info("Configuration {} PASSED", itr.getName());
    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {
        LOGGER.warn("Configuration {} SKIPPED", itr.getName());
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        String[] data = getElementData(element);
        LOGGER.info("Click on [{} | {}]", data[0], data[1]);
    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {
        // Do nothing.
    }

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver) {
        // Do nothing.
    }

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        // Do nothing.
    }

    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver) {
        // Do nothing.
    }

    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        // Do nothing.
    }

    @Override
    public void beforeGetText(WebElement element, WebDriver driver) {
        // Do nothing.
    }

    @Override
    public void afterGetText(WebElement element, WebDriver driver, String text) {
        // Do nothing.
    }

    private String[] getElementData(WebElement element) {
        String[] data = new String[2];
        data[0] = element.getTagName() == null ? element.getAttribute("className") : element.getTagName();
        String txtElem = element.getText().trim();
        String txtLocator = element.toString().contains(" -> ") ? element.toString().substring(element.toString().indexOf("->")) : element.toString();
        if (txtElem == null) {
            data[1] = txtLocator;
        } else if (txtElem.length() == 0) {
            data[1] = txtLocator;
        } else {
            data[1] = txtElem;
        }
        return data;
    }
}