package codes.ztereohype.example.core;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class NebulaSeedManager {
    public static long getSeed() {
        if (Minecraft.getInstance().hasSingleplayerServer()) {
            // calculate seed from overworld seed
            return Objects.hash(Minecraft.getInstance().getSingleplayerServer().getLevel(Level.OVERWORLD).getSeed());
        } else {
            return Objects.requireNonNull(Minecraft.getInstance().getCurrentServer()).ip.hashCode();
        }
    }

    public static boolean canGetSeed() {
        return Minecraft.getInstance().hasSingleplayerServer() || Minecraft.getInstance().getCurrentServer() != null;
    }
}
