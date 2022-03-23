    package com.mach.core.pageobject;

import com.mach.core.config.driver.AppiumDriverFactory;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

    public abstract class Screen {

        public static final String MACH_BUNDLE_ID = "cl.bci.sismo.mach.automation";
        public static final String ID = MACH_BUNDLE_ID + ":id/";

        protected Screen() {
            PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
        }

        protected AppiumDriver<MobileElement> getDriver() {
            return AppiumDriverFactory.getInstance().getDriver();
        }
    }