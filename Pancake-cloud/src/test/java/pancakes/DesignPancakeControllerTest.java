package pancakes;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import pancakes.Ingredient.Type;
import pancakes.data.IngredientRepository;
import pancakes.data.OrderRepository;
import pancakes.data.PancakeRepository;
import pancakes.web.DesignPancakeController;

@RunWith(SpringRunner.class)
@WebMvcTest(DesignPancakeController.class)
public class DesignPancakeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private List<Ingredient> ingredients;

  private Pancake design;

  @MockBean
  private IngredientRepository ingredientRepository;

  @MockBean
  private PancakeRepository designRepository;

  @MockBean
  private OrderRepository orderRepository;

  @Before
  public void setup() {
    ingredients = Arrays.asList(
            new Ingredient("IKRA", "Икра", Type.IKRA),
    new Ingredient("SGUH", "Сгущенка", Type.SGUHA),
    new Ingredient("KLUB", "Клубничка", Type.KLUBNIKA),
    new Ingredient("KLKO", "Клубничка и кокос", Type.KLUBNIKA),
    new Ingredient("VETC", "Ветчина", Type.VETCHINA)
    );

    when(ingredientRepository.findAll())
        .thenReturn(ingredients);

    when(ingredientRepository.findById("SGUH"))
            .thenReturn(Optional.of(new Ingredient("SGUH", "Сгущенка", Type.SGUHA)));

    design = new Pancake();
    design.setName("Test Pancake");

    design.setIngredients(Arrays.asList(
            new Ingredient("KLUB", "Клубничка", Type.KLUBNIKA),
            new Ingredient("KLKO", "Клубничка и кокос", Type.KLUBNIKA),
            new Ingredient("VETC", "Ветчина", Type.VETCHINA)
  ));

  }

  @Test
  public void testShowDesignForm() throws Exception {
    mockMvc.perform(get("/design"))
        .andExpect(status().isOk())
        .andExpect(view().name("design"))
        .andExpect(model().attribute("ikra", ingredients.subList(0, 1)))
        .andExpect(model().attribute("sguha", ingredients.subList(1, 2)))
        .andExpect(model().attribute("klubnika", ingredients.subList(2, 4)));
  }

  @Test
  public void processDesign() throws Exception {
    when(designRepository.save(design))
        .thenReturn(design);

    mockMvc.perform(post("/design")
        .content("name=Test+Pancake&ingredients=IKRA,KLBO,KLKO")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().stringValues("Location", "/orders/current"));
  }

}
