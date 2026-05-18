package net.chauvedev.woodencog.recipes.heatedRecipes.output;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

/**
 * This class obstracts outputs that need world information before calling rollOutput()
 * setDynamicData() must be called before rollOutput()
 * @param <T> type of the dynamic data
 */
public abstract class DynamicProcessingOutput<T> {

    public static final DynamicProcessingOutput<?> EMPTY;

    public static final Codec<DynamicProcessingOutput<?>> CODEC = ProcessingOutputTypes.CODEC.dispatch(
            "type", DynamicProcessingOutput::getType, ProcessingOutputTypes::codec);

    public static final StreamCodec<RegistryFriendlyByteBuf, DynamicProcessingOutput<?>> STREAM_CODEC = StreamCodec.of(
            DynamicProcessingOutput::encode,
            DynamicProcessingOutput::decode);

    private final ProcessingOutput internal;
    private T data;

    public DynamicProcessingOutput(ProcessingOutput internal) {
        this.internal = internal;
    }

    public void setDynamicData(T data){
        this.data = data;
    }

    public T getDynamicData(){
        return this.data;
    }

    public abstract ProcessingOutputTypes getType();

    public ProcessingOutput getInternal() {
        return this.internal;
    }

    public ItemStack getStack() {
        return internal.getStack();
    }

    public float getChance() {
        return internal.getChance();
    }

    public ItemStack rollOutput() {
        return rollOutput(RandomSource.create());
    }

    public ItemStack rollOutput(RandomSource randomSource) {
        return internal.rollOutput(randomSource);
    }

    public static <T> void setDynamic(DynamicProcessingOutput<T> output, T data) {
        output.setDynamicData(data);
    }

    @SuppressWarnings("unchecked")
    public static <T> void setDynamicData(DynamicProcessingOutput<T> output, Object data) {
        output.setDynamicData((T) data);
    }

    private static void encode(RegistryFriendlyByteBuf buffer, DynamicProcessingOutput<?> output) {
        ByteBufCodecs.VAR_INT.encode(buffer, output.getType().ordinal());
        switch (output.getType()) {
            case HEATED -> HeatedProcessingOutput.STREAM_CODEC.encode(buffer, (HeatedProcessingOutput) output);
            case FOOD -> FoodProcessingOutput.STREAM_CODEC.encode(buffer, (FoodProcessingOutput) output);
            case SALAD -> SaladProcessingOutput.STREAM_CODEC.encode(buffer, (SaladProcessingOutput) output);
            case SOUP -> SoupProcessingOutput.STREAM_CODEC.encode(buffer, (SoupProcessingOutput) output);
        }
    }

    private static DynamicProcessingOutput<?> decode(RegistryFriendlyByteBuf buffer) {
        return switch (ProcessingOutputTypes.values()[ByteBufCodecs.VAR_INT.decode(buffer)]) {
            case HEATED -> HeatedProcessingOutput.STREAM_CODEC.decode(buffer);
            case FOOD -> FoodProcessingOutput.STREAM_CODEC.decode(buffer);
            case SALAD -> SaladProcessingOutput.STREAM_CODEC.decode(buffer);
            case SOUP -> SoupProcessingOutput.STREAM_CODEC.decode(buffer);
        };
    }

    public enum ProcessingOutputTypes {
        HEATED("heated", HeatedProcessingOutput.CODEC),
        FOOD("food", FoodProcessingOutput.CODEC),
        SALAD("salad", SaladProcessingOutput.CODEC),
        SOUP("soup", SoupProcessingOutput.CODEC);

        public static final Codec<ProcessingOutputTypes> CODEC = Codec.STRING.comapFlatMap(
                name -> {
                    for (ProcessingOutputTypes type : values())
                        if (type.serializedName.equals(name))
                            return DataResult.success(type);
                    return DataResult.error(() -> "Unknown dynamic processing output type: " + name);
                },
                ProcessingOutputTypes::serializedName);

        private final String serializedName;
        private final MapCodec<? extends DynamicProcessingOutput<?>> codec;

        ProcessingOutputTypes(String serializedName, MapCodec<? extends DynamicProcessingOutput<?>> codec) {
            this.serializedName = serializedName;
            this.codec = codec;
        }

        public String serializedName() {
            return serializedName;
        }

        public MapCodec<? extends DynamicProcessingOutput<?>> codec() {
            return codec;
        }
    }

    static {
        EMPTY = new DynamicProcessingOutput<>(new ProcessingOutput(ItemStack.EMPTY, 1.0F)) {
            @Override
            public ProcessingOutputTypes getType() {
                return null;
            }
        };
    }
}
