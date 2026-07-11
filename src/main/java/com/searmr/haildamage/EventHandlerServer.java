package com.searmr.haildamage;

import dev.protomanly.pmweather.PMWeather;
import dev.protomanly.pmweather.config.ServerConfig;
import dev.protomanly.pmweather.event.GameBusEvents;
import dev.protomanly.pmweather.weather.Storm;
import dev.protomanly.pmweather.weather.WeatherHandlerServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static dev.protomanly.pmweather.event.GameBusClientEvents.canPrecipitateAt;
import static dev.protomanly.pmweather.weather.ThermodynamicEngine.getPrecipitationType;


@EventBusSubscriber(modid = PmWeatherHailDamageServer.MODID, bus=EventBusSubscriber.Bus.GAME)
public class EventHandlerServer {
    static private  float GetBlockSpeed(BlockState blockState) {
    float mineValue = (blockState.requiresCorrectToolForDrops()) ? 5f : 1.5f;
    return blockState.getBlock().defaultDestroyTime() * mineValue;
    }

    static public int maxHailDamage = 4;
    private static final Logger log = LoggerFactory.getLogger(EventHandlerServer.class);
    static  boolean run = true;
    static HashMap<CachedHailLevel,Float> cachedHailLevels = new HashMap<>();
    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        var logs = PmWeatherHailDamageServer.LOGGER;

        Entity entity = event.getEntity();
        if (entity.tickCount % 5 != 0) return;
        var managers = GameBusEvents.MANAGERS;

        WeatherHandlerServer handle = (WeatherHandlerServer) managers.get(entity.level().dimension());


