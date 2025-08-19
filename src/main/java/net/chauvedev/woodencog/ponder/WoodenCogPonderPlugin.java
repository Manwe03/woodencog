package net.chauvedev.woodencog.ponder;

import net.chauvedev.woodencog.WoodenCog;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

//Thanks to Create Enchantment Industry :)

public class WoodenCogPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return WoodenCog.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        WoodenCogPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        WoodenCogPonderTags.register(helper);
    }

}
