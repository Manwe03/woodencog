package net.chauvedev.woodencog.recipes.advancedProcessingRecipe.baseRecipes;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;

public class AdvancedRecipe implements SetItemStackProvider{

    ArrayList<ItemStackProvider> list = new ArrayList<>();

    public AdvancedRecipe() {}

    @Override
    public void setItemStackProviders( ArrayList<ItemStackProvider>  list) {
        this.list = list;
    }

    @Override
    public  ArrayList<ItemStackProvider>  getItemStackProviders() {
        return this.list;
    }


    @Override
    public ArrayList<ProcessingOutput> onResult(NonNullList<ProcessingOutput> results) {
        ArrayList<ProcessingOutput> newList = new ArrayList<>();

        final int[] count = {-1};

        results.forEach(processingOutput -> {
            count[0] = count[0] +1;
            newList.add(new ProcessingOutput(list.get(count[0]).getStack(processingOutput.getStack()),processingOutput.getChance()));
        });

        return newList;
    }

    @Override
    public ItemStack onResultStackSingle(ItemStack input,ItemStack stack) {
        boolean hasItem = false;
        ItemStackProvider output = null;
        for (ItemStackProvider itemStackProvider: this.list) {
            if (input.is(itemStackProvider.getEmptyStack().getItem())){
                output = itemStackProvider;
                hasItem = true;
            }
        }
        if (!hasItem){
            return stack;
        }

        ItemStackProvider provider = new ItemStackProvider(input,output.modifiers());
        ItemStack finalStack = provider.getStack(input);
        finalStack.setTag(stack.getTag());
        return finalStack;
    }


}
