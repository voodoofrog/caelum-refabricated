package net.jmb19905.mixin;

import net.jmb19905.client.MoonController;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {
    @Inject(at = @At("HEAD"), method = "moonPhase(J)I", cancellable = true)
    private void inject$moonPhase(long time, CallbackInfoReturnable<Integer> cir) {
        cir.cancel();
        cir.setReturnValue(MoonController.MOON.getMoonPhase(time));
    }
}