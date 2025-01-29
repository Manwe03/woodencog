package net.chauvedev.woodencog.recipes.advancedProcessingRecipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.baseRecipes.AdvancedRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition;
import java.util.ArrayList;

public class AdvancedRecipeSerializer<T extends ProcessingRecipe<?> > extends ProcessingRecipeSerializer<T> {

    private final AllRecipeTypes create_type;

    public AdvancedRecipeSerializer(ProcessingRecipeBuilder.ProcessingRecipeFactory<T> factory, AllRecipeTypes create_type) {
        super(factory);
        this.create_type = create_type;
    }

    public ProcessingRecipeSerializer<T> getCreateSerializer(){
        return this.create_type.getSerializer();
    }

    public AllRecipeTypes getCreate_type(){
        return this.create_type;
    }

    public ArrayList<ItemStack> getStackFromJson(JsonObject recipeJson){
        JsonArray results = recipeJson.get("results").getAsJsonArray();
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        results.forEach(jsonElement -> {
            JsonObject resultElement = jsonElement.getAsJsonObject();
            ItemStackProvider itemStackProvider = ItemStackProvider.fromJson(resultElement);
            ItemStack stack = itemStackProvider.getEmptyStack();
            if (resultElement.has("temperature")) {
                float temperature = resultElement.get("temperature").getAsFloat();
                if (!stack.hasTag()) {
                    stack.setTag(new CompoundTag());
                }
                stack.getTag().putFloat("temperature", temperature);
            }
            itemStacks.add(stack);
        });
        return itemStacks;
    }
    public ArrayList<ItemStackProvider> getProviderStackFromJson(JsonObject recipeJson){
        JsonArray results = recipeJson.get("results").getAsJsonArray();
        ArrayList<ItemStackProvider> itemStacks = new ArrayList<>();
        results.forEach(jsonElement -> {
            ItemStackProvider itemStackProvider = ItemStackProvider.fromJson(jsonElement.getAsJsonObject());
            itemStacks.add(itemStackProvider);
        });
        return itemStacks;
    }
    public JsonObject writeStackToJson(JsonObject recipeJson,ArrayList<ItemStack> stacks){
        JsonArray results = new JsonArray();
        stacks.forEach(stack -> {
            //If stack has temperature tag (mixin) serialize handles it
            results.add(new ProcessingOutput(stack, 1).serialize());
        });

        recipeJson.remove("results");
        recipeJson.add("results", results);

        return recipeJson;
    }

    @Override
    public T fromJson(ResourceLocation recipeLoc, JsonObject recipeJson, ICondition.IContext context) {
        ArrayList<ItemStack> stack = getStackFromJson(recipeJson);
        ArrayList<ItemStackProvider> providers = getProviderStackFromJson(recipeJson);
        recipeJson = writeStackToJson(recipeJson, stack);

        T recipe =  getCreateSerializer().fromJson(recipeLoc,recipeJson,context);

        AdvancedRecipe recipe1 = new AdvancedRecipe();

        recipe1.setItemStackProviders(providers);

        AllAdvancedRecipeTypes.registerRecipe(recipe,recipe1);


        return recipe;
    }

    @Override
    protected void writeToBuffer(FriendlyByteBuf buffer, T recipe) {
        getCreateSerializer().toNetwork(buffer,recipe);
    }

    @Override
    protected T readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        return getCreateSerializer().fromNetwork(recipeId, buffer);
    }
}
