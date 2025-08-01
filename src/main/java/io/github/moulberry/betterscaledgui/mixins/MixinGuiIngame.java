package io.github.moulberry.betterscaledgui.mixins;

import io.github.moulberry.betterscaledgui.ScaledResolutionOverride;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderTooltip", at = @At("HEAD"))
    private void onRenderTooltipStart(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
        float scale = ScaledResolutionOverride.getDesiredScaleOverride();

        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        float offsetX = (screenWidth / 2.0F) * (1 - scale);
        float offsetY = (screenHeight - 22) * (1 - scale);

        GlStateManager.pushMatrix();
        GlStateManager.translate(offsetX, offsetY, 0);
        GlStateManager.scale(scale, scale, 1.0F);
    }

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void onRenderTooltipEnd(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
        GlStateManager.popMatrix();
    }
}
