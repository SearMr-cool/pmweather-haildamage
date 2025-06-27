package com.searmr.haildamage;

import dev.protomanly.pmweather.PMWeather;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class HailDestroyBlock {
    public int id;
    public BlockPos blockPos;
    public float progress = 0;
    public HailDestroyBlock(BlockPos blockPos) {
        this.blockPos=blockPos;
        this.id=blockPos.hashCode();
    }

}