        if (!entity.level().isClientSide && handle != null && run ) {
                  if (canPrecipitateAt(entity.level(),entity.blockPosition())) {

                      float hailLevel = getHail(entity.level(),entity.position());

                      Random rng = new Random();
                      rng.nextFloat(0,1);

                      if (hailLevel > 0 && rng.nextFloat(0,1) <= 0.10 && canPrecipitateAt(entity.level(),entity.blockPosition()) && entity instanceof LivingEntity lEntity) {
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
                          entity.hurt( entity.damageSources().cactus(),totalDamage * (float)Config.entityDamageModifier);
                      }
                  }
              }
        }

    static  HashMap<BlockPos,HailDestroyBlock> hailAffectedBlocks = new HashMap<BlockPos, HailDestroyBlock>();
        @SubscribeEvent
        public static void LevelTick(LevelTickEvent.Post event) {

            List<BlockPos> toRemove = new ArrayList<>();
            var logs = PmWeatherHailDamageServer.LOGGER;

            Level level = event.getLevel();
            if (!Config.hailDestroysBlocks && !hailAffectedBlocks.isEmpty()) hailAffectedBlocks.clear();
            if (!level.isClientSide() && ServerConfig.validDimensions.contains(level.dimension()) && level instanceof ServerLevel serverLevel && run && Config.hailDestroysBlocks) {

                List<ServerPlayer> validPlayers = new ArrayList();
                List<ServerPlayer> plrs = serverLevel.players();
                Collections.shuffle(plrs);
                for (var hailBlock : hailAffectedBlocks.entrySet()) {
                    if (PMWeather.RANDOM.nextInt(2) == 1){
                        BlockPos blockPos = hailBlock.getValue().blockPos;
                        if (!serverLevel.shouldTickBlocksAt(blockPos)) {
                            toRemove.add(blockPos);
                            continue;
                        }
                        float hailLevel = getHail(level, blockPos.getCenter());
                        float hailSize = 5 * hailLevel;
                        BlockState block = level.getBlockState(blockPos);
                        boolean validPos = false;
                        if (hailSize < 0.7 && hailSize > 0.3) {
                            // base vegetation is destroyed here
                            if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS) && block.is(BlockTags.FLOWERS)) {
                                if (block.is(BlockTags.FLOWERS)) level.destroyBlock(blockPos, false);
                            }

                        }
                        if (hailSize < 1.1 && hailSize >= 0.7) {
                            // leaves start getting broken here
                            float maxBreakSpeed = 0.2f;

                            if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS)) {
                                handleHailBlockDestroyProcess(hailBlock, block, maxBreakSpeed, level, blockPos, toRemove, plrs);
                            }
                        }
                        if (hailSize < 1.5 && hailSize >= 1.1) {
                            float maxBreakSpeed = 0.875f;
                            if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS)) {
                                handleHailBlockDestroyProcess(hailBlock, block, maxBreakSpeed, level, blockPos, toRemove, plrs);
                            }
                        }
                        if (hailSize < 1.9 && hailSize >= 1.5) {
                            // Glass will break here
                            float maxBreakSpeed = 1.55f;
                            handleHailBlockDestroyProcess(hailBlock, block, maxBreakSpeed, level, blockPos, toRemove, plrs);
                        }
                        if (hailSize < 2.2 && hailSize >= 1.9) {
                            // first instances of low health mobs getting damaged
                            float maxBreakSpeed = 2.225f;
                            handleHailBlockDestroyProcess(hailBlock, block, maxBreakSpeed, level, blockPos, toRemove, plrs);
                        }
                        if (hailSize < 3.1 && hailSize >= 2.2) {
                            // wood is completely broken
                            float maxBreakSpeed = 3f;
                            handleHailBlockDestroyProcess(hailBlock, block, maxBreakSpeed, level, blockPos, toRemove, plrs);
                        }
                        if (hailSize < 3.7 && hailSize >= 3.1) {
                            // more robust blocks are broken
                            float maxBreakSpeed = 8f;
                            handleHailBlockDestroyProcess(hailBlock, block, maxBreakSpeed, level, blockPos, toRemove, plrs);
                        }
                        if (hailSize < 4.4 && hailSize >= 3.7) {
                            // concrete is damaged and players can suffer large amounts of damage
                            float maxBreakSpeed = 10f;
                            handleHailBlockDestroyProcess(hailBlock, block, maxBreakSpeed, level, blockPos, toRemove, plrs);
                        }
                        if (hailSize < 5 && hailSize >= 4.4) {
                            float maxBreakSpeed = 20f;
                            handleHailBlockDestroyProcess(hailBlock, block, maxBreakSpeed, level, blockPos, toRemove, plrs);
                            // Player can die if not protected and some strong blocks start breaking
                        }
                        if (hailSize == 5) {
                            // bricks start getting damaged
                            float maxBreakSpeed = 25f;
                            handleHailBlockDestroyProcess(hailBlock, block, maxBreakSpeed, level, blockPos, toRemove, plrs);
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
                               toRemove.add(blockPos);

                               continue;
                           }
                       }
                    }


                }
                for (BlockPos blockPosT : toRemove) {
                    hailAffectedBlocks.remove(blockPosT);
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

                    for (int x = 0; x < Config.hailBlocksPerTick; x++) {
                        BlockPos blockPos = player.blockPosition().offset(new Vec3i(PMWeather.RANDOM.nextInt(-60, 60), PMWeather.RANDOM.nextInt(-10, 10), PMWeather.RANDOM.nextInt(-60, 60)));
                        int topMostBlock = player.level().getHeight(Heightmap.Types.WORLD_SURFACE,(int)blockPos.getX(),(int)blockPos.getZ());
                        blockPos = new BlockPos(blockPos.getX(),topMostBlock,blockPos.getZ());
                        boolean atBlock = canPrecipitateAt(player.level(),blockPos);
                        if (blockPos.getY() >=-63 && atBlock) {

                           float hailLevel = getHail(level,blockPos.getCenter());



                            BlockState block = level.getBlockState(blockPos.below());
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
                           if (hailLevel > 0 && validBlock) {


                               float hailSize = 5f * hailLevel;
                               if (hailSize < 0.7 &&  hailSize > 0.3) {
                                   // base vegetation is destroyed here
                                   if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS) && block.is(BlockTags.FLOWERS)) level.destroyBlock(blockPos,false);
                               }
                               if (hailSize < 1.1 &&  hailSize >= 0.7) {
                                   // leaves start getting broken here
                                   float maxBreakSpeed = 0.2f;
                                   if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS) && block.getDestroySpeed(level,blockPos) <= maxBreakSpeed)
                                       extracted(blockPos);
                               }
                               if (hailSize < 1.5 &&  hailSize >= 1.1) {
                                   float maxBreakSpeed = 0.875f;
                                   if (!block.is(Tags.Blocks.GLASS_BLOCKS) && !block.is(Tags.Blocks.GLASS_BLOCKS) && block.getDestroySpeed(level,blockPos) <= maxBreakSpeed)
                                       extracted(blockPos);
                               }
                               if (hailSize < 1.9 &&  hailSize >= 1.5) {
                                    // Glass will break here
                                   float maxBreakSpeed = 1.55f;
                                   if (!block.is(Tags.Blocks.GLASS_BLOCKS) || block.is(Tags.Blocks.GLASS_BLOCKS) || block.getDestroySpeed(level,blockPos) <= maxBreakSpeed)
                                       extracted(blockPos);
                               }
                               if (hailSize < 2.2 &&  hailSize >= 1.9) {
                                    // first instances of low health mobs getting damaged
                                   float maxBreakSpeed = 2.225f;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                               }
                               if (hailSize < 3.1 &&  hailSize >= 2.2) {
                                    // wood is completely broken
                                   float maxBreakSpeed = 3f;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                               }
                               if (hailSize < 3.7 &&  hailSize >= 3.1) {
                                    // more robust blocks are broken
                                   float maxBreakSpeed = 7f;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                               }
                               if (hailSize < 4.4 &&  hailSize >= 3.7) {
                                    // concrete is damaged and players can suffer large amounts of damage
                                   float maxBreakSpeed = 10;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                               }
                               if (hailSize < 5 &&  hailSize >= 4.4) {
                                   float maxBreakSpeed = 20f;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                                   // Player can die if not protected and some strong blocks start breaking
                               }
                               if (hailSize == 5) {
                                    // bricks start getting damaged
                                   float maxBreakSpeed = 25f;
                                   if (block.getDestroySpeed(level,blockPos) <= maxBreakSpeed) extracted(blockPos);
                               }
                           }
                        }
                    }
                }

            }
            cachedHailLevels.clear();
        }

    private static void handleHailBlockDestroyProcess(Map.Entry<BlockPos, HailDestroyBlock> hailBlock, BlockState block, float maxBreakSpeed, Level level, BlockPos blockPos, List<BlockPos> toRemove, List<ServerPlayer> plrs) {
        if (GetBlockSpeed(block) <= maxBreakSpeed) {
            float destroySpeed = getDestroySpeed(block, level, blockPos, maxBreakSpeed);

            float tickCount = (destroySpeed) * 20;
            float tickIncrement = 10 / tickCount;
            hailBlock.getValue().progress += tickIncrement * (float)Config.blockDamageModifier;
            ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(blockPos.hashCode(), blockPos, (int) hailBlock.getValue().progress);
            if (hailBlock.getValue().progress >= 10) {
                toRemove.add(hailBlock.getKey());
                level.destroyBlock(blockPos, false);
            }
            for (ServerPlayer player : plrs) {
                player.connection.send(packet);
            }
        }
    }

    private static float getDestroySpeed(BlockState block, Level level, BlockPos blockPos, float maxBreakSpeed) {
        return ((GetBlockSpeed(block) * (float) Config.destroyTime) / maxBreakSpeed) * (GetBlockSpeed(block) * (float) Config.destroyTime);
    }

    private static void extracted(BlockPos blockPos) {
        if (!hailAffectedBlocks.containsKey(blockPos.below())) {
            hailAffectedBlocks.put(blockPos.below(), new HailDestroyBlock(blockPos.below()));
        }
    }


    public static float getHail(Level level,Vec3 pos) {
            ChunkPos chunkPos = new ChunkPos(new BlockPos((int)pos.x,(int)pos.y,(int)pos.z));
            CachedHailLevel cHL = new CachedHailLevel(level.dimension(),chunkPos);
            WeatherHandlerServer handler = (WeatherHandlerServer) GameBusEvents.MANAGERS.get(level.dimension());
            float precip = 0.0F;
            float finalPrecip = 0.0f;
            if (!cachedHailLevels.containsKey(cHL)) {
                for(Storm storm : handler.getStorms()) {
                    if (!storm.visualOnly) {
                        precip += storm.getHail(pos);
                    }
                }
                finalPrecip = Math.clamp(precip, 0.0F, 1.0F);
            }
            else {
                finalPrecip = cachedHailLevels.get(cHL);
            }
            return finalPrecip;
    }

    private static boolean canPrecipitateAt(Level level, BlockPos pos) {
            WeatherHandlerServer handler = (WeatherHandlerServer) GameBusEvents.MANAGERS.get(level.dimension());
           return  handler.getWorld().canSeeSky(pos);
    }
}
