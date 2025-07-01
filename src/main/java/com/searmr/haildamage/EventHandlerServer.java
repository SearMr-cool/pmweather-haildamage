package com.searmr.haildamage;


import com.sun.jna.platform.win32.WinDef;
import dev.protomanly.pmweather.PMWeather;
import dev.protomanly.pmweather.config.ServerConfig;
import dev.protomanly.pmweather.event.GameBusEvents;
import dev.protomanly.pmweather.weather.Storm;
import dev.protomanly.pmweather.weather.ThermodynamicEngine;
import dev.protomanly.pmweather.weather.WeatherHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


import static dev.protomanly.pmweather.weather.ThermodynamicEngine.getPrecipitationType;


@EventBusSubscriber(modid = PmWeatherHailDamage.MODID, bus=EventBusSubscriber.Bus.GAME)
public class EventHandlerServer {
    public static   int lAmount = 1;
    public static boolean canPrecipitateAt(Level level, BlockPos pos) {
        if ((double)pos.getY() > ServerConfig.layer0Height) {
            return false;
        } else {
            return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() <= pos.getY();
        }
    }
    static private  float GetBlockSpeed(BlockState blockState) {
    float mineValue = (blockState.requiresCorrectToolForDrops()) ? 5f : 1.5f;
    return blockState.getBlock().defaultDestroyTime() * mineValue;
    }

    

