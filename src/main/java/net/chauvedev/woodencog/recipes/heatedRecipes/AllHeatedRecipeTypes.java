package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.equipment.toolbox.ToolboxDyeingRecipe;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Lang;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedBasinRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedMixingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum AllHeatedRecipeTypes implements IRecipeTypeInfo {
    BASIN(HeatedBasinRecipe::new),
    PRESSING(HeatedPressingRecipe::new),
    MIXING(HeatedMixingRecipe::new);

    public static final Predicate<? super Recipe<?>> CAN_BE_AUTOMATED = (r) -> {
        return !r.getId().getPath().endsWith("_manual_only");
    };
    private final ResourceLocation id;
    private final RegistryObject<RecipeSerializer<?>> serializerObject;
    private final @Nullable RegistryObject<RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    private AllHeatedRecipeTypes(Supplier serializerSupplier, Supplier typeSupplier, boolean registerType) {
        String name = Lang.asId(this.name());
        this.id = Create.asResource(name);
        this.serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        if (registerType) {
            this.typeObject = Registers.TYPE_REGISTER.register(name, typeSupplier);
            this.type = this.typeObject;
        } else {
            this.typeObject = null;
            this.type = typeSupplier;
        }

    }

    private AllHeatedRecipeTypes(Supplier serializerSupplier) {
        String name = Lang.asId(this.name());
        this.id = Create.asResource(name);
        this.serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        this.typeObject = Registers.TYPE_REGISTER.register(name, () -> {
            return RecipeType.simple(this.id);
        });
        this.type = this.typeObject;
    }

    private AllHeatedRecipeTypes(HeatedProcessingRecipeBuilder.HeatedProcessingRecipeFactory processingFactory) {
        this(() -> {
            return new HeatedProcessingRecipeSerializer<>(processingFactory);
        });
    }

    public static void register(IEventBus modEventBus) {
        ShapedRecipe.setCraftingSize(9, 9);
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) this.serializerObject.get();
    }

    public <T extends RecipeType<?>> T getType() {
        return (T) this.type.get();
    }

    public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
        return world.getRecipeManager().getRecipeFor(this.getType(), inv, world);
    }

    public static boolean shouldIgnoreInAutomation(Recipe<?> recipe) {
        RecipeSerializer<?> serializer = recipe.getSerializer();
        if (serializer != null && AllTags.AllRecipeSerializerTags.AUTOMATION_IGNORE.matches(serializer)) {
            return true;
        } else {
            return !CAN_BE_AUTOMATED.test(recipe);
        }
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER;
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER;

        private Registers() {
        }

        static {
            SERIALIZER_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, WoodenCog.MOD_ID);
            TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, WoodenCog.MOD_ID);
        }
    }
}
