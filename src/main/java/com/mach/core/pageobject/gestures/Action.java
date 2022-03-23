package com.mach.core.pageobject.gestures;

import com.google.common.collect.ImmutableList;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.PerformsActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;

import java.time.Duration;
import java.util.List;

public class Action implements PerformsActions<Action> {

	private AppiumDriver<? extends MobileElement> appiumDriver;

	private ImmutableList.Builder<PerformsActions> parameterBuilder;

	public Action(AppiumDriver<? extends MobileElement> appiumDriver) {
		this.appiumDriver = appiumDriver;
		this.parameterBuilder = ImmutableList.builder();
	}

	/**
	 * Build a TouchAction to make a Swipe by coordinates
	 * 
	 * @param startx   coordinate in x where the swipe starts
	 * @param starty   coordinate in y where the swipe starts
	 * @param endx     coordinate in x where the swipe ends
	 * @param endy     coordinate in y where the swipe ends
	 * @param duration the duration of the gesture in ms
	 * @return
	 */
	public Action swipeByTouchAction(int startx, int starty, int endx, int endy, int duration) {
		parameterBuilder.add(new TouchAction(appiumDriver).press(PointOption.point(startx, starty))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration))).moveTo(PointOption.point(endx, endy))
				.release());
		return this;
	}

	/**
	 * Build a TouchAction to make a tap in a element
	 * 
	 * @param element
	 * @return
	 */
	public Action tap(MobileElement element) {
		parameterBuilder.add(
				new TouchAction(appiumDriver).tap(TapOptions.tapOptions().withElement(ElementOption.element(element))));
		return this;
	}

	public Action longTap(MobileElement element, int seconds) {
		parameterBuilder.add(new TouchAction(appiumDriver).longPress(LongPressOptions.longPressOptions()
				.withElement(ElementOption.element(element)).withDuration(Duration.ofSeconds(seconds))).release());
		return this;
	}

	/**
	 * Build a TouchAction to scroll one element up
	 * 
	 * @param element
	 * @param duration the duration of the gesture in ms
	 * @return
	 */
	public Action swipeUpOneElement(MobileElement element, int duration) {
		parameterBuilder.add(swipeByTouchAction(element.getLocation().getX(),
				element.getLocation().getY() + element.getSize().getHeight(), element.getLocation().getX(),
				element.getLocation().getY(), duration));
		return this;
	}

	/**
	 * Build a TouchAction to scroll one element down
	 * 
	 * @param element
	 * @param duration the duration of the gesture in ms
	 * @return
	 */
	public Action swipeDownOneElement(MobileElement element, int duration) {
		parameterBuilder.add(swipeByTouchAction(element.getLocation().getX(), element.getLocation().getY(),
				element.getLocation().getX(), element.getLocation().getY() + element.getSize().getHeight(), duration));
		return this;
	}

	/**
	 * @return a TouchAction to perform
	 */
	protected List<PerformsActions> getParameters() {
		return parameterBuilder.build();
	}

	/**
	 * execute the perform
	 * 
	 * @return
	 */
	public Action perform() {
		for (PerformsActions actions : getParameters()) {
			actions.perform();
		}
		return this;
	}
}
