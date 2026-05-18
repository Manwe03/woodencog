package net.chauvedev.woodencog.ponder;

import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.chauvedev.woodencog.block.WoodencogBlocks;
import net.chauvedev.woodencog.ponder.scene.BlockScenes;
import net.chauvedev.woodencog.ponder.scene.HeatingScenes;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.registry.IdHolder;
import net.minecraft.resources.ResourceLocation;

public class WoodenCogPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?,?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(WoodencogBlocks.WOODEN_GENERATOR)
                .addStoryBoard("kinetics/generator_waterwheel", BlockScenes::generatorWaterWheel)
                .addStoryBoard("kinetics/generator_windmill", BlockScenes::generatorWindmill)
                .addStoryBoard("kinetics/generator_windmill_rustic", BlockScenes::generatorWindmillRustic);

        //For all windmill blade colors
        helper.forComponents(TFCItems.WINDMILL_BLADES.values().stream().map(IdHolder::getId).toList())
                .addStoryBoard("kinetics/generator_waterwheel", BlockScenes::generatorWaterWheel)
                .addStoryBoard("kinetics/generator_windmill", BlockScenes::generatorWindmill)
                .addStoryBoard("kinetics/generator_windmill_rustic", BlockScenes::generatorWindmillRustic);
        //Extra windmills
        helper.forComponents(TFCItems.LATTICE_WINDMILL_BLADE.getId(), TFCItems.RUSTIC_WINDMILL_BLADE.getId())
                .addStoryBoard("kinetics/generator_waterwheel", BlockScenes::generatorWaterWheel)
                .addStoryBoard("kinetics/generator_windmill", BlockScenes::generatorWindmill)
                .addStoryBoard("kinetics/generator_windmill_rustic", BlockScenes::generatorWindmillRustic);

        HELPER.forComponents(AllBlocks.ENCASED_FAN)
                .addStoryBoard("heating/heat", HeatingScenes::heating)
                .addStoryBoard("heating/cool", HeatingScenes::cooling);
    }
}
