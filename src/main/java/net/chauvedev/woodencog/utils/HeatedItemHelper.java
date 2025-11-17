package net.chauvedev.woodencog.utils;

import net.createmod.catnip.data.Pair;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;

public class HeatedItemHelper {

    public static <T extends HeatableIngredient> List<Pair<T, MutableInt>> condenseIngredients(NonNullList<T> recipeIngredients) {
        List<Pair<T, MutableInt>> actualIngredients = new ArrayList<>();
        Ingredients: for (T igd : recipeIngredients) {
            for (Pair<T, MutableInt> pair : actualIngredients) {
                ItemStack[] stacks1 = pair.getFirst().getItems();
                ItemStack[] stacks2 = igd.getItems();
                if (stacks1.length != stacks2.length)
                    continue;
                for (int i = 0; i <= stacks1.length; i++) {
                    if (i == stacks1.length) {
                        pair.getSecond().increment();
                        continue Ingredients;
                    }
                    if (!ItemStack.matches(stacks1[i], stacks2[i])) break;
                }
            }
            actualIngredients.add(Pair.of(igd, new MutableInt(1)));
        }
        return actualIngredients;
    }
}
