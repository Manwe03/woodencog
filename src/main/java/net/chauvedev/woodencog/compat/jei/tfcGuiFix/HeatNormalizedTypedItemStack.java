package net.chauvedev.woodencog.compat.jei.tfcGuiFix;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class HeatNormalizedTypedItemStack implements ITypedIngredient<ItemStack>{
    Holder<Item> itemHolder;
    CompoundTag tag;
    IHeat heat;

    public HeatNormalizedTypedItemStack(ItemStack ingredient) {
        this.itemHolder = ingredient.getItemHolder();
        this.tag = ingredient.getTag();
        this.heat = ingredient.getCapability(HeatCapability.CAPABILITY).resolve().orElse(null);
    }
    public HeatNormalizedTypedItemStack(HeatTypedItemStack typedItemStack) {
        this.itemHolder = typedItemStack.itemHolder();
        this.tag = typedItemStack.tag();
        this.heat = typedItemStack.heat();
    }

    public static ITypedIngredient<ItemStack> create(ItemStack itemStack) {
        return new HeatNormalizedTypedItemStack(itemStack);
    }

    public static ITypedIngredient<ItemStack> normalize(ITypedIngredient<ItemStack> typedIngredient) {
        if (typedIngredient instanceof HeatNormalizedTypedItemStack normalized) {
            return normalized;
        } else if (typedIngredient instanceof HeatTypedItemStack typedItemStack) {
            return new HeatNormalizedTypedItemStack(typedItemStack);
        } else {
            return create(typedIngredient.getIngredient());
        }
    }

    public ItemStack getIngredient() {
        //WoodenCog.LOGGER.info("HeatTypedItemStack getIngredient");
        ItemStack itemStack = new ItemStack(this.itemHolder, 1);
        itemStack.setTag(this.tag);
        if(this.heat != null){
            //WoodenCog.LOGGER.info("HeatNormalizedTypedItemStack returned "+itemStack.getItem()+" ingredient with "+ heat.getTemperature());
            HeatCapability.setTemperature(itemStack, heat.getTemperature());
        }
        return itemStack;
    }

    public Optional<ItemStack> getItemStack() {
        return Optional.of(this.getIngredient());
    }

    public IIngredientType<ItemStack> getType() {
        return VanillaTypes.ITEM_STACK;
    }

    public String toString() {
        return "NormalizedTypedItemStack{itemHolder=" + this.itemHolder + ", tag=" + this.tag + "}";
    }

    public Holder<Item> itemHolder() {
        return this.itemHolder;
    }

    public CompoundTag tag() {
        return this.tag;
    }
}
