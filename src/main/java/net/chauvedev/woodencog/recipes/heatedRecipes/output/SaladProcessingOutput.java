package net.chauvedev.woodencog.recipes.heatedRecipes.output;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.dries007.tfc.common.capabilities.food.*;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SaladProcessingOutput extends BowlProcessingOutput{
    public static final float SALAD_DECAY_MODIFIER = 4.0F;

    public SaladProcessingOutput(Item bowlOutput, int count, float chance) {
        super(bowlOutput, count, chance);
    }

    public SaladProcessingOutput(ItemStack bowlOutput, float chance) {
        super(bowlOutput, chance);
    }

    @Override
    public List<ItemStack> getStacks() {
        return TFCItems.SALADS.values().stream().map(ro -> new ItemStack(ro.get())).toList();
    }

    @Override
    public ItemStack rollOutput() {
        return this.getBowlItem(TFCItems.SALADS, SALAD_DECAY_MODIFIER);
    }

    @Override
    public ItemStack getBowlItem(Map<Nutrient, RegistryObject<Item>> map, float decayModifier) {
        List<ItemStack> usedItems = this.getDynamicData();
        usedItems.sort(Comparator.comparing(ItemStack::getCount)
                .thenComparing((itemx) -> BuiltInRegistries.ITEM.getKey(itemx.getItem())));

        int ingredientCount = 0;
        float water = 0.0F;
        float saturation = 0.0F;
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
                water += data.water()*stack.getCount();
                saturation += data.saturation()*stack.getCount();
                for (Nutrient nutrient : Nutrient.VALUES) {
                    nutrition[nutrient.ordinal()] += data.nutrient(nutrient)*stack.getCount();
                }
                ingredientCount++;

            }
        }

        if (ingredientCount > 0) {
            final float multiplier = 0.75f; // salad multiplier
            water *= multiplier;
            saturation *= multiplier;

            Nutrient maxNutrient = null;
            float maxNutrientValue = 0;
            for (Nutrient nutrient : Nutrient.values()) {
                nutrition[nutrient.ordinal()] *= multiplier;
                if (nutrition[nutrient.ordinal()] > maxNutrientValue)
                {
                    maxNutrientValue = nutrition[nutrient.ordinal()];
                    maxNutrient = nutrient;
                }
            }

            if (maxNutrient != null) {
                resultStack = new ItemStack(TFCItems.SALADS.get(maxNutrient).get(), getStack().getCount());
                final @Nullable IFood saladCap = FoodCapability.get(resultStack);
                if (saladCap instanceof DynamicBowlHandler handler)
                {
                    handler.setCreationDate(FoodCapability.getRoundedCreationDate());
                    handler.setIngredients(itemIngredients);
                    handler.setBowl(getStack());
                    handler.setFood(FoodData.create(4, water, saturation, nutrition, 4.0f));
                }
            }
        }
        return resultStack;
    }

    @Override
    public ProcessingOutputTypes getType() {
        return ProcessingOutputTypes.SALAD;
    }

    public static SaladProcessingOutput deserialize(JsonElement je) {
        if (!je.isJsonObject()) {
            throw new JsonSyntaxException("ProcessingOutput must be a json object");
        } else {
            JsonObject json = je.getAsJsonObject();
            String itemId = GsonHelper.getAsString(json, "item");
            int count = GsonHelper.getAsInt(json, "count", 1);
            float chance = GsonHelper.getAsFloat(json, "chance", 1F);
            return new SaladProcessingOutput(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)), count, chance);
        }
    }

    public static SaladProcessingOutput read(FriendlyByteBuf buf) {
        ItemStack itemstack = buf.readItem();
        float chance = buf.readFloat();
        return new SaladProcessingOutput(itemstack, chance);
    }
}
