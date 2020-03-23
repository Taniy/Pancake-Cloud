package pancakes.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import pancakes.Ingredient;
import pancakes.Ingredient.*;
import pancakes.Order;
import pancakes.Pancake;
import pancakes.data.IngredientRepository;
import pancakes.data.PancakeRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignPancakeController {
    private final IngredientRepository ingredientRepo;
    private PancakeRepository designRepo;

    @Autowired
    public DesignPancakeController(IngredientRepository ingredientRepo, PancakeRepository designRepo) {
        this.ingredientRepo = ingredientRepo;
        this.designRepo = designRepo;
    }

    @ModelAttribute(name = "order")
    public Order order() {
        return new Order();
    }
    @ModelAttribute(name = "pancake")
    public Pancake pancake() {
        return new Pancake();
    }

    @GetMapping
    public String showDesignForm(Model model) {

        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepo.findAll().forEach(ingredients::add);

        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }
        model.addAttribute("design", new Pancake());
        return "design";
    }

    @PostMapping
    public String processDesign(@Valid @ModelAttribute Pancake design,
                                Errors errors,
                                @ModelAttribute Order order){
        if (errors.hasErrors()) {
            return "design";
        }
        Pancake saved = designRepo.save(design);
        order.addDesign(saved);
        log.info("Processing design: " + design);
        return "redirect:/orders/current";
    }

    private List<Ingredient> filterByType(
            List<Ingredient> ingredients, Type type) {
        return ingredients
                .stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }

}
