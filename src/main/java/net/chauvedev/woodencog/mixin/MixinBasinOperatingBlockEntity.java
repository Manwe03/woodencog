package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(value = BasinOperatingBlockEntity.class, remap = false)
public abstract class MixinBasinOperatingBlockEntity extends KineticBlockEntity {
    public MixinBasinOperatingBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    /**
     * @author ChauveDev
     * @reason This function didn't take in account the fluid ingredients which is pretty bad in a basin
     */
    @Overwrite
    protected List<Recipe<?>> getMatchingRecipes() {
        if (this.getBasin().map(BasinBlockEntity::isEmpty).orElse(true)) {
            return new ArrayList();
        } else {
            List<Recipe<?>> list = RecipeFinder.get(this.getRecipeCacheKey(), this.level, this::matchStaticFilters);
            return list.stream().filter(this::matchBasinRecipe).sorted((r1, r2) -> {
                int r1Size = 0;
                int r2Size = 0;
                if (r1 instanceof BasinRecipe basinR1){
                    r1Size = (basinR1.getIngredients().size() + basinR1.getFluidIngredients().size());
                }
                if(r2 instanceof BasinRecipe basinR2){
                    r2Size = (basinR2.getIngredients().size() + basinR2.getFluidIngredients().size());
                }
                return r2Size - r1Size;
            }).collect(Collectors.toList());
        }
    }

    @Shadow() abstract boolean matchStaticFilters(Recipe<?> recipe);

    @Shadow() abstract boolean matchBasinRecipe(Recipe<?> recipe);

    @Shadow() abstract Object getRecipeCacheKey();

    @Shadow() abstract Optional<BasinBlockEntity> getBasin();
}
