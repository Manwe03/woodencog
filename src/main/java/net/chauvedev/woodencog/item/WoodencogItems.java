package net.chauvedev.woodencog.item;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class WoodencogItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WoodenCog.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

        //Unfinished variants
        TFCItems.METAL_ITEMS.forEach((aDefault, itemTypeRegistryObjectMap) -> {
            itemTypeRegistryObjectMap.forEach((itemType, itemRegistryObject) -> {
                assert itemRegistryObject.getKey() != null;
                String name = itemRegistryObject.getId().toString();
                String newname = name.replaceAll("tfc:|minecraft:", "") +"/unfinished";
                ITEMS.register(
                        newname,
                        () -> new SequencedAssemblyItem(new Item.Properties())
                );
            });
        });

    }
}
