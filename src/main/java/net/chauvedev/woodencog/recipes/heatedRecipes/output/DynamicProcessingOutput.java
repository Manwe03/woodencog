package net.chauvedev.woodencog.recipes.heatedRecipes.output;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Random;

/**
 * This class obstracts outputs that need world information before calling rollOutput()
 * setDynamicData() must be called before rollOutput()
 * @param <T> type of the dynamic data
 */
public abstract class DynamicProcessingOutput<T> extends ProcessingOutput {

    public static final DynamicProcessingOutput<?> EMPTY;

    private T data;

    public DynamicProcessingOutput(ItemStack stack, float chance) {
        super(stack, chance);
    }

    public void setDynamicData(T data){
        this.data = data;
    }

    public T getDynamicData(){
        return data;
    }

    public abstract ProcessingOutputTypes getType();

    public static <T> void setDynamic(DynamicProcessingOutput<T> output, T data) {
        output.setDynamicData(data);
    }

    @SuppressWarnings("unchecked")
    public static <T> void setDynamicData(DynamicProcessingOutput<T> output, Object data) {
        output.setDynamicData((T) data);
    }

    /**
     * Stores type of output
     * @return
     */
    @Override
    public JsonElement serialize() {
        JsonObject json = (JsonObject) super.serialize();
        json.addProperty("type", this.getType().name().toLowerCase());
        return json;
    }

    /**
     * All write overrides must call super first
     * @param buf
     */
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.getType().ordinal()); //Save type before anyThing
        super.write(buf);
    }

    public static DynamicProcessingOutput<?> deserialize(JsonElement je) {
        String type = GsonHelper.getAsString((JsonObject) je, "type").toUpperCase();
        WoodenCog.LOGGER.info("DynamicProcessingOutput deserialize json type: "+type);
        switch (ProcessingOutputTypes.valueOf(type)){
            case SALAD -> {
                return SaladProcessingOutput.deserialize(je);
            }
            case SOUP -> {
                return SoupProcessingOutput.deserialize(je);
            }
            case FOOD -> {
                return FoodProcessingOutput.deserialize(je);
            }
            case HEATED -> {
                return HeatedProcessingOutput.deserialize(je);
            }
            default -> {
                WoodenCog.LOGGER.warn("Could not deserialize output in recipe:\n"+je.getAsString());
                return null;
            }
        }
    }

    public static DynamicProcessingOutput<?> read(FriendlyByteBuf buf) {
        switch (ProcessingOutputTypes.values()[buf.readInt()]){
            case SALAD -> {
                return SaladProcessingOutput.read(buf);
            }
            case SOUP -> {
                return SoupProcessingOutput.read(buf);
            }
            case FOOD -> {
                return FoodProcessingOutput.read(buf);
            }
            case HEATED -> {
                return HeatedProcessingOutput.read(buf);
            }
            default -> {
                WoodenCog.LOGGER.warn("Could not read output in recipe");
                return null;
            }
        }
    }

    public enum ProcessingOutputTypes{
        HEATED,
        FOOD,
        SALAD,
        SOUP
    }

    static {
        EMPTY = new DynamicProcessingOutput<>(ItemStack.EMPTY, 1.0F) {
            @Override
            public ProcessingOutputTypes getType() {
                return null;
            }
        };
    }
}
