package com.mach.core.pageobject.gestures;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.PerformsActions;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebElement;

public class ScrollAction implements PerformsActions<ScrollAction> {

    private static final String ELEMENT = "element";
    private static final String DIRECTION = "direction";
    private static final String VISIBILITY = "toVisible";
    private static final String NAME = "name";

    private AppiumDriver<? extends MobileElement> appiumDriver;

    private ImmutableMap.Builder<String, String> parameterBuilder;

    public ScrollAction(AppiumDriver<? extends MobileElement> appiumDriver) {
        this.appiumDriver = appiumDriver;
        this.parameterBuilder = ImmutableMap.builder();
    }

    /**
     * Scrolls over a component to a certain visible element
     *
     * @param by The id of the element that you want to scroll
     * @param toVisible parameter
     * @return this ScrollAction, for chaining
     */
    public ScrollAction scrollToVisible(By by, String toVisible) {
        String parentID = ((RemoteWebElement) appiumDriver.findElement(by)).getId();
        parameterBuilder.put(ELEMENT, parentID);
        parameterBuilder.put(VISIBILITY, toVisible);
        return this;
    }

    /**
     * Scrolls over a component to a certain visible element
     *
     * @param element The id of the element that you want to scroll
     * @param toVisible parameter
     * @return this ScrollAction, for chaining
     */
    public ScrollAction scrollToVisible(MobileElement element, String toVisible) {
        parameterBuilder.put(ELEMENT, element.getId());
        parameterBuilder.put(VISIBILITY, toVisible);
        return this;
    }

    /**
     * Scrolls a component to an element determined by the name
     *
     * @param by The id of the element that you want to scroll
     * @param elementName name parameter
     * @return this ScrollAction, for chaining
     */
    public ScrollAction scrollByName(By by, String elementName) {
        String parentID = ((RemoteWebElement) appiumDriver.findElement(by)).getId();
        parameterBuilder.put(ELEMENT, parentID);
        parameterBuilder.put(NAME, elementName);
        return this;
    }

    /**
     * Scrolls a component to an element determined by the name
     *
     * @param element The id of the element that you want to scroll, if the name of the target UI element is known,
     *                it can use the name directly as a parameter to scroll to the correct position of the screen so the target element is visible
     * @param elementName name parameter
     * @return this ScrollAction, for chaining
     */
    public ScrollAction scrollByName(MobileElement element, String elementName) {
        parameterBuilder.put(ELEMENT, element.getId());
        parameterBuilder.put(NAME, elementName);
        return this;
    }

    /**
     * Scrolls a component to an element determined by the direction up, down, left, right
     *
     * @param by The id of the element that you want to scroll
     * @param direction direction: up, down, left, right
     * @return this ScrollAction, for chaining
     */
    public ScrollAction scrollByDirection(By by, String direction) {
        String parentID = ((RemoteWebElement) appiumDriver.findElement(by)).getId();
        parameterBuilder.put(ELEMENT, parentID);
        parameterBuilder.put(DIRECTION, direction);
        return this;
    }

    /**
     * Scrolls a component to an element determined by the direction.
     *
     * @param element The id of the element that you want to scroll
     * @param direction irection: up, down, left, right
     * @return this ScrollAction, for chaining
     */
    public ScrollAction scrollByDirection(MobileElement element, String direction) {
        parameterBuilder.put(ELEMENT, element.getId());
        parameterBuilder.put(DIRECTION, direction);
        return this;
    }

    /**
     * Scrolls a full screen unit in the direction provided in the single parameter, direction.
     *
     * @param direction irection: up, down, left, right
     * @return this ScrollAction, for chaining
     */
    public ScrollAction scrollByDirectionInScreen(String direction) {
        parameterBuilder.put(DIRECTION, direction);
        return this;
    }

    /**
     * Scrolls a component to an element determined by the direction
     *
     * @param element The id of the parent UI element – element must a scrollable element
     * @param predicate A predicate string that describes, using attribute values, the target UI element
     * @return this ScrollAction, for chaining
     */
    public ScrollAction scrollByPredicateString(MobileElement element, String predicate) {
        parameterBuilder.put(ELEMENT, element.getId());
        parameterBuilder.put("predicateString", predicate);
        return this;
    }

    /**
     * @return A map of parameters for this scroll action to pass as part of script
     */
    protected ImmutableMap<String, String> getParameters() {
        return parameterBuilder.build();
    }

    /**
     * Perform this chain of actions on the executeScript.
     *
     * @return this ScrollAction, for possible segmented-scroll
     */
    public ScrollAction perform() {
        appiumDriver.executeScript("mobile:scroll", getParameters());
        return this;
    }
}