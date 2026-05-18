package net.chauvedev.woodencog.utils;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.compat.Compat;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Contract;

import java.util.List;

public class CogUtil {
    /**
     * Runs the guard on condition
     * @param condition to run guard
     * @param onTrue guard
     * @return is executed
     */
    public static boolean guard(boolean condition, Runnable onTrue) {
        if (condition) {
            onTrue.run();
            return true;
        }
        return false;
    }

    /**
     * Logs if on condition
     * @param condition to run log
     * @param c class
     * @param message error message
     * @return id logged
     */
    @Contract("true, _, _ -> true; false, _, _ -> false")
    public static boolean logConditional(boolean condition, Class<?> c, String message){
        return guard(condition,()-> WoodenCog.LOGGER.error(c.getName()+" : "+message));
    }

    public static <T> T getOrDefault(List<T> list, int index, T defaultValue) {
        return (index >= 0 && index < list.size()) ? list.get(index) : defaultValue;
    }

    public static JsonObject foodDataNbtToJson(CompoundTag nbt) {
        JsonObject json = new JsonObject();

        json.addProperty("hunger", nbt.getInt("food"));
        json.addProperty("water", nbt.getFloat("water"));
        json.addProperty("saturation", nbt.getFloat("sat"));
        json.addProperty("decay_modifier", nbt.getFloat("decay"));
        float grain = nbt.getFloat("grain");
        if(grain != 0) json.addProperty("grain", grain);
        float fruit = nbt.getFloat("fruit");
        if(fruit != 0)json.addProperty("fruit", fruit);
        float veg = nbt.getFloat("veg");
        if(veg != 0)json.addProperty("veg", veg);
        float protein = nbt.getFloat("meat");
        if(protein != 0)json.addProperty("protein", protein);
        float dairy = nbt.getFloat("dairy");
        if(dairy != 0)json.addProperty("dairy", dairy);
        return json;
    }

    public static float max(float[] a){
        float max = Float.NEGATIVE_INFINITY;
        for (float f : a) {
            if (f > max) {
                max = f;
            }
        }
        return max;
    }

    public static int maxIndex(float[] a){
        float max = 0;
        int max_i = 0;
        for (int i = 0; i<a.length; i++) {
            if (a[i] > max) {
                max = a[i];
                max_i = i;
            }
        }
        return max_i;
    }

    public static float heatLevelToTemp(BlazeBurnerBlock.HeatLevel heatLevel){
        switch (heatLevel) {
            case NONE -> {
                return WoodenCogCommonConfigs.BLAZE_BURNER_NONE.get().floatValue();
            }
            case SMOULDERING -> {
                return WoodenCogCommonConfigs.BLAZE_BURNER_SMOULDERING.get().floatValue();
            }
            case FADING -> {
                return WoodenCogCommonConfigs.BLAZE_BURNER_FADING.get().floatValue();
            }
            case KINDLED -> {
                return WoodenCogCommonConfigs.BLAZE_BURNER_KINDLED.get().floatValue();
            }
            case SEETHING -> {
                return WoodenCogCommonConfigs.BLAZE_BURNER_SEETHING.get().floatValue();
            }
        }

        float t = Compat.CLH_INSTANCE.lowHeatTemp(heatLevel);
        if (t>0) return t;
        return 0;
    }

    public static BlazeBurnerBlock.HeatLevel tempToHeatLevel(float temperature){
        if(temperature >= WoodenCogCommonConfigs.BLAZE_BURNER_SEETHING.get()){
            return BlazeBurnerBlock.HeatLevel.SEETHING;
        } else if(temperature >= WoodenCogCommonConfigs.BLAZE_BURNER_KINDLED.get()){
            return BlazeBurnerBlock.HeatLevel.KINDLED;
        } else if(temperature >= WoodenCogCommonConfigs.BLAZE_BURNER_FADING.get()){
            return BlazeBurnerBlock.HeatLevel.FADING;
        } else if(temperature >= WoodenCogCommonConfigs.BLAZE_BURNER_SMOULDERING.get()){
            return BlazeBurnerBlock.HeatLevel.SMOULDERING;
        } else if(temperature >= WoodenCogCommonConfigs.BLAZE_BURNER_NONE.get()){
            return BlazeBurnerBlock.HeatLevel.NONE;
        } else {
            return BlazeBurnerBlock.HeatLevel.NONE;
        }
    }

    /**
     * Use with precaution as this method temperature handling is not precise and has very limited temp values
     */
    public static float tempFromBlockstate(int blockstateHeat){
        return ((blockstateHeat - 1)/6.0F) * WoodenCogCommonConfigs.BLAZE_BURNER_SEETHING.get().floatValue(); // set max temperature as max config
    }

    public static float toRadPerTick(float rpm) {
        return (float) (rpm * (2 * Math.PI / 1200.0));
    }

    public static float toRPM(double radPerTick) {
        return (float) (radPerTick * 1200.0 / (2 * Math.PI));
    }

}
