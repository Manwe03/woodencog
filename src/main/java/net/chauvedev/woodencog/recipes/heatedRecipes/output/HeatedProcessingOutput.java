package net.chauvedev.woodencog.recipes.heatedRecipes.output;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class HeatedProcessingOutput extends DynamicProcessingOutput<Float> {

    public static final MapCodec<HeatedProcessingOutput> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ProcessingOutput.CODEC_NEW.fieldOf("internal").forGetter(DynamicProcessingOutput::getInternal),
            Codec.INT.optionalFieldOf("temperature", 0).forGetter(HeatedProcessingOutput::getTemperature),
            Codec.BOOL.optionalFieldOf("copy_heat", false).forGetter(HeatedProcessingOutput::getCopyHeat),
            Codec.INT.optionalFieldOf("cooling", 0).forGetter(HeatedProcessingOutput::getCooling)
    ).apply(instance, HeatedProcessingOutput::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, HeatedProcessingOutput> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    private final int temperature;
    private final boolean copyHeat;
    private final int cooling;

    public HeatedProcessingOutput(ProcessingOutput processingOutput, int temperature, boolean copyHeat, int cooling) {
        super(processingOutput);
        this.temperature = temperature;
        this.copyHeat = copyHeat;
        this.cooling = cooling;
    }

    public HeatedProcessingOutput(ItemStack stack, float chance, int temperature, boolean copyHeat, int cooling) {
        this(new ProcessingOutput(stack,chance),temperature,copyHeat,cooling);
    }

    public HeatedProcessingOutput(ItemStack stack, float chance, HeatedProcessingRecipeBuilder.HeatedIngridientParams params) {
        this(stack, chance, params.temperature, params.copyHeat, params.cooling);
    }

    public static HeatedProcessingOutput of(Item item, int count, int chance, int temperature, boolean copyheat, int cooling){
        return new HeatedProcessingOutput(new ItemStack(item,count),chance,temperature,copyheat,cooling);
    }

    @Override
    public ProcessingOutputTypes getType() {
        return ProcessingOutputTypes.HEATED;
    }

    public int getTemperature() {
        return temperature;
    }
    public boolean getCopyHeat(){
        return copyHeat;
    }
    public int getCooling(){
        return cooling;
    }

    /**
     * Returns the itemStack with the applied capability
     */
    @Override
    public ItemStack getStack() {
        ItemStack itemStack = super.getStack();
        if(!this.copyHeat){
            HeatCapability.setTemperature(itemStack,this.temperature);
        }
        return itemStack;
    }

    @Override
    public ItemStack rollOutput() {
        ItemStack outputStack = super.rollOutput();
        if(WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()){
            HeatCapability.setTemperature(outputStack,this.getTemperature());
            if(this.getCopyHeat()) { //If copy input item heat - cooling
                HeatCapability.setTemperature(outputStack, this.getDynamicData() - this.getCooling());
            }
        }
        return outputStack;
    }
}
