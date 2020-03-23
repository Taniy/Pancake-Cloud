package pancakes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DesignAndOrderPancakesBrowserTest {
  
  private static HtmlUnitDriver browser;
  
  @LocalServerPort
  private int port;
  
  @Autowired
  TestRestTemplate rest;
  
  @BeforeClass
  public static void setup() {
    browser = new HtmlUnitDriver();
    browser.manage().timeouts()
        .implicitlyWait(10, TimeUnit.SECONDS);
  }
  
  @AfterClass
  public static void closeBrowser() {
    browser.quit();
  }
  
  @Test
  public void testDesignAPancakePage_HappyPath() throws Exception {
    browser.get(homePageUrl());
    clickDesignAPancake();
    assertDesignPageElements();
    buildAndSubmitAPancake("Basic Pancake", "IKRA", "SGUH", "KLUB");
    clickBuildAnotherPancake();
    buildAndSubmitAPancake("Another Pancake", "IKRA", "SGUH", "KLKO");
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
  }
  
  @Test
  public void testDesignAPancakePage_EmptyOrderInfo() throws Exception {
    browser.get(homePageUrl());
    clickDesignAPancake();
    assertDesignPageElements();
    buildAndSubmitAPancake("Basic Pancake", "IKRA", "SGUH", "KLUB");
    submitEmptyOrderForm();
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
  }

  @Test
  public void testDesignAPancakePage_InvalidOrderInfo() throws Exception {
    browser.get(homePageUrl());
    clickDesignAPancake();
    assertDesignPageElements();
    buildAndSubmitAPancake("Basic Pancake", "IKRA", "SGUH", "KLUB");
    submitInvalidOrderForm();
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
  }

  //
  // Browser test action methods
  //
  private void buildAndSubmitAPancake(String name, String... ingredients) {
    assertDesignPageElements();

    for (String ingredient : ingredients) {
      browser.findElementByCssSelector("input[value='" + ingredient + "']").click();      
    }
    browser.findElementByCssSelector("input#name").sendKeys(name);
    browser.findElementByCssSelector("form").submit();
  }

  private void assertDesignPageElements() {
    assertEquals(designPageUrl(), browser.getCurrentUrl());
    List<WebElement> ingredientGroups = browser.findElementsByClassName("ingredient-group");
    assertEquals(3, ingredientGroups.size());
    
    WebElement ikraGroup = browser.findElementByCssSelector("div.ingredient-group#ikra");
    List<WebElement> ikra = ikraGroup.findElements(By.tagName("div"));
    assertEquals(1, ikra.size());
    assertIngredient(ikraGroup, 0, "IKRA", "Икра");

    WebElement sguhaGroup = browser.findElementByCssSelector("div.ingredient-group#sguha");
    List<WebElement> sguha = sguhaGroup.findElements(By.tagName("div"));
    assertEquals(1, sguha.size());
    assertIngredient(sguhaGroup, 0, "SGUH", "Сгущенка");

    WebElement klubnikaGroup = browser.findElementByCssSelector("div.ingredient-group#klubnika");
    List<WebElement> klubnika = klubnikaGroup.findElements(By.tagName("div"));
    assertEquals(2, klubnika.size());
    assertIngredient(klubnikaGroup, 0, "KLUB", "Клубничка");
    assertIngredient(klubnikaGroup, 1, "KLKO", "Клубничка и кокос");
  }
  

  private void fillInAndSubmitOrderForm() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    fillField("input#name", "Ima Hungry");
    fillField("input#street", "1234 Culinary Blvd.");
    fillField("input#city", "Foodsville");
    fillField("input#state", "CO");
    fillField("input#zip", "81019");
    fillField("input#ccNumber", "4111111111111111");
    fillField("input#ccExpiration", "10/19");
    fillField("input#ccCVV", "123");
    browser.findElementByCssSelector("form").submit();
  }

  private void submitEmptyOrderForm() {
    assertEquals(currentOrderDetailsPageUrl(), browser.getCurrentUrl());
    browser.findElementByCssSelector("form").submit();
    
    assertEquals(orderDetailsPageUrl(), browser.getCurrentUrl());

    List<String> validationErrors = getValidationErrorTexts();
    assertEquals(9, validationErrors.size());
    assertTrue(validationErrors.contains("Please correct the problems below and resubmit."));
    assertTrue(validationErrors.contains("Name is required"));
    assertTrue(validationErrors.contains("Street is required"));
    assertTrue(validationErrors.contains("City is required"));
    assertTrue(validationErrors.contains("State is required"));
    assertTrue(validationErrors.contains("Zip code is required"));
    assertTrue(validationErrors.contains("Not a valid credit card number"));
    assertTrue(validationErrors.contains("Must be formatted MM/YY"));
    assertTrue(validationErrors.contains("Invalid CVV"));
  }

  private List<String> getValidationErrorTexts() {
    List<WebElement> validationErrorElements = browser.findElementsByClassName("validationError");
    List<String> validationErrors = validationErrorElements.stream()
        .map(el -> el.getText())
        .collect(Collectors.toList());
    return validationErrors;
  }

  private void submitInvalidOrderForm() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    fillField("input#name", "I");
    fillField("input#street", "1");
    fillField("input#city", "F");
    fillField("input#state", "C");
    fillField("input#zip", "8");
    fillField("input#ccNumber", "1234432112344322");
    fillField("input#ccExpiration", "14/91");
    fillField("input#ccCVV", "1234");
    browser.findElementByCssSelector("form").submit();
    
    assertEquals(orderDetailsPageUrl(), browser.getCurrentUrl());

    List<String> validationErrors = getValidationErrorTexts();
    assertEquals(4, validationErrors.size());
    assertTrue(validationErrors.contains("Please correct the problems below and resubmit."));
    assertTrue(validationErrors.contains("Not a valid credit card number"));
    assertTrue(validationErrors.contains("Must be formatted MM/YY"));
    assertTrue(validationErrors.contains("Invalid CVV"));    
  }

  private void fillField(String fieldName, String value) {
    WebElement field = browser.findElementByCssSelector(fieldName);
    field.clear();
    field.sendKeys(value);
  }
  
  private void assertIngredient(WebElement ingredientGroup, 
                                int ingredientIdx, String id, String name) {
    List<WebElement> proteins = ingredientGroup.findElements(By.tagName("div"));
    WebElement ingredient = proteins.get(ingredientIdx);
    assertEquals(id, 
        ingredient.findElement(By.tagName("input")).getAttribute("value"));
    assertEquals(name, 
        ingredient.findElement(By.tagName("span")).getText());
  }

  private void clickDesignAPancake() {
    assertEquals(homePageUrl(), browser.getCurrentUrl());
    browser.findElementByCssSelector("a[id='design']").click();
  }

  private void clickBuildAnotherPancake() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    browser.findElementByCssSelector("a[id='another']").click();
  }

 
  //
  // URL helper methods
  //
  private String designPageUrl() {
    return homePageUrl() + "design";
  }

  private String homePageUrl() {
    return "http://localhost:" + port + "/";
  }

  private String orderDetailsPageUrl() {
    return homePageUrl() + "orders";
  }

  private String currentOrderDetailsPageUrl() {
    return homePageUrl() + "orders/current";
  }

}
