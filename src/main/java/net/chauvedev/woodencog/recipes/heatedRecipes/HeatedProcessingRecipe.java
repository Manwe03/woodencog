package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonObject;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.dries007.tfc.common.recipes.outputs.CopyHeatModifier;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class HeatedProcessingRecipe<T extends Container> implements Recipe<T> {
    protected ResourceLocation id;
    protected NonNullList<HeatableIngredient> ingredients;
    protected NonNullList<HeatedProcessingOutput> results;
    protected NonNullList<FluidIngredient> fluidIngredients;
    protected NonNullList<FluidStack> fluidResults;
    protected int processingDuration;
    protected HeatCondition requiredHeat;
    private RecipeType<?> type;
    private RecipeSerializer<?> serializer;
    private IRecipeTypeInfo typeInfo;
    private Supplier<ItemStack> forcedResult = null;

    public HeatedProcessingRecipe(IRecipeTypeInfo typeInfo, HeatedProcessingRecipeBuilder.HeatedProcessingRecipeParams params) {
        this.typeInfo = typeInfo;
        this.processingDuration = params.processingDuration;
        this.fluidIngredients = params.fluidIngredients;
        this.fluidResults = params.fluidResults;
        this.serializer = typeInfo.getSerializer();
        this.requiredHeat = params.requiredHeat;
        this.ingredients = params.ingredients;
        this.type = typeInfo.getType();
        this.results = params.results;
        this.id = params.id;
        this.validate(typeInfo.getId());
    }

    protected abstract int getMaxInputCount();

    protected abstract int getMaxOutputCount();

    protected boolean canRequireHeat() {
        return false;
    }

    protected boolean canSpecifyDuration() {
        return false;
    }

    protected int getMaxFluidInputCount() {
        return 0;
    }

    protected int getMaxFluidOutputCount() {
        return 0;
    }

    private void validate(ResourceLocation recipeTypeId) {
        String messageHeader = "Your custom " + recipeTypeId + " recipe (" + this.id.toString() + ")";
        Logger logger = Create.LOGGER;
        int ingredientCount = this.ingredients.size();
        int outputCount = this.results.size();
        if (ingredientCount > this.getMaxInputCount()) {
            logger.warn(messageHeader + " has more item inputs (" + ingredientCount + ") than supported (" + this.getMaxInputCount() + ").");
        }

        if (outputCount > this.getMaxOutputCount()) {
            logger.warn(messageHeader + " has more item outputs (" + outputCount + ") than supported (" + this.getMaxOutputCount() + ").");
        }

        if (this.processingDuration > 0 && !this.canSpecifyDuration()) {
            logger.warn(messageHeader + " specified a duration. Durations have no impact on this type of recipe.");
        }

        if (this.requiredHeat != HeatCondition.NONE && !this.canRequireHeat()) {
            logger.warn(messageHeader + " specified a heat condition. Heat conditions have no impact on this type of recipe.");
        }

        ingredientCount = this.fluidIngredients.size();
        outputCount = this.fluidResults.size();
        if (ingredientCount > this.getMaxFluidInputCount()) {
            logger.warn(messageHeader + " has more fluid inputs (" + ingredientCount + ") than supported (" + this.getMaxFluidInputCount() + ").");
        }

        if (outputCount > this.getMaxFluidOutputCount()) {
            logger.warn(messageHeader + " has more fluid outputs (" + outputCount + ") than supported (" + this.getMaxFluidOutputCount() + ").");
        }

    }

    /**
     * @deprecated Do not use, Use -> getHeatedIngredients();
     */
    public NonNullList<Ingredient> getIngredients() {
        return null;
    }
    public NonNullList<HeatableIngredient> getHeatedIngredients(){
        return this.ingredients;
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        return this.fluidIngredients;
    }

    public List<HeatedProcessingOutput> getRollableResults() {
        return this.results;
    }

    public NonNullList<FluidStack> getFluidResults() {
        return this.fluidResults;
    }

    /*
    public List<ItemStack> getRollableResultsAsItemStacks() {
        return (List) this.getRollableResults().stream().map(ProcessingOutput::getStack).collect(Collectors.toList());
    }*/

    public void enforceNextResult(Supplier<ItemStack> stack) {
        this.forcedResult = stack;
    }

    public List<ItemStack> rollResults() {
        return this.rollResults(this.getRollableResults(),List.of());
    }

    public List<ItemStack> rollResults(List<Float> tempList) {
        return this.rollResults(this.getRollableResults(),tempList);
    }

    public List<ItemStack> rollResults(List<HeatedProcessingOutput> rollableResults, List<Float> tempList) {
        List<ItemStack> results = new ArrayList();
        for(int i = 0; i < rollableResults.size(); ++i) {
            HeatedProcessingOutput output = rollableResults.get(i);
            ItemStack stack = i == 0 && this.forcedResult != null ? (ItemStack)this.forcedResult.get() : output.rollOutput();
            if (!stack.isEmpty()) {
                if(WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()){
                    HeatCapability.setTemperature(stack,output.getTemperature());
                    if(output.getCopyHeat()) { //If copy input item heat - cooling
                        Float temp = tempList.get(i);
                        if (temp != null) HeatCapability.setTemperature(stack, temp - output.getCooling());
                    }
                }
                results.add(stack);
            }
        }

        return results;
    }

    public int getProcessingDuration() {
        return this.processingDuration;
    }

    public HeatCondition getRequiredHeat() {
        return this.requiredHeat;
    }

    public ItemStack assemble(T inv, RegistryAccess registryAccess) {
        return this.getResultItem(registryAccess);
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.getRollableResults().isEmpty() ? ItemStack.EMPTY : ((ProcessingOutput)this.getRollableResults().get(0)).getStack();
    }

    public boolean isSpecial() {
        return true;
    }

    public String getGroup() {
        return "processing";
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    public RecipeType<?> getType() {
        return this.type;
    }

    public IRecipeTypeInfo getTypeInfo() {
        return this.typeInfo;
    }

    public void readAdditional(JsonObject json) {
    }

    public void readAdditional(FriendlyByteBuf buffer) {
    }

    public void writeAdditional(JsonObject json) {
    }

    public void writeAdditional(FriendlyByteBuf buffer) {
    }
}
