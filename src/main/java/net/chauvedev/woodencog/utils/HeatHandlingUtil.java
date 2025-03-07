package net.chauvedev.woodencog.utils;

import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.stream.Stream;

public class HeatHandlingUtil {

    private static List<Integer> getMaterialProperties(ItemStack itemStack){
        if(itemStack.hasTag()) {
            for (String tag : itemStack.getTag().getAllKeys()){
                if(tag.startsWith(INGOT_PREFIX)){
                    String key = tag.substring(14);
                    return WoodenCogCommonConfigs.MATERIAL_PROPERTIES.get(key).get();
                }
            }

        }
        return List.of(2700,897); //Aluminium
    }

    private static final float DEFAULT_VALUE = 2700 * 897; //Aluminium as default value
    private static final String INGOT_PREFIX = "forge:ingots/";
    private static final int INGOT_PREFIX_LENGTH = INGOT_PREFIX.length();

    private static float getMaterialDensityCapacity(ItemStack itemStack){
        System.out.println("getMaterialDensityCapacity");
        Stream<TagKey<Item>> stream = itemStack.getTags();


        for (TagKey<Item> tag : stream.toList()){
            String tagName = tag.location().toString();

            if(tagName.startsWith(INGOT_PREFIX)){
                String key = tagName.substring(INGOT_PREFIX_LENGTH);
                System.out.println("Found tag: "+key);
                ForgeConfigSpec.ConfigValue<List<Integer>> configValue = WoodenCogCommonConfigs.MATERIAL_PROPERTIES.get(key);
                if(configValue == null) return DEFAULT_VALUE;
                List<Integer> properties = configValue.get();
                System.out.println(properties.get(0));
                System.out.println(properties.get(1));
                if(properties.size() != 2) {
                    WoodenCog.LOGGER.info("DEFAULT_VALUE");
                    return DEFAULT_VALUE;
                }
                System.out.println("Density: "+properties.get(0));
                System.out.println("Capacity: "+properties.get(1));
                return properties.get(0)*properties.get(1);
            }
        }

        WoodenCog.LOGGER.info("No Tag");
        return DEFAULT_VALUE;
    }

    /**
     * Computes thermal equilibrium between a number of itemStacks, physical properties are defined in config for each
     * material, lookup from tags ej: #forge:ingots/gold gets gold material properties
     * @param itemStacks items to compute temp from
     * @return thermal equilibrium temperature
     */
    public static float computeThermalEquilibrium(ItemStack... itemStacks){
        float sumTop = 0;
        float sumBot = 0;
        for (ItemStack itemStack : itemStacks){
            float temp1 = 0;
            if(itemStack.getCapability(HeatCapability.CAPABILITY).resolve().isPresent()){
                temp1 = itemStack.getCapability(HeatCapability.CAPABILITY).resolve().get().getTemperature();
                System.out.println("Temp: "+temp1);
            }
            float mult = getMaterialDensityCapacity(itemStack);
            sumTop += mult*temp1;
            sumBot += mult;
        }
        if(sumBot == 0) return 0;
        return sumTop/sumBot;
    }

    /**
     * Computes thermal equilibrium between a number of itemStacks, physical properties are defined in config for each
     * material, lookup from tags ej: #forge:ingots/gold gets gold material properties
     * @param itemStacks items to compute temp from
     * @return thermal equilibrium temperature
     */
    public static float computeThermalEquilibrium(List<ItemStack> itemStacks){
        System.out.println("computeThermalEquilibrium");
        float sumTop = 0;
        float sumBot = 0;
        for (ItemStack itemStack : itemStacks){
            float temp1 = 0;
            if(itemStack.getCapability(HeatCapability.CAPABILITY).resolve().isPresent()){
                temp1 = itemStack.getCapability(HeatCapability.CAPABILITY).resolve().get().getTemperature();
            }
            System.out.println("Temp: "+temp1);
            float mult = getMaterialDensityCapacity(itemStack);
            sumTop += mult*temp1;
            sumBot += mult;
        }
        if(sumBot == 0) return 0;
        WoodenCog.LOGGER.info("ThermalEquilibrium Temp: "+sumTop/sumBot);
        return sumTop/sumBot;
    }
}
