package net.chauvedev.woodencog.mixin.heat;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.chauvedev.woodencog.compat.Compat;
import net.chauvedev.woodencog.datapack.DataPackRegistries;
import net.chauvedev.woodencog.mixin.blockEnitites.accessors.BasinBlockEntityAccessor;
import net.chauvedev.woodencog.mixin.blockEnitites.accessors.SmartBlockEntityAccessor;
import net.chauvedev.woodencog.mixin.blockEnitites.accessors.BlockEntityAccessor;
import net.chauvedev.woodencog.recipes.heatedRecipes.ItemHeatingBehaviour;
import net.chauvedev.woodencog.blockEntities.BasinBlockEntityExtended;
import net.chauvedev.woodencog.blockEntities.BlazeBurnerBlockentityExtended;
import net.chauvedev.woodencog.utils.CogUtil;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = BasinBlockEntity.class, remap = false)
public abstract class MixinBasinBlockEntity implements BasinBlockEntityExtended {

    @Shadow public SmartFluidTankBehaviour inputTank;
    @Shadow private boolean contentsChanged;

    ScrollOptionBehaviour selectionMode;

    public MixinBasinBlockEntity() {}

    @Unique
    public ScrollOptionBehaviour getSelectionMode(){
        return this.selectionMode;
    }

    /**
     * Called on heated recipes and passive item heating on basins
     * @return temperature of the block below
     */
    @Unique
    public float getHeatSourceTemperature(){
        Level level = ((BlockEntityAccessor) this).getLevel();
        //BlockEntities
        BlockEntity source = level.getBlockEntity(((BlockEntityAccessor) this).getBlockPos().below());
        if (source instanceof CharcoalForgeBlockEntity charcoalForgeBlockEntity) {
            return charcoalForgeBlockEntity.getTemperature();
        } else if(source instanceof BlazeBurnerBlockentityExtended blazeBurnerBlockEntity){
            return blazeBurnerBlockEntity.getTemperature();
        } else if (source instanceof AbstractFirepitBlockEntity<?> firepitBlockEntityl) {
            return firepitBlockEntityl.getTemperature() / 2.0f;
        }

        //Optional Compatibility
        float t = Compat.CCA_INSTANCE.getTFCTemperatureOf(source);
        if (t > 0) return t;
        t = Compat.CLH_INSTANCE.getTFCTemperatureOf(source);
        if (t > 0) return t;

        //Blocks
        Block sourceBlock = level.getBlockState(((BlockEntityAccessor) this).getBlockPos().below()).getBlock();
        RegistryAccess registry = level.registryAccess();
        Map<ResourceLocation, Integer> temperatureData = registry.registryOrThrow(DataPackRegistries.TEMPERATURE_KEY).get(DataPackRegistries.BLOCK_TEMPERATURE_LOCATION);
        ResourceLocation id = registry.registryOrThrow(Registries.BLOCK).getKey(sourceBlock);
        if(temperatureData == null) return 0.0f;
        Integer temp = temperatureData.get(id);
        return temp == null ? 0.0f : temp;
    }

    @Unique
    public void autoChainRecipes(){
        if(this.getSelectionMode().get() == BasinBlockEntityExtended.SelectionMode.AUTO_FEED){
            IFluidHandler ouputHandler = ((BasinBlockEntityAccessor) this).getOutputTank().getCapability().resolve().get();
            IFluidHandler inputHandler = this.inputTank.getCapability().resolve().get();

            for(int slot = 0; slot < ouputHandler.getTanks(); ++slot) {
                FluidStack fs = ouputHandler.getFluidInTank(slot).copy();
                if (!fs.isEmpty()) {
                    int fluidTranfered = inputHandler.fill(fs, IFluidHandler.FluidAction.SIMULATE);
                    if(fluidTranfered == 0) continue;
                    fs.setAmount(fluidTranfered);
                    ouputHandler.drain(fs, IFluidHandler.FluidAction.EXECUTE);
                    inputHandler.fill(fs, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    /**
     * @author Manwe
     * AddHeatingBehaviour to basinBlock
     */
    @Inject(method = "<init>",at = @At("RETURN"))
    private void onInit(BlockEntityType type, BlockPos pos, BlockState state, CallbackInfo ci){
        BasinBlockEntity blockEntity = (BasinBlockEntity) (Object) this;
        Map<BehaviourType<?>, BlockEntityBehaviour> behaviours = ((SmartBlockEntityAccessor) blockEntity).getBehaviours();

        ItemHeatingBehaviour itemHeatingBehaviour = new ItemHeatingBehaviour(blockEntity,blockEntity.inputInventory);
        behaviours.put(itemHeatingBehaviour.getType(), itemHeatingBehaviour); //A little hack, directly access behaviour map and add behaviour

        this.selectionMode = new ScrollOptionBehaviour(BasinBlockEntityExtended.SelectionMode.class, Component.translatable("woodencog.recipes.chain"), (BasinBlockEntity)(Object)this, new BasinBlockEntityExtended.SelectionModeValueBox());
        behaviours.put(selectionMode.getType(), selectionMode);
    }

    /**
     * @author chauveDev
     * Change input tank Behaviour to handle 4 fluids
     */
    @Inject(method="addBehaviours", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/fluid/SmartFluidTankBehaviour;forbidInsertion()Lcom/simibubi/create/foundation/blockEntity/behaviour/fluid/SmartFluidTankBehaviour;"))
    public void addBehaviours(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        this.inputTank = (new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, (BasinBlockEntity)(Object)this, 4, 1000, true)).whenFluidUpdates(() -> {
            this.contentsChanged = true;
        });
    }

    /**
     *     Default Create HeatLevel
     */
    @Inject(
            method = {"getHeatLevelOf"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private static void getHeatLevelOf(BlockState state, CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
        if (state.getBlock() instanceof CharcoalForgeBlock) {
            int heat = state.getValue(CharcoalForgeBlock.HEAT);
            BlazeBurnerBlock.HeatLevel level = CogUtil.tempToHeatLevel(CogUtil.tempFromBlockstate(heat));
            cir.setReturnValue(level);
        }
    }

    @Inject(
            method = "addToGoggleTooltip",
            at = @At("TAIL")
    )
    public void addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, CallbackInfoReturnable<Boolean> cir){
        float temp = getHeatSourceTemperature();
        if(temp <= 0) return;
        ChatFormatting color = ChatFormatting.GRAY;
        Component displayName = Component.literal("");
        for(Heat heat :Heat.values()){
            if(temp > heat.getMin() && temp <= heat.getMax()){
                color = heat.getColor();
                displayName = heat.getDisplayName();
                break;
            }
        }
        CreateLang.text("").add(Component.literal(temp+" ºC ")).style(color).add(displayName).forGoggles(tooltip, 0);
    }
}
