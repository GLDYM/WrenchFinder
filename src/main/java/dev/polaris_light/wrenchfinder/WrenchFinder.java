package dev.polaris_light.wrenchfinder;

import dev.polaris_light.wrenchfinder.containers.ContainerManager;
import dev.polaris_light.wrenchfinder.containers.ContainerRegistrar;
import dev.polaris_light.wrenchfinder.config.ClientConfig;
import dev.polaris_light.wrenchfinder.network.ModMessages;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WrenchFinder.MODID)
public class WrenchFinder {
    public static final String MODID = "wrenchfinder";
    public static final Logger LOGGER = LogManager.getLogger();
    public static WrenchFinder instance;
    public static ContainerManager containerManager;

    public WrenchFinder() {
        instance = this;
        containerManager = new ContainerManager();

        ClientConfig.init();
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        context.getModEventBus().addListener(this::commonSetup);
        context.getModEventBus().addListener(this::clientSetup);
        context.getModEventBus().addListener(dev.polaris_light.wrenchfinder.client.WrenchFinderClient::registerKeyMappings);
        MinecraftForge.EVENT_BUS.register(new dev.polaris_light.wrenchfinder.client.KeybindHandler());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> registerConfigScreen());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("WrenchFinder is ready to search.");
        ModMessages.register();
        ContainerRegistrar.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }

    private void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) ->
                dev.polaris_light.wrenchfinder.client.WrenchFinderClient.createConfigScreen(parent)
            )
        );
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }
}
