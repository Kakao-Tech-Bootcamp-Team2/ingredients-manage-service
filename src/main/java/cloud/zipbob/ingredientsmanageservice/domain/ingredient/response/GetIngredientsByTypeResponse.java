package cloud.zipbob.ingredientsmanageservice.domain.ingredient.response;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetIngredientsByTypeResponse implements Serializable {
    private List<IngredientType> ingredients;

    public static GetIngredientsByTypeResponse of(List<IngredientType> ingredients) {
        return new GetIngredientsByTypeResponse(ingredients);
    }
}
