package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dries007.tfc.util.JsonHelpers;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record WoodenCogFoodPortion(float nutrientModifier, float waterModifier, float saturationModifier) {

    public static WoodenCogFoodPortion empty(){
        return new WoodenCogFoodPortion(0,0,0);
    }

    public static WoodenCogFoodPortion flat(float value){
        return new WoodenCogFoodPortion(value,value,value);
    }

    public static WoodenCogFoodPortion read(JsonObject json) {
        float nutrientModifier = JsonHelpers.getAsFloat(json, "nutrient_modifier", 0.0F);
        float waterModifier = JsonHelpers.getAsFloat(json, "water_modifier", 0.0F);
        float saturationModifier = JsonHelpers.getAsFloat(json, "saturation_modifier", 0.0F);
        return new WoodenCogFoodPortion(nutrientModifier, waterModifier, saturationModifier);
    }

    public JsonObject write(){
        JsonObject json = new JsonObject();
        json.addProperty("nutrient_modifier", this.nutrientModifier);
        json.addProperty("water_modifier", this.waterModifier);
        json.addProperty("saturation_modifier", this.saturationModifier);
        return json;
    }

    public static List<WoodenCogFoodPortion> readArray(JsonArray jsonArray){
        List<WoodenCogFoodPortion> portions = new ArrayList<>();
        for(JsonElement portionJsonElement : jsonArray){
            portions.add(WoodenCogFoodPortion.read((JsonObject) portionJsonElement));
        }
        return portions;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.nutrientModifier);
        buffer.writeFloat(this.waterModifier);
        buffer.writeFloat(this.saturationModifier);
    }

    public static WoodenCogFoodPortion decode(FriendlyByteBuf buffer) {
        float nutrientModifier = buffer.readFloat();
        float waterModifier = buffer.readFloat();
        float saturationModifier = buffer.readFloat();
        return new WoodenCogFoodPortion(nutrientModifier, waterModifier, saturationModifier);
    }

    public static List<WoodenCogFoodPortion> decodeArray(FriendlyByteBuf buffer){
        int portionsLength = buffer.readInt();
        List<WoodenCogFoodPortion> portions = new ArrayList<>();
        for(int i = 0; i<portionsLength; i++){
            portions.add(WoodenCogFoodPortion.decode(buffer));
        }
        return portions;
    }
}
