package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.platform.CatnipServices;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Unique;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

public class HeatedProcessingOutput extends ProcessingOutput {

    private static final Logger log = LoggerFactory.getLogger(HeatedProcessingOutput.class);
    private Pair<ResourceLocation, Integer> childCompatDatagenOutput;
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
        super(stack, chance);
        this.temperature = params.temperature;
        this.copyHeat = params.copyHeat;
        this.cooling = params.cooling;
    }

    public HeatedProcessingOutput(Pair<ResourceLocation, Integer> item, float chance, HeatedProcessingRecipeBuilder.HeatedIngridientParams params) {
        super(item,chance);
        this.childCompatDatagenOutput = item;
        this.temperature = params.temperature;
        this.copyHeat = params.copyHeat;
        this.cooling = params.cooling;
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
    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        ResourceLocation resourceLocation = this.childCompatDatagenOutput == null ? CatnipServices.REGISTRIES.getKeyOrThrow(this.getStack().getItem()) : this.childCompatDatagenOutput.getFirst();
        json.addProperty("item", resourceLocation.toString());
        int count = this.childCompatDatagenOutput == null ? this.getStack().getCount() : this.childCompatDatagenOutput.getSecond();
        if (count != 1) {
            json.addProperty("count", count);
        }

        if (this.getStack().hasTag()) {
            json.add("nbt", JsonParser.parseString(this.getStack().getTag().toString()));
        }

        if (this.getChance() != 1.0F) {
            json.addProperty("chance", this.getChance());
        }

        if (WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()){
            json.addProperty("temperature", this.getTemperature());
            json.addProperty("copy_heat",this.getCopyHeat());
            json.addProperty("cooling",this.getCooling());
        }
        return json;
    }

    public static HeatedProcessingOutput deserialize(JsonElement je) {
        WoodenCog.LOGGER.info("deserialize");

        if (!je.isJsonObject()) {
            throw new JsonSyntaxException("ProcessingOutput must be a json object");
        } else {
            JsonObject json = je.getAsJsonObject();
            String itemId = GsonHelper.getAsString(json, "item");
            int count = GsonHelper.getAsInt(json, "count", 1);
            float chance = GsonHelper.isValidNode(json, "chance") ? GsonHelper.getAsFloat(json, "chance") : 1.0F;

            WoodenCog.LOGGER.info("[WoodenCog] Create Resource Location from: " + itemId);
            try {
                ResourceLocation rl = new ResourceLocation(itemId);
            } catch (Exception e) {
                WoodenCog.LOGGER.error("[WoodenCog] Invalid Resource Location: " + itemId, e);
            }

            ItemLike item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
            if (item == null) {
                WoodenCog.LOGGER.error("[WoodenCog] Unknown item in registry: " + new ResourceLocation(itemId));
                return null;
            }

            ItemStack itemstack = new ItemStack(item, count);

            if (GsonHelper.isValidNode(json, "nbt")) {
                try {
                    JsonElement element = json.get("nbt");
                    itemstack.setTag(TagParser.parseTag(element.isJsonObject() ? Create.GSON.toJson(element) : GsonHelper.convertToString(element, "nbt")));
                } catch (CommandSyntaxException var7) {
                    WoodenCog.LOGGER.error(var7.getStackTrace().toString());
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

    @Override
    public void write(FriendlyByteBuf buf) {
        /*
        ItemStack stack = getStack();
        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(stack.getItem());

        try (FileWriter fw = new FileWriter("server_stack_output.log", true)) {
            fw.write("[SERVER] Writing stack: " + stack +
                    " | RegistryName: " + rl +
                    " | Count: " + stack.getCount() + "\n");
        } catch (IOException e) {
            WoodenCog.LOGGER.error("Error writing server log", e);
        }*/

        //super.write(buf);
        buf.writeItem(getStack());
        buf.writeFloat(getChance());

        buf.writeInt(getTemperature());
        buf.writeBoolean(getCopyHeat());
        buf.writeInt(getCooling());
    }

    public static HeatedProcessingOutput read(FriendlyByteBuf buf) {

        ItemStack stack = buf.readItem();
        float chance = buf.readFloat();
        int temperature = buf.readInt();
        boolean copyHeat = buf.readBoolean();
        int cooling = buf.readInt();

        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(stack.getItem());

        try (FileWriter fw = new FileWriter("client_stack_input.log", true)) {
            fw.write("[CLIENT] Reading stack: " + stack +
                    " | RegistryName: " + rl +
                    " | Count: " + stack.getCount() + "\n");
        } catch (IOException e) {
            WoodenCog.LOGGER.error("Error writing client log", e);
        }

        return new HeatedProcessingOutput(stack, chance, temperature, copyHeat, cooling);
    }
}
