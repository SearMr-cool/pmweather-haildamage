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
    public static final ModConfigSpec thing;

    @SubscribeEvent
    private static void onLoad(ModConfigEvent event) {
        if (!(event instanceof ModConfigEvent.Unloading) && event.getConfig().getSpec() == thing){
            destroyTime = (Integer) hail_destroy_time.get();
        }


    }



    static  {
        hail_destroy_time = BUILDER.comment("Yo").defineInRange("haildestroytime",2,1,10);
      thing = BUILDER.build();
    }
}

