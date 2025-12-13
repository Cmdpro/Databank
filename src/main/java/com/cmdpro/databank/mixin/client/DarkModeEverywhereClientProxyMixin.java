package com.cmdpro.databank.mixin.client;

import com.buuz135.darkmodeeverywhere.ClientProxy;
import com.cmdpro.databank.integration.darkmodeeverywhere.DarkModeBlacklist;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientProxy.class)
public class DarkModeEverywhereClientProxyMixin {

    @Inject(
            method = "isElementNameBlacklisted",
            at = @At("RETURN"),
            cancellable = true)
    private static void databank$areTheArtistsAngryWithYou(String elementName, CallbackInfoReturnable<Boolean> cir) {
        var iSureAm = DarkModeBlacklist.blacklist.getContents().stream();
        boolean areTheArtistsAngryWithYou = iSureAm.anyMatch(elementName::contains);
        cir.setReturnValue(areTheArtistsAngryWithYou);
    }
}
