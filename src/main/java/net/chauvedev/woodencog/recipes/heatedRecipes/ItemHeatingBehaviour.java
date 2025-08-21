package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.simibubi.create.content.processing.basin.BasinInventory;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.chauvedev.woodencog.utils.BlazeBurnerBlockentityExtended;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemHeatingBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<ItemHeatingBehaviour> TYPE = new BehaviourType();
    BasinInventory inventory;

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
        BlockEntity bellowBlockEntity = this.blockEntity.getLevel().getBlockEntity(this.blockEntity.getBlockPos().below());
        if(bellowBlockEntity instanceof CharcoalForgeBlockEntity charcoalForgeBlockEntity){
            float targetTemp = charcoalForgeBlockEntity.getTemperature();
            heatInventory(targetTemp);
        } else if(bellowBlockEntity instanceof BlazeBurnerBlockentityExtended blazeBurnerBlockEntity){
            float targetTemp = blazeBurnerBlockEntity.getTemperature();
            heatInventory(targetTemp);
        }
    }

    private void heatInventory(float targetTemp) {
        if(inventory != null){
            for (int i = 0; i<inventory.getSlots(); i++){
                ItemStack itemStack = inventory.getItem(i);
                itemStack.getCapability(HeatCapability.CAPABILITY).resolve().ifPresent(heat -> {
                    HeatCapability.addTemp(heat,targetTemp,2);
                });
            }
        }
    }
}
