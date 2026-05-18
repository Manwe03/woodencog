package net.chauvedev.woodencog.recipes.heatedRecipes.output;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.component.item.ItemListComponent;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class BowlProcessingOutput extends DynamicProcessingOutput<List<ItemStack>> {

    public static final int HUNGER_VALUE = 4;

    public BowlProcessingOutput(ProcessingOutput internal) {
        super(internal);
    }

    public BowlProcessingOutput(Item outputBowl, int count, float chance) {
        super(new ProcessingOutput(outputBowl,count,chance));
    }

    public BowlProcessingOutput(ItemStack outputBowl, float chance) {
        this(new ProcessingOutput(outputBowl,chance));
    }

    /**
     * Used only for JEI
     * @return all posible results for this recipe
     */
    public abstract List<ItemStack> getStacks();

    public ItemStack getBowlItem(Map<Nutrient, TFCItems.ItemId> map, float decayModifier){
        List<ItemStack> usedItems = this.getDynamicData();
        usedItems.sort(Comparator.comparing(ItemStack::getCount)
                .thenComparing((itemx) -> BuiltInRegistries.ITEM.getKey(itemx.getItem())));

        int ingredientCount = 0;
        float water = 20, saturation = 2;
        float[] nutrition = new float[Nutrient.TOTAL];
        ItemStack resultStack = ItemStack.EMPTY;

        // ingredientes recibidos
        final List<ItemStack> itemIngredients = new ArrayList<>();

        for (ItemStack stack : usedItems) {
            final @Nullable IFood food = FoodCapability.get(stack);
            if (food != null) {
                itemIngredients.add(stack);
                if (food.isRotten()) {
                    ingredientCount = 0;
                    break;
                }
                final FoodData data = food.getData();
                water += data.water();
                saturation += data.saturation();
                for (Nutrient nutrient : Nutrient.VALUES) {
                    nutrition[nutrient.ordinal()] += data.nutrient(nutrient);
                }
                ingredientCount++;
            }
        }

        if (ingredientCount > 0) {
            float multiplier = 1 - (0.05f * ingredientCount);
            water *= multiplier;
            saturation *= multiplier;

            Nutrient maxNutrient = Nutrient.GRAIN;
            float maxNutrientValue = 0.0F;

            for (Nutrient nutrient : Nutrient.VALUES) {
                final int idx = nutrient.ordinal();
                nutrition[idx] *= multiplier;

                if (nutrition[idx] > maxNutrientValue) {
                    maxNutrientValue = nutrition[idx];
                    maxNutrient = nutrient;
                }
            }

            FoodData data = new FoodData(HUNGER_VALUE, water, saturation, 0, nutrition, decayModifier);

            long created = FoodCapability.getRoundedCreationDate();


            resultStack = new ItemStack(map.get(maxNutrient).get(), this.getStack().getCount());

            FoodCapability.setFoodForDynamicItemOnCreate(resultStack, data);
            resultStack.set(TFCComponents.INGREDIENTS, ItemListComponent.of(itemIngredients));
            FoodCapability.setCreationDate(resultStack,created);

            /* TODO es necesario con la nueva version?
            CompoundTag bowlTag = new CompoundTag();
            bowlTag.putString("id", BuiltInRegistries.ITEM.getKey(getStack().getItem()).toString());
            bowlTag.putByte("Count", (byte) 1);

            CompoundTag custom = resultStack.getOrCreateTag();
            custom.put("bowl", bowlTag);*/
        }

        return resultStack;
    }
}
