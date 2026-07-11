package com.searmr.haildamage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class CachedHailLevel {
    public ResourceKey<Level> level;
    public ChunkPos chunkPos;

    public CachedHailLevel(ResourceKey<Level>
            levelT, ChunkPos chunkPosT) {
        level = levelT;
        chunkPos = chunkPosT;
    }

}
