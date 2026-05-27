package dev.polaris_light.wrenchfinder.network;

import dev.polaris_light.wrenchfinder.WrenchFinder;
import dev.polaris_light.wrenchfinder.config.HandActionMode;
import dev.polaris_light.wrenchfinder.logic.ItemLookupService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record PacketFindItem(
    String blockId,
    List<String> itemPatterns,
    HandActionMode actionMode,
    boolean notifyFailures
) implements CustomPacketPayload {
    public static final Type<PacketFindItem> ID = new Type<>(WrenchFinder.loc("find_item"));
    public static final StreamCodec<FriendlyByteBuf, PacketFindItem> CODEC = CustomPacketPayload.codec(
        PacketFindItem::encode,
        PacketFindItem::new
    );

    public PacketFindItem(FriendlyByteBuf buffer) {
        this(
            buffer.readUtf(200),
            readPatterns(buffer),
            buffer.readEnum(HandActionMode.class),
            buffer.readBoolean()
        );
    }

    private static List<String> readPatterns(FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        ArrayList<String> patterns = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            patterns.add(buffer.readUtf(200));
        }
        return patterns;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(blockId, 200);
        buffer.writeVarInt(itemPatterns.size());
        for (String pattern : itemPatterns) {
            buffer.writeUtf(pattern, 200);
        }
        buffer.writeEnum(actionMode);
        buffer.writeBoolean(notifyFailures);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static class Handler {
        public static void handle(PacketFindItem message, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.player() instanceof ServerPlayer player) {
                    ItemLookupService.lookupAndEquip(player, message.itemPatterns(), message.actionMode(), message.notifyFailures());
                }
            }).exceptionally(error -> {
                ctx.disconnect(Component.translatable("message.wrenchfinder.network_failed", error.getMessage()));
                return null;
            });
        }
    }
}
