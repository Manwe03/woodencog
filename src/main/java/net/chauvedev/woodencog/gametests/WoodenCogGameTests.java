package net.chauvedev.woodencog.gametests;

import com.simibubi.create.content.kinetics.gauge.GaugeBlock;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.block.generator.WoodenGeneratorBlock;
import net.chauvedev.woodencog.block.transformer.CTTransformerBlockEntity;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.dries007.tfc.common.blockentities.rotation.AxleBlockEntity;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.mold.Mold;
import net.dries007.tfc.common.items.MoldItem;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ingredients.TFCIngredients;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(WoodenCog.MOD_ID)
@PrefixGameTestTemplate(false)
public class WoodenCogGameTests {

    @GameTest(template = "lamp_test")
    public static void vanillaLampTurnsOnWhenLeverIsPulled(GameTestHelper helper) {
        BlockPos leverPos = new BlockPos(0, 2, 1);
        BlockPos lampPos = new BlockPos(2, 2, 1);

        helper.assertBlockProperty(leverPos, LeverBlock.POWERED, false);
        helper.assertBlockProperty(lampPos, RedstoneLampBlock.LIT, false);

        helper.pullLever(leverPos);

        helper.succeedWhen(() -> {
            helper.assertBlockProperty(leverPos, LeverBlock.POWERED, true);
            helper.assertBlockProperty(lampPos, RedstoneLampBlock.LIT, true);
        });
    }

    @GameTest(template = "transformer")
    public static void woodencogGeneratorAndTransformer(GameTestHelper helper) {
        BlockPos stressPos = new BlockPos(4, 6, 6);
        BlockPos transformerPos = new BlockPos(6, 6, 6);
        BlockPos axlePos = new BlockPos(7, 6, 6);

        helper.succeedWhen(() -> {
            helper.assertBlockEntityData(
                    stressPos,
                    (StressGaugeBlockEntity be) -> be.getNetworkCapacity() == WoodenCogCommonConfigs.WOODEN_GENERATOR_BASE_SU.get() * 32,
                    () -> "Incorrect network capacity"
            );
            helper.assertBlockEntityData(
                    transformerPos,
                    (CTTransformerBlockEntity be) -> be.hasNetwork() && be.hasSource() && be.calculateStressApplied() == WoodenCogCommonConfigs.CT_TRANSFORMER_IMPACT.get(),
                    () -> "Incorrect stress applied by Transformer"
            );

            helper.assertBlockEntityData(
                    axlePos,
                    (AxleBlockEntity be) -> {
                        WoodenCog.LOGGER.info(!be.isInvalidInNetwork() +"&&"+ be.getRotationNode().isConnectedToNetwork() +"&&"+ (be.getRotationNode().rotation() != null) +"&&"+ (Math.abs(be.getRotationNode().rotation().speed()) > 0));
                        return !be.isInvalidInNetwork() && be.getRotationNode().isConnectedToNetwork() && be.getRotationNode().rotation() != null && Math.abs(be.getRotationNode().rotation().speed()) > 0;
                    },
                    () -> "Power is not being transferred to TFC Axle"
            );
        });
    }

    @GameTest(template = "melting", timeoutTicks = 1200)
    public static void heatedMixingRecipeMeltIngot(GameTestHelper helper){
        final BlockPos pos = new BlockPos(0, 2, 0);

        Item ingot = TFCItems.METAL_ITEMS.get(Metal.TIN).get(Metal.ItemType.INGOT).asItem();
        ItemStack ingotstack = new ItemStack(ingot,1);
        HeatCapability.setTemperature(ingotstack,250);
        if(helper.getBlockEntity(pos) instanceof BasinBlockEntity basinBlockEntity){
            basinBlockEntity.inputInventory.insertItem(0,ingotstack,false);
        }

        helper.succeedWhen(() -> {
            helper.assertBlockEntityData(pos,(BasinBlockEntity b) -> {
                return !b.getBehaviour(SmartFluidTankBehaviour.OUTPUT).isEmpty();
            }, () -> "Recipe produced an incorrect output");
        });
    }
    @GameTest(template = "chisel_andesite_alloy", timeoutTicks = 1200)
    public static void deployingAndesiteAlloy(GameTestHelper helper){
        final BlockPos pos1 = new BlockPos(0, 2, 0);
        final BlockPos pos2 = new BlockPos(3, 2, 0);

        Item andesiteRock = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse("tfc:rock/loose/andesite"));
        Item andesiteAlloy = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse("create:andesite_alloy"));

        if(helper.getBlockEntity(pos1) instanceof BarrelBlockEntity barrel){
            barrel.setItem(0,new ItemStack(andesiteRock,1));
        }

        helper.succeedWhen(() -> {
            helper.assertBlockEntityData(
                    pos2,
                    (BarrelBlockEntity b) -> b.hasAnyMatching(itemStack -> itemStack.is(andesiteAlloy)),
                    () -> "Recipe produced an incorrect output");
        });

    }
    @GameTest(template = "clay_mold", timeoutTicks = 1200)
    public static void pressingPickaxeHeadMold(GameTestHelper helper){
        final BlockPos pos = new BlockPos(0, 1, 0);

        Item clayBall = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse("minecraft:clay_ball"));
        Item headMold = TFCItems.MOLDS.get(Metal.ItemType.PICKAXE_HEAD).asItem();

        if(helper.getBlockEntity(pos) instanceof BasinBlockEntity basinBlockEntity){
            basinBlockEntity.inputInventory.insertItem(0,new ItemStack(clayBall,4),false);
        }

        helper.succeedWhen(() -> {
            helper.assertBlockEntityData(pos, (BasinBlockEntity b) -> {
                return b.getOutputInventory().isItemValid(0,new ItemStack(headMold,1));
            }, () -> "Recipe produced an incorrect output");
        });
    }

    @GameTest(template = "sheet")
    public static void pressingDoubleIngotIntoSheet(GameTestHelper helper){
        final BlockPos pos1 = new BlockPos(0, 1, 0);
        final BlockPos pos2 = new BlockPos(0, 1, 1);
        Item doubleIngot = TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.DOUBLE_INGOT).asItem();
        Item sheet = TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.SHEET).asItem();

        ItemStack doubleIngotStack1 = new ItemStack(doubleIngot,1);
        ItemStack doubleIngotStack2 = doubleIngotStack1.copy();
        HeatCapability.setTemperature(doubleIngotStack1,1000);
        ((DepotBlockEntity) helper.getBlockEntity(pos1)).setHeldItem(doubleIngotStack1); //Set double ingot with sufficient temp
        ((DepotBlockEntity) helper.getBlockEntity(pos2)).setHeldItem(doubleIngotStack2); //Set double ingot with insufficient temp


        helper.succeedWhen(() -> {
            helper.assertBlockEntityData(pos1, (DepotBlockEntity b) -> b.getHeldItem().is(sheet), () -> "Recipe produced an incorrect output");
            helper.assertBlockEntityData(pos1, (DepotBlockEntity b) -> HeatCapability.get(b.getHeldItem()).getTemperature() >= 500, () -> "Temperature has not been provided to the output");
            //TODO fix heated pressing does not copy temperature
            helper.assertBlockEntityData(pos2, (DepotBlockEntity b) -> b.getHeldItem().is(doubleIngot), () -> "Recipe produced an incorrect output");
        });
    }
}
