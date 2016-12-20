import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BingTest {
    EventFiringWebDriver driver;
    JavascriptExecutor jse;
    WebDriverWait wait;
    Actions builder;

    @BeforeTest
    public void setup() {
        //get driver path property
        String driverPath = System.getProperty("user.dir") + "/driver/chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", driverPath);
        //initialise driver with new ChromeDriver
        driver = new EventFiringWebDriver(new ChromeDriver());
        //register the WebDriverEventListener
        driver.register(new EventHandler());
        //initialise the JavascriptExecutor object
        jse = (JavascriptExecutor) driver;
        //initialise the wait element
        wait = new WebDriverWait(driver, 10);
        //initialise the actions element
        builder = new Actions(driver);

        Reporter.setEscapeHtml(false);
    }

    @Test
    public void validateSearchPageTitle() {
        log("open main page");
        driver.navigate().to("https://www.bing.com/");

        log("wait the next navigation element by id");
        By images = By.id("scpl1");
        wait.until(ExpectedConditions.presenceOfElementLocated(images));
        log("find the 'Pictures' text link");
        WebElement pictureTextLink = driver.findElement(images);
        log("click the 'Pictures' text link");
        pictureTextLink.click();

        log("check page title");
        log("page title is '" + driver.getTitle() + "'");
        Assert.assertEquals(driver.getTitle(), "Лента изображений Bing", "Unexpected page title!");
    }

    @Test(dependsOnMethods = {"validateSearchPageTitle"})
    public void checkScrolling() {
        log("check scrolling");
        for (int i = 0; i < 3; i++) {
            final By imagesList = By.xpath("//div[@class='img_cont hoff']/img");
            //create the first list of the search results
            final List<WebElement> first = driver.findElements(imagesList);
            log("scroll to the bottom of the page");
            jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            //wait for loading new content
            wait.until(new Predicate<WebDriver>() {
                public boolean apply(WebDriver webDriver) {
                    return first.size() < driver.findElements(imagesList).size();
                }
            });
            //create the second list of the search results
            List<WebElement> second = driver.findElements(imagesList);
            //log the result with quantity of previous and last results
            log(String.format("The images are uploaded! The size before uploading is %d, the size after uploading is %d", first.size(), second.size()));
            Assert.assertTrue(first.size() < second.size(), "New images are not uploaded!");
        }
        log("scroll to the top of the page");
        jse.executeScript("window.scrollTo(0, 0)");
    }

    @Test(dependsOnMethods = {"checkScrolling"}, dataProvider = "keySearchString")
    public void checkLoadingImages(String text) {
        log("check loading images");
        //find the input search field by class name
        By inputBox = By.className("b_searchbox");
        wait.until(ExpectedConditions.presenceOfElementLocated(inputBox));
        WebElement input = driver.findElement(inputBox);
        //fill the input field
        log("fill the input box with '" + text + "'");
        if (!input.getAttribute("value").equals("")) {
            input.clear();
        }
        input.sendKeys(text);

        log("click the 'submit'");
        WebElement submit = driver.findElement(By.className("b_searchboxSubmit"));
        submit.click();

        log("focus on the first image");
        //Find and wait the pictures by xpath
        By image = By.xpath("//div[@id='dg_c']//img");
        wait.until(ExpectedConditions.presenceOfElementLocated(image));
        final WebElement imageElement = driver.findElement(image);
        final By bigImage = By.className("irhc");
        wait.until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver webDriver) {
                //Focus on the first picture
                builder.moveToElement(imageElement).build().perform();
                return driver.findElement(bigImage).isDisplayed();
            }
        });
        log("check that the enlarged image and its elements are shown");
        //check that the enlarged image is shown
        Assert.assertTrue(driver.findElements(bigImage).size() == 1, "The enlarged image is not displayed!");
        //check the "Add to collection" element
        Assert.assertTrue(driver.findElements(By.xpath("//span//*[@class='fav_active active_line']")).size() > 0, "The 'Add to collection' element is not displayed!");
        //check the icon for finding by image element
        By byImage = By.xpath("//span[@class='irhcsb']/img[1]");
        Assert.assertTrue(driver.findElements(byImage).size() == 1, "The icon for finding by image element is not displayed!");
        //check the icon for noting for adult
        Assert.assertTrue(driver.findElements(By.xpath("//span[@class='irhcsb']/img[2]")).size() == 1, "The icon for noting the image for adult is not displayed!");

        log("finish checking with the dataProvider param");
    }

    @Test(dependsOnMethods = {"checkLoadingImages"}, parameters = {"imagesQuantity"})
    public void checkImagesQuantity(int imagesQuantity) {

        //check the icon for finding by image element
        By byImage = By.xpath("//span[@class='irhcsb']/img[1]");
        log("click the 'Search by image' icon");
        WebElement byImageElement = driver.findElement(byImage);
        byImageElement.click();

        //wait for the main image
        By mainImage = By.xpath("//img[@class='mainImage accessible nofocus']");
        wait.until(ExpectedConditions.presenceOfElementLocated(mainImage));
        Assert.assertTrue(driver.findElements(mainImage).size() == 1, "The slideshow is not loaded!");

        log("click the 'Other images' text link");
        By otherImagesButton = By.xpath("//div[@class ='expandButton clickable active']/span");
        WebElement clickOtherImages = driver.findElement(otherImagesButton);
        clickOtherImages.click();

        log("sum of the quantity of images");
        By allImages = By.className("mimg");
        List<WebElement> allImagesElement = driver.findElements(allImages);

        Assert.assertTrue(allImagesElement.size() >= imagesQuantity, String.format("The quantity of images is %d, expected quantity is not less then %d", allImagesElement.size(), imagesQuantity));
        log("finish checking with the Parameters param");
    }

    @AfterTest
    public void tearDown() {
        //quit the google driver
        driver.quit();
    }

    private void log(String message) {
        Reporter.log(message + "<br>");
    }

    @DataProvider
    public Object[][] keySearchString() {
        //read the file
        ArrayList<String> words = new ArrayList<String>();
        Scanner scn = null;
        try {
            scn = new Scanner(new File("src/main/resources/words.txt"));
        } catch (FileNotFoundException e) {
            log(e.getMessage());
            e.printStackTrace();
        }
        while (scn.hasNext()) {
            words.add(scn.nextLine());
        }
        Object[][] result = new Object[words.size()][1];
        for (int i = 0; i < words.size(); i++) {
            result[i][0] = words.get(i);
        }
        scn.close();
        return result;
    }
}
