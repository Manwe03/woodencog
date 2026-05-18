package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record WoodenCogFoodPortion(float nutrientModifier, float waterModifier, float saturationModifier) {

    public static final Codec<WoodenCogFoodPortion> CODEC = RecordCodecBuilder.create(instance -> // Given an instance
            instance.group(
                    Codec.FLOAT.fieldOf("nutrientModifier").forGetter(WoodenCogFoodPortion::nutrientModifier),
                    Codec.FLOAT.fieldOf("waterModifier").forGetter(WoodenCogFoodPortion::waterModifier),
                    Codec.FLOAT.fieldOf("saturationModifier").forGetter(WoodenCogFoodPortion::saturationModifier)
            ).apply(instance, WoodenCogFoodPortion::new)
    );

    public static WoodenCogFoodPortion empty(){
        return new WoodenCogFoodPortion(0,0,0);
    }

    public static WoodenCogFoodPortion flat(float value){
        return new WoodenCogFoodPortion(value,value,value);
    }
}
