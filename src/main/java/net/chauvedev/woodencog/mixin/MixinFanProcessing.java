package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.utils.ModTags;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FanProcessing.class, remap = false)
public class MixinFanProcessing {



    private static ItemStack applyProcessingTCF(ItemStack inputStack, FanProcessingType type) {
        if (
                !inputStack.getCapability(HeatCapability.CAPABILITY).isPresent()
                || type.equals(AllFanProcessingTypes.HAUNTING)
        ) {
            return inputStack;
        }

        IHeat cap = inputStack.getCapability(HeatCapability.CAPABILITY).resolve().get();

        float itemTemp = cap.getTemperature();
        if(type.equals(AllFanProcessingTypes.BLASTING)) {
            HeatCapability.addTemp(cap, 1700);
        } else if (type.equals(AllFanProcessingTypes.SMOKING)) {
            HeatCapability.addTemp(cap, 200);
        } else if (type.equals(AllFanProcessingTypes.NONE)) {
            cap.setTemperature(cap.getTemperature() - 2F);
            if(cap.getTemperature() <= 0F) {
                cap.setTemperature(0F);
            }
        } else if (type.equals(AllFanProcessingTypes.SPLASHING)) {
            cap.setTemperature(cap.getTemperature() - 5F);
            if(cap.getTemperature() <= 0F) {
                cap.setTemperature(0F);
            }
        }

        HeatingRecipe recipe = HeatingRecipe.getRecipe(inputStack);

        if (recipe!=null){
            if ((double)itemTemp > 1.1 * (double)recipe.getTemperature()) {
                if (recipe.assemble(new ItemStackInventory(inputStack), null).isEmpty()){
                    return null;
                }
            }
            if (recipe.isValidTemperature(cap.getTemperature()))
            {
                ItemStack output = recipe.assemble(new ItemStackInventory(inputStack), null);
                FluidStack fluidStack = recipe.assembleFluid(new ItemStackInventory(inputStack));
                if(!fluidStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                FoodCapability.applyTrait(output, FoodTraits.WOOD_GRILLED);
                if (!output.isEmpty()){
                    output.setCount(inputStack.getCount());
                    return output;
                }else{
                    return inputStack;
                }
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
        boolean hasHeat = transported.stack.getCapability(HeatCapability.CAPABILITY).isPresent();
        boolean isUnburnable = transported.stack.is(ModTags.Items.UNBURNABLE);

        if(isUnburnable) {
            cir.setReturnValue(TransportedItemStackHandlerBehaviour.TransportedResult.doNothing());
            return;
        }

        if (hasHeat && WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get())
        {
            ItemStack oldStack = transported.stack;
            ItemStack newStack = MixinFanProcessing.applyProcessingTCF(transported.stack, type);
            if(newStack != null) {
                if(newStack.isEmpty()) {
                    cir.setReturnValue(TransportedItemStackHandlerBehaviour.TransportedResult.removeItem());
                    return;
                }
                if(oldStack.is(newStack.getItem())) {
                    cir.setReturnValue(TransportedItemStackHandlerBehaviour.TransportedResult.doNothing());
                    return;
                } else {
                    TransportedItemStack newTransportedStack = transported.getSimilar();
                    newTransportedStack.stack = newStack;
                    cir.setReturnValue(
                            TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(
                                    newTransportedStack
                            )
                    );
                    return;
                }
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


        boolean hasHeat = inputStack.getCapability(HeatCapability.CAPABILITY).isPresent();
        boolean isUnburnable = inputStack.is(ModTags.Items.UNBURNABLE);

        if(isUnburnable) {
            cir.cancel();
        }

        if (hasHeat && WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get())
        {
            ItemStack result = MixinFanProcessing.applyProcessingTCF(inputStack,type);

            if (result==null){
                entity.kill();
            }else{
                entity.setItem(result);
            }
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
