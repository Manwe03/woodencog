package net.chauvedev.woodencog.mixin.armor;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.utils.DivingGearUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(value = BacktankItem.Layered.class, remap = false)
public class MixinBacktankItemLayered {

    @Inject(method = "getArmorTextureLocation", at = @At("HEAD"), cancellable = true)
    public void getArmorTextureLocation(LivingEntity entity, EquipmentSlot slot, ItemStack stack, int layer, CallbackInfoReturnable<String> cir) {
        if(entity instanceof Player player &&
                DivingGearUtil.isDivingGear(stack.getItem()) &&
                !DivingGearUtil.isWearingNetheritePants(player) &&
                stack.getItem() == AllItems.NETHERITE_BACKTANK.get() &&
                WoodenCogCommonConfigs.NETHERITE_RESKIN.get()
        ) {
            cir.setReturnValue(String.format(Locale.ROOT, "woodencog:textures/models/armor/netherite_diving_layer_%d.png", layer));
        }
    }
}
