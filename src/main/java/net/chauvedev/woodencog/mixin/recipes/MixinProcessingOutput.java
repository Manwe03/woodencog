package net.chauvedev.woodencog.mixin.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingOutput;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.*;

@Mixin(value = ProcessingOutput.class, remap = false)
public class MixinProcessingOutput {

    /**
     * @author Manwe
     * @reason Adds temperature value deserialization
     * @implNote Returns CustomProcessingOutput if WoodenCog handles temperature with temperature and copyheat values
     */
    @Overwrite
    public static ProcessingOutput deserialize(JsonElement jsonElement) {
        if (!jsonElement.isJsonObject()) {
            throw new JsonSyntaxException("ProcessingOutput must be a json object");
        } else {
            JsonObject json = jsonElement.getAsJsonObject();
            String itemId = GsonHelper.getAsString(json, "item");
            int count = GsonHelper.getAsInt(json, "count", 1);
            float chance = GsonHelper.isValidNode(json, "chance") ? GsonHelper.getAsFloat(json, "chance") : 1.0F;

            ItemStack itemstack = new ItemStack((ItemLike) ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId)), count);
            if (GsonHelper.isValidNode(json, "nbt")) {
                try {
                    JsonElement element = json.get("nbt");
                    itemstack.setTag(TagParser.parseTag(element.isJsonObject() ? Create.GSON.toJson(element) : GsonHelper.convertToString(element, "nbt")));
                } catch (CommandSyntaxException var7) {
                    var7.printStackTrace();
                }
            }
            if (WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()){
                float temperature = GsonHelper.getAsFloat(json,"temperature",0.0f);
                boolean copy = GsonHelper.getAsBoolean(json,"copyheat",false);
                float cooling = GsonHelper.getAsFloat(json,"cooling",0.0f);
                return new HeatedProcessingOutput(itemstack, chance, temperature, copy, cooling);
            }

            return new ProcessingOutput(itemstack, chance);
        }
    }
}
