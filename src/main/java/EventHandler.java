import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

public class EventHandler implements WebDriverEventListener {
    public void beforeNavigateTo(String s, WebDriver webDriver) {
        System.out.println("beforeNavigateTo: " + s);
    }

    public void afterNavigateTo(String s, WebDriver webDriver) {
        System.out.println("afterNavigateTo: " + s);
    }

    public void beforeNavigateBack(WebDriver webDriver) {
        System.out.println("beforeNavigateBack");
    }

    public void afterNavigateBack(WebDriver webDriver) {
        System.out.println("afterNavigateBack");
    }

    public void beforeNavigateForward(WebDriver webDriver) {
        System.out.println("beforeNavigateForward");
    }

    public void afterNavigateForward(WebDriver webDriver) {
        System.out.println("afterNavigateForward");
    }

    public void beforeNavigateRefresh(WebDriver webDriver) {
        System.out.println("beforeNavigateRefresh");
    }

    public void afterNavigateRefresh(WebDriver webDriver) {
        System.out.println("afterNavigateRefresh");
    }

    public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {
        System.out.println("looking for element: " + by);
    }

    public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {
        System.out.println("found element: " + by);
    }

    public void beforeClickOn(WebElement webElement, WebDriver webDriver) {
        System.out.println("beforeClickOn: " + webElement.getText());
    }

    public void afterClickOn(WebElement webElement, WebDriver webDriver) {
        System.out.println("afterClickOn: " + webElement);
    }

    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
        System.out.println("beforeChangeValueOf: " + webElement);
    }

    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
        System.out.println("afterChangeValueOf: " + webElement);
    }

    public void beforeScript(String s, WebDriver webDriver) {
        System.out.println("beforeScript: " + s);
    }

    public void afterScript(String s, WebDriver webDriver) {
        System.out.println("afterScript: " + s);
    }

    public void onException(Throwable throwable, WebDriver webDriver) {
        System.err.println("The element is not found yet");
    }
}
