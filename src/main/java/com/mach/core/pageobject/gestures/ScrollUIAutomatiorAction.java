package com.mach.core.pageobject.gestures;

import com.google.common.collect.ImmutableList;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.PerformsActions;
import io.appium.java_client.android.AndroidDriver;

import java.util.List;

import static java.lang.String.format;

public class ScrollUIAutomatiorAction implements PerformsActions<ScrollUIAutomatiorAction> {

    private AppiumDriver<? extends MobileElement> appiumDriver;

    private ImmutableList.Builder<String> parameterBuilder;

    public ScrollUIAutomatiorAction(AppiumDriver<? extends MobileElement> appiumDriver) {
        this.appiumDriver = appiumDriver;
        this.parameterBuilder = ImmutableList.builder();
    }

    /**
     *
     * @param value
     * @return this ScrollAction, for chaining
     */
    public ScrollUIAutomatiorAction scrollByText(String value) {
        parameterBuilder.add(format("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"%s\").instance(0))", value));
        return this;
    }

    public ScrollUIAutomatiorAction scrollByID(String value) {
        parameterBuilder.add(format("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().resourceId(\"%s\").instance(0))", value));
        return this;
    }

    /**
     *
     * @param element
     * @param text
     * @return this ScrollAction, for chaining
     */
    public ScrollUIAutomatiorAction scrollToElementByText(String element, String text) {
        parameterBuilder.add(format("new UiScrollable(new UiSelector().resourceId('%s')).scrollIntoView(new UiSelector().textContains('%s'))", element, text));
        return this;
    }

    /**
     * @return A map of parameters for this scroll action to pass as part of script
     */
    protected List<String> getParameters() {
        return parameterBuilder.build();
    }

    /**
     * Perform this chain of actions on the executeScript.
     *
     * @return this ScrollAction, for possible segmented-scroll
     */
    public ScrollUIAutomatiorAction perform() {
        getParameters().forEach(
                value -> ((AndroidDriver<? extends MobileElement>) appiumDriver).findElementByAndroidUIAutomator(value));
        return this;
    }
}
