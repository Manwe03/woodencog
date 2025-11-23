package net.chauvedev.woodencog.recipes.heatedRecipes.output;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class HeatedProcessingOutput extends DynamicProcessingOutput<Float> {

    private final int temperature;
    private final boolean copyHeat;
    private final int cooling;

    public HeatedProcessingOutput(ItemStack stack, float chance, int temperature, boolean copyHeat, int cooling) {
        super(stack, chance);
        this.temperature = temperature;
        this.copyHeat = copyHeat;
        this.cooling = cooling;
    }

    public HeatedProcessingOutput(ItemStack stack, float chance, HeatedProcessingRecipeBuilder.HeatedIngridientParams params) {
        this(stack, chance, params.temperature, params.copyHeat, params.cooling);
    }

    public static HeatedProcessingOutput of(Item item, int count, int chance, int temperature, boolean copyheat, int cooling){
        return new HeatedProcessingOutput(new ItemStack(item,count),chance,temperature,copyheat,cooling);
    }

    @Override
    public ProcessingOutputTypes getType() {
        return ProcessingOutputTypes.HEATED;
    }

    public int getTemperature() {
        return temperature;
    }
    public boolean getCopyHeat(){
        return copyHeat;
    }
    public int getCooling(){
        return cooling;
    }

    /**
     * Returns the itemStack with the applied capability
     */
    @Override
    public ItemStack getStack() {
        ItemStack itemStack = super.getStack();
        if(!this.copyHeat){
            HeatCapability.setTemperature(itemStack,this.temperature);
        }
        return itemStack;
    }

    @Override
    public ItemStack rollOutput() {
        ItemStack outputStack = super.rollOutput();
        if(WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()){
            HeatCapability.setTemperature(outputStack,this.getTemperature());
            if(this.getCopyHeat()) { //If copy input item heat - cooling
                HeatCapability.setTemperature(outputStack, this.getDynamicData() - this.getCooling());
            }
        }
        return outputStack;
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = (JsonObject) super.serialize();
        json.addProperty("temperature", this.getTemperature());
        json.addProperty("copy_heat",this.getCopyHeat());
        json.addProperty("cooling",this.getCooling());
        return json;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeInt(getTemperature());
        buf.writeBoolean(getCopyHeat());
        buf.writeInt(getCooling());
    }

    public static HeatedProcessingOutput deserialize(JsonElement je) {

        if (!je.isJsonObject()) {
            throw new JsonSyntaxException("ProcessingOutput must be a json object");
        } else {
            JsonObject json = je.getAsJsonObject();
            String itemId = GsonHelper.getAsString(json, "item");
            int count = GsonHelper.getAsInt(json, "count", 1);
            float chance = GsonHelper.isValidNode(json, "chance") ? GsonHelper.getAsFloat(json, "chance") : 1.0F;

            WoodenCog.LOGGER.info("[WoodenCog] Create Resource Location from: " + itemId);
            try {
                ResourceLocation rl = ResourceLocation.tryParse(itemId);
            } catch (Exception e) {
                WoodenCog.LOGGER.error("[WoodenCog] Invalid Resource Location: " + itemId, e);
            }

            ItemLike item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(itemId));
            if (item == null) {
                WoodenCog.LOGGER.error("[WoodenCog] Unknown item in registry: " + ResourceLocation.tryParse(itemId));
                return null;
            }

            ItemStack itemstack = new ItemStack(item, count);

            if (GsonHelper.isValidNode(json, "nbt")) {
                try {
                    JsonElement element = json.get("nbt");
                    itemstack.setTag(TagParser.parseTag(element.isJsonObject() ? Create.GSON.toJson(element) : GsonHelper.convertToString(element, "nbt")));
                } catch (CommandSyntaxException var7) {
                    WoodenCog.LOGGER.error(Arrays.toString(var7.getStackTrace()));
                }
            }

            int temperature = 0;
            boolean copyHeat = false;
            int cooling = 0;
            try {
                temperature = GsonHelper.getAsInt(json, "temperature");
            }catch (JsonSyntaxException ignored){}
            try {
                copyHeat = GsonHelper.getAsBoolean(json, "copy_heat");
            }catch (JsonSyntaxException ignored){}
            try {
                cooling = GsonHelper.getAsInt(json, "cooling");
            }catch (JsonSyntaxException ignored){}

            return new HeatedProcessingOutput(itemstack, chance, temperature, copyHeat, cooling);
        }
    }

    public static HeatedProcessingOutput read(FriendlyByteBuf buf) {

        ItemStack stack = buf.readItem();
        float chance = buf.readFloat();
        int temperature = buf.readInt();
        boolean copyHeat = buf.readBoolean();
        int cooling = buf.readInt();

        return new HeatedProcessingOutput(stack, chance, temperature, copyHeat, cooling);
    }
}
