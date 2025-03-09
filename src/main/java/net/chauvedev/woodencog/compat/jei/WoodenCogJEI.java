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
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedMixingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.chauvedev.woodencog.utils.CreateBlocksAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
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
                AllHeatedRecipeTypes.HEATED_MIXING::getRecipes,
                List.of(()-> CreateBlocksAccess.BASIN.asItem().getDefaultInstance())
        ));
        allCategories.add(mixing);
        registration.addRecipeCategories(mixing);
        WoodenCog.LOGGER.info("Registered Mixing category "+ mixing.type.name());
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AbstractSimiContainerScreen.class, new SlotMover());

        registration.addGhostIngredientHandler(AbstractFilterScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(BlueprintScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(LinkedControllerScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(ScheduleScreen.class, new GhostIngredientHandler());
    }

    /*
    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        //IModPlugin.super.registerIngredients(registration);
        registration.register(HeatableIngredient.EMPTY.get, generateHeatedItems(), new HeatableIngredientHelper(), new HeatableItemRenderer());
    }

    public static List<ItemStack> generateHeatedItems() {
        List<ItemStack> heatedItems = new ArrayList<>();

        for (HeatedProcessingRecipe<?> recipe : AllHeatedRecipeTypes.HEATED_MIXING.getRecipes()) {
            for (HeatableIngredient ingredient : recipe.getHeatedIngredients()) {
                for (ItemStack stack : ingredient.getItems()) {
                    ItemStack heatedStack = stack.copy();
                    HeatCapability.setTemperature(heatedStack, ((HeatableIngredientAccessor) ingredient).getMinTemp());
                    heatedItems.add(heatedStack);
                }
            }
        }

        return heatedItems;
    }
    */

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