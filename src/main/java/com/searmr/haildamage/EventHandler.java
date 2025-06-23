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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.effects.DamageEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static dev.protomanly.pmweather.event.GameBusClientEvents.canPrecipitateAt;
import static dev.protomanly.pmweather.weather.ThermodynamicEngine.getPrecipitationType;
import static net.minecraft.world.damagesource.DamageTypes.FREEZE;


@EventBusSubscriber(modid = PmWeatherHailDamage.MODID, bus=EventBusSubscriber.Bus.GAME)
public class EventHandler {
    static Map<BlockPos,ServerPlayer> breakBlocks = new HashMap<>();
    static public int maxHailDamage = 4;
    private static final Logger log = LoggerFactory.getLogger(EventHandler.class);

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {

        var logs = PmWeatherHailDamageServer.LOGGER;
        Entity entity = event.getEntity();
        var managers = GameBusEvents.MANAGERS;

        WeatherHandler handle = (WeatherHandler) managers.get(entity.level().dimension());


        if (!entity.level().isClientSide() && handle != null) {
                  float f = getHail(entity,entity.position());

                      Random rng = new Random();
                      rng.nextFloat(0,1);
                        int damage = Math.round((float)maxHailDamage * f);
                      if (f > 0 && damage > 0 && rng.nextFloat(0,1) <= 0.02) {

                          DamageSources sources = ((ServerLevel) entity.level()).damageSources();
                          entity.hurt(sources.generic(), damage);
                      }
            ThermodynamicEngine.Precipitation type = getPrecipitationType(handle,entity.position(),entity.level(),0);
                  if (canPrecipitateAt(entity.level(),entity.blockPosition())) {
                      logs.info(Float.toString(f) + " " + type);
                  }
              }
        }

        @SubscribeEvent
        public static void LevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
            if (!level.isClientSide() && ServerConfig.validDimensions != null && level instanceof ServerLevel serverLevel) {
                List<ServerPlayer> validPlayers = new ArrayList();
                List<ServerPlayer> plrs = serverLevel.players();
                Collections.shuffle(plrs);
                for (var test : breakBlocks.entrySet()) {
                    ServerPlayer player = test.getValue();
                    BlockPos blockPos = test.getKey();
                    ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(player.getId(),blockPos,5);
                    player.connection.send(packet);

                }
                breakBlocks.clear();
                for (ServerPlayer player : plrs) {
                    boolean isTooNear = false;

                    for (ServerPlayer existing : validPlayers) {
                        if (existing.distanceTo(player) <= 64.0F) {
                            isTooNear = true;
                            break;
                        }
                    }

                    if (!isTooNear) {
                        validPlayers.add(player);
                    }
                }
                for (ServerPlayer player : validPlayers) {
                    Vec3 playerLoc = player.position();
                    for (int x = 0; x < 60; x++) {
                        BlockPos blockPos = player.blockPosition().offset(new Vec3i(PMWeather.RANDOM.nextInt(-10, 10), PMWeather.RANDOM.nextInt(-10, 10), PMWeather.RANDOM.nextInt(-10, 10)));
                        if (blockPos.getY() >=-63) {
                           float hail = getHail(player,blockPos.getCenter());
                            BlockState block = level.getBlockState(blockPos);

                            if (   (block.getBlock().getDescriptionId().contains("glass")) && (getHail(player,blockPos.getCenter()) > 0 || getHail(player,blockPos.east().getCenter()) > 0 || getHail(player,blockPos.west().getCenter()) > 0 || getHail(player,blockPos.north().getCenter()) > 0 || getHail(player,blockPos.south().getCenter()) > 0)) {

                              breakBlocks.put(blockPos,player);

                            }
                        }
                    }
                }

            }
        }


    public static float getHail(Entity entity,Vec3 pos) {

        if (entity == null) {
            return 0.0F;
        } else {
            WeatherHandler handler = GameBusEvents.MANAGERS.get(entity.level().dimension());
            float precip = 0.0F;

            for(Storm storm : handler.getStorms()) {
                if (!storm.visualOnly) {
                    double dist = pos.distanceTo(new Vec3(storm.position.x + (double)2000.0F, pos.y, storm.position.z - (double)900.0F));
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

            return Math.clamp(precip, 0.0F, 1.0F);
        }
    }
}
