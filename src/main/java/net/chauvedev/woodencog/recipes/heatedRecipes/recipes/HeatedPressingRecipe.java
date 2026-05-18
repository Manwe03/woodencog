package net.chauvedev.woodencog.recipes.heatedRecipes.recipes;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeParams;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;


import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class HeatedPressingRecipe extends HeatedProcessingRecipe<SingleRecipeInput, HeatedProcessingRecipeParams> implements IAssemblyRecipe {

    public HeatedPressingRecipe(HeatedProcessingRecipeParams params) {
        super(AllHeatedRecipeTypes.HEATED_PRESSING, params);
    }

    @Override
    public boolean matches(SingleRecipeInput inv, @NotNull Level worldIn) {
        if (inv.isEmpty())
            return false;
        return ingredients.get(0).test(inv.getItem(0));
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
        list.add(BuiltInRegistries.BLOCK.get(Create.asResource("mechanical_press")));
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> SequencedAssemblySubCategory.AssemblyPressing::new;
    }
}
