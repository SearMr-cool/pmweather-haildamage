package com.searmr.haildamage;

import dev.protomanly.pmweather.PMWeather;
import dev.protomanly.pmweather.config.ServerConfig;
import dev.protomanly.pmweather.event.GameBusEvents;
import dev.protomanly.pmweather.weather.Storm;
import dev.protomanly.pmweather.weather.ThermodynamicEngine;
import dev.protomanly.pmweather.weather.WeatherHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static dev.protomanly.pmweather.event.GameBusClientEvents.canPrecipitateAt;
import static dev.protomanly.pmweather.weather.ThermodynamicEngine.getPrecipitationType;


@EventBusSubscriber(modid = PmWeatherHailDamage.MODID, bus=EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class EventHandlerClient {

    static public int maxHailDamage = 4;
    private static final Logger log = LoggerFactory.getLogger(EventHandlerServer.class);

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {

        var logs = PmWeatherHailDamageServer.LOGGER;
        Entity entity = event.getEntity();
        var managers = GameBusEvents.MANAGERS;

        WeatherHandler handle = (WeatherHandler) managers.get(entity.level().dimension());



    }






}
