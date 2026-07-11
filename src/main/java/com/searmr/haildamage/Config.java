package com.searmr.haildamage;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Range;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(
        modid = "searmrhaildamage",
        bus=EventBusSubscriber.Bus.MOD

)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue hail_destroy_time;
    public static int destroyTime ;
    private static final ModConfigSpec.BooleanValue hail_destroys_blocks;
    public static boolean hailDestroysBlocks;
    private static final ModConfigSpec.DoubleValue entity_damage_modifier;
    public static double entityDamageModifier;
    private static final ModConfigSpec.DoubleValue block_damage_modifier;
    public static double blockDamageModifier ;
    public static final ModConfigSpec thing;

    @SubscribeEvent
    private static void onLoad(ModConfigEvent event) {
        if (!(event instanceof ModConfigEvent.Unloading) && event.getConfig().getSpec() == thing){
            destroyTime = (Integer) hail_destroy_time.get();
            hailDestroysBlocks = (Boolean) hail_destroys_blocks.get();
            entityDamageModifier = (double) entity_damage_modifier.get();
            blockDamageModifier = (double) block_damage_modifier.get();
        }


    }



    static  {
        hail_destroy_time = BUILDER.comment("How long it takes for blocks to be destroyed (1 means they break at their normal block speed while setting it to 2 would mean it takes 2 times as long and etc)").defineInRange("haildestroytime",4,1,10);
        hail_destroys_blocks = BUILDER.comment("Whether or not blocks are destroyed by hail").define("haildestroysblocks",true);
        entity_damage_modifier = BUILDER.comment("Multiplies the final entity damage calculated by this value").defineInRange("entitydamagemodifier",1d,0d,10d);
        block_damage_modifier = BUILDER.comment("multiplies the final increment value for block destruction by this value").defineInRange("blockdamagemodifier",1d,0d,10d);
      thing = BUILDER.build();
    }
}

