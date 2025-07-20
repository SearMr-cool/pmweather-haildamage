
package com.searmr.haildamage;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;


@Mod(value = PmWeatherHailDamageServer.MODID)
public class PmWeatherHailDamageServer {

    public static final String MODID = "searmrhaildamage";

    public static final Logger LOGGER = LogUtils.getLogger();



    public PmWeatherHailDamageServer(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER,Config.thing);
    if (Dist.CLIENT.isClient()) {

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    }

 }

