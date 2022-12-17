package codes.ztereohype.nicerskies.core;

import codes.ztereohype.nicerskies.IClientLevelAccessor;
import net.minecraft.client.Minecraft;

public class NebulaSeedManager {
    public static long getSeed() {

        // Use hashed seed. This is available in
        if (Minecraft.getInstance().level != null) {
            return ((IClientLevelAccessor) Minecraft.getInstance().level).nicerSkies_getHashedSeed();
        }
        return 0;
    }

    public static boolean canGetSeed() {
        return Minecraft.getInstance().hasSingleplayerServer() || Minecraft.getInstance().getCurrentServer() != null;
    }
}
