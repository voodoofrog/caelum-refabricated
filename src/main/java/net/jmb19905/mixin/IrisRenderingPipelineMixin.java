package net.jmb19905.mixin;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.jmb19905.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded(value = "iris", minVersion = "1.7")
@Mixin({IrisRenderingPipeline.class})
public abstract class IrisRenderingPipelineMixin {
    @Inject(remap = false, method = "shouldRenderMoon()Z", at = @At("HEAD"), cancellable = true)
    private void shouldRenderMoon(CallbackInfoReturnable<Boolean> cir) {
        ClientLevel level = Minecraft.getInstance().level;
        assert level != null;
        int phase = level.getMoonPhase();

        if (phase == 4 && !ClientConfig.renderNewMoon.get()) {
            cir.setReturnValue(false);
        }
    }
}
