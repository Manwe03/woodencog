package net.chauvedev.woodencog;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.chauvedev.woodencog.block.CTTransformerRenderer;
import net.chauvedev.woodencog.block.WoodencogBlockEntityTypes;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.config.WoodenCogConfigs;
import net.chauvedev.woodencog.datagen.DataGenerators;
import net.chauvedev.woodencog.interaction.CustomArmInteractionPointTypes;
import net.chauvedev.woodencog.item.WoodencogItems;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.AllAdvancedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.block.WoodencogBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(WoodenCog.MOD_ID)
public class WoodenCog
{
    public static final String MOD_ID = "woodencog";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(WoodenCog.MOD_ID);
    //private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    //static final PonderRegistrationHelper PONDER_HELPER = new PonderRegistrationHelper(WoodenCog.MOD_ID);

    public WoodenCog()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        REGISTRATE.registerEventListeners(modEventBus);

        WoodenCogConfigs.registerConfigs();

        WoodencogItems.register(modEventBus);
        WoodencogBlocks.register();
        WoodencogBlockEntityTypes.register();

        AllAdvancedRecipeTypes.register(modEventBus);
        AllHeatedRecipeTypes.register(modEventBus);

        if(FMLEnvironment.dist == Dist.CLIENT) {/*PONDER_HELPER.forComponents(FIRECLAY_CRUCIBLE_ITEM).addStoryBoard("heating/heat", Heating::heating).addStoryBoard("heating/cool", Heating::cooling);*/}
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
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }

    private void setup(final FMLCommonSetupEvent event)
    {

    }

    public static void onRegister(final RegisterEvent event) {
        CustomArmInteractionPointTypes.init();
    }

    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent event) {
        BlockEntityRenderers.register(WoodencogBlockEntityTypes.CT_TRANSFORMER.get(), CTTransformerRenderer::new);
    }

    @SubscribeEvent
    public void onRegisterCommandEvent(RegisterCommandsEvent event) {

    }

    public static ResourceLocation asResource(String path) {
        System.out.println("[DANGER] resourceLocation created: "+new ResourceLocation(WoodenCog.MOD_ID, path));
        return new ResourceLocation(WoodenCog.MOD_ID, path);
    }

}


