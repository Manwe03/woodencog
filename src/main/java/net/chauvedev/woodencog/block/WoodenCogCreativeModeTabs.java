package net.chauvedev.woodencog.block;

import com.simibubi.create.AllBlocks;
import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WoodenCogCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WoodenCog.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOODENCOG_TAB =
            CREATIVE_MODE_TABS.register("woodencog_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.woodencog"))
                    .icon(() -> new ItemStack(AllBlocks.COGWHEEL))
                    .displayItems((parameters, output) -> {
                        output.accept(WoodencogBlocks.CT_TRANSFORMER.get());
                        output.accept(WoodencogBlocks.WOODEN_GENERATOR.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
