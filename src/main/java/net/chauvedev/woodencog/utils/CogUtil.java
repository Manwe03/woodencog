package net.chauvedev.woodencog.utils;

import com.google.gson.JsonObject;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.common.capabilities.food.DynamicBowlHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
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
}
