package net.chauvedev.woodencog.utils;

import com.simibubi.create.Create;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemAccess {

    public static final Item IRON_PLATE = BuiltInRegistries.ITEM.get(Create.asResource("iron_sheet"));
    public static final Item flux = BuiltInRegistries.ITEM.get(ResourceLocation.tryBuild("tfc","powder/flux"));
    public static final Item rawIron = BuiltInRegistries.ITEM.get(ResourceLocation.tryBuild("tfc","raw_iron_bloom"));
    public static final Item refinedIron = BuiltInRegistries.ITEM.get(ResourceLocation.tryBuild("tfc","refined_iron_bloom"));
    public static final Item wroughtIron = BuiltInRegistries.ITEM.get(TFCIngotResourceLocation("wrought_iron"));
    public static final Item ceramicBowl = BuiltInRegistries.ITEM.get(ResourceLocation.tryBuild("tfc","ceramic/bowl"));
    public static final Item riceGrain = BuiltInRegistries.ITEM.get(ResourceLocation.tryBuild("tfc","food/rice_grain"));
    public static final Item cookedRice = BuiltInRegistries.ITEM.get(ResourceLocation.tryBuild("tfc","food/cooked_rice"));
    public static final Item egg = BuiltInRegistries.ITEM.get(ResourceLocation.tryBuild("minecraft","egg"));
    public static final Item boiledEgg = BuiltInRegistries.ITEM.get(ResourceLocation.tryBuild("tfc","food/boiled_egg"));

    private static ResourceLocation TFCIngotResourceLocation(String metalId) {
        return ResourceLocation.tryBuild("tfc","metal/ingot/"+metalId);
    }
}
