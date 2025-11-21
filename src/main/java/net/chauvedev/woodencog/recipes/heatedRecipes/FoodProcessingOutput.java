package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FoodProcessingOutput extends ProcessingOutput {

    private final FoodData baseFoodData;
    private List<WoodenCogFoodPortion> portions;
    private List<ItemStack> dynamicUsedFoodItems;

    public static final int SOUP_HUNGER_VALUE = 4;
    public static final float SOUP_DECAY_MODIFIER = 3.5F;
    public static final float SALAD_DECAY_MODIFIER = 4.0F;

    public FoodProcessingOutput(ItemStack stack, float chance, FoodData baseFoodData) {
        super(stack, chance);
        this.baseFoodData = baseFoodData;
    }

}
