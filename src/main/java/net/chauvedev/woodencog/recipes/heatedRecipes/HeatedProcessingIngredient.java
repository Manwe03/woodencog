package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonObject;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.util.JsonHelpers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

public class HeatedProcessingIngredient extends Ingredient {

    private final int minTemp;
    private final int maxTemp;

    protected HeatedProcessingIngredient(ItemStack itemStack, int minTemp, int maxTemp) {
        super(Stream.of(new ItemValue(itemStack)));
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }
    protected HeatedProcessingIngredient(ItemStack itemStack, int minTemp) {
        super(Stream.of(new ItemValue(itemStack)));
        this.minTemp = minTemp;
        this.maxTemp = Integer.MAX_VALUE;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        if (super.test(stack) && stack != null && !stack.isEmpty())
        {
            final @Nullable IHeat heat = HeatCapability.get(stack);
            return heat != null && heat.getTemperature() >= minTemp && heat.getTemperature() <= maxTemp;
        }
        return false;
    }

    public static HeatedProcessingIngredient fromJson(JsonObject json) {
        int count = JsonHelpers.getAsInt(json,"count",1);
        Item item = JsonHelpers.getAsItem(json, "item", null);
        if(item == null) WoodenCog.LOGGER.error("No valid item in recipe");
        final ItemStack itemStack = new ItemStack(JsonHelpers.getAsItem(json, "item"),count);
        final int min = JsonHelpers.getAsInt(json, "min_temp", Integer.MIN_VALUE);
        final int max = JsonHelpers.getAsInt(json, "max_temp", Integer.MAX_VALUE);
        System.out.println(itemStack);
        System.out.println("FROM JSON");
        return new HeatedProcessingIngredient(itemStack, min, max);
    }

    public static Ingredient fromBuffer(FriendlyByteBuf pBuffer){
        boolean isHeated = pBuffer.readBoolean();

        Ingredient baseIngredient = Ingredient.fromNetwork(pBuffer); //Original Call fromNetwork

        if(isHeated) {
            int minTemp = pBuffer.readInt();
            int maxTemp = pBuffer.readInt();
            return new HeatedProcessingIngredient(baseIngredient.getItems()[0], minTemp, maxTemp);
        }
        return baseIngredient;
    }

    public static void toBuffer(FriendlyByteBuf pBuffer, Ingredient ingredient) {
        boolean isHeated = ingredient instanceof HeatedProcessingIngredient;
        pBuffer.writeBoolean(isHeated);

        ingredient.toNetwork(pBuffer); //Original Call toNetwork

        if(isHeated){
            pBuffer.writeInt(((HeatedProcessingIngredient)ingredient).minTemp);
            pBuffer.writeInt(((HeatedProcessingIngredient)ingredient).minTemp);
        }
    }
}
