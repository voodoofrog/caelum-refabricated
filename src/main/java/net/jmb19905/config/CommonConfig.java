package net.jmb19905.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static ForgeConfigSpec.EnumValue<MoonOrbitType> moonOrbitType;
    public static ForgeConfigSpec.LongValue lunarOrbitPeriod;

    public static void init(ForgeConfigSpec.Builder client) {
        moonOrbitType = client.comment("If the moon orbit and phases should be custom or vanilla").defineEnum("moon.orbit_type", MoonOrbitType.CUSTOM);
        lunarOrbitPeriod = client.comment("How long does it take for the Moon to complete its orbit in ticks. (If CUSTOM moon orbit type is selected)").defineInRange("moon.orbit_period", 708734L, 1, Long.MAX_VALUE);
    }
}
