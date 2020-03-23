package pancakes;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import pancakes.data.IngredientRepository;
import pancakes.data.UserRepository;

@Profile("!prod")
@Configuration
public class DevelopmentConfig {

  @Bean
  public CommandLineRunner dataLoader(IngredientRepository repo,
                                      UserRepository userRepo, PasswordEncoder encoder) {
    return new CommandLineRunner() {
      @Override
      public void run(String... args) throws Exception {
        repo.save(new Ingredient("IKRA", "Икра", Ingredient.Type.IKRA));
        repo.save(new Ingredient("SGUH", "Сгущенка", Ingredient.Type.SGUHA));
        repo.save(new Ingredient("KLUB", "Клубничка", Ingredient.Type.KLUBNIKA));
        repo.save(new Ingredient("KLKO", "Клубничка и кокос", Ingredient.Type.KLUBNIKA));
        repo.save(new Ingredient("VETC", "Ветчина", Ingredient.Type.VETCHINA));

        userRepo.save(new User("habuma", encoder.encode("password"),
                "Craig Walls", "123 North Street", "Cross Roads", "TX",
                "76227", "123-123-1234"));
    };
  }
  
}
