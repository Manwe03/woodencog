package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import net.dries007.tfc.util.Drinkable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Drinkable.class,remap = false)
public class MixinDrinkable {

    @Inject(
            method = "attemptDrink",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void attemptDrink(Level level, Player player, boolean doDrink, CallbackInfoReturnable<InteractionResult> cir){
        if(player instanceof DeployerFakePlayer){
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
