package net.chauvedev.woodencog.recipes.heatedRecipes.output;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.WoodenCogFoodPortion;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;

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
