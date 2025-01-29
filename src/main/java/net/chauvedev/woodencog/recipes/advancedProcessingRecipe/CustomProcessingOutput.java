package net.chauvedev.woodencog.recipes.advancedProcessingRecipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CustomProcessingOutput extends ProcessingOutput {

    private Pair<ResourceLocation, Integer> childCompatDatagenOutput;
    private final float temperature;
    private final boolean copyHeat;

    public CustomProcessingOutput(ItemStack stack, float chance, float temperature, boolean copyHeat) {
        super(stack, chance);
        this.temperature = temperature;
        this.copyHeat = copyHeat;
    }

    public CustomProcessingOutput(Pair<ResourceLocation, Integer> item, float chance, float temperature, boolean copyHeat) {
        super(item,chance);
        this.childCompatDatagenOutput = item;
        this.temperature = temperature;
        this.copyHeat = copyHeat;
    }

    public float getTemperature() {
        return temperature;
    }
    public boolean getCopyHeat(){
        return copyHeat;
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
            json.addProperty("copyheat",this.getCopyHeat());
        }
        return json;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeFloat(getTemperature());
        buf.writeBoolean(getCopyHeat());
    }

    public static CustomProcessingOutput read(FriendlyByteBuf buf) {
        return new CustomProcessingOutput(buf.readItem(), buf.readFloat(),buf.readFloat(),buf.readBoolean());
    }
}
