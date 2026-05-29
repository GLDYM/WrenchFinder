package dev.polaris_light.wrenchfinder.network;

import dev.polaris_light.wrenchfinder.config.HandActionMode;
import dev.polaris_light.wrenchfinder.logic.ItemLookupService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketFindItem {
    private final String blockId;
    private final List<String> itemPatterns;
    private final HandActionMode actionMode;
    private final boolean notifyFailures;

    public PacketFindItem(String blockId, List<String> itemPatterns, HandActionMode actionMode, boolean notifyFailures) {
        this.blockId = blockId;
        this.itemPatterns = List.copyOf(itemPatterns);
        this.actionMode = actionMode;
        this.notifyFailures = notifyFailures;
    }

    public static void encode(PacketFindItem message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.blockId, 200);
        buffer.writeVarInt(message.itemPatterns.size());
        for (String pattern : message.itemPatterns) {
            buffer.writeUtf(pattern, 200);
        }
        buffer.writeEnum(message.actionMode);
        buffer.writeBoolean(message.notifyFailures);
    }

    public static PacketFindItem decode(FriendlyByteBuf buffer) {
        String blockId = buffer.readUtf(200);
        int size = buffer.readVarInt();
        ArrayList<String> patterns = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            patterns.add(buffer.readUtf(200));
        }
        HandActionMode actionMode = buffer.readEnum(HandActionMode.class);
        boolean notifyFailures = buffer.readBoolean();
        return new PacketFindItem(blockId, patterns, actionMode, notifyFailures);
    }

    public static class Handler {
        public static void handle(PacketFindItem message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player != null) {
                    ItemLookupService.lookupAndEquip(player, message.itemPatterns, message.actionMode, message.notifyFailures);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
