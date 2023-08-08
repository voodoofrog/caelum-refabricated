package net.jmb19905.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHelper {

    private static final ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec commonConfig;
    public static final ForgeConfigSpec clientConfig;

    static {
        ClientConfig.init(clientBuilder);
        clientConfig = clientBuilder.build();

        CommonConfig.init(commonBuilder);
        commonConfig = commonBuilder.build();
    }
}
