package com.mach.core.pageobject;

import com.mach.core.config.MachProfileResolver;
import com.mach.core.config.driver.AppiumDriverFactory;
import com.mach.core.model.repository.TextRepository;
import com.mach.core.pageobject.gestures.Action;
import com.mach.core.util.ConstantsLengthInput;
import com.mach.core.util.ConstantsTimeOut;
import com.mach.core.util.EnumDirections;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.clipboard.HasClipboard;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.Widget;
import io.appium.java_client.remote.MobilePlatform;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.mach.core.util.ConstantsTimeOut.TIMEOUT_DYNAMIC_ELEMENTS;
import static com.mach.core.util.ConstantsTimeOut.TIMEOUT_STATIC_ELEMENTS;
import static io.appium.java_client.touch.offset.ElementOption.element;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfNestedElementLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

/**
 * Abstract class representation of a screen in the AUT.
 */
public abstract class Service implements ScreenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private Environment environment;

    public static final int FAST_CHECK = 3;
    public static final String ID = Screen.ID;
    public static final String EXCEPTION_MESSAGE = "method not implemented";
    public static final String XPATH_BY_TEXT = ".//*[@text='%s']";

    protected AppiumDriver<MobileElement> getDriver() {
        return AppiumDriverFactory.getInstance().getDriver();
    }

    /**
     * An implementation of the Wait interface that may have its timeout and
     * polling interval configured on the fly.
     *
     * @param timeOutSeconds The timeout duration (seconds)
     * @return
     */
    private FluentWait<WebDriver> waitOn(AppiumDriver<MobileElement> driver, int timeOutSeconds) {
        return new WebDriverWait(driver, timeOutSeconds)
                .withTimeout(Duration.ofSeconds(timeOutSeconds))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * Waits specific time
     * @param time in seconds
     */
    protected void waitOn(int time) {
        try {
            new WebDriverWait(getDriver(), time)
                    .withTimeout(Duration.ofSeconds(time))
                    .pollingEvery(Duration.ofSeconds(FAST_CHECK))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(WebDriverException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nonExistingElement")));
        } catch (TimeoutException e) {
            LOGGER.debug(String.format("wait on seconds: %s", time));
        }
    }

    /**
     * Get a FluentWait that has a dynamic timeout of {@link ConstantsTimeOut} set up.
     *
     * @return A <code>FluentWait</code> instance.
     */
    protected FluentWait<WebDriver> getWait() {
        return waitOn(getDriver(), TIMEOUT_STATIC_ELEMENTS);
    }

    /**
     * Get a FluentWait with the specified timeout in seconds.
     *
     * @param timeOutSeconds
     * @return
     */
    protected FluentWait<WebDriver> getWait(int timeOutSeconds) {
        return waitOn(getDriver(), timeOutSeconds);
    }

    /**
     * Get an instance of the specified ScreenService.
     *
     * @param tClass
     * @return
     */
    protected <T> T getServiceScreen(Class<T> tClass) {
        return appContext.getBean(tClass);
    }

    @Step("Verify if any of {0} texts is present")
    public boolean isAnyTextPresent(String... texts) {
        return isAnyElementPresent(texts);
    }

    /**
     * Tap on x and y relative coordinates.
     *
     * @param x the horizontal position as a percentage starting from left to right.
     * @param y the vertical position as a percentage starting from top to bottom.
     */
    protected void tapByRelativeCoordinates(double x, double y) {
        new TouchAction<>(getDriver())
                .tap(PointOption.point(((int) Math.round(getDriver().manage().window().getSize().getWidth() * x)),
                        ((int) Math.round(getDriver().manage().window().getSize().getHeight() * y)))).perform();

    }

    /**
     * Click on a given <code>MobileElement</code> element with the timeout specified in {@link #getWait()}. If this causes a new
     * screen to load, you should discard all references to this element and any
     * further operations performed on this element will throw a
     * <code>StaleElementReferenceException</code>.
     *
     * @param element A <code>MobileElement</code> instance.
     * @throws <code>TimeoutException</code> - If the timeout expires because the element is not visible
     *                                       or is disabled such that you cannot click it.
     */
    protected void click(MobileElement element) {
        try {
            getWait().until(elementToBeClickable(element)).click();
        } catch (TimeoutException | ElementNotVisibleException e) {
            if (MobilePlatform.IOS.equals(MachProfileResolver.getActiveProfile())) {
                LOGGER.warn("click falling back to tap elem: {}",element);
                tap(element);
            } else {
                throw e;
            }
        }
    }

    /**
     * Overload of {@link #click(MobileElement)} for WebElements (used by {@link Widget}).
     *
     * @see #click(MobileElement)
     */
    protected void click(WebElement element) {
        getWait().until(elementToBeClickable(element)).click();
    }

    /**
     * Click on a given <code>MobileElement</code> element with the provided timeout in seconds. If this causes a new
     * screen to load, you should discard all references to this element and any
     * further operations performed on this element will throw a
     * <code>StaleElementReferenceException</code>.
     *
     * @param timeOutSeconds The timeout duration (seconds)
     * @param element        A <code>MobileElement</code> instance.
     * @throws <code>TimeoutException</code> - If the timeout expires because the element is not visible
     *                                       or is disabled such that you cannot click it.
     */
    protected void click(MobileElement element, int timeOutSeconds) {
        getWait(timeOutSeconds).until(elementToBeClickable(element)).click();
    }

    /**
     * Click on an element element given the element text If this causes a new
     * screen to load, you should discard all references to this element and any
     * further operations performed on this element will throw a
     * <code>StaleElementReferenceException</code>.
     *
     * @param text             The element text.
     * @param timeoutInSeconds the maximum amount of seconds to wait for text to be visible.
     * @throws <code>TimeoutException</code> - If the timeout expires because the element is not visible
     *                                       or is disabled such that you cannot click it.
     */
    protected void click(String text, int timeoutInSeconds) {
        getWait(timeoutInSeconds)
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format("//*[@name='%s' or @label='%s' or @text='%s']", text, text, text))))
                .click();
    }

    /**
     * Get the visible text of this element, searching for the element with the timeout specified on {@link #getWait()}.
     *
     * @param element A <code>MobileElement</code> instance.
     * @return The element text, or <code>null</code> if element is not found
     */
    protected String getText(MobileElement element, int timeoutSeconds) {
        try {
            isElementPresent(element, timeoutSeconds);
            final String text = cleanText(element.getText());
            if (!text.isEmpty()) {
                return text;
            }
            return getElementTextOrIOSName(element);
        } catch (TimeoutException | NoSuchElementException e) {
            LOGGER.error("Error Text not found on element");
            return null;
        }
    }

    /**
     * Get the visible text of this element, searching for the element with @link TIMEOUT_STATIC_ELEMENTS timeout.
     *
     * @param element A <code>MobileElement</code> instance.
     * @return The element text, or <code>null</code> if element is not found
     */
    protected String getText(MobileElement element) {
        return getText(element, TIMEOUT_STATIC_ELEMENTS);
    }

    /**
     * Use this method to simulate typing into an element, which may set its
     * value.
     *
     * @param element A <code>MobileElement</code> instance.
     * @param text    Character sequence to send to the element
     * @throws <code>TimeoutException</code> - If the timeout expires because the element is not visible
     *                                       or is disabled such that you cannot type on it.
     */
    protected void type(MobileElement element, String text) {
        LOGGER.debug("type: {}", text);
        getWait().until(elementToBeClickable(element));
        element.clear();
        element.sendKeys(text);
    }

    /**
     *
     * Use typeSpecial when normal {@link #type(MobileElement, String)} does not work on Android. It does not
     * interact with the element and instead interacts directly with keyboard/key events.
     *
     * @param text
     * @throws <code>TimeoutException</code> - If the timeout expires because the element is not visible
     *                                       or is disabled such that you cannot type on it.
     *
     * The method typeSpecial may receive a MobileElement and a String or just a String.
     * The String may contain any letter of the alphabet, any digit and a few special characters (please see
     * methods convertCharToAndroidKey(char) and convertSpecialCharacterToAndroidKey(char).
     *
     * The escape character for special characters is '%'.
     *
     */
    private void typeSpecialAndroid(String text, MobileElement element) {
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) getDriver();
        Pattern p = Pattern.compile("%[a-z]");
        Matcher m = p.matcher(text);
        for (int i = 0; i < text.length(); i++) {
            Character ch = text.charAt(i);
            if (m.find(i) && i == m.start()) {
                Character sp = text.charAt(i + 1);
                driver.pressKey(new KeyEvent(convertSpecialCharacterToAndroidKey(sp)));
                i = i + 1;
            } else {
                if (Character.isUpperCase(ch)) {
                    driver.pressKey(new KeyEvent(AndroidKey.SHIFT_LEFT));
                }
                AndroidKey keyCode = convertCharToAndroidKey(ch);
                if (keyCode != null) {
                    driver.pressKey(new KeyEvent(keyCode));
                } else {
                    element.sendKeys(String.valueOf(ch));
                }
            }
        }
    }

    /**
     * type each letter individually, only needed in android
     * @param element inputText
     * @param text to type
     */
    protected void typeSpecial(MobileElement element, String text) {
        click(element);
        element.clear();
        LOGGER.debug("typeSpecial: {}", text);
        if (MobilePlatform.ANDROID.equals(MachProfileResolver.getActiveProfile())) {
            typeSpecialAndroid(text, element);
        } else {
            element.sendKeys(text);
        }
    }

    /**
     * convertSpecialCharacterToAndroidKey is a helper method for typeSpecial. It may be extended by adding more cases.
     * If the String parameter for typeSpecial contains the escape character (%), then the following character is parsed
     * by convertSpecialCharacterToAndroidKey instead of convertCharToAndroidKey.
     */
    private AndroidKey convertSpecialCharacterToAndroidKey(char ch) {
        switch(Character.toLowerCase(ch)) {
            case 'n':
                return AndroidKey.ENTER;
            case 't':
                return AndroidKey.TAB;
            default: return null;
        }
    }

    /**
     * convertCharToAndroidKey is a helper method for typeSpecial. It may be extended by adding more cases.
     * If the String parameter for typeSpecial contains the escape character (%), then the following character is parsed
     * by convertSpecialCharacterToAndroidKey instead of convertCharToAndroidKey.
     */
    private AndroidKey convertCharToAndroidKey(char ch) {
        switch(Character.toLowerCase(ch)) {
            case '0': return AndroidKey.DIGIT_0;
            case '1': return AndroidKey.DIGIT_1;
            case '2': return AndroidKey.DIGIT_2;
            case '3': return AndroidKey.DIGIT_3;
            case '4': return AndroidKey.DIGIT_4;
            case '5': return AndroidKey.DIGIT_5;
            case '6': return AndroidKey.DIGIT_6;
            case '7': return AndroidKey.DIGIT_7;
            case '8': return AndroidKey.DIGIT_8;
            case '9': return AndroidKey.DIGIT_9;
            case 'a': return AndroidKey.A;
            case 'b': return AndroidKey.B;
            case 'c': return AndroidKey.C;
            case 'd': return AndroidKey.D;
            case 'e': return AndroidKey.E;
            case 'f': return AndroidKey.F;
            case 'g': return AndroidKey.G;
            case 'h': return AndroidKey.H;
            case 'i': return AndroidKey.I;
            case 'j': return AndroidKey.J;
            case 'k': return AndroidKey.K;
            case 'l': return AndroidKey.L;
            case 'm': return AndroidKey.M;
            case 'n': return AndroidKey.N;
            case 'o': return AndroidKey.O;
            case 'p': return AndroidKey.P;
            case 'q': return AndroidKey.Q;
            case 'r': return AndroidKey.R;
            case 's': return AndroidKey.S;
            case 't': return AndroidKey.T;
            case 'u': return AndroidKey.U;
            case 'v': return AndroidKey.V;
            case 'w': return AndroidKey.W;
            case 'x': return AndroidKey.X;
            case 'y': return AndroidKey.Y;
            case 'z': return AndroidKey.Z;
            case '@': return AndroidKey.AT;
            case '.': return AndroidKey.PERIOD;
            case '-': return AndroidKey.MINUS;
            default: return null;
        }
    }

    /**
     * Press and hold the at the center of an element until the contextmenu
     * event has fired. <code>StaleElementReferenceException</code>.
     *
     * @param element An <MobileElement> instance.
     * @throws <code>TimeoutException</code> - If the timeout expires because the element is not visible
     *                                       or is disabled such that you cannot click it.
     */
    protected void tap(MobileElement element) {
        new Action(getDriver())
                .tap(element)
                .perform();
    }

    /**
     * Taps on different parts of an element: near top left corner, near top right corner, near bottom left corner, near bottom right corner, center.
     * This method is useful for clicking on links that are embedded on MobileElements.
     *
     * @param element
     */
    protected void tapOnDifferentParts(MobileElement element) {
        if (isElementDisplayed(element)) {
            new TouchAction<>(getDriver())
                    .tap(PointOption.point(element.getLocation().getX() + ((int) Math.round(element.getSize().getWidth() * 0.05)),
                            element.getLocation().getY() + ((int) Math.round(element.getSize().getHeight() * 0.05)))).perform();
        }
        if (isElementDisplayed(element)) {
            new TouchAction<>(getDriver())
                    .tap(PointOption.point(element.getLocation().getX() + ((int) Math.round(element.getSize().getWidth() * 0.05)),
                            element.getLocation().getY() + ((int) Math.round(element.getSize().getHeight() * 0.95)))).perform();
        }
        if (isElementDisplayed(element)) {
            new TouchAction<>(getDriver())
                    .tap(PointOption.point(element.getLocation().getX() + ((int) Math.round(element.getSize().getWidth() * 0.95)),
                            element.getLocation().getY() + ((int) Math.round(element.getSize().getHeight() * 0.95)))).perform();
        }
        if (isElementDisplayed(element)) {
            new TouchAction<>(getDriver())
                    .tap(PointOption.point(element.getLocation().getX() + ((int) Math.round(element.getSize().getWidth() * 0.95)),
                            element.getLocation().getY() + ((int) Math.round(element.getSize().getHeight() * 0.05)))).perform();
        }
        if (isElementDisplayed(element)) {
            new TouchAction<>(getDriver())
                    .tap(PointOption.point(element.getLocation().getX() + ((int) Math.round(element.getSize().getWidth() * 0.5)),
                            element.getLocation().getY() + ((int) Math.round(element.getSize().getHeight() * 0.5)))).perform();
        }
    }

    /**
     * Hides the keyboard if it is showing.
     */
    protected void hideKeyboard() {
        try {
            if (MobilePlatform.ANDROID.equals(MachProfileResolver.getActiveProfile())) {
                getDriver().hideKeyboard();
            } else {
                hideKeyboardIOSByButton();
            }
        } catch (WebDriverException t) {
            LOGGER.warn("Error hiding keyboard. {}", t.getMessage());
        }
    }

    /**
     * try to hide Keyboard in IOS by pressing ok buttons or by swiping the Keyboard
     */
    private void hideKeyboardIOSByButton(){
        if (!isKeyboardShown()) {
            LOGGER.debug("hideKeyboardIOSByButton is not necessary");
            return;
        }
        try{
            By by = MobileBy.iOSNsPredicateString("label==[c]'return' or label ==[c]'intro' or label==[c]'done'");
            MobileElement button = getClickableElement(by,TIMEOUT_DYNAMIC_ELEMENTS);
            click(button, TIMEOUT_DYNAMIC_ELEMENTS);
            LOGGER.info("hideKeyboardIOSByButton ok");
        } catch (TimeoutException | NoSuchElementException e){
            hideKeyboardIOSBySwipe();
        }
    }

    protected boolean isKeyboardShown(){
        if (MobilePlatform.ANDROID.equals(MachProfileResolver.getActiveProfile())) {
            AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) getDriver();
            return driver.isKeyboardShown();
        }
        IOSDriver<MobileElement> driver = (IOSDriver<MobileElement>) getDriver();
        return driver.isKeyboardShown();
    }

    /**
     * try to hide Keyboard in IOS by swiping the Keyboard
     */
    private void hideKeyboardIOSBySwipe(){
        final List<MobileElement> elements = getElementsBy(By.className("XCUIElementTypeKeyboard"));
        if (elements.isEmpty()) {
            LOGGER.debug("hideKeyboardIOSBySwipe is not necessary");
            return;
        }
        final Rectangle rect = elements.get(0).getRect();
        final int xCoordinate = rect.getX() + rect.getWidth() / 2;
        final int yCoordinate = rect.getY() - 1;
        swipeFull(xCoordinate,yCoordinate,xCoordinate,yCoordinate + 20, 500);
    }

    /**
     * Checking that an element, known to be present on the screen, is
     * displayed.
     *
     * @param element A <code>MobileElement</code> instance.
     * @return True if the element is visible otherwise return false.
     * @throws <code>TimeoutException</code> - If the timeout expires because the element is not visible.
     */
    protected boolean isElementDisplayed(MobileElement element) {
        boolean isDisplayed = isElementPresent(element, ConstantsTimeOut.TIMEOUT_DYNAMIC_ELEMENTS);
        if(isDisplayed){
            LOGGER.debug("isElement: {} displayed:{}", element, true);
        } else {
            LOGGER.warn("isElement: {} displayed:{}", element, false);
        }
        return isDisplayed;
    }

    /**
     * @param text of the Mobile Element to find
     * @return True is element is present in the screen
     */
    protected boolean isElementPresent(String text) {
        try {
            By locator = null;
            if (MobilePlatform.IOS.equals(MachProfileResolver.getActiveProfile())) {
                locator = MobileBy.iOSNsPredicateString(String.format("name == '%s' OR label == '%s' OR value == '%s'", text, text, text));
            } else {
                locator = By.xpath(String.format("//*[@text='%s']", text));
            }
            getWait(ConstantsTimeOut.TIMEOUT_DYNAMIC_ELEMENTS).ignoring(NoSuchElementException.class).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
        return true;
    }

    /**
     * @param texts of the Mobiles Elements to find (parallel search)
     * @return True is element is present in the screen
     */
    protected boolean isAnyElementPresent(String... texts) {
        String[] textsFromKeys = TextRepository.getTexts(texts).toArray(new String[0]);
        try {
            By locator = null;
            if (MobilePlatform.IOS.equals(MachProfileResolver.getActiveProfile())) {
                locator = MobileBy.iOSNsPredicateString(buildCondtitionsIos(textsFromKeys.length > 0? textsFromKeys : texts));
            } else {
                locator = By.xpath(buildCondtitionsAndroid(textsFromKeys.length > 0? textsFromKeys : texts));
            }
            getWait(ConstantsTimeOut.TIMEOUT_DYNAMIC_ELEMENTS).ignoring(NoSuchElementException.class).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (WebDriverException w) {
            return false;
        }
        return true;
    }

    @Step("Verify if any of {0} texts is contained")
    public boolean isAnyTextContainedPresent(String... texts) {
        String[] textsFromKeys = TextRepository.getTexts(texts).toArray(new String[0]);
        try {
            By locator = null;
            if (MobilePlatform.IOS.equals(MachProfileResolver.getActiveProfile())) {
                locator = MobileBy.iOSNsPredicateString(containsConditionsIOS(textsFromKeys.length > 0? textsFromKeys : texts));
            } else {
                locator = By.xpath(containsConditionsAndroid(textsFromKeys.length > 0? textsFromKeys : texts));
            }
            getWait(ConstantsTimeOut.TIMEOUT_DYNAMIC_ELEMENTS).ignoring(NoSuchElementException.class).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (WebDriverException w) {
            return false;
        }
        return true;
    }

    /**
     * used by {@link #isAnyElementPresent(String... texts)}
     * @param concatFormat
     * @param joinFormat
     * @param texts
     * @return
     */
    private String buildConditions(String concatFormat, String joinFormat, String... texts) {
        final List<String> conditions = new ArrayList<>();
        for (String text : texts) {
            conditions.add(String.format(concatFormat, text));
        }
        return String.format(joinFormat, String.join(" OR ", conditions));
    }

    /**
     * used by {@link #isAnyElementPresent(String... texts)}
     * @param texts
     * @return
     */
    private String buildCondtitionsIos(String... texts) {
        return buildConditions("name == \"%1$s\" OR label == \"%1$s\" OR value == \"%1$s\"", "%s", texts);
    }

    /**
     * used by {@link #isAnyElementPresent(String... texts)}
     * @param texts
     * @return
     */
    private String buildCondtitionsAndroid(String... texts) {
        final List<String> conditions = new ArrayList<>();
        for (String text : texts) {
            conditions.add(String.format("//*[@text=\"%s\"]", text));
        }
        return String.join("|", conditions);
    }

    private String containsConditionsIOS(String... texts) {
        final List<String> conditions = new ArrayList<>();
        for (String text : texts) {
            conditions.add(String.format("name CONTAINS \"%1$s\" OR label CONTAINS \"%1$s\" OR value CONTAINS \"%1$s\"", text));
        }
        return String.format("%s", String.join(" OR ", conditions));
    }

    private String containsConditionsAndroid(String... texts) {
        final List<String> conditions = new ArrayList<>();
        for (String text : texts) {
            conditions.add(String.format("//*[contains(normalize-space(@text), \"%s\")]", text));
        }
        return String.join("|", conditions);
    }

    /**
     * return true if the widget is visible
     * @param widget of the Widget to find
     * @return True is widget is displayed in the screen
     */
    protected boolean isWidgetDisplayed(Widget widget) {
        boolean exist = false;
        String widgetName = widget.getClass().getSimpleName().substring(0,widget.getClass().getSimpleName().indexOf("$"));
        try {
            getWait().until(ExpectedConditions.visibilityOf(widget.getWrappedElement()));
            LOGGER.debug("isWidget: {} Displayed: {}", widgetName, true);
            exist = true;
        } catch (TimeoutException | NoSuchElementException e) {
            LOGGER.error("isWidget: {} Displayed: {}", widgetName, false);
        }
        return exist;
    }

    /**
     * Return true if the element exists in the timeout
     * @param element
     * @param timeoutInSeconds
     * @return
     */
    protected boolean isElementPresent(final MobileElement element, final int timeoutInSeconds) {
        boolean resp;
        try {
            getWait(timeoutInSeconds).ignoring(NoSuchElementException.class)
                    .until(webDriver -> null != element.getText() || null != element.getId() || null != element.getTagName());
            resp = true;
        } catch (Exception e) {
            resp = false;
        }
        LOGGER.debug("isElementPresent: {}", resp);
        return resp;
    }

    /**
     * Wait for a given <code>MobileElement</code> instance to be visible
     *
     * @param element The element used to check its visibility.
     * @param timeout The timeout duration (seconds)
     * @throws <code>TimeoutException</code> - If the timeout expires because the element is not visible.
     */
    protected void waitForVisibilityOf(MobileElement element, int timeout) {
        waitOn(getDriver(), timeout).until(visibilityOf(element));
    }

    /**
     * Checking that an element, known to be present on the screen, is visible.
     *
     * @param element A  <code>MobileElement</code> instance.
     * @return True if the element is visible otherwise return false.
     * @throws <code>TimeoutException</code> - If the timeout expires because the element is not visible.
     */
    protected Boolean nestedElementExists(WebElement element, By subLocator, int timeout) {
        try {
            return null != waitOn(getDriver(), timeout)
                    .until(presenceOfNestedElementLocatedBy(element, subLocator));
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    /**
     * Verify if at least one of the ids passed as parameters is part of the given container.
     *
     * @param container to be checked. It's a <code>MobileElement</code> instance.
     * @param paramIds  identifier list to be located.
     * @param timeout
     * @return True if at least one of the ids passed as parameters is part of the given container, otherwise return false.
     * @throws <code>TimeoutException</code> - If the timeout expires because one of items is not visible.
     */
    protected Boolean nestedElementsExist(WebElement container, List<String> paramIds, int timeout) {
        return paramIds.stream().anyMatch(param -> nestedElementExists(container, id(param), timeout));
    }

    /**
     * Determine whether or not this element is enabled. This operation only
     * applies to elements that has the "Enabled" attribute.
     *
     * @param element The element to check
     * @return True if the element is currently focused, false otherwise.
     */
    protected boolean isElementEnabled(MobileElement element) {
        final String descError = "Minor Error at isElementEnabled e: {}";
        final String descResult = element + " isElementEnabled: {}";
        try {
            getWait().until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            LOGGER.warn("Minor Error at isElementEnabled: {}  element: {}", false, e.toString());
            return false;
        }
        try {
            if (element.getAttribute("value").equals("normal")) {
                LOGGER.debug(descResult, true);
                return true;
            }
            if (element.getAttribute("value").equals("disabled")) {
                LOGGER.debug(descResult, false);
                return false;
            }
        } catch (Exception e) {
            LOGGER.warn(descError, e.toString());
        }
        try{
            if(element.getAttribute("enabled").equals("true")) {
                LOGGER.debug(descResult, true);
                return true;
            }
            if(element.getAttribute("enabled").equals("false")) {
                LOGGER.debug(descResult, false);
                return false;
            }
        } catch (Exception e) {
            LOGGER.warn(descError, e.toString());
        }
        try {
            getWait().ignoring(NoSuchElementException.class).until(ExpectedConditions.elementToBeClickable(element));
            LOGGER.debug(descResult, true);
            return true;
        } catch (Exception e) {
            LOGGER.warn("Minor Error at isElementEnabled: {}  element: {}", false, e.toString());
            return false;
        }
    }

    /**
     * Determine whether or not this element is checked. This operation only
     * applies to elements that has the "Checked" attribute.
     *
     * @param element The element to check
     * @return True if the element is currently checked, false otherwise.
     */
    protected boolean isElementChecked(MobileElement element) {
        return getAttribute(element, "checked");
    }

    /**
     * Scroll until Mobile Element with specific text
     *
     * @param text to find in Mobile Element
     * @return Mobile Element located
     */
    protected MobileElement scrollToElementByText(String text, EnumDirections direction) {
        return scrollToElementBy(By.xpath(String.format(XPATH_BY_TEXT, text)), direction);
    }

    /**
     * Scroll a page searching for an element by locator "by".
     * @param by locator
     * @param direction direction for swipe (up/down)
     * @return firstMobile element found or throws an exception if nothing found after 10 scrolls.
     */
    protected MobileElement scrollToElementBy(By by, EnumDirections direction) {
        final int maxScrolls = 10;
        int currentScroll = 0;
        final List<MobileElement> elementsFound = new ArrayList<>(getElementsBy(by));
        while (currentScroll < maxScrolls && elementsFound.isEmpty()) {
            if(direction == EnumDirections.UP){
                swipeVertical(0.5, 0.95, 0.5);
            }
            else {
                swipeVertical(0.5, 0.05, 0.5);
            }
            elementsFound.addAll(getElementsBy(by));
            currentScroll++;
        }
        if (elementsFound.isEmpty()) {
            throw new NotFoundException("Element not found by:" + by);
        }
        return elementsFound.get(0);
    }

    /**
     * Returns a list of elements found by a "Mobile.By" condition.
     * @param by locator
     * @return
     */
    protected List<MobileElement> getElementsBy(By by){
        return getDriver().findElements(by);
    }

    /**
     * Scroll down in page. used in {@link #scrollToElementBy(By by, EnumDirections directions)}
     */
    protected void scrollDown() {
        Dimension dimension = getDriver().manage().window().getSize();
        int scrollXStart = (int) (dimension.getWidth() * 0.5);
        int scrollYStart = (int) (dimension.getHeight() * 0.5);
        int scrollYEnd = (int) (dimension.getHeight() * 0.2);
        (new TouchAction<>(getDriver())).press(PointOption.point(scrollXStart, scrollYStart))
                .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(1))).moveTo(PointOption.point(scrollXStart, scrollYEnd))
                .release().perform();
    }

    protected void scrollUp() {
        try {
            Dimension dimension = getDriver().manage().window().getSize();
            int scrollXStart = (int) (dimension.getWidth() * 0.5);
            int scrollYStart = (int) (dimension.getHeight() * 0.2);
            int scrollYEnd = (int) (dimension.getHeight() * 0.5);
            (new TouchAction<>(getDriver())).press(PointOption.point(scrollXStart, scrollYStart))
                    .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(1))).moveTo(PointOption.point(scrollXStart, scrollYEnd))
                    .release().perform();
        } catch (WebDriverException e) {
            LOGGER.debug("Error when scrolling up, e: {}", e.toString());
        }
    }

    /**
     * Tap on the Android's native back button.
     */
    protected void tapOnNativeBackButton() {
        ((AndroidDriver<MobileElement>) getDriver()).pressKey(new KeyEvent(AndroidKey.BACK));
    }

    /**
     * A simple wrapper to <code>getAttribute()</code> method.
     *
     * @param element An <code>MobileElement</code> instance.
     * @param attr    The name of the attribute.
     * @return The attribute/property's current value or false if the value is
     * not set.
     */
    protected boolean getAttribute(MobileElement element, String attr) {
        String selected = element.getAttribute(attr);
        return (selected != null) ? Boolean.valueOf(selected) : Boolean.FALSE;
    }

    /**
     * @param list
     * @return an element {@link Class}
     */
    protected <T> T getLastElement(List<T> list) {
        return list.stream()
                .collect(Collectors.toCollection(LinkedList::new))
                .getLast();
    }

    /**
     * @param list
     * @return an element {@link Class}
     */
    protected <T> T getFirstElement(List<T> list) {
        return list.stream()
                .collect(Collectors.toCollection(LinkedList::new))
                .getFirst();
    }

    /**
     * @param element, String x or y
     * @return int, coordinate x or y of the MobileElement in upper center
     */
    protected int getCoordinatesUpCenter(MobileElement element, String xy) {
        return xy.equals("x") ?
                (element.getLocation().getX()) + (element.getSize().getWidth() / 2)
                : (element.getLocation().getY());
    }

    protected void doubleTapGesture(MobileElement mobileElement) {
        JavascriptExecutor js = getDriver();
        Map<String, Object> params = new HashMap<>();
        params.put("element", mobileElement.getId());
        js.executeScript("mobile: doubleTap", params);
    }

    /**
     * Perform a Swipe with a duration of 2000 miliseconds
     * @param startx initial value x
     * @param starty initial value y
     * @param endx finish value x
     * @param endy finish value y
     */
    protected void swipe(int startx, int starty, int endx, int endy) {
        swipeFull(startx, starty, endx, endy,2000);
    }

    /**
     * Perform a Swipe with all possible options. used in {@link #swipe(int startx, int starty, int endx, int endy)}
     * @param startX initial value x
     * @param startY initial value y
     * @param endX finish value x
     * @param endY finish value y
     * @param duration time in mili seconds
     */
    private void swipeFull(int startX, int startY, int endX, int endY, int duration){
        try {
            LOGGER.trace("try to swipe from {},{} to {},{}",startX,startY,endX,endY);
            new Action(getDriver())
                    .swipeByTouchAction(startX, startY, endX, endY, duration)
                    .perform();
        } catch (InvalidElementStateException e){
            LOGGER.warn("Swipe did not complete successfully e: ", e);
        }

    }

    /**
     * @param startPercentage
     * @param finalPercentage
     * @param widthPercentage
     */
    protected void swipeVertical(double startPercentage, double finalPercentage, double widthPercentage) {
        Dimension size = getDriver().manage().window().getSize();
        int anchor = (int) (size.width * widthPercentage);
        swipe(anchor, (int) (size.height * startPercentage), anchor, (int) (size.height * finalPercentage));
    }

    /**
     * @param widget
     * @param by
     * @return mobileElement
     */
    protected MobileElement getNestedElementFromWidget(Widget widget, By by) {
        return (MobileElement) widget.findElement(by);
    }

    /**
     * Press "done", "ok" or "confirm" button to close Keyboard in Android
     */
    protected void tapDoneOnKeyboardAndroid() {
        ((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.ENTER));
    }

    /**
     * Paste content of clipboard into given element.
     */
    protected void pasteClipboardText(MobileElement editableElement) {
        click(editableElement);
        waitOn(FAST_CHECK);
        if (MobilePlatform.ANDROID.equals(MachProfileResolver.getActiveProfile())) {
            ((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.PASTE));
        } else {
            new TouchAction<>(getDriver()).longPress(LongPressOptions.longPressOptions().withElement(element(editableElement)).withDuration(Duration.ofSeconds(2))).release().perform();
            waitOn(FAST_CHECK);
            By byPaste = By.xpath("(//*[@name='Paste' or @label='Paste' or @name='Pegar' or @label='Pegar'])[last()]");
            getDriver().findElement(byPaste).click();
        }
    }

    /**
     * press DEL key when the keyboard is displayed.
     */
    protected void pressKeyDELAndroid() {
        AndroidDriver<MobileElement> driver = (AndroidDriver) getDriver();
        driver.pressKey(new KeyEvent(AndroidKey.DPAD_RIGHT));
        driver.pressKey(new KeyEvent(AndroidKey.DPAD_RIGHT));
        driver.pressKey(new KeyEvent(AndroidKey.DEL));
    }

    /**
     * Get text copied to clipboard.
     * @return text.
     */
    protected String getClipboardText() {
        return ((HasClipboard) getDriver()).getClipboardText();
    }

    /**
     * Set the text into the clipboard.
     * @param text
     */
    public void setClipboardText(String text){
        HasClipboard driver = ((HasClipboard) getDriver());
        driver.setClipboardText(text);
    }

    /**
     * Return the mobile element found in the instance
     * @param by locator to found the element
     * @param timeOutSeconds seconds to wait
     * @return
     */
    protected MobileElement getClickableElement(By by, int timeOutSeconds) {
        waitOn(getDriver(), timeOutSeconds).until(visibilityOfElementLocated(by));
        waitOn(getDriver(), timeOutSeconds).until(elementToBeClickable(by));
        MobileElement element = getDriver().findElement(by);
        LOGGER.trace("getClickableElement by: {}", by);
        return element;
    }

    public Integer parseInt(String originalString) {
        String numberString = originalString.replaceAll("[^\\d]", "");
        if(numberString.isEmpty()) {
            return null;
        }
        return Integer.parseInt(numberString);
    }

    /**
     * Return true if the complete (or part) of the text {0} is contained in any text on the current screen.
     * This method is different from isAnyTextContainedPresent in that it does not use driver waits,
     * it manually polls for the element.
     *
     * @param texts complete or incomplete texts to find
     * @return
     */
    @Step("Verify presence of {0} texts")
    public boolean isAnyTextContainedPresentSpecial(String... texts) {
        String textSent = Arrays.toString(texts);
        textSent = textSent.lastIndexOf("->") > 0 ? textSent.substring(textSent.lastIndexOf("->")) : textSent;
        LOGGER.info("searching text: {}", textSent);
        boolean found = false;

        for (String text : texts) {
            try{
                List<String> textsFromKeys = TextRepository.getTexts(text);
                if(textsFromKeys.isEmpty()){
                    textsFromKeys.add(text);
                }
                for(String value : textsFromKeys){
                    found = !getElementsByTextContained(value).isEmpty();
                    LOGGER.info("text: {} found?: {}", value, found);
                    if(found){
                        return true;
                    }
                }
            } catch (TimeoutException | NoSuchElementException e) {
                LOGGER.warn("not found text: {}", text);
            }
        }
        return found;
    }

    /**
     * Return a list of MobileElement if the complete (or part) of the text {0} is contained in any text on the current screen
     * @param texts complete or incomplete text to find
     * @return
     */
    protected List<MobileElement> getElementsByTextContained(String... texts) {
        List<MobileElement> results = new ArrayList<>();
        for (String text : texts) {
            results = getListPresentElement(getByFromText(text), TIMEOUT_DYNAMIC_ELEMENTS);
            if (!results.isEmpty()){
                LOGGER.info("text: {}, size: {}", text, results.size());
                return results;
            }
        }
       return results;
    }

    /**
     * clear a input text in ios by sending keys or pressing the delete button
     * @param elem element (inputText)
     * @param eraseTimes times for pressing the delete button
     */
    protected void clearInputText(MobileElement elem, ConstantsLengthInput eraseTimes) {
        elem.clear();
        if (MobilePlatform.IOS.equals(MachProfileResolver.getActiveProfile())) {
            elem.click();
            elem.sendKeys(Keys.END + Stream.of((StringUtils.repeat("0", eraseTimes.getNumber()) + elem.getText()).split("")).map(a -> "\b").collect(Collectors.joining("")));
        }
    }

    /**
     * Return the List of MobileElement (or null) if the locator by its found in the time (seconds).
     * This is another way for wait an element, different from polling
     * @param by Locator
     * @param seconds for wait the element
     * @return
     */
    protected List<MobileElement> getListPresentElement(By by, int seconds) {
        List<MobileElement> elements = getDriver().findElements(by);
        Instant start = Instant.now();
        Duration timeElapsed = Duration.ZERO;
        int retry = 0;

        while (elements.isEmpty() && timeElapsed.getSeconds() < seconds){
            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            elements = getDriver().findElements(by);
            retry += 1;
            timeElapsed = Duration.between(start, Instant.now());
        }
        
        if(elements.isEmpty()){
            LOGGER.warn("zero elements using by: {}, retry: {}", by, retry);
        }

        return elements;
    }

    /**
     * Return a list (with items duplicated) of the current visible texts. You must wait before use this method
     * @return
     */
    public List<String> getAllVisibleTexts(){
        return getAllVisibleTexts(getDriver());
    }

    /**
     * Return a list (with items duplicated) of the current visible texts inside element passed as parameter. You must wait before use this method
     * @param driverOrElement searchable element or appium driver.
     * @return
     */
    protected List<String> getAllVisibleTexts(SearchContext driverOrElement){
        List<String> listText = new ArrayList<>();

        if (driverOrElement == null){
            return listText;
        }

        By by = By.xpath("//android.view.View | //android.widget.TextView | //android.widget.Button | //android.widget.RadioButton | //android.widget.EditText");

        if (MobilePlatform.IOS.equals(MachProfileResolver.getActiveProfile())) {
            by = MobileBy.iOSNsPredicateString("visible==1 and value.length > 0 || (type == 'XCUIElementTypeButton' && name != '') || (type == 'XCUIElementTypeNavigationBar' && name != '') || (type == 'XCUIElementTypeSearchField' && name != '')");
        }

        List<MobileElement> listElements = driverOrElement.findElements(by);
        String text;

        for (MobileElement mobileElement : listElements) {

            try {
                text = getElementTextOrIOSName(mobileElement);
            }catch (StaleElementReferenceException e) {
                text = null;
            }

            if(text == null || text.isEmpty()) {
                continue;
            }
            listText.add(cleanText(text));

        }

        LOGGER.debug("getAllVisibleTexts: {}, size: {}", listText, listText.size());

        return listText;
    }

    /**
     * remove hidden symbols and excessive spaces, replace tabs and brake line into one space, for smooth text
     * @param txt
     * @return
     */
    protected String cleanText(String txt){
        return txt.trim()
                .replace("\u00A0", "")
                .replace(String.valueOf((char) 160), "")
                .replaceAll("\\s{2,}", " ")
                .replace("\n"," ")
                .replace("\t"," ")
                .trim();
    }

    /**
     * Returns the element name if it is an XCUIElementTypeNavigationBar or an XCUIElementTypeButton.
     * otherwise returns the text of the element
     *
     * @param element
     * @return
     */
    private String getElementTextOrIOSName(MobileElement element){
        return ("XCUIElementTypeButton".equals(element.getTagName()) || "XCUIElementTypeNavigationBar".equals(element.getTagName())) ? element.getAttribute("name") : element.getText();
    }

    /**
     * Return the By (or locator) to the specific text
     * @param text
     * @return
     */
    protected By getByFromText(String text){
        By by = (MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + text + "\")"));
        if (MobilePlatform.IOS.equals(MachProfileResolver.getActiveProfile())) {
            String textLike = "*"+ text.replaceAll("\\s+", "*") + "*";
            by = MobileBy.iOSNsPredicateString("name like '" + textLike + "' or value like '" + textLike + "' and visible == 1");
        }
        LOGGER.trace("getByFrom text: {} by: {}", text, by);
        return by;
    }

    public Set<String> listAndroidDeviceFolderFiles(final String folderName) {
        final Set<String> results = new HashSet<>();
        try {
            final byte[] zippedFolder = getDriver().pullFolder(folderName);
            final ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(zippedFolder));
            ZipEntry entry = null;
            while ((entry = zipStream.getNextEntry()) != null) {
                results.add(entry.getName());
            }
        } catch (WebDriverException | IOException e) {
            LOGGER.error("Error at listAndroidDeviceFolderFiles, e: ", e);
        }
        return results;
    }

}
