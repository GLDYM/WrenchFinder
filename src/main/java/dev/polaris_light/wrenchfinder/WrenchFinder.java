package dev.polaris_light.wrenchfinder;

import dev.polaris_light.wrenchfinder.containers.ContainerManager;
import dev.polaris_light.wrenchfinder.containers.ContainerRegistrar;
import dev.polaris_light.wrenchfinder.config.ClientConfig;
import dev.polaris_light.wrenchfinder.network.ModMessages;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WrenchFinder.MODID)
public class WrenchFinder {
    public static final String MODID = "wrenchfinder";
    public static final Logger LOGGER = LogManager.getLogger();
    public static ContainerManager containerManager;

    public WrenchFinder(IEventBus eventBus, ModContainer container) {
        containerManager = new ContainerManager();

        eventBus.addListener(this::commonSetup);
        eventBus.addListener(ModMessages::registerPayloads);

        ClientConfig.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("WrenchFinder is ready to search.");
        ContainerRegistrar.register();
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
