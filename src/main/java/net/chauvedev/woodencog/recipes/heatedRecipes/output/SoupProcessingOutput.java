package net.chauvedev.woodencog.recipes.heatedRecipes.output;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.chauvedev.woodencog.recipes.heatedRecipes.WoodenCogFoodPortion;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SoupProcessingOutput extends BowlProcessingOutput{
    public static final float SOUP_DECAY_MODIFIER = 3.5F;

    public SoupProcessingOutput(Item outputBowl, int count, float chance) {
        super(outputBowl, count, chance);
    }

    public SoupProcessingOutput(ItemStack outputBowl, float chance) {
        super(outputBowl, chance);
    }

    @Override
    public ItemStack rollOutput() {
        return this.getBowlItem(TFCItems.SOUPS, SOUP_DECAY_MODIFIER);
    }

    @Override
    public ProcessingOutputTypes getType() {
        return ProcessingOutputTypes.SOUP;
    }

    public static SoupProcessingOutput deserialize(JsonElement je) {
        if (!je.isJsonObject()) {
            throw new JsonSyntaxException("ProcessingOutput must be a json object");
        } else {
            JsonObject json = je.getAsJsonObject();
            String itemId = GsonHelper.getAsString(json, "item");
            int count = GsonHelper.getAsInt(json, "count", 1);
            float chance = GsonHelper.getAsFloat(json, "chance", 1F);
            return new SoupProcessingOutput(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)), count, chance);
        }
    }

    public static SoupProcessingOutput read(FriendlyByteBuf buf) {
        ItemStack itemstack = buf.readItem();
        float chance = buf.readFloat();
        return new SoupProcessingOutput(itemstack, chance);
    }
}
