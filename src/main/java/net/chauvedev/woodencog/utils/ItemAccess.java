package net.chauvedev.woodencog.utils;

import com.simibubi.create.Create;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemAccess {

    public static final Item IRON_PLATE = CogUtil.findNotNullItem(Create.asResource("iron_sheet"));
    public static final Item flux = CogUtil.findNotNullItem(ResourceLocation.tryBuild("tfc","powder/flux"));
    public static final Item rawIron = CogUtil.findNotNullItem(ResourceLocation.tryBuild("tfc","raw_iron_bloom"));
    public static final Item refinedIron = CogUtil.findNotNullItem(ResourceLocation.tryBuild("tfc","refined_iron_bloom"));
    public static final Item wroughtIron = CogUtil.findNotNullItem(TFCIngotResourceLocation("wrought_iron"));
    public static final Item ceramicBowl = CogUtil.findNotNullItem(ResourceLocation.tryBuild("tfc","ceramic/bowl"));
    public static final Item riceGrain = CogUtil.findNotNullItem(ResourceLocation.tryBuild("tfc","food/rice_grain"));
    public static final Item cookedRice = CogUtil.findNotNullItem(ResourceLocation.tryBuild("tfc","food/cooked_rice"));
    public static final Item egg = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryBuild("minecraft","egg"));
    public static final Item boiledEgg = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryBuild("tfc","food/boiled_egg"));

    private static ResourceLocation TFCIngotResourceLocation(String metalId) {
        return ResourceLocation.tryBuild("tfc","metal/ingot/"+metalId);
    }
}
