package net.chauvedev.woodencog.compat.jei;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class WoodenCogRecipeCategory<T extends HeatedProcessingRecipe<?>> implements IRecipeCategory<T> {
    private static final IDrawable BASIC_SLOT = asDrawable(AllGuiTextures.JEI_SLOT);
    private static final IDrawable CHANCE_SLOT = asDrawable(AllGuiTextures.JEI_CHANCE_SLOT);

    protected final RecipeType<T> type;
    protected final Component title;
    protected final IDrawable background;
    protected final IDrawable icon;

    private final Supplier<List<T>> recipes;
    private final List<Supplier<ItemStack>> catalysts;

    public WoodenCogRecipeCategory(Info<T> info) {
        this.type = info.recipeType();
        this.title = info.title();
        this.background = info.background();
        this.icon = info.icon();
        this.recipes = info.recipes();
        this.catalysts = info.catalysts();
    }

    @Override
    public RecipeType<T> getRecipeType() {
        return type;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    public void registerRecipes(IRecipeRegistration registration) {
        recipes.get().forEach(t -> System.out.println("Registered Recipe: "+t.getId()));
        registration.addRecipes(type, recipes.get());
    }

    public void registerCatalysts(IRecipeCatalystRegistration registration) {
        catalysts.forEach(s -> registration.addRecipeCatalyst(s.get(), type));
    }

    public static IDrawable getRenderedSlot() {
        return BASIC_SLOT;
    }
    public static IDrawable getRenderedSlot(ProcessingOutput output) {
        return getRenderedSlot(output.getChance());
    }
    public static IDrawable getRenderedSlot(float chance) {
        if (chance == 1)
            return BASIC_SLOT;

        return CHANCE_SLOT;
    }

    public static List<FluidStack> withImprovedVisibility(List<FluidStack> stacks) {
        return stacks.stream()
                .map(WoodenCogRecipeCategory::withImprovedVisibility)
                .collect(Collectors.toList());
    }

    public static FluidStack withImprovedVisibility(FluidStack stack) {
        FluidStack display = stack.copy();
        int displayedAmount = (int) (stack.getAmount() * .75f) + 250;
        display.setAmount(displayedAmount);
        return display;
    }

    public static IRecipeSlotRichTooltipCallback addFluidTooltip(int mbAmount) {
        return (view, tooltip) -> {
            Optional<FluidStack> displayed = view.getDisplayedIngredient(ForgeTypes.FLUID_STACK);
            if (displayed.isEmpty())
                return;

            FluidStack fluidStack = displayed.get();

            if (fluidStack.getFluid().isSame(AllFluids.POTION.get())) {
                ArrayList<Component> potionTooltip = new ArrayList<>();
                PotionFluidHandler.addPotionTooltip(fluidStack, potionTooltip, 1);
                tooltip.addAll(potionTooltip.stream().toList());
            }

            int amount = mbAmount == -1 ? fluidStack.getAmount() : mbAmount;
            Component text = Component.literal(String.valueOf(amount)).append(CreateLang.translateDirect("generic.unit.millibuckets")).withStyle(ChatFormatting.GOLD);
            tooltip.add(text);
        };
    }

    public static IRecipeSlotRichTooltipCallback addStochasticTooltip(ProcessingOutput output) {
        return (view, tooltip) -> {
            float chance = output.getChance();
            if (chance != 1)
                tooltip.add(CreateLang.translateDirect("recipe.processing.chance", chance < 0.01 ? "<1" : (int) (chance * 100))
                        .withStyle(ChatFormatting.GOLD));
        };
    }

    protected static IDrawable asDrawable(AllGuiTextures texture) {
        return new IDrawable() {
            @Override
            public int getWidth() {
                return texture.getWidth();
            }

            @Override
            public int getHeight() {
                return texture.getHeight();
            }

            @Override
            public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
                texture.render(graphics, xOffset, yOffset);
            }
        };
    }

    @Override
    public int getHeight() {
        //System.out.println("getHeight: "+background.getHeight());
        return background.getHeight();
    }

    @Override
    public int getWidth() {
        //System.out.println("getWidth: "+background.getWidth());
        return background.getWidth();
    }

    public record Info<T extends HeatedProcessingRecipe<?>>(RecipeType<T> recipeType, Component title, IDrawable background, IDrawable icon, Supplier<List<T>> recipes, List<Supplier<ItemStack>> catalysts) { }

}
