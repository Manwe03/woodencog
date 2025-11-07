package net.chauvedev.woodencog;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.chauvedev.woodencog.block.generator.WoodenGeneratorRenderer;
import net.chauvedev.woodencog.block.transformer.CTTransformerRenderer;
import net.chauvedev.woodencog.block.WoodencogBlockEntityTypes;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.datagen.DataGenerators;
import net.chauvedev.woodencog.datapack.DataPackRegistries;
import net.chauvedev.woodencog.interaction.CustomArmInteractionPointTypes;
import net.chauvedev.woodencog.item.WoodencogItems;
import net.chauvedev.woodencog.ponder.WoodenCogPonderPlugin;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.AllAdvancedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.block.WoodencogBlocks;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(WoodenCog.MOD_ID)
public class WoodenCog
{
    public static final String MOD_ID = "woodencog";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(WoodenCog.MOD_ID);
    //private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public WoodenCog()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        REGISTRATE.registerEventListeners(modEventBus);

        WoodenCogCommonConfigs.register();

        WoodencogItems.register(modEventBus);
        WoodencogBlocks.register();
        WoodencogBlockEntityTypes.register();

        AllAdvancedRecipeTypes.register(modEventBus);
        AllHeatedRecipeTypes.register(modEventBus);

        //if(FMLEnvironment.dist == Dist.CLIENT) {
            /*PONDER_HELPER.forComponents(FIRECLAY_CRUCIBLE_ITEM).addStoryBoard("heating/heat", Heating::heating).addStoryBoard("heating/cool", Heating::cooling);*/
        //}

        /*
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
        });*/

        modEventBus.addListener(WoodenCog::onRegister);
        modEventBus.addListener(DataGenerators::gatherData);
        modEventBus.addListener(DataPackRegistries::register);
        modEventBus.addListener(this::addCreative);
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    public static void onRegister(final RegisterEvent event) {
        CustomArmInteractionPointTypes.init();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event){
        if(event.getTabKey() == AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey()){
            event.accept(WoodencogBlocks.CT_TRANSFORMER.get());
            event.accept(WoodencogBlocks.WOODEN_GENERATOR.get());
        }
    }

    public void onClientSetup(final FMLClientSetupEvent event) {
        BlockEntityRenderers.register(WoodencogBlockEntityTypes.CT_TRANSFORMER.get(), CTTransformerRenderer::new);
        BlockEntityRenderers.register(WoodencogBlockEntityTypes.WOODEN_GENERATOR.get(), WoodenGeneratorRenderer::new);

        PonderIndex.addPlugin(new WoodenCogPonderPlugin());
    }

    public static ResourceLocation asResource(String path) {
        WoodenCog.LOGGER.info("ResourceLocation created: "+ ResourceLocation.tryBuild(WoodenCog.MOD_ID, path));
        return ResourceLocation.tryBuild(WoodenCog.MOD_ID, path);
    }

}


