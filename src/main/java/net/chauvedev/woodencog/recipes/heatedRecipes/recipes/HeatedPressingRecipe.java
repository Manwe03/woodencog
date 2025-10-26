package net.chauvedev.woodencog.recipes.heatedRecipes.recipes;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;


import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class HeatedPressingRecipe extends HeatedProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {

    public HeatedPressingRecipe(HeatedProcessingRecipeBuilder.HeatedProcessingRecipeParams params) {
        super(AllHeatedRecipeTypes.HEATED_PRESSING, params);
    }

    @Override
    public boolean matches(RecipeWrapper inv, @NotNull Level worldIn) {
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
        return Component.translatable("recipe.assembly.pressing"); //Change to CreateLang in future create version
    }

    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(ForgeRegistries.BLOCKS.getValue(Create.asResource("mechanical_press")));
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> SequencedAssemblySubCategory.AssemblyPressing::new;
    }
}
