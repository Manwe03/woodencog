package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.simibubi.create.content.processing.basin.BasinInventory;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.chauvedev.woodencog.WoodenCog;

public class ItemHeatingBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<ItemHeatingBehaviour> TYPE = new BehaviourType();
    BasinInventory inventory;

    public ItemHeatingBehaviour(SmartBlockEntity be, BasinInventory inventory) {
        super(be);
        this.inventory = inventory;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void tick() {
        super.tick();

        if(inventory != null){ //TODO - heat items
            WoodenCog.LOGGER.info(inventory.getItem(0).getItem().toString());
        }
    }

}
