package net.chauvedev.woodencog.mixin.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingIngredient;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.List;

@Mixin(value = ProcessingRecipeSerializer.class, remap = false)
public class MixinProcessingRecipeSerializer <T extends ProcessingRecipe<?>>{

    /**
     * @return Returns tfc HeatableIngredient if necessary. If not, return default implementation
     * @author Manwe
     * @see com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer
     * @see net.dries007.tfc.common.recipes.ingredients.HeatableIngredient
     * Create uses custom recipe results "ProccesingOutput" and it's serilaizer
     * For ingredients it uses default minecraft ingredient and serializer, this is a replacement to take into account tfc recipes
     */
    @Redirect(
        method = "readFromJson",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/crafting/Ingredient;fromJson(Lcom/google/gson/JsonElement;)Lnet/minecraft/world/item/crafting/Ingredient;")
    )
    private Ingredient redirectFromJson(JsonElement jsonE){

        if(jsonE instanceof JsonObject jsonO){
            if(jsonO.has("min_temp")){
                System.out.println("Serializer: HeatableIngredient");
                System.out.println(jsonO);
                HeatedProcessingIngredient ingredient = HeatedProcessingIngredient.fromJson(jsonO);

                System.out.println(Arrays.toString(ingredient.getItems()));

                return ingredient;
            }
        }

        return Ingredient.fromJson(jsonE);
    }

    /**
     * @author Manwe
     * @reason Redirect to read HeatedProcessingIngredients
     */
    @Redirect(
            method = "readFromBuffer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/Ingredient;fromNetwork(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/world/item/crafting/Ingredient;"
            )
    )
    private Ingredient redirectFromNetwork(FriendlyByteBuf pBuffer) {
        return HeatedProcessingIngredient.fromBuffer(pBuffer);
    }

    /**
     * @author Manwe
     * @reason Write HeatedProcessingIngredients to buffer when flag is true
     */
    @Overwrite
    protected void writeToBuffer(FriendlyByteBuf buffer, T recipe) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        NonNullList<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();
        List<ProcessingOutput> outputs = recipe.getRollableResults();
        NonNullList<FluidStack> fluidOutputs = recipe.getFluidResults();

        buffer.writeVarInt(ingredients.size());
        ingredients.forEach((i) -> {
            //Modified
            HeatedProcessingIngredient.toBuffer(buffer,i);
            //Modified
        });
        buffer.writeVarInt(fluidIngredients.size());
        fluidIngredients.forEach((i) -> {
            i.write(buffer);
        });
        buffer.writeVarInt(outputs.size());
        outputs.forEach((o) -> {
            o.write(buffer);
        });
        buffer.writeVarInt(fluidOutputs.size());
        fluidOutputs.forEach((o) -> {
            o.writeToPacket(buffer);
        });
        buffer.writeVarInt(recipe.getProcessingDuration());
        buffer.writeVarInt(recipe.getRequiredHeat().ordinal());
        recipe.writeAdditional(buffer);
    }
}
