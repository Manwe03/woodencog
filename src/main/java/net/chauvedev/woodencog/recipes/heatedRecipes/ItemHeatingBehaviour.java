package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.simibubi.create.content.processing.basin.BasinInventory;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.chauvedev.woodencog.datapack.DataPackRegistries;
import net.chauvedev.woodencog.blockEntities.BasinBlockEntityExtended;
import net.chauvedev.woodencog.utils.CogUtil;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.minecraft.world.item.ItemStack;

public class ItemHeatingBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<ItemHeatingBehaviour> TYPE = new BehaviourType<>();
    final BasinInventory inventory;

    public ItemHeatingBehaviour(SmartBlockEntity be, BasinInventory inventory) {
        super(be);
        this.inventory = inventory;
        this.blockEntity.getBlockPos();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    /**
     * Heat items inside inventory with CharcoalForge an BlazeBurners
     */
    @Override
    public void tick() {
        super.tick();
        if(this.blockEntity.getLevel() == null) return;

        if(this.blockEntity instanceof BasinBlockEntityExtended basinBlockEntity){
            heatInventory(basinBlockEntity.getHeatSourceTemperature());
        }
    }

    private void heatInventory(float targetTemp) {
        if(inventory != null){
            for (int i = 0; i<inventory.getSlots(); i++){
                ItemStack itemStack = inventory.getItem(i);
                if(this.blockEntity.getLevel() == null) return;

                if(CogUtil.logConditional(this.blockEntity.getLevel() == null,this.getClass(),"Tried to heat up items in basin level of be is null")) return;

                if(DataPackRegistries.isInTempBlacklist(itemStack,this.blockEntity.getLevel().registryAccess())) continue; //Skip if item is in blacklist
                IHeat heat = HeatCapability.get(itemStack);
                if(heat != null) HeatCapability.addTemp(heat,targetTemp,2);
            }
        }
    }
}
