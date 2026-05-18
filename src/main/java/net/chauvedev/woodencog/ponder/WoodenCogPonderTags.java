package net.chauvedev.woodencog.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.chauvedev.woodencog.block.WoodencogBlocks;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class WoodenCogPonderTags {

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?,?>> entryHelper = helper.withKeyFunction(RegistryEntry::getId);

        entryHelper.addToTag(AllCreatePonderTags.KINETIC_SOURCES)
                .add(WoodencogBlocks.WOODEN_GENERATOR);

        entryHelper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES)
                .add(WoodencogBlocks.CT_TRANSFORMER);
    }
}
