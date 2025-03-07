package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public class HeatedProcessingOutput extends ProcessingOutput {

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

    @Override
    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        ResourceLocation resourceLocation = this.childCompatDatagenOutput == null ? RegisteredObjects.getKeyOrThrow(this.getStack().getItem()) : (ResourceLocation)this.childCompatDatagenOutput.getFirst();
        json.addProperty("item", resourceLocation.toString());
        int count = this.childCompatDatagenOutput == null ? this.getStack().getCount() : (Integer)this.childCompatDatagenOutput.getSecond();
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
        if (!je.isJsonObject()) {
            throw new JsonSyntaxException("ProcessingOutput must be a json object");
        } else {
            JsonObject json = je.getAsJsonObject();
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
        super.write(buf);
        buf.writeInt(getTemperature());
        buf.writeBoolean(getCopyHeat());
        buf.writeInt(getCooling());
    }

    public static HeatedProcessingOutput read(FriendlyByteBuf buf) {
        return new HeatedProcessingOutput(buf.readItem(), buf.readFloat(),buf.readInt(),buf.readBoolean(),buf.readInt());
    }
}
