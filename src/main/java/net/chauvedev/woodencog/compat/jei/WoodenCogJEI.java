package net.chauvedev.woodencog.compat.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.*;
import com.simibubi.create.content.equipment.blueprint.BlueprintScreen;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSetItemScreen;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterScreen;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerScreen;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.item.ItemHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeParams;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedCompactingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedMixingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.chauvedev.woodencog.utils.CogUtil;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.jei.category.HeatingRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

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

    private void loadCategories() {
        allCategories.clear();

        WoodenCogRecipeCategory<?> heatedMixing = builder(HeatedMixingRecipe.class)
                .addTypedRecipes(AllHeatedRecipeTypes.HEATED_MIXING)
                .catalyst(AllBlocks.MECHANICAL_MIXER::get)
                .catalyst(AllBlocks.BASIN::get)
                .doubleItemIcon(AllBlocks.MECHANICAL_MIXER.get(), AllBlocks.BASIN.get())
                .emptyBackground(177, 103)
                .build("heated_mixing", HeatedMixingCategory::new);

        WoodenCogRecipeCategory<?> heatedPressing = builder(HeatedPressingRecipe.class)
                .addTypedRecipes(AllHeatedRecipeTypes.HEATED_PRESSING)
                .catalyst(AllBlocks.MECHANICAL_PRESS::get)
                .catalyst(AllBlocks.BASIN::get)
                .doubleItemIcon(AllBlocks.MECHANICAL_PRESS.get(), AllItems.IRON_SHEET.get())
                .emptyBackground(177, 103)
                .build("heated_pressing", HeatedPressingCategory::new);

        WoodenCogRecipeCategory<?> heatedCompacting = builder(HeatedCompactingRecipe.class)
                .addTypedRecipes(AllHeatedRecipeTypes.HEATED_COMPACTING)
                .catalyst(AllBlocks.MECHANICAL_PRESS::get)
                .catalyst(AllBlocks.BASIN::get)
                .doubleItemIcon(AllBlocks.MECHANICAL_PRESS.get(), AllBlocks.BASIN.get())
                .emptyBackground(177, 103)
                .build("heated_compacting", HeatedCompactingCategory::new);
    }

    @Override
    @NotNull
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();

        allCategories.forEach(c -> c.registerRecipes(registration));

        registration.addRecipes(RecipeTypes.CRAFTING, ToolboxColoringRecipeMaker.createRecipes().toList());
        //Register dynamic blasting recipes in JEI
        registration.addRecipes(CREATE_FAN_BLASTING, getTFCHeatingAsCreateFanBlasting());
    }

    private static final mezz.jei.api.recipe.RecipeType<RecipeHolder<AbstractCookingRecipe>> CREATE_FAN_BLASTING =
            mezz.jei.api.recipe.RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create", "fan_blasting")
            );

    private List<RecipeHolder<AbstractCookingRecipe>> getTFCHeatingAsCreateFanBlasting() {
        Level level = Minecraft.getInstance().level;
        if (level == null) return List.of();

        return level.getRecipeManager()
                .getAllRecipesFor(TFCRecipeTypes.HEATING.get())
                .stream()
                .map(this::toFanBlastingRecipe)
                .filter(Objects::nonNull)
                .toList();
    }

    private RecipeHolder<AbstractCookingRecipe> toFanBlastingRecipe(RecipeHolder<HeatingRecipe> holder) {
        HeatingRecipe heating = holder.value();

        ItemStack[] inputs = heating.getIngredient().getItems();
        if (inputs.length == 0) return null;

        ItemStack input = inputs[0].copy();
        ItemStack output = heating.assembleItem(input);

        if (output.isEmpty()) return null;
        if (!heating.assembleFluid(input).isEmpty()) return null;

        for(ItemStack itemStack : heating.getIngredient().getItems()){
            HeatCapability.setStaticTemperature(itemStack,heating.getTemperature());
        }

        AbstractCookingRecipe recipe = new BlastingRecipe(
                "",
                CookingBookCategory.MISC,
                heating.getIngredient(),
                output,
                0.0F, 0
        );

        ResourceLocation id = WoodenCog.asWoodencogResource("as_fan_blasting",holder.id(),"");
        return new RecipeHolder<>(id, recipe);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(c -> c.registerCatalysts(registration));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new BlueprintTransferHandler(), RecipeTypes.CRAFTING);
        registration.addUniversalRecipeTransferHandler(new StockKeeperTransferHandler(registration.getJeiHelpers()));
    }

    @Override
    public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
        PotionFluidSubtypeInterpreter interpreter = new PotionFluidSubtypeInterpreter();
        PotionFluid potionFluid = AllFluids.POTION.get();
        registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, potionFluid.getSource(), interpreter);
        registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, potionFluid.getFlowing(), interpreter);
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
        List<Holder.Reference<Potion>> potions = registryAccess.lookupOrThrow(Registries.POTION)
                .listElements()
                .toList();
        Collection<FluidStack> potionFluids = new ArrayList<>(potions.size() * 3);
        Set<Set<Holder<MobEffect>>> visitedEffects = new HashSet<>();
        for (Holder.Reference<Potion> potion : potions) {
            // @goshante: Ingame potion fluids always have Bottle tag that specifies
            // to what bottle type this potion belongs
            // Potion fluid without this tag wouldn't be recognized by other mods

//			for (PotionFluid.BottleType bottleType : PotionFluid.BottleType.values()) {
//				FluidStack potionFluid = PotionFluid.of(1000, new PotionContents(potion), bottleType);
//				potionFluids.add(potionFluid);
//			}

            PotionContents potionContents = new PotionContents(potion);

            if (potionContents.hasEffects()) {
                Set<Holder<MobEffect>> effectSet = new HashSet<>();
                potionContents.forEachEffect(mei -> effectSet.add(mei.getEffect()));
                if (!visitedEffects.add(effectSet))
                    continue;
            }

            potionFluids.add(PotionFluid.of(1000, potionContents, PotionFluid.BottleType.REGULAR));
        }
        registration.addExtraIngredients(NeoForgeTypes.FLUID_STACK, potionFluids);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AbstractSimiContainerScreen.class, new SlotMover());

        registration.addGhostIngredientHandler(AbstractFilterScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(BlueprintScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(LinkedControllerScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(ScheduleScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(RedstoneRequesterScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(FactoryPanelSetItemScreen.class, new GhostIngredientHandler());

        registration.addGuiContainerHandler(StockKeeperRequestScreen.class, new StockKeeperGuiContainerHandler(ingredientManager));
    }

    //FACTORY//
    private <T extends HeatedProcessingRecipe<?,?>> CategoryBuilder<T> builder(Class<T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private class CategoryBuilder<T extends HeatedProcessingRecipe<?,?>> extends WoodenCogRecipeCategory.Builder<T> {
        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public WoodenCogRecipeCategory<T> build(ResourceLocation id, WoodenCogRecipeCategory.Factory<T> factory) {
            WoodenCogRecipeCategory<T> category = super.build(id, factory);
            allCategories.add(category);
            return category;
        }
    }

    private <C extends Container, T extends HeatedProcessingRecipe<RecipeInput, HeatedProcessingRecipeParams>> Supplier<List<RecipeHolder<T>>> getRecipes(RecipeType<T> type){
        Level level = Minecraft.getInstance().level;
        if(level != null && level.isClientSide){
            return () -> // Filter specific recipes
                    level.getRecipeManager().getAllRecipesFor(type).stream()
                            .filter(recipe -> recipe.value() instanceof HeatedProcessingRecipe<?,?>) //filter
                            .toList();
        }
        return List::of;
    }


    //UTILITIES
    public static boolean doInputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
        if(recipe1 instanceof HeatedPressingRecipe recipe1H && recipe2 instanceof HeatedPressingRecipe recipe2H){
            if (recipe1H.getIngredients().isEmpty() || recipe2H.getIngredients().isEmpty()) return false;

            ItemStack[] matchingStacks = recipe1H.getIngredients().get(0).getItems();
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
        if (CogUtil.logConditional(Minecraft.getInstance().level == null, WoodenCogJEI.class,"minecraft.level is null")) return false;

        RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
        return ItemHelper.sameItem(recipe1.getResultItem(registryAccess), recipe2.getResultItem(registryAccess));
    }

}
