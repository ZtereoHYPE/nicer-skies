package codes.ztereohype.nicerskies.core;

import codes.ztereohype.nicerskies.IClientLevelAccessor;
import net.minecraft.client.Minecraft;

public class NebulaSeedManager {
    public static long getSeed() {
        if (Minecraft.getInstance().level != null) {
            return ((IClientLevelAccessor) Minecraft.getInstance().level).nicerSkies_getHashedSeed();
        }
        return 321L; // handpicked decent default ;)
    }

    public static boolean canGenerateSky() {
        return Minecraft.getInstance().hasSingleplayerServer() || Minecraft.getInstance().getCurrentServer() != null;
    }
}
