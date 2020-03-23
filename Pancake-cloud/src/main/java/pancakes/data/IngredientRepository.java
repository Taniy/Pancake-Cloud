package pancakes.data;

import org.springframework.data.repository.CrudRepository;

import pancakes.Ingredient;

public interface IngredientRepository 
         extends CrudRepository<Ingredient, String> {

}
