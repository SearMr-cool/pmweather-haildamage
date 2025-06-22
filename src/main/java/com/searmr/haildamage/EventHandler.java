package com.searmr.haildamage;

import dev.protomanly.pmweather.config.ServerConfig;
import dev.protomanly.pmweather.event.GameBusEvents;
import dev.protomanly.pmweather.weather.Storm;
import dev.protomanly.pmweather.weather.WeatherHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.effects.DamageEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;

import static net.minecraft.world.damagesource.DamageTypes.FREEZE;


@EventBusSubscriber(modid = PmWeatherHailDamage.MODID, bus=EventBusSubscriber.Bus.GAME)
public class EventHandler {
    private static final Logger log = LoggerFactory.getLogger(EventHandler.class);

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        var logs = PmWeatherHailDamageServer.LOGGER;
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
                  float f = getHail(entity);
                  if (f >= 0.43) {
                      Random rng = new Random();
                      rng.nextFloat(0,1);
                      if (  rng.nextFloat(0,1) <= 0.01) {
                          DamageSources sources = ((ServerLevel) entity.level()).damageSources();
                          entity.hurt(sources.generic(), 1);
                      }
                  }
                  logs.info(Float.toString(f));
              }
        }


    public static float getHail(Entity player) {
        float precip = 0.0F;
try {


    var managers = GameBusEvents.MANAGERS;
    if (managers instanceof Map<?,?>) {
        Map<?, ?> map = (Map<?, ?>) managers;
        WeatherHandler handle = (WeatherHandler) map.get(player.level().dimension());
        for(Storm storm : handle.getStorms()) {
            if (!storm.visualOnly) {
                double dist = player.position().distanceTo(new Vec3(storm.position.x + (double)2000.0F, player.position().y, storm.position.z - (double)900.0F));
                if (!(dist > ServerConfig.stormSize * (double)4.0F)) {
                    double perc = (double)0.0F;
                    if (storm.stormType == 0) {
                        perc = (double)1.0F - Math.clamp(dist / (ServerConfig.stormSize * (double)6.0F), (double)0.0F, (double)1.0F);
                        if (storm.stage == 2) {
                            perc *= (double)((float)storm.energy / 100.0F);
                        }

                        if (storm.stage > 2) {
                            perc *= (double)1.0F;
                        }

                        if (storm.stage < 2) {
                            perc *= (double)0.0F;
                        }
                    }

                    precip += (float)perc;
                }
            }
        }



    }
}

catch ( Exception ex) {}
        return Math.clamp(precip, 0.0F, 1.0F);
    }
}
