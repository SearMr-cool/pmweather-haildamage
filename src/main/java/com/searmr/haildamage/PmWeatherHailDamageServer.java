
package com.searmr.haildamage;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.slf4j.Logger;


@Mod(value = PmWeatherHailDamageServer.MODID, dist = Dist.DEDICATED_SERVER)
public class PmWeatherHailDamageServer {

    public static final String MODID = "searmrhaildamage";

    public static final Logger LOGGER = LogUtils.getLogger();



    public PmWeatherHailDamageServer(IEventBus modEventBus, ModContainer modContainer) {

    }

 }

