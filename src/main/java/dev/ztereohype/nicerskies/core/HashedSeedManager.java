package dev.ztereohype.nicerskies.core;

import dev.ztereohype.nicerskies.ClientLevelAccessor;
import dev.ztereohype.nicerskies.NicerSkies;
import net.minecraft.client.Minecraft;

public class HashedSeedManager {
    public static long getSeed() {
        if (Minecraft.getInstance().level != null) {
            return ((ClientLevelAccessor) Minecraft.getInstance().level).nicerSkies_getHashedSeed();
        }

        NicerSkies.LOGGER.warn("Generating nebulas with fallback seed!");
        return 321L; // handpicked decent default ;)
    }

    public static boolean canGenerateSky() {
        return Minecraft.getInstance().level != null;
    }
}
