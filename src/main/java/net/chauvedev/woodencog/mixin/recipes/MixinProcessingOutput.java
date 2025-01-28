package net.chauvedev.woodencog.mixin.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ProcessingOutput.class, remap = false)
public class MixinProcessingOutput {

    @Shadow
    @Final
    private ItemStack stack;

    @Shadow
    @Final
    private float chance;

    @Shadow private Pair<ResourceLocation, Integer> compatDatagenOutput;

    @Unique
    private float temperature;

    // Sobrescribir el método serialize
    /**
     * @author
     * @reason
     */
    @Overwrite
    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        ResourceLocation resourceLocation = this.compatDatagenOutput == null ? RegisteredObjects.getKeyOrThrow(this.stack.getItem()) : (ResourceLocation)this.compatDatagenOutput.getFirst();
        json.addProperty("item", resourceLocation.toString());
        int count = this.compatDatagenOutput == null ? this.stack.getCount() : (Integer)this.compatDatagenOutput.getSecond();
        if (count != 1) {
            json.addProperty("count", count);
        }

        if (this.stack.hasTag()) {
            json.add("nbt", JsonParser.parseString(this.stack.getTag().toString()));
        }

        if (this.chance != 1.0F) {
            json.addProperty("chance", this.chance);
        }

        if (this.stack.hasTag()) {
            json.addProperty("temperature", stack.getTag().getFloat("temperature"));
        }

        return json;
    }

    // Sobrescribir el método deserialize
    /**
     * @author
     * @reason
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


            if (json.has("temperature")) {
                float temperature = json.get("temperature").getAsFloat();
                itemstack.getOrCreateTag().putFloat("temperature", temperature);
            }

            return new ProcessingOutput(itemstack, chance);
        }
    }

    @Inject(method = "write", at = @At("TAIL"))
    public void write(FriendlyByteBuf buf, CallbackInfo ci) {
        buf.writeFloat(this.temperature);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static ProcessingOutput read(FriendlyByteBuf buf) {
        ItemStack stack = buf.readItem();
        float chance = buf.readFloat();
        ProcessingOutput output = new ProcessingOutput(stack, chance);
        ((MixinProcessingOutput) (Object) output).setTemperature(buf.readFloat());
        return output;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
