package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.repository;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.repository.IngredientRepository;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefrigeratorRepositoryTest {

    @Container
    static final MariaDBContainer<?> mariadbContainer = new MariaDBContainer<>("mariadb:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        if (!mariadbContainer.isRunning()) {
            mariadbContainer.start();
        }
        registry.add("spring.datasource.url", mariadbContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariadbContainer::getUsername);
        registry.add("spring.datasource.password", mariadbContainer::getPassword);
    }

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    private Instant startTime;

    @BeforeAll
    @Transactional
    public void setUp() {
        for (int i = 10; i <= 100; i++) {
            Refrigerator refrigerator = Refrigerator.builder()
                    .memberId((long) i)
                    .build();
            refrigeratorRepository.save(refrigerator);

            Ingredient ingredient1 = Ingredient.builder()
                    .type(IngredientType.EGG)
                    .quantity(10)
                    .unitType(UnitType.COUNT)
                    .expiredDate(LocalDate.now().plusDays(7))
                    .refrigerator(refrigerator)
                    .build();

            Ingredient ingredient2 = Ingredient.builder()
                    .type(IngredientType.MILK)
                    .quantity(2)
                    .unitType(UnitType.LITER)
                    .expiredDate(LocalDate.now().plusDays(5))
                    .refrigerator(refrigerator)
                    .build();

            ingredientRepository.saveAll(List.of(ingredient1, ingredient2));
        }
    }

    @BeforeEach
    public void startTimer() {
        startTime = Instant.now();
    }

    @AfterEach
    public void stopTimer() {
        Instant endTime = Instant.now();
        long executionTime = endTime.toEpochMilli() - startTime.toEpochMilli();
        System.out.println("테스트 실행 시간: " + executionTime + "ms");
    }

    @Test
    @DisplayName("냉장고 조회시, n+1문제 발생 확인")
    public void testNPlusOneProblemInRefrigerators() {
        System.out.println("-------- 냉장고 전체 조회 요청 --------");
        List<Refrigerator> refrigerators = refrigeratorRepository.findAll();
        System.out.println("-------- 냉장고 조회 완료 --------");

        refrigerators.forEach(refrigerator -> {
            System.out.println("Refrigerator ID: " + refrigerator.getId());
            refrigerator.getIngredients().forEach(ingredient -> {
                System.out.println("  Ingredient Type: " + ingredient.getType() +
                        ", Quantity: " + ingredient.getQuantity() +
                        ", Unit: " + ingredient.getUnitType());
            });
        });
    }

    @Test
    @DisplayName("재료 조회시, n+1문제 발생 확인")
    public void testNPlusOneProblemInIngredients() {
        System.out.println("-------- 재료 전체 조회 요청 --------");
        List<Ingredient> ingredients = ingredientRepository.findAll();
        System.out.println("-------- 재료 조회 완료 --------");

        ingredients.forEach(ingredient -> {
            System.out.println("Ingredients ID: " + ingredient.getId());
        });
    }
}
