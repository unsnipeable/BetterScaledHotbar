package io.github.moulberry.betterscaledgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;

public class ScaledResolutionOverride {

    //What we want the GUI scale to be, currentScaleOverride is set to this by
    // GuiScreen#setWorldAndRenderer
    private static float desiredScaleOverride = -1;


    public static float getDesiredScaleOverride() {
        return desiredScaleOverride;
    }

    public static void setDesiredScaleOverride(float desiredScaleOverride) {
        ScaledResolutionOverride.desiredScaleOverride = desiredScaleOverride;
    }

}
