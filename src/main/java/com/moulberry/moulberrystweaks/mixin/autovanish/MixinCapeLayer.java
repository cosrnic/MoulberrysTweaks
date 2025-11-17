package com.moulberry.moulberrystweaks.mixin.autovanish;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.moulberrystweaks.ext.TranslucentAlphaExt;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CapeLayer.class)
public class MixinCapeLayer {

    @WrapOperation(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
    public void render_renderToBuffer(SubmitNodeCollector instance, Model model, Object o, PoseStack poseStack, RenderType renderType, int i, int j, int k, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, Operation<Void> original, @Local(argsOnly = true)AvatarRenderState avatarRenderState) {
        if (avatarRenderState instanceof TranslucentAlphaExt ext) {
            int translucentAlpha = ext.moulberrystweaks$getTranslucentAlpha();
            if (translucentAlpha < 0xFF) {
                original.call(instance, model, o, poseStack, renderType, 0xFFFFFF | (translucentAlpha << 24), j, k, crumblingOverlay);
                return;
            }
        }

        original.call(instance, model, o, poseStack, renderType, i, j, k, crumblingOverlay);
    }

    @WrapOperation(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entitySolid(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType render_entitySolid(ResourceLocation location, Operation<RenderType> original, @Local(argsOnly = true) AvatarRenderState avatarRenderState) {
        if (avatarRenderState instanceof TranslucentAlphaExt ext && ext.moulberrystweaks$getTranslucentAlpha() < 0xFF) {
            return RenderType.armorTranslucent(location);
        } else {
            return original.call(location);
        }
    }

}
