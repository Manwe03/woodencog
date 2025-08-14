package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.equipment.armor.NetheriteDivingHandler;
import net.dries007.tfc.common.TFCArmorMaterials;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = NetheriteDivingHandler.class, remap = false)
public class MixinNetheriteDivingHandler {

    /**
     * @author chauveDev - manwe
     * @reason to handle redsteel as lavaImmune
     */
    @Overwrite()
    public static boolean isNetheriteArmor(ItemStack stack) {
        Item armor = stack.getItem();
        if (armor instanceof ArmorItem armorItem) {
            return armorItem.getMaterial() == TFCArmorMaterials.RED_STEEL || armorItem.getMaterial() == ArmorMaterials.NETHERITE;
        }
        return false;
    }
}
