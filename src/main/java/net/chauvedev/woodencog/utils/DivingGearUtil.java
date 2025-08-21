package net.chauvedev.woodencog.utils;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class DivingGearUtil {
    public static boolean isDivingGear(Item item){
        return item instanceof DivingHelmetItem || item instanceof BacktankItem || item instanceof DivingBootsItem;
    }

    public static boolean isWearingNetheritePants(Player player){
        return player.getInventory().armor.get(EquipmentSlot.LEGS.getIndex()).getItem() == Items.NETHERITE_LEGGINGS;
    }
}
