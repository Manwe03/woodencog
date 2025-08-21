package net.chauvedev.woodencog.mixin.armor;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.utils.DivingGearUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(value = BaseArmorItem.class, remap = false)
public class MixinBaseArmorItem {

    @Shadow @Final protected ResourceLocation textureLoc;

    @Inject(
            method = "getArmorTexture",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectGetArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type, CallbackInfoReturnable<String> cir) {
        if(entity instanceof Player player &&
                DivingGearUtil.isDivingGear(stack.getItem()) &&
                !DivingGearUtil.isWearingNetheritePants(player) &&
                stack.getItem() != AllItems.COPPER_DIVING_BOOTS.get() &&
                WoodenCogCommonConfigs.NETHERITE_RESKIN.get()
        ) {
            cir.setReturnValue(String.format(Locale.ROOT, "woodencog:textures/models/armor/%s_layer_%d%s.png", textureLoc.getPath(), slot == EquipmentSlot.LEGS ? 2 : 1, type == null ? "" : String.format(Locale.ROOT, "_%s", type)));
        }
    }
}
