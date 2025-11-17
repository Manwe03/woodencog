package net.chauvedev.woodencog.utils;

import com.google.gson.JsonObject;
import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.nbt.CompoundTag;
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
        json.addProperty("grain", nbt.getFloat("grain"));
        json.addProperty("fruit", nbt.getFloat("fruit"));
        json.addProperty("veg", nbt.getFloat("veg"));
        json.addProperty("protein", nbt.getFloat("meat"));
        json.addProperty("dairy", nbt.getFloat("dairy"));

        return json;
    }
}
