package pancakes;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pancakes.Ingredient.Type;
import pancakes.data.IngredientRepository;


@SpringBootApplication
public class PancakeCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(PancakeCloudApplication.class, args);
	}
}