    static public int maxHailDamage = 4;
    private static final Logger log = LoggerFactory.getLogger(EventHandlerServer.class);
    static  boolean run = true;
    public static void onTick(Entity entity,Level level) {







        if (run ) {
            var logs = PmWeatherHailDamageServer.LOGGER;
            var managers = GameBusEvents.MANAGERS;
            WeatherHandler handle = (WeatherHandler) managers.get(entity.level().dimension());
            ThermodynamicEngine.Precipitation type = getPrecipitationType(handle,entity.position(),entity.level(),0);
                  if (canPrecipitateAt(entity.level(),entity.blockPosition())) {
                      float hailLevel = getHail(entity.level(),entity.position());

                      Random rng = new Random();
                      rng.nextFloat(0,1);

                      if (hailLevel > 0 && rng.nextFloat(0,1) <= 0.02 && canPrecipitateAt(entity.level(),entity.blockPosition()) && entity instanceof LivingEntity lEntity) {
                          float totalDamage = 0;
                          float hailSize = 5f * hailLevel;
                          if (hailSize <= 0.3 && hailSize >= 0) {

                          }
                          if (hailSize <= 0.7 && hailSize > 0.3) {

                          }
                          if (hailSize <= 1.1 && hailSize > 0.7) {

                          }
                          if (hailSize <= 1.5 && hailSize > 1.1) {

                          }
                          if (hailSize <= 1.9 && hailSize > 1.5) {

                          }
                          if (hailSize <= 2.2 && hailSize > 1.9) {
                            // fist damage starts here
                            float instantDeath = 3;
                            totalDamage =  instantDeath * (instantDeath / lEntity.getMaxHealth());


                          }
                          if (hailSize <= 3.1 && hailSize > 2.2) {
                              float instantDeath = 7.25f;
                              totalDamage =  instantDeath * (instantDeath / lEntity.getMaxHealth());


                          }
                          if (hailSize <= 3.7 && hailSize > 3.1) {
                              if (hailSize <= 3.1 && hailSize > 2.2) {
                                  float instantDeath = 11.5f;
                                  totalDamage =  instantDeath * (instantDeath / lEntity.getMaxHealth());


                              }
                          }
                          if (hailSize <= 4.4 && hailSize > 3.7) {
                              float instantDeath = 15.45f;
                              totalDamage =  instantDeath * (instantDeath / lEntity.getMaxHealth());


                          }
                          if (hailSize <= 5.0 && hailSize > 4.4) {
                              float instantDeath = 20f;
                              totalDamage =  instantDeath * (instantDeath / lEntity.getMaxHealth());


                          }
                          entity.hurt( entity.damageSources().cactus(),totalDamage);
                      }
                  }
              }
        }
static int nextCheck = 0;
    static  HashMap<Integer,HailDestroyBlock> hailAffectedBlocks = new HashMap<Integer, HailDestroyBlock>();
        static int nextTick = 0;
        @SubscribeEvent
        public static void LevelTick(ServerTickEvent.Post event) {

           if (Config.isAdaptive) {
                if (nextCheck == 60) {

                    double tps = getEstimatedTPS();

                    if (tps >= 35) {
                        lAmount = Math.clamp(lAmount - 1, 0, Config.maxWaterAmount);

                    } else lAmount = Math.clamp(lAmount + 1, 0, Config.maxWaterAmount);
                    nextCheck = 0;
                    PmWeatherHailDamageServer.LOGGER.info(Integer.toString(lAmount));
                } else nextCheck++;
            }
           else lAmount = Config.maxWaterAmount;

            List<Integer> toRemove = new ArrayList<>();
            var logs = PmWeatherHailDamageServer.LOGGER;

            Level level = event.getServer().overworld();

            if (ServerConfig.validDimensions != null && level instanceof ServerLevel serverLevel && run && Config.hailDestroysBlocks) {
                var entities = serverLevel.getAllEntities();
                for (Entity entity : entities) {
                    onTick(entity,level);
                }
                if (!Config.hailDestroysBlocks && !hailAffectedBlocks.isEmpty()) hailAffectedBlocks.clear();
                List<ServerPlayer> validPlayers = new ArrayList();
                List<ServerPlayer> plrs = serverLevel.players();
                Collections.shuffle(plrs);
                for (var hailBlock : hailAffectedBlocks.entrySet()) {

                    if (PMWeather.RANDOM.nextInt(2) == 1){
                        BlockPos blockPos = hailBlock.getValue().blockPos;
                        float hailLevel = getHail(level, blockPos.getCenter());
                        float hailSize = 5 * hailLevel;
                        BlockState block = level.getBlockState(blockPos);
                        block.getBlock().defaultDestroyTime();
                        double maxDist = 103;
                        double xPos = (double)blockPos.getX();
                        double yPos = (double)blockPos.getY();
                        double zPos = (double)blockPos.getZ();
                        boolean yes = true;
                        boolean validPos = false;

                        if (hailSize < 0.7 && hailSize > 0.3) {
                            // base vegetation is destroyed here
                            if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS) && block.is(BlockTags.FLOWERS)) {
                                if (block.is(BlockTags.FLOWERS)) level.destroyBlock(blockPos, false);
                            }

                        }
                        else if (hailSize < 1.1 && hailSize >= 0.7) {
                            // leaves start getting broken here
                            float maxBreakSpeed = 0.2f;

                            if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS) && GetBlockSpeed(block) <= maxBreakSpeed) {
                                float destroySpeed = getDestroySpeed(block, level, blockPos, maxBreakSpeed);

                                float tickCount = (destroySpeed) * 20;
                                float tickIncrement = 10 / tickCount;
                                hailBlock.getValue().progress += tickIncrement;
                                ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(hailBlock.getKey(), blockPos, (int) hailBlock.getValue().progress);
                                if (hailBlock.getValue().progress >= 10) {
                                    toRemove.add(hailBlock.getKey());
                                    level.destroyBlock(blockPos, false);
                                }
                                for (ServerPlayer player : plrs) {
                                    player.connection.send(packet);
                                }
                            }
                        }
                        else if (hailSize < 1.5 && hailSize >= 1.1) {
                            float maxBreakSpeed = 0.875f;
                            if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS) && GetBlockSpeed(block) <= maxBreakSpeed) {
                                float destroySpeed = getDestroySpeed(block, level, blockPos, maxBreakSpeed);

                                float tickCount = (destroySpeed) * 20;
                                float tickIncrement = 10 / tickCount;
                                hailBlock.getValue().progress += tickIncrement;
                                ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(hailBlock.getKey(), blockPos, (int) hailBlock.getValue().progress);
                                if (hailBlock.getValue().progress >= 10) {
                                    toRemove.add(hailBlock.getKey());
                                    level.destroyBlock(blockPos, false);
                                }
                                for (ServerPlayer player : plrs) {
                                    player.connection.send(packet);
                                }
                            }
                        }
                        else if (hailSize < 1.9 && hailSize >= 1.5) {
                            // Glass will break here
                            float maxBreakSpeed = 1.55f;
                            if (GetBlockSpeed(block) <= maxBreakSpeed) {
                                float destroySpeed = getDestroySpeed(block, level, blockPos, maxBreakSpeed);

                                float tickCount = (destroySpeed) * 20;
                                float tickIncrement = 10 / tickCount;
                                hailBlock.getValue().progress += tickIncrement;
                                ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(hailBlock.getKey(), blockPos, (int) hailBlock.getValue().progress);
                                if (hailBlock.getValue().progress >= 10) {
                                    toRemove.add(hailBlock.getKey());
                                    level.destroyBlock(blockPos, false);
                                }
                                for (ServerPlayer player : plrs) {
                                    player.connection.send(packet);
                                }
                            }
                        }
                        else if (hailSize < 2.2 && hailSize >= 1.9) {
                            // first instances of low health mobs getting damaged
                            float maxBreakSpeed = 2.225f;
                            if (GetBlockSpeed(block) <= maxBreakSpeed) {
                                float destroySpeed = getDestroySpeed(block, level, blockPos, maxBreakSpeed);

                                float tickCount = (destroySpeed) * 20;
                                float tickIncrement = 10 / tickCount;
                                hailBlock.getValue().progress += tickIncrement;
                                ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(hailBlock.getKey(), blockPos, (int) hailBlock.getValue().progress);
                                if (hailBlock.getValue().progress >= 10) {
                                    toRemove.add(hailBlock.getKey());
                                    level.destroyBlock(blockPos, false);
                                }
                                for (ServerPlayer player : plrs) {
                                    player.connection.send(packet);
                                }
                            }
                        }
                        else if (hailSize < 3.1 && hailSize >= 2.2) {
                            // wood is completely broken
                            float maxBreakSpeed = 3f;
                            if (GetBlockSpeed(block) <= maxBreakSpeed) {
                                float destroySpeed = getDestroySpeed(block, level, blockPos, maxBreakSpeed);

                                float tickCount = (destroySpeed) * 20;
                                float tickIncrement = 10 / tickCount;
                                hailBlock.getValue().progress += tickIncrement;
                                ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(hailBlock.getKey(), blockPos, (int) hailBlock.getValue().progress);
                                if (hailBlock.getValue().progress >= 10) {
                                    toRemove.add(hailBlock.getKey());
                                    level.destroyBlock(blockPos, false);
                                }
                                for (ServerPlayer player : plrs) {
                                    player.connection.send(packet);
                                }
                            }
                        }
                        else if (hailSize < 3.7 && hailSize >= 3.1) {
                            // more robust blocks are broken
                            float maxBreakSpeed = 8f;
                            if (GetBlockSpeed(block) <= maxBreakSpeed) {
                                float destroySpeed = getDestroySpeed(block, level, blockPos, maxBreakSpeed);

                                float tickCount = (destroySpeed) * 20;
                                float tickIncrement = 10 / tickCount;
                                hailBlock.getValue().progress += tickIncrement;
                                ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(hailBlock.getKey(), blockPos, (int) hailBlock.getValue().progress);
                                if (hailBlock.getValue().progress >= 10) {
                                    toRemove.add(hailBlock.getKey());
                                    level.destroyBlock(blockPos, false);
                                }
                                for (ServerPlayer player : plrs) {
                                    player.connection.send(packet);
                                }
                            }
                        }
                        else if (hailSize < 4.4 && hailSize >= 3.7) {
                            // concrete is damaged and players can suffer large amounts of damage
                            float maxBreakSpeed = 10f;
                            if (GetBlockSpeed(block) <= maxBreakSpeed) {
                                float destroySpeed = getDestroySpeed(block, level, blockPos, maxBreakSpeed);

                                float tickCount = (destroySpeed) * 20;
                                float tickIncrement = 10 / tickCount;
                                hailBlock.getValue().progress += tickIncrement;
                                ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(hailBlock.getKey(), blockPos, (int) hailBlock.getValue().progress);
                                if (hailBlock.getValue().progress >= 10) {
                                    level.destroyBlock(blockPos, false);
                                    toRemove.add(hailBlock.getKey());

                                }
                                for (ServerPlayer player : plrs) {
                                    player.connection.send(packet);
                                }
                            }
                        }
                        else if (hailSize < 5 && hailSize >= 4.4) {
                            float maxBreakSpeed = 20f;
                            if (GetBlockSpeed(block) <= maxBreakSpeed) {
                                float destroySpeed = getDestroySpeed(block, level, blockPos, maxBreakSpeed);

                                float tickCount = (destroySpeed) * 20;
                                float tickIncrement = 10 / tickCount;
                                hailBlock.getValue().progress += tickIncrement;
                                ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(hailBlock.getKey(), blockPos, (int) hailBlock.getValue().progress);
                                if (hailBlock.getValue().progress >= 10) {
                                    toRemove.add(hailBlock.getKey());
                                    level.destroyBlock(blockPos, false);
                                }
                                for (ServerPlayer player : plrs) {
                                    player.connection.send(packet);
                                }
                            }
                            // Player can die if not protected and some strong blocks start breaking
                        }
                        else if (hailSize == 5) {
                            // bricks start getting damaged
                            float maxBreakSpeed = 25f;
                            if (GetBlockSpeed(block) <= maxBreakSpeed) {
                                float destroySpeed = getDestroySpeed(block, level, blockPos, maxBreakSpeed);

                                float tickCount = (destroySpeed) * 20;
                                float tickIncrement = 10 / tickCount;
                                hailBlock.getValue().progress += tickIncrement;
                                ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(hailBlock.getKey(), blockPos, (int) hailBlock.getValue().progress);
                                if (hailBlock.getValue().progress >= 10) {
                                    toRemove.add(hailBlock.getKey());
                                    level.destroyBlock(blockPos, false);
                                }
                                for (ServerPlayer player : plrs) {
                                    player.connection.send(packet);
                                }
                            }
                        }
                       if (!plrs.isEmpty()) {
                           for (ServerPlayer player : plrs) {
                               Vec3 pos = player.position();


                               if (pos.distanceTo(Vec3.atLowerCornerOf(blockPos)) <= 105)  {
                                   validPos = true;
                                   break;
                               }
                           }
                           if (!validPos || !canPrecipitateAt(level,blockPos.above()) || block.is(Blocks.AIR)) {
                               toRemove.add(blockPos.hashCode());

                               continue;
                           }
                       }
                    }


                }
                for (int id : toRemove) {
                    hailAffectedBlocks.remove(id);
                }





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

                    for (int x = 0; x < 60; x++) {
                        BlockPos blockPos = player.blockPosition().offset(new Vec3i(PMWeather.RANDOM.nextInt(-60, 60), PMWeather.RANDOM.nextInt(-10, 10), PMWeather.RANDOM.nextInt(-60, 60)));
                        if (blockPos.getY() >=-63) {

                           float hailLevel = getHail(level,blockPos.getCenter());

                            BlockPos tempPos = blockPos.above();
                            boolean atBlock = canPrecipitateAt(player.level(),tempPos);
                            boolean east = getHail(level,blockPos.east().getCenter()) > 0 && canPrecipitateAt(player.level(),blockPos.east());
                            boolean west = getHail(level,blockPos.west().getCenter()) > 0 && canPrecipitateAt(player.level(),blockPos.west());
                            boolean north = getHail(level,blockPos.north().getCenter()) > 0 && canPrecipitateAt(player.level(),blockPos.north());
                            boolean south = getHail(level,blockPos.south().getCenter()) > 0 && canPrecipitateAt(player.level(),blockPos.south());
                            BlockState block = level.getBlockState(blockPos);
                            boolean validBlock = true;
                            for (var tag : ServerConfig.blacklistedBlockTags) {
                                if (block.is(tag)) {
                                    validBlock = false;
                                    break;
                                }
                            }
                            if (validBlock) {
                                for (var id : ServerConfig.blacklistedBlocks) {
                                    if (block.is(id)) {
                                        validBlock = false;
                                        break;
                                    }
                                }
                            }
                           if (hailLevel > 0 && atBlock && validBlock) {


                               float hailSize = 5f * hailLevel;
                               if (hailSize <= 0.3 && hailSize >= 0) {

                               }
                               else if (hailSize < 0.7 &&  hailSize > 0.3) {
                                   // base vegetation is destroyed here
                                   if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS) && block.is(BlockTags.FLOWERS)) level.destroyBlock(blockPos,false);
                               }
                               else if (hailSize < 1.1 &&  hailSize >= 0.7) {
                                   // leaves start getting broken here
                                   float maxBreakSpeed = 0.2f;
                                   if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS) && block.getDestroySpeed(level,blockPos) <= maxBreakSpeed)
                                       extracted(blockPos);
                               }
                               else if (hailSize < 1.5 &&  hailSize >= 1.1) {
                                   float maxBreakSpeed = 0.875f;
                                   if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS) && block.getDestroySpeed(level,blockPos) <= maxBreakSpeed)
                                       extracted(blockPos);
                               }
                               else if (hailSize < 1.9 &&  hailSize >= 1.5) {
                                    // Glass will break here
                                   float maxBreakSpeed = 1.55f;
                                   if (!block.is(Tags.Blocks.GLASS_BLOCKS) || block.is(Tags.Blocks.GLASS_BLOCKS) || block.getDestroySpeed(level,blockPos) <= maxBreakSpeed)
                                       extracted(blockPos);
                               }
                               else if (hailSize < 2.2 &&  hailSize >= 1.9) {
                                    // first instances of low health mobs getting damaged
                                   float maxBreakSpeed = 2.225f;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                               }
                               else if (hailSize < 3.1 &&  hailSize >= 2.2) {
                                    // wood is completely broken
                                   float maxBreakSpeed = 3f;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                               }
                               else if (hailSize < 3.7 &&  hailSize >= 3.1) {
                                    // more robust blocks are broken
                                   float maxBreakSpeed = 7f;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                               }
                               else if (hailSize < 4.4 &&  hailSize >= 3.7) {
                                    // concrete is damaged and players can suffer large amounts of damage
                                   float maxBreakSpeed = 10;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                               }
                               else if (hailSize < 5 &&  hailSize >= 4.4) {
                                   float maxBreakSpeed = 20f;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                                   // Player can die if not protected and some strong blocks start breaking
                               }
                               else if (hailSize == 5) {
                                    // bricks start getting damaged
                                   float maxBreakSpeed = 25f;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                               }
                           }
                        }
                    }
                }

            }

        }

    private static float getDestroySpeed(BlockState block, Level level, BlockPos blockPos, float maxBreakSpeed) {
        return ((GetBlockSpeed(block) * (float) Config.destroyTime) / maxBreakSpeed) * (GetBlockSpeed(block) * (float) Config.destroyTime);
    }

    private static void extracted(BlockPos blockPos) {
            int key = blockPos.hashCode();
        if (!hailAffectedBlocks.containsKey(key)) {
            hailAffectedBlocks.put(blockPos.hashCode(), new HailDestroyBlock(blockPos));
        }
    }


    public static float getHail(Level level,Vec3 pos) {


            WeatherHandler handler = GameBusEvents.MANAGERS.get(level.dimension());
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
    public static double getEstimatedTPS() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return (double) server.getAverageTickTimeNanos() / 1000000; // nanoseconds to milliseconds
        }
        return 0.0;
    }
}
