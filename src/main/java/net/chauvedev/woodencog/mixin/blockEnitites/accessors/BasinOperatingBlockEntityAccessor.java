package net.chauvedev.woodencog.mixin.blockEnitites.accessors;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(value = BasinOperatingBlockEntity.class, remap = false)
public interface BasinOperatingBlockEntityAccessor {

    @Accessor("currentRecipe")
    Recipe<?> getCurrentRecipe();

    @Invoker("tick")
    void invokeTick();

    @Invoker("applyBasinRecipe")
    void invokeApplyBasinRecipe();

    @Invoker("getBasin")
    Optional<BasinBlockEntity> invokeGetBasin();
}
