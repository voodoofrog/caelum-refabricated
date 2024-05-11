package net.jmb19905.common;

import net.jmb19905.config.CommonConfig;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

public record MoonController(long initialPosition, long orbitPeriod, int phases) {
    private static final LazyInitializer<MoonController> MOON = new LazyInitializer<>() {
        @Override
        protected MoonController initialize() {
            return new MoonController(0, CommonConfig.lunarOrbitPeriod.get(), 8);
        }
    };

    public float getMoonOrbitPosition(long gameTime) {
        return ((gameTime+initialPosition) % orbitPeriod)/(float)orbitPeriod;
    }

    public static MoonController getInstance() {
        try {
            return MOON.get();
        } catch (ConcurrentException e) {
            throw new RuntimeException(e);
        }
    }

    public int getMoonPhase(long gameTime) {
        double orbitTime = getMoonOrbitPosition(gameTime);
        double eighth = 1/8d;

        int stage  = (int) Math.ceil(orbitTime*7);
        if(orbitTime < eighth/2d || orbitTime > 1-eighth/2d){
            stage = 0;
        }

        return stage == 0 ? stage : 8 - stage;
    }

}