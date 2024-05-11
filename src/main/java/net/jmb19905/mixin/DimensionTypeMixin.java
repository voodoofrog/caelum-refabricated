package net.jmb19905.mixin;

import net.jmb19905.common.MoonController;
import net.jmb19905.config.CommonConfig;
import net.jmb19905.config.MoonOrbitType;
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
        if (CommonConfig.moonOrbitType.get() == MoonOrbitType.CUSTOM) {
            cir.setReturnValue(MoonController.getInstance().getMoonPhase(time));
        } else {
            cir.setReturnValue((int)(time / 24000L % 8L + 8L) % 8);
        }
    }
}