package net.chauvedev.woodencog.item;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;


public class WoodencogItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, WoodenCog.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

        //Register Unfinished variants for sequenced assembly TODO implement SequencedAssembly
        TFCItems.METAL_ITEMS.forEach((aDefault, itemTypeRegistryObjectMap) -> {
            itemTypeRegistryObjectMap.forEach((itemType, itemRegistryObject) -> {
                String name = itemRegistryObject.getId().toString();
                String newname = name.replaceAll("tfc:|minecraft:", "") +"/unfinished";
                ITEMS.register(newname, () -> new SequencedAssemblyItem(new Item.Properties()));
            });
        });

    }
}
