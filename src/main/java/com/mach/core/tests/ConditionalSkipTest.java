package com.mach.core.tests;

import com.mach.core.exception.MachException;
import com.mach.core.util.annottation.OnlyAndroid;
import com.mach.core.util.annottation.OnlyIOS;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ConditionalSkipTest implements IInvokedMethodListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionalSkipTest.class);
    private boolean skipAll = false;

    @Override
    public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        if (method == null
                || method.isAnnotationPresent(AfterClass.class)
                || method.isAnnotationPresent(AfterSuite.class)
                || method.isAnnotationPresent(AfterTest.class)
                || method.isAnnotationPresent(AfterMethod.class)
                || method.toString().contains(BaseTests.class.getName())
                || method.toString().contains("springframework")) {
            return;
        }

        if (skipAll) {
            throw new SkipException("Skipping method as precondition was not met");
        }

        AppiumDriver<MobileElement> appiumDriver = ((BaseTests) invokedMethod.getTestMethod().getInstance()).getDriver();
        
        if (method.isAnnotationPresent(OnlyAndroid.class) && !(appiumDriver instanceof AndroidDriver)) {
            throw new SkipException("This method should only run in Android");
        }
        
        if (method.isAnnotationPresent(OnlyIOS.class) && !(appiumDriver instanceof IOSDriver)) {
            throw new SkipException("This method should only run in Ios");
        }

        if (appiumDriver == null) {
            LOGGER.error("Error on method: {}", method);
            throw new MachException("Driver is null");
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod invokedMethod, ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();

        if (skipAll || method == null || !invokedMethod.isTestMethod() || result.getThrowable() instanceof SkipException) {
            LOGGER.trace("skip afterInvocation method: {}", method);
            return;
        }

        if (result.getStatus() == ITestResult.FAILURE) {
            Class<?> c = invokedMethod.getTestMethod().getRealClass();
            String testName = invokedMethod.getTestMethod().getMethodName();
            SeverityLevel severityTest = getSeverityTest(c, testName);

            // if a test fails and has a CRITICAL or BLOCKER Severity Annotation, then stop the next tests
            if(severityTest != null && severityTest.ordinal() <= SeverityLevel.CRITICAL.ordinal()){
                skipAll = true;
                LOGGER.trace("skipAll test true");
            }
        }
    }

    /**
     * Return the SeverityLevel if in the class the indicated method has a Severity annotation
     * @param c the class of the method
     * @param testMethodName the name of the test
     * @return SeverityLevel or null
     */
    private SeverityLevel getSeverityTest(Class<?> c, String testMethodName){
        SeverityLevel severityLevel = null;

        Method[] methods = c.getMethods();
        Method method = null;
        for (Method m : methods) {
            if (m.getName().equals(testMethodName)) {
                method = m;
                break;
            }
        }
        if (method == null) {
            return null;
        }

        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation.annotationType().toString().contains("Severity")){
                Severity self = (Severity) annotation;
                severityLevel = SeverityLevel.valueOf(self.value().toString().toUpperCase());
                break;
            }
        }

        return severityLevel;
    }

}
