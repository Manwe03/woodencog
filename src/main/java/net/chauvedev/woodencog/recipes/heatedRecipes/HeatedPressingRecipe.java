package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Lang;
import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class HeatedPressingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {
    public HeatedPressingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(Type.INSTANCE, params);
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level worldIn) {
        if (inv.isEmpty())
            return false;
        return ingredients.get(0)
                .test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 2;
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getDescriptionForAssembly() {
        return Lang.translateDirect("recipe.assembly.pressing");
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        Block mechanicalPress = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("create", "mechanical_press"));
        if (mechanicalPress != null) {
            list.add(mechanicalPress);
        }
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> SequencedAssemblySubCategory.AssemblyPressing::new;
    }

    private static class Type implements IRecipeTypeInfo {
        private static final Type INSTANCE = new Type();
        @Override
        public ResourceLocation getId() {
            return new ResourceLocation(WoodenCog.MOD_ID, "heated_pressing");
        }

        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) AllHeatedProcessingRecipes.HEATED_PRESSING_SERIALIZER.get();
        }

        @Override
        public <T extends RecipeType<?>> T getType() {
            return (T) HEATED_PRESSING_TYPE;
        }

        // Define el RecipeType para HeatedPressingRecipe
        private static final RecipeType<HeatedPressingRecipe> HEATED_PRESSING_TYPE = new RecipeType<>() {
            @Override
            public String toString() {
                return WoodenCog.MOD_ID + ":heated_pressing";
            }
        };
    }
}
