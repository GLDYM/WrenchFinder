package dev.polaris_light.wrenchfinder.containers;

import dev.polaris_light.wrenchfinder.WrenchFinder;
import dev.polaris_light.wrenchfinder.containers.handlers.HandlerAdvWirelessTerminal;
import dev.polaris_light.wrenchfinder.containers.handlers.HandlerBundle;
import dev.polaris_light.wrenchfinder.containers.handlers.HandlerCapability;
import dev.polaris_light.wrenchfinder.containers.handlers.HandlerNetTerminal;
import dev.polaris_light.wrenchfinder.containers.handlers.HandlerOccultism;
import dev.polaris_light.wrenchfinder.containers.handlers.HandlerPortableCell;
// import dev.polaris_light.wrenchfinder.containers.handlers.HandlerProjectE;
import dev.polaris_light.wrenchfinder.containers.handlers.HandlerShulkerbox;
import dev.polaris_light.wrenchfinder.containers.handlers.HandlerWirelessGrid;
import dev.polaris_light.wrenchfinder.containers.handlers.HandlerWirelessTerminal;
import net.neoforged.fml.ModList;

public final class ContainerRegistrar {
    private ContainerRegistrar() {
    }

    public static void register() {
        if (ModList.get().isLoaded("ae2")) {
            WrenchFinder.containerManager.register(new HandlerPortableCell());
            WrenchFinder.containerManager.register(new HandlerWirelessTerminal());
            WrenchFinder.LOGGER.info("Applied Energistics 2 integration added");
        }

        if (ModList.get().isLoaded("toms_storage")) {
            WrenchFinder.containerManager.register(new HandlerAdvWirelessTerminal());
            WrenchFinder.LOGGER.info("Tom's Simple Storage integration added");
        }

        if (ModList.get().isLoaded("refinedstorage")) {
            WrenchFinder.containerManager.register(new HandlerWirelessGrid());
            WrenchFinder.LOGGER.info("Refined Storage integration added");
        }

        if (ModList.get().isLoaded("beyonddimensions")) {
            WrenchFinder.containerManager.register(new HandlerNetTerminal());
            WrenchFinder.LOGGER.info("Beyond Dimensions integration added");
        }

        if (ModList.get().isLoaded("occultism")) {
            WrenchFinder.containerManager.register(new HandlerOccultism());
            WrenchFinder.LOGGER.info("Occultism integration added");
        }

        // if (ModList.get().isLoaded("projecte")) {
        //     WrenchFinder.containerManager.register(new HandlerProjectE());
        //     WrenchFinder.LOGGER.info("ProjectE integration added");
        // }

        WrenchFinder.containerManager.register(new HandlerShulkerbox());
        WrenchFinder.containerManager.register(new HandlerBundle());
        WrenchFinder.containerManager.register(new HandlerCapability());
    }
}
