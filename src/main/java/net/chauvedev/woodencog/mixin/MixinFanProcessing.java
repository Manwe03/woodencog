package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.datapack.DataPackRegistries;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FanProcessing.class, remap = false)
public class MixinFanProcessing {

    @Unique
    private static void applyTemp(ItemStack inputStack, IHeat cap, FanProcessingType type, RegistryAccess registryAccess) {
        if(!inputStack.getCapability(HeatCapability.CAPABILITY).isPresent()) return;

        if(type.equals(AllFanProcessingTypes.BLASTING)) {
            if(!DataPackRegistries.isInTempBlacklist(inputStack, registryAccess)) {
                HeatCapability.addTemp(cap, 1700);
            }
        } else if (type.equals(AllFanProcessingTypes.SMOKING)) {
            if(!DataPackRegistries.isInTempBlacklist(inputStack, registryAccess)) {
                HeatCapability.addTemp(cap, 200);
            }
        } else if (type.equals(AllFanProcessingTypes.SPLASHING)) {
            cap.setTemperature(HeatCapability.adjustTempTowards(cap.getTemperature(),0,8));
        } else {
            cap.setTemperature(cap.getTemperature() - 2F);
            if(cap.getTemperature() <= 0F) {
                cap.setTemperature(0F);
            }
        }
    }

    @Unique
    private static ItemStack applyTFCHeatingRecipe(ItemStack inputStack, IHeat cap){
        HeatingRecipe recipe = HeatingRecipe.getRecipe(inputStack);

        if (recipe!=null){
            if (recipe.isValidTemperature(cap.getTemperature())) {
                ItemStack output = recipe.assemble(new ItemStackInventory(inputStack), null);
                if(output.isEmpty()) return inputStack; //No output for this recipe do not change input
                FluidStack fluidStack = recipe.assembleFluid(new ItemStackInventory(inputStack));
                if(!fluidStack.isEmpty()) {
                    return ItemStack.EMPTY; //Melting recipe input is distorted
                }

                if(FoodCapability.has(output)) FoodCapability.applyTrait(output, FoodTraits.WOOD_GRILLED);

                output.setCount(inputStack.getCount());
                return output;
            }
        }
        return inputStack;
    }

    @Inject(
            method = {"applyProcessing(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour$TransportedResult;"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private static void applyProcessing(TransportedItemStack transported, Level world, FanProcessingType type,CallbackInfoReturnable<TransportedItemStackHandlerBehaviour.TransportedResult> cir) {

        ItemStack inputStack = transported.stack;

        if(inputStack.getCapability(HeatCapability.CAPABILITY).isPresent() && WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()){
            if(inputStack.getCapability(HeatCapability.CAPABILITY).resolve().isEmpty()) {
                cir.setReturnValue(TransportedItemStackHandlerBehaviour.TransportedResult.doNothing());
                return;
            }

            IHeat cap = inputStack.getCapability(HeatCapability.CAPABILITY).resolve().get();

            MixinFanProcessing.applyTemp(inputStack, cap, type, world.registryAccess());
            ItemStack result = MixinFanProcessing.applyTFCHeatingRecipe(inputStack, cap);

            if(result.equals(inputStack)){
                cir.setReturnValue(TransportedItemStackHandlerBehaviour.TransportedResult.doNothing());
                return;
            }

            if(result == ItemStack.EMPTY){
                cir.setReturnValue(TransportedItemStackHandlerBehaviour.TransportedResult.removeItem());
            }else{
                TransportedItemStack newTransportedStack = transported.getSimilar();
                newTransportedStack.stack = result;
                cir.setReturnValue(TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(newTransportedStack));
            }
            cir.cancel();
        }
    }


    @Inject(
            method = {"applyProcessing(Lnet/minecraft/world/entity/item/ItemEntity;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Z"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private static void applyProcessing(ItemEntity entity, FanProcessingType type, CallbackInfoReturnable<Boolean> cir) {
        ItemStack inputStack = entity.getItem();

        if(inputStack.getCapability(HeatCapability.CAPABILITY).isPresent() && WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()){
            if(inputStack.getCapability(HeatCapability.CAPABILITY).resolve().isEmpty()) return;

            IHeat cap = inputStack.getCapability(HeatCapability.CAPABILITY).resolve().get();

            MixinFanProcessing.applyTemp(inputStack, cap, type, entity.level().registryAccess());
            ItemStack result = MixinFanProcessing.applyTFCHeatingRecipe(inputStack, cap);

            if(result.equals(inputStack)){
                cir.setReturnValue(false);
                return;
            }

            if (result == ItemStack.EMPTY){
                entity.kill();
            }else{
                entity.setItem(result);
            }
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(
            method = "applyProcessing(Lnet/minecraft/world/entity/item/ItemEntity;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"),
            cancellable = true
    )
    private static void cancelDiscard(ItemEntity entity, FanProcessingType type, CallbackInfoReturnable<Boolean> cir){
        cir.cancel();
    }

}
