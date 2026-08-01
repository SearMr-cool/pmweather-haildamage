
package com.searmr.haildamage;

import com.mojang.logging.LogUtils;
import dev.protomanly.pmweather.addons.AddonHelper;
import dev.protomanly.pmweather.addons.AddonInfo;
import net.minecraft.server.dedicated.DedicatedServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Mod(value = PmWeatherHailDamageServer.MODID)
public class PmWeatherHailDamageServer {
    public static final String MODID = "searmrhaildamage";
    public static final Logger LOGGER = LogUtils.getLogger();
    public PmWeatherHailDamageServer(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.thing);
        AddonHelper.registerAddon(new AddonInfo(modContainer, List.of("0.16.4","0.17.0","0.17.1","0.17.2")));
    }
}

