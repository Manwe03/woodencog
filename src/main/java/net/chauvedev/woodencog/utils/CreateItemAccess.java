package net.chauvedev.woodencog.utils;

import com.simibubi.create.Create;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class CreateItemAccess {
    public static Item IRON_PLATE = ForgeRegistries.ITEMS.getValue(Create.asResource("iron_sheet"));
}
