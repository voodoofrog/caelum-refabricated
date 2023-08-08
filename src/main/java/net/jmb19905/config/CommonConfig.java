package net.jmb19905.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static ForgeConfigSpec.LongValue lunarOrbitPeriod;

    public static void init(ForgeConfigSpec.Builder client) {
        lunarOrbitPeriod = client.comment("How long does it take for the Moon to complete its orbit in ticks.").defineInRange("moon.orbit_period", 708734L, 1, Long.MAX_VALUE);
    }
}
