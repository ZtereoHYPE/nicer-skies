package codes.ztereohype.example;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	public static boolean toggle = true;

	@Override
	public void onInitialize() {}
}
