package net.chauvedev.woodencog.recipes.advancedProcessingRecipe;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import com.simibubi.create.foundation.utility.Lang;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.baseRecipes.SetItemStackProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.function.Supplier;

public enum AllAdvancedRecipeTypes {

    PRESSING(AllRecipeTypes.PRESSING, PressingRecipe::new),
    FILLING(AllRecipeTypes.FILLING, FillingRecipe::new);

    private final RegistryObject<RecipeSerializer<?>> serializerObject;

    public static final HashMap<String, SetItemStackProvider> CACHES = new HashMap<>();


    AllAdvancedRecipeTypes(AllRecipeTypes type,Supplier<AdvancedRecipeSerializer<?>> serializerSupplier){
        String name = Lang.asId(this.name());
        this.serializerObject = AllAdvancedRecipeTypes.Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        WoodenCog.LOGGER.info("register convert recipe "+WoodenCog.MOD_ID+":"+name+" to "+type.getId());

    }
    AllAdvancedRecipeTypes(AllRecipeTypes type,ProcessingRecipeBuilder.ProcessingRecipeFactory processingFactory) {
        this(type,() -> new AdvancedRecipeSerializer(processingFactory,type));
    }
    public static void register(IEventBus modEventBus) {
        AllAdvancedRecipeTypes.Registers.SERIALIZER_REGISTER.register(modEventBus);
    }

    public static <T extends ProcessingRecipe<?>> void registerRecipe(ProcessingRecipe tAdvancedRecipeSerializer,SetItemStackProvider provider) {
        WoodenCog.LOGGER.info("[advancerecipe] registerRecipe for "+tAdvancedRecipeSerializer.getId().toString());
        if (CACHES.containsKey(tAdvancedRecipeSerializer.getId().toString())){
            CACHES.replace(tAdvancedRecipeSerializer.getId().toString(),provider);
        }else{
            CACHES.put(tAdvancedRecipeSerializer.getId().toString(),provider);
        }
    }

    public RecipeSerializer<?> getSerializer() {
        return this.serializerObject.get();
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER;

        private Registers() {
        }

        static {
            SERIALIZER_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, WoodenCog.MOD_ID);
        }
    }
}
