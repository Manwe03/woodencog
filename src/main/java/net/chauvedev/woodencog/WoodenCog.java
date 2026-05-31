package net.chauvedev.woodencog;

import com.mojang.logging.LogUtils;
import net.chauvedev.woodencog.block.WoodenCogCreativeModeTabs;
import net.chauvedev.woodencog.block.WoodenCogRegistrate;
import net.chauvedev.woodencog.block.generator.WoodenGeneratorRenderer;
import net.chauvedev.woodencog.block.transformer.CTTransformerRenderer;
import net.chauvedev.woodencog.block.WoodencogBlockEntityTypes;
import net.chauvedev.woodencog.compat.Compat;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.datagen.WoodenCogDatagen;
import net.chauvedev.woodencog.datapack.DataPackRegistries;
import net.chauvedev.woodencog.interaction.CustomArmInteractionPointTypes;
import net.chauvedev.woodencog.item.WoodencogItems;
import net.chauvedev.woodencog.ponder.WoodenCogPonderPlugin;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.block.WoodencogBlocks;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.WoodenCogIngredients;
import net.createmod.ponder.foundation.PonderIndex;
import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(WoodenCog.MOD_ID)
public class WoodenCog {
    public static final String MOD_ID = "woodencog";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final WoodenCogRegistrate REGISTRATE = WoodenCogRegistrate.create(WoodenCog.MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public WoodenCog(ModContainer container) {
        Compat.init(); //Load addon compatibility

        IEventBus modEventBus = container.getEventBus();
        modEventBus.addListener(this::onClientSetup);
        REGISTRATE.registerEventListeners(modEventBus);
        WoodenCogCreativeModeTabs.register(modEventBus);

        WoodenCogCommonConfigs.register(container);

        WoodencogItems.register(modEventBus);
        WoodencogBlocks.register();
        WoodencogBlockEntityTypes.register();

        AllHeatedRecipeTypes.register(modEventBus);

        WoodenCogIngredients.register(modEventBus);

        modEventBus.addListener(WoodenCog::onRegister);
        modEventBus.addListener(WoodenCogDatagen::gatherData);
        modEventBus.addListener(DataPackRegistries::register);
    }

    public static WoodenCogRegistrate registrate() {
        return REGISTRATE;
    }

    public static void onRegister(final RegisterEvent event) {
        CustomArmInteractionPointTypes.init();
    }

    public void onClientSetup(final FMLClientSetupEvent event) {
        BlockEntityRenderers.register(WoodencogBlockEntityTypes.CT_TRANSFORMER.get(), CTTransformerRenderer::new);
        BlockEntityRenderers.register(WoodencogBlockEntityTypes.WOODEN_GENERATOR.get(), WoodenGeneratorRenderer::new);

        PonderIndex.addPlugin(new WoodenCogPonderPlugin());
    }

    public static ResourceLocation asResource(String path) {
        WoodenCog.LOGGER.info("ResourceLocation created: "+ ResourceLocation.fromNamespaceAndPath(WoodenCog.MOD_ID, path));
        return ResourceLocation.fromNamespaceAndPath(WoodenCog.MOD_ID, path);
    }

    /**
     * Returns the input ResourceLocation as a WoodenCog ResourceLocation
     * @param item
     * @return modified item ResourceLocation
     */
    public static ResourceLocation asWoodencogResource(Item item){
        return WoodenCog.asResource(BuiltInRegistries.ITEM.getKey(item).getPath());
    }
    /**
     * Returns the input ResourceLocation as a WoodenCog ResourceLocation
     * @param item
     * @return modified item ResourceLocation
     */
    public static ResourceLocation asWoodencogResource(Item item, String additional){
        return WoodenCog.asResource(BuiltInRegistries.ITEM.getKey(item).getPath()+ additional);
    }
    public static ResourceLocation asWoodencogResource(String prefix, Item item, String additional){
        return WoodenCog.asResource(prefix + BuiltInRegistries.ITEM.getKey(item).getPath() + additional);
    }
    public static ResourceLocation asTFCResource(String path){
        return ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, path.toLowerCase());
    }

    public static ResourceLocation asWoodencogResource(ResourceLocation resourceLocation){
        return WoodenCog.asResource(resourceLocation.getPath());
    }
    public static ResourceLocation asWoodencogResource(String prefix, ResourceLocation resourceLocation){
        return WoodenCog.asResource(prefix + resourceLocation.getPath());
    }
    public static ResourceLocation asWoodencogResource(String prefix, ResourceLocation resourceLocation, String sufix){
        return WoodenCog.asResource(prefix + resourceLocation.getPath() + sufix);
    }
}


