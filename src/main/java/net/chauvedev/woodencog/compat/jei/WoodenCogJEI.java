package net.chauvedev.woodencog.compat.jei;

import com.simibubi.create.compat.jei.*;
import com.simibubi.create.content.equipment.blueprint.BlueprintScreen;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerScreen;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.item.ItemHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedCompactingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedMixingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.chauvedev.woodencog.utils.CreateBlocksAccess;
import net.chauvedev.woodencog.utils.CreateItemAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class WoodenCogJEI implements IModPlugin {

    private static final ResourceLocation ID = WoodenCog.asResource("jei_plugin");

    private final List<WoodenCogRecipeCategory<?>> allCategories = new ArrayList<>();
    private IIngredientManager ingredientManager;

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        HeatedMixingCategory mixing = new HeatedMixingCategory(new WoodenCogRecipeCategory.Info<>(
                new mezz.jei.api.recipe.RecipeType<>(WoodenCog.asResource("heated_mixin"),HeatedMixingRecipe.class),
                Component.translatable("category.woodencog.heated_mixing"),
                new EmptyBackground(177,103),
                new DoubleItemIcon(() -> new ItemStack(CreateBlocksAccess.MECHANICAL_MIXER.asItem()), () -> new ItemStack(CreateBlocksAccess.BASIN.asItem())),
                this.getRecipes(AllHeatedRecipeTypes.HEATED_MIXING.getType()),
                List.of(()-> CreateBlocksAccess.MECHANICAL_MIXER.asItem().getDefaultInstance(), ()-> CreateBlocksAccess.BASIN.asItem().getDefaultInstance())
        ));
        allCategories.add(mixing);
        registration.addRecipeCategories(mixing);

        HeatedPressingCategory pressing = new HeatedPressingCategory(new WoodenCogRecipeCategory.Info<>(
                new mezz.jei.api.recipe.RecipeType<>(WoodenCog.asResource("heated_pressing"),HeatedPressingRecipe.class),
                Component.translatable("category.woodencog.heated_pressing"),
                new EmptyBackground(177,103),
                new DoubleItemIcon(() -> new ItemStack(CreateBlocksAccess.MECHANICAL_PRESS.asItem()), ()-> new ItemStack(CreateItemAccess.IRON_PLATE)),
                this.getRecipes(AllHeatedRecipeTypes.HEATED_PRESSING.getType()),
                List.of(()-> CreateBlocksAccess.MECHANICAL_PRESS.asItem().getDefaultInstance())
        ));
        allCategories.add(pressing);
        registration.addRecipeCategories(pressing);

        HeatedCompactingCategory compacting = new HeatedCompactingCategory(new WoodenCogRecipeCategory.Info<>(
                new mezz.jei.api.recipe.RecipeType<>(WoodenCog.asResource("heated_compacting"),HeatedCompactingRecipe.class),
                Component.translatable("category.woodencog.heated_compacting"),
                new EmptyBackground(177,103),
                new DoubleItemIcon(() -> new ItemStack(CreateBlocksAccess.MECHANICAL_PRESS.asItem()), () -> new ItemStack(CreateBlocksAccess.BASIN.asItem())),
                this.getRecipes(AllHeatedRecipeTypes.HEATED_COMPACTING.getType()),
                List.of(()-> CreateBlocksAccess.MECHANICAL_PRESS.asItem().getDefaultInstance())
        ));
        allCategories.add(compacting);
        registration.addRecipeCategories(compacting);


    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        allCategories.forEach(woodenCogRecipeCategory -> woodenCogRecipeCategory.registerRecipes(registration)); //register recipe list in category
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(createRecipeCategory -> createRecipeCategory.registerCatalysts(registration));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new BlueprintTransferHandler(), RecipeTypes.CRAFTING);
    }

    private <C extends Container, T extends HeatedProcessingRecipe<C>> Supplier<List<T>> getRecipes(RecipeType<T> type){
        Level level = Minecraft.getInstance().level;
        if(level != null && level.isClientSide){
            return () -> level.getRecipeManager().getAllRecipesFor(type).stream()
                    .filter(recipe -> recipe instanceof HeatedProcessingRecipe<?>)  // Filter specific recipes
                    .map(recipe -> (T) recipe).toList();
        }
        return List::of;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AbstractSimiContainerScreen.class, new SlotMover());

        registration.addGhostIngredientHandler(AbstractFilterScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(BlueprintScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(LinkedControllerScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(ScheduleScreen.class, new GhostIngredientHandler());
    }

    public static boolean doInputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
        if(recipe1 instanceof HeatedPressingRecipe recipe1H && recipe2 instanceof HeatedPressingRecipe recipe2H){
            if (recipe1H.getHeatedIngredients().isEmpty() || recipe2H.getHeatedIngredients().isEmpty()) return false;

            ItemStack[] matchingStacks = recipe1H.getHeatedIngredients().get(0).getItems();
            if (matchingStacks.length == 0) {
                return false;
            }
            return recipe2H.getIngredients().get(0).test(matchingStacks[0]);
        }else {
            if (recipe1.getIngredients().isEmpty() || recipe2.getIngredients().isEmpty()) return false;

            ItemStack[] matchingStacks = recipe1.getIngredients().get(0).getItems();
            if (matchingStacks.length == 0) {
                return false;
            }
            return recipe2.getIngredients().get(0).test(matchingStacks[0]);
        }
    }

    public static boolean doOutputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
        RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
        return ItemHelper.sameItem(recipe1.getResultItem(registryAccess), recipe2.getResultItem(registryAccess));
    }

}