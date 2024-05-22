package net.jmb19905.mixin;

import com.mojang.math.Axis;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.CelestialUniforms;
import net.jmb19905.client.SkyUtils;
import net.jmb19905.common.MoonController;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded(value = "iris", minVersion = "1.7")
@Mixin({CelestialUniforms.class})
public abstract class IrisCelestialUniformsMixin {
    @Unique
    private float sunPathRotation;

    @Inject(remap = false, method = "<init>", at = @At("TAIL"))
    void init(float sunPathRotation, CallbackInfo ci) {
        this.sunPathRotation = sunPathRotation;
    }

    @Unique
    private static float getSkyAngle() {
        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        assert level != null;

        return level.getTimeOfDay(CapturedRenderingState.INSTANCE.getTickDelta());
    }

    @Unique
    private Vector4f getCelestialPosition(boolean isMoon) {
        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        Camera camera = client.gameRenderer.getMainCamera();

        assert level != null;

        Vector4f position = new Vector4f(0.0F, isMoon ? -100F : 100F, 0.0F, 0.0F);
        Matrix4f celestial = new Matrix4f(CapturedRenderingState.INSTANCE.getGbufferModelView());

        celestial.rotate(Axis.XP.rotationDegrees((float) (-180 * SkyUtils.starLatitudeRotation(level, camera.getPosition().z()))));
        celestial.rotate(Axis.YP.rotationDegrees(-90.0F));
        celestial.rotate(Axis.ZP.rotationDegrees(this.sunPathRotation));
        celestial.rotate(Axis.XP.rotationDegrees(getSkyAngle() * 360.0F));

        if (isMoon) {
            celestial.rotate(Axis.XP.rotation(-MoonController.getInstance().getMoonOrbitPosition(level.getDayTime()) * Mth.TWO_PI));
            celestial.rotate(Axis.YP.rotationDegrees(90));
        }

        return celestial.transform(position);
    }

    @Redirect(remap = false, at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/uniforms/CelestialUniforms;getCelestialPosition(F)Lorg/joml/Vector4f;"), method = {"getSunPosition()Lorg/joml/Vector4f;"})
    private Vector4f getSunPosition$getCelestialPosition(CelestialUniforms instance, float f) {
        return this.getCelestialPosition(false);
    }

    @Redirect(remap = false, at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/uniforms/CelestialUniforms;getCelestialPosition(F)Lorg/joml/Vector4f;"), method = {"getMoonPosition()Lorg/joml/Vector4f;"})
    private Vector4f getMoonPosition$getCelestialPosition(CelestialUniforms instance, float f) {
        return this.getCelestialPosition(true);
    }
}
