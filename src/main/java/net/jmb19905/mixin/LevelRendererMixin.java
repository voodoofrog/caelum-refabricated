package net.jmb19905.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.math.Axis;
import net.jmb19905.common.MoonController;
import net.jmb19905.client.SkyUtils;
import net.jmb19905.config.*;
import net.jmb19905.client.data.StarDataManager;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Shadow @Nullable private VertexBuffer starBuffer;

    @Shadow private double lastCameraZ;

    @Inject(at = @At("HEAD"), method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private void renderSky(PoseStack p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci) {
        SkyUtils.calculateStarLatitudeRotation(level, p_202427_.getPosition().z());
        if(StarDataManager.vanillaStarBuffer == null){
            StarDataManager.vanillaStarBuffer = starBuffer;
        }
        if(ClientConfig.starsType.get() == StarsType.CUSTOM){
            starBuffer = StarDataManager.INSTANCE.getStarBuffer();
        }
        else if(ClientConfig.starsType.get() == StarsType.VANILLA){
            starBuffer = StarDataManager.vanillaStarBuffer;
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 2), method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private void renderSky$customStars(PoseStack stack, Matrix4f matrix, float p_202426_, Camera camera, boolean p_202428_, Runnable p_202429_, CallbackInfo ci) {
        if(ClientConfig.starsType.get() != StarsType.CUSTOM) return;
        assert this.level != null;
        float f11 = 1.0F - this.level.getRainLevel(p_202426_);
        float f10 = (float) (level.getStarBrightness(p_202426_) * f11 * ClientConfig.starBrightness.get());
        if (f10 > 0.0F) {
            stack.pushPose();
            stack.mulPose(Axis.XP.rotationDegrees(180.0F));
            stack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            if(ClientConfig.latitudeEffects.get() == LatitudeEffects.STARS_ONLY) {
                stack.mulPose(Axis.YP.rotationDegrees((float) (180 * SkyUtils.starLatitudeRotation(level, camera.getPosition().z()))));
            }
            stack.mulPose(Axis.ZP.rotationDegrees(-level.getTimeOfDay(p_202426_) * 360.0F));
            stack.mulPose(Axis.ZP.rotationDegrees((float) (-SkyUtils.yearRotation(level) * 360.0F)));
            RenderSystem.setShaderColor(f10, f10, f10, f10);
            FogRenderer.setupNoFog();
            assert starBuffer != null;
            starBuffer.bind();
            starBuffer.drawWithShader(stack.last().pose(), matrix, GameRenderer.getPositionColorShader());
            VertexBuffer.unbind();
            p_202429_.run();
            stack.popPose();
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V"), method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private void renderSky$skipVanillaStars(VertexBuffer buffer, Matrix4f p_254480_, Matrix4f p_254555_, ShaderInstance p_253993_) {
        if(!buffer.equals(starBuffer) || ClientConfig.starsType.get() == StarsType.VANILLA){
            buffer.drawWithShader(p_254480_, p_254555_, p_253993_);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"), method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private float[] renderSky$getSunriseColor(DimensionSpecialEffects effects, float p_108872_, float p_108873_) {
        return SkyUtils.getSunriseColor(level, lastCameraZ, p_108873_);
    }
    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 1), method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private void renderSky$sunriseRotationRemoveVanilla(PoseStack instance, Quaternionf p_254385_) {

    }
    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 1), method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private void renderSky$sunriseRotation(PoseStack p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci) {
        p_202424_.mulPose(Axis.ZP.rotationDegrees((float) SkyUtils.getSunriseColorRotation(level, lastCameraZ, p_202426_)));
    }
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getMoonPhase()I"), method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private void renderSky$renderMoon$Pre(PoseStack p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci) {
        if (CommonConfig.moonOrbitType.get() == MoonOrbitType.VANILLA) return;
        assert level != null;
        p_202424_.mulPose(Axis.XP.rotation(-MoonController.getInstance().getMoonOrbitPosition(level.getDayTime()) * Mth.TWO_PI));
        p_202424_.mulPose(Axis.YP.rotationDegrees(90));
        int phase = this.level.getMoonPhase();
        if(phase == 4){
            RenderSystem.setShaderColor(0,0,0,0);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"), method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private void renderSky$renderMoon$Post(PoseStack p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci) {
        assert level != null;
        p_202424_.mulPose(Axis.XP.rotation(MoonController.getInstance().getMoonOrbitPosition(level.getDayTime()) * Mth.TWO_PI));
        p_202424_.mulPose(Axis.YP.rotationDegrees(-90));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"), method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private void renderSky$renderCelestial$Pre(PoseStack p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci) {
        if(ClientConfig.latitudeEffects.get() == LatitudeEffects.ALL) {
            p_202424_.mulPose(Axis.XP.rotationDegrees((float) (-180 * SkyUtils.starLatitudeRotation(level, p_202427_.getPosition().z()))));
        }
    }
}