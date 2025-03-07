package net.chauvedev.woodencog.mixin.blockEnitites;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.VecHelper;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.*;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(value = MechanicalPressBlockEntity.class, remap = false)
public class MixinMechanicalPressBlockEntity {

    @Final
    @Shadow private static RecipeWrapper pressingInv;
    @Shadow public PressingBehaviour pressingBehaviour;

    /**
     * @author Manwe
     * @implNote More general method, return pressing and heatedPressing recipes
     */
    @Unique
    public Optional<?> getHeatedRecipe(ItemStack item) {
        Level level = ((BlockEntity) (Object) this).getLevel();
        if(level == null) {
            WoodenCog.LOGGER.error("getHeatedRecipe - level null");
            return Optional.empty();
        }

        Optional<PressingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, item, AllRecipeTypes.PRESSING.getType(), PressingRecipe.class);
        if (assemblyRecipe.isPresent()) {
            return assemblyRecipe;
        }
        pressingInv.setItem(0, item);
        //Find heated recipe
        Optional<HeatedPressingRecipe> heatedPressingRecipe = AllHeatedRecipeTypes.HEATED_PRESSING.find(pressingInv, level);
        if (heatedPressingRecipe.isPresent()) {
            return heatedPressingRecipe;
        }
        //Find default create recipe
        return AllRecipeTypes.PRESSING.find(pressingInv, level);
    }

    /**
     * @author Manwe
     * @reason Added HeatedPressing recipe support
     * @implNote Changed call to getRecipe - getHeatedRecipe and return type
     */
    @Overwrite
    public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
        Level level = ((BlockEntity) (Object) this).getLevel();
        if(level == null) return false;
        ItemStack item = itemEntity.getItem();
        Optional<?> recipe = getHeatedRecipe(item);
        if (recipe.isEmpty()) {
            return false;
        } else if (simulate) {
            return true;
        } else {
            ItemStack itemCreated = ItemStack.EMPTY;
            this.pressingBehaviour.particleItems.add(item);
            if (!((MechanicalPressBlockEntity)(Object)this).canProcessInBulk() && item.getCount() != 1) {
                Iterator<ItemStack> var6 = RecipeApplier.applyRecipeOn(level, ItemHandlerHelper.copyStackWithSize(item, 1), (Recipe<?>) recipe.get()).iterator();

                while(var6.hasNext()) {
                    ItemStack result = var6.next();
                    if (itemCreated.isEmpty()) {
                        itemCreated = result.copy();
                    }

                    ItemEntity created = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
                    created.setDefaultPickUpDelay();
                    created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, 0.05F));
                    level.addFreshEntity(created);
                }

                item.shrink(1);
            } else {
                RecipeApplier.applyRecipeOn(itemEntity, (Recipe<?>) recipe.get());
                itemCreated = itemEntity.getItem().copy();
            }

            if (!itemCreated.isEmpty()) {
                ((MechanicalPressBlockEntity)(Object)this).onItemPressed(itemCreated);
            }

            return true;
        }
    }

    /**
     * @author Manwe
     * @reason Added HeatedPressing recipe support
     * @implNote Changed call to getRecipe - getHeatedRecipe and return type
     */
    @Overwrite
    public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
        Level level = ((BlockEntity) (Object) this).getLevel();
        Optional<?> recipe = getHeatedRecipe(input.stack);

        if (recipe.isEmpty()) {
            return false;
        } else if (simulate) {
            return true;
        } else {
            this.pressingBehaviour.particleItems.add(input.stack);
            List<ItemStack> outputs = RecipeApplier.applyRecipeOn(level, ((MechanicalPressBlockEntity)(Object)this).canProcessInBulk() ? input.stack : ItemHandlerHelper.copyStackWithSize(input.stack, 1), (Recipe<?>)recipe.get());

            for (ItemStack created : outputs) {
                if (!created.isEmpty()) {
                    ((MechanicalPressBlockEntity) (Object) this).onItemPressed(created);
                    break;
                }
            }

            outputList.addAll(outputs);
            return true;
        }
    }
}
