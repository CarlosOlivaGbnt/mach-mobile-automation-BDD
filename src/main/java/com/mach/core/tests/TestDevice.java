package com.mach.core.tests;

import com.mach.core.config.CommandRunner;
import com.mach.core.pageobject.gestures.Action;
import com.mach.core.util.ConstantsTimeOut;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDevice {

    private static final Logger LOG = LoggerFactory.getLogger(TestDevice.class);

    private TestDevice() {

    }

    @Step("Turn Wifi OFF")
    public static void turnOffWifi(AppiumDriver<MobileElement> driver) {
        if(driver == null){
            LOG.error("Error at turnOffWifi, driver is null");
            return;
        }
        if (driver instanceof AndroidDriver) {
            boolean connected = ((AndroidDriver<MobileElement>) driver).getConnection().isWiFiEnabled();
            if (connected) {
                ((AndroidDriver<MobileElement>) driver).toggleWifi();
            }
        } else {
            turnOffNetworkIOS(driver);
        }
    }

    @Step("Turn Wifi ON")
    public static void turnOnWifi(AppiumDriver<MobileElement> driver) {
        if(driver == null){
            LOG.error("Error at turnOnWifi, driver is null");
            return;
        }
        if (driver instanceof AndroidDriver) {
            boolean connected = ((AndroidDriver<MobileElement>) driver).getConnection().isWiFiEnabled();
            if (!connected) {
                ((AndroidDriver<MobileElement>) driver).toggleWifi();
            }
        } else {
            turnOnNetworkIOS(driver);
        }
        // This wait is for aws recover internet
        waitOn(driver, ConstantsTimeOut.TIMEOUT_STATIC_ELEMENTS);
    }

    private static void turnOffNetworkIOS(AppiumDriver<MobileElement> driver) {
        try {
            if (displayControlCenterIOS(driver) && isWifiOnIOS(driver)) {
                getWifiButtonIOS(driver).click();
                confirmChange(driver);
            }
            hideControlCenterIOS(driver);
        } catch (WebDriverException e) {
            LOG.warn(String.format("Exception when interacting with native menu: %s", e));
        }
    }

    private static void confirmChange(AppiumDriver<MobileElement> driver) {
        try {
            driver.findElement(By.id("OK")).click();
        } catch (NoSuchElementException e){
            LOG.info("No need to confirm WiFi change");
        }
    }

    private static void turnOnNetworkIOS(AppiumDriver<MobileElement> driver) {
        try {
            if (displayControlCenterIOS(driver) && !isWifiOnIOS(driver)) {
                getWifiButtonIOS(driver).click();
                confirmChange(driver);
            }
            hideControlCenterIOS(driver);
        } catch (WebDriverException e) {
            LOG.warn(String.format("Exception when interacting with native menu: %s", e));
        }
    }

    /**
     * Displays the native network toggles menu
     * @return true if correctly displayed, false if not opened
     */
    private static boolean displayControlCenterIOS(AppiumDriver<MobileElement> driver) {
        if (((IOSDriver<MobileElement>) driver).isKeyboardShown()) {
            LOG.info("hiding keyboard before changing WiFi");
            driver.hideKeyboard();
        }
        if (null == isWifiOnIOS(driver)) {
            if (deviceHasPhysicalHomeButton()) {
                swipeFromBottomToTop(driver);
            } else {
                swipeFromTopToBottom(driver);
            }
            waitOn(driver,1);
            return null != isWifiOnIOS(driver);
        } else {
            return true;
        }
    }

    /**
     * Hides the native network toggles menu
     * @return true if no longer displayed
     */
    private static boolean hideControlCenterIOS(AppiumDriver<MobileElement> driver) {
        if (null != isWifiOnIOS(driver)) {
            if (deviceHasPhysicalHomeButton()) {
                swipeFromTopToBottom(driver);
            } else {
                swipeFromBottomToTop(driver);
            }
            waitOn(driver,1);
            return null == isWifiOnIOS(driver);
        } else {
            return true;
        }
    }

    private static boolean deviceHasPhysicalHomeButton() {
        final String DEVICES_WITH_PHYSICAL_HOME_BUTTON = "iPhone7,1.iPhone7,2.iPhone8,1.iPhone8,2.iPhone8,4.iPhone9,1.iPhone9,2.iPhone9,3.iPhone9,4.iPhone10,1.iPhone10,2.iPhone10,4.iPhone10,5.iPhone12,8";
        String deviceName = CommandRunner.executeCommand("ideviceinfo -k ProductType").get(0);
        return DEVICES_WITH_PHYSICAL_HOME_BUTTON.contains(deviceName);
    }

    /**
     * Returns the state as seen by looking at wifi-button value, null if not found.
     * @return the state of the wifi-button, null otherwise
     */
    private static Boolean isWifiOnIOS(AppiumDriver<MobileElement> driver) {
        Boolean result;
        try {
            result = getWifiButtonIOS(driver).getAttribute("value").equals("1");
        } catch (TimeoutException | NoSuchElementException e) {
            result = null;
        }
        LOG.info("Checked if isWiFiOnIOS: {}", result);
        return result;
    }

    private static MobileElement getWifiButtonIOS(AppiumDriver<MobileElement> driver) {
        return (MobileElement) new WebDriverWait(driver, ConstantsTimeOut.FAST_CHECK).until(ExpectedConditions.elementToBeClickable(By.id("wifi-button")));
    }

    private static void swipeFromBottomToTop(AppiumDriver<MobileElement> driver) {
        final Dimension size = driver.manage().window().getSize();
        int xPosition = (int) (size.width * 0.15);
        swipe(driver, xPosition, size.height - 1, xPosition, (int) (size.height * 0.5));
    }

    private static void swipeFromTopToBottom(AppiumDriver<MobileElement> driver) {
        final Dimension size = driver.manage().window().getSize();
        int xPosition = (int) (size.width * 0.95);
        swipe(driver, xPosition, 1, xPosition, (int) (size.height * 0.5));
    }

    private static void swipe(AppiumDriver<MobileElement> driver, int startx, int starty, int endx, int endy) {
        LOG.info("swipe from ({},{}) to ({},{}).", startx, starty, endx, endy);
        try {
            new Action(driver)
                    .swipeByTouchAction(startx, starty, endx, endy, 1000)
                    .perform();
        } catch (InvalidArgumentException e){
            LOG.warn("Swipe did not complete successfully e: ", e);
        }
    }

    @Step("Wait for {1} seconds")
    private static void waitOn(AppiumDriver<MobileElement> driver, int seconds) {
        try {
            new WebDriverWait(driver, seconds)
                    .ignoring(NoSuchElementException.class)
                    .ignoring(WebDriverException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nonExistingElement")));
        } catch (TimeoutException e) {
            LOG.debug("Finished waiting {} seconds.", seconds);
        }
    }

}
