package io.github.arcticpot.jublockly.mixin;

import io.github.arcticpot.jublockly.statusbars.ActionbarParser;
import io.github.arcticpot.jublockly.statusbars.FancyStatusBar;
import io.github.arcticpot.jublockly.utils.SkyblockHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    final private FancyStatusBar fancyStatusBar = FancyStatusBar.INSTANCE;

    @Shadow
    public void setOverlayMessage(Text message, boolean tinted) {}

    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void $renderStatusBar(DrawContext context, CallbackInfo ci) {
        if (!SkyblockHelper.INSTANCE.getOnSkyblock()) return;
        // :)
        final boolean healthBarBlinking = false;
        fancyStatusBar.draw(context, this.scaledWidth, this.scaledHeight, healthBarBlinking);
        ci.cancel();
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void $renderExperienceBar(DrawContext context, int x, CallbackInfo ci) {
        if (SkyblockHelper.INSTANCE.getOnSkyblock()) ci.cancel();
    }

    @Inject(method = "setOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void $setOverlayMessage(Text message, boolean tinted, CallbackInfo ci) {
        if (!SkyblockHelper.INSTANCE.getOnSkyblock()) return;
        final String restString = ActionbarParser.INSTANCE.parse(message.getString());
        if (!restString.isEmpty()) {
            if (restString.equals(message.getString())) {
                return;
            } else {
                setOverlayMessage(Text.of(restString), tinted);
            }
        }
        ci.cancel();
    }
}