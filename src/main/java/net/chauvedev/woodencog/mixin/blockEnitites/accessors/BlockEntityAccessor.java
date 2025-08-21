package net.chauvedev.woodencog.mixin.blockEnitites.accessors;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockEntity.class, remap = true)
public interface BlockEntityAccessor {
    @Accessor("worldPosition")
    BlockPos getWorldPosition();

    @Accessor("level")
    Level getLevel();

    @Accessor("worldPosition")
    BlockPos getBlockPos();
}
