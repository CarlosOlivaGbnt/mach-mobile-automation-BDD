package com.mach.core.config.driver;

import com.mach.core.api.SlackAPI;
import com.mach.core.config.CommandRunner;
import com.mach.core.config.MachExecutionEnvironment;
import com.mach.core.config.MachProfileResolver;
import com.mach.core.config.MachProperties;
import com.mach.core.config.amazon.ResultReporter;
import com.mach.core.model.SuiteResult;
import com.mach.core.tests.MachTestNGListener;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.events.EventFiringWebDriverFactory;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import static io.appium.java_client.remote.MobileCapabilityType.APP;
import static io.appium.java_client.remote.MobileCapabilityType.AUTOMATION_NAME;
import static io.appium.java_client.remote.MobileCapabilityType.CLEAR_SYSTEM_FILES;
import static io.appium.java_client.remote.MobileCapabilityType.DEVICE_NAME;
import static io.appium.java_client.remote.MobileCapabilityType.FULL_RESET;
import static io.appium.java_client.remote.MobileCapabilityType.NEW_COMMAND_TIMEOUT;
import static io.appium.java_client.remote.MobileCapabilityType.NO_RESET;
import static org.openqa.selenium.remote.CapabilityType.PLATFORM_NAME;

public class AppiumDriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AppiumDriverFactory.class);
    private static final String USE_NEW_WDA = "useNewWDA";
    private static AppiumDriverFactory instance;

    private MachProperties properties;
    private MachExecutionEnvironment machExecutionEnvironment;
    private AppiumDriver<MobileElement> appiumDriver;
    private final String deviceFarmLink;

    private AppiumDriverFactory() {
        properties = MachProperties.getInstance();
        machExecutionEnvironment = MachExecutionEnvironment.valueOf(properties.getString("EXECUTION_ENVIRONMENT", "LOCAL").toUpperCase());
        deviceFarmLink = ResultReporter.getDeviceFarmLink();
        if (MachExecutionEnvironment.AWS.equals(machExecutionEnvironment)) {
            addSOSMsgSlackFile(deviceFarmLink);
        }
        if (MobilePlatform.ANDROID.equals(MachProfileResolver.getActiveProfile())) {
            appiumDriver = getAndroidDriver();
        } else {
            appiumDriver = getIOSDriver();
        }
    }

    public static synchronized AppiumDriverFactory getInstance() {
        if (instance == null) {
            instance = new AppiumDriverFactory();
        }
        return instance;
    }

    public AppiumDriver<MobileElement> getDriver() {
        return appiumDriver;
    }

    private AppiumDriver<MobileElement> getAndroidDriver() {
        try {
            URL appiumServer = new URL(properties.getString("appium.server"));
            AndroidDriver<MobileElement> driver = new AndroidDriver<>(appiumServer, getAndroidCapabilities());
            appiumDriver = EventFiringWebDriverFactory.getEventFiringWebDriver(driver, new MachTestNGListener());
            return appiumDriver;
        } catch (Throwable e) {
            LOG.error("check the capabilities, e:", e);
            CommandRunner.executeCommand("adb devices");
            SuiteResult suiteResult = new SuiteResult.SuiteResultBuilder().ofContext(Reporter.getCurrentTestResult().getTestContext()).build();
            SlackAPI.notifyOnSlack(suiteResult, deviceFarmLink);
            return null;
        }
    }

    private DesiredCapabilities getAndroidCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(PLATFORM_NAME, properties.getString("android.platform"));
        capabilities.setCapability(AUTOMATION_NAME, properties.getString("android.automation.name"));
        capabilities.setCapability(NEW_COMMAND_TIMEOUT, properties.getInteger("appium.new.command.timeout"));

        if (MachExecutionEnvironment.LOCAL.equals(machExecutionEnvironment) || MachExecutionEnvironment.GRID.equals(machExecutionEnvironment)) {
            capabilities.setCapability(APP, properties.getString("android.app"));
            capabilities.setCapability(NO_RESET, properties.getBoolean("appium.app.noReset"));
            capabilities.setCapability(FULL_RESET, properties.getBoolean("appium.app.fullReset"));
            capabilities.setCapability(CLEAR_SYSTEM_FILES, properties.getString("appium.clearSystemFiles"));
            capabilities.setCapability(DEVICE_NAME, properties.getString("android.device.name"));
        }

        if (MachExecutionEnvironment.AWS.equals(machExecutionEnvironment)) {
            capabilities.setCapability("noSign", true);
        }

        LOG.info("capabilities from java: {}", capabilities);
        return capabilities;
    }

    private AppiumDriver<MobileElement> getIOSDriver() {
        try {
            URL appiumServer = new URL(properties.getString("appium.server"));
            IOSDriver<MobileElement> driver = new IOSDriver<>(appiumServer, getIOSCapabilities());
            appiumDriver = EventFiringWebDriverFactory.getEventFiringWebDriver(driver, new MachTestNGListener());
            return appiumDriver;
        } catch (Throwable e) {
            LOG.error("check the capabilities, e:", e);
            CommandRunner.executeCommand("idevice_id --list");
            CommandRunner.executeCommand("env");
            SuiteResult suiteResult = new SuiteResult.SuiteResultBuilder().ofContext(Reporter.getCurrentTestResult().getTestContext()).build();
            SlackAPI.notifyOnSlack(suiteResult, deviceFarmLink);
            return null;
        }
    }

    private DesiredCapabilities getIOSCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PLATFORM_NAME, properties.getString("ios.platform"));
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, properties.getString("ios.automationName"));
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, properties.getInteger("appium.new.command.timeout"));
        capabilities.setCapability("appPushTimeout", Integer.valueOf(properties.getString("ios.device.appPushTimeout")));

        if (MachExecutionEnvironment.LOCAL.equals(machExecutionEnvironment) || MachExecutionEnvironment.GRID.equals(machExecutionEnvironment)) {
            capabilities.setCapability(MobileCapabilityType.APP, properties.getString("ios.app"));
            capabilities.setCapability(MobileCapabilityType.NO_RESET, properties.getBoolean("appium.app.noReset"));
            capabilities.setCapability(MobileCapabilityType.FULL_RESET, properties.getBoolean("appium.app.fullReset"));
            capabilities.setCapability(MobileCapabilityType.CLEAR_SYSTEM_FILES, properties.getString("appium.clearSystemFiles"));
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, properties.getString("ios.device.name"));
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, properties.getString("ios.platform.version"));
            capabilities.setCapability(MobileCapabilityType.UDID, properties.getString("ios.device.udid"));
            capabilities.setCapability(USE_NEW_WDA, properties.getString("ios.device.useNewWDA", "false"));
        }

        if (MachExecutionEnvironment.AWS.equals(machExecutionEnvironment)) {
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, properties.getString("DEVICEFARM_DEVICE_NAME"));
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, properties.getString("DEVICEFARM_DEVICE_OS_VERSION"));
            capabilities.setCapability(USE_NEW_WDA,true);
            capabilities.setCapability("updatedWDABundleId","com.facebook.WebDriverAgentRunner");
        }

        if (MachExecutionEnvironment.LOCAL.equals(machExecutionEnvironment)) {
            capabilities.setCapability("waitForQuiescence", properties.getString("ios.device.waitForQuiescence", "true"));
        }

        LOG.info("capabilities from java: {}", capabilities);
        return capabilities;
    }

    /**
     * Genera un archivo con la url de la ejecucion actual en aws df (runs)
     * @param urlAwsDF actual run
     */
    private void addSOSMsgSlackFile(String urlAwsDF) {
        LOG.info("addFile: {} urlAwsDF: {}", SlackAPI.SLACK_SOS_FILE, urlAwsDF);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(SlackAPI.SLACK_SOS_FILE))){
            writer.write(urlAwsDF); // do something with the file we've opened
        }
        catch(IOException e){
            LOG.warn("Error addSOSMsgSlackFile");
        }

    }

}
