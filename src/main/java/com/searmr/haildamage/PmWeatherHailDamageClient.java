package com.searmr.haildamage;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = PmWeatherHailDamageServer.MODID, dist = Dist.CLIENT)
public class PmWeatherHailDamageClient {
    public PmWeatherHailDamageClient(ModContainer modContainer) {
        if (Dist.CLIENT.isClient()) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }
}
