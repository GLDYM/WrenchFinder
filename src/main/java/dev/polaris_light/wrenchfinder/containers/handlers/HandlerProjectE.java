// package dev.polaris_light.wrenchfinder.containers.handlers;

// import dev.polaris_light.wrenchfinder.api.IContainerHandler;
// import dev.polaris_light.wrenchfinder.containers.ContainerTrace;
// import java.math.BigInteger;
// import java.util.Objects;
// import moze_intel.projecte.api.ItemInfo;
// import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
// import moze_intel.projecte.api.capabilities.PECapabilities;
// import moze_intel.projecte.api.proxy.IEMCProxy;
// import net.minecraft.core.registries.BuiltInRegistries;
// import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.item.ItemStack;

// public class HandlerProjectE implements IContainerHandler {
//     private static final String PROJECTE = "projecte";

//     @Override
//     public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
//         return inventoryStack != null && !inventoryStack.isEmpty() && isTransmutationAccess(inventoryStack);
//     }

//     @Override
//     public int getSignature(Player player, ItemStack inventoryStack) {
//         return Objects.hash(PROJECTE, player.getUUID());
//     }

//     @Override
//     public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
//         IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
//         if (knowledge == null) {
//             return 0;
//         }

//         ItemInfo info = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(itemStack));
//         long value = IEMCProxy.INSTANCE.getValue(info);
//         if (!knowledge.hasKnowledge(info) || value <= 0) {
//             return 0;
//         }

//         BigInteger available = knowledge.getEmc().divide(BigInteger.valueOf(value));
//         return available.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 ? Integer.MAX_VALUE : available.intValue();
//     }

//     @Override
//     public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
//         if (count <= 0 || player.level().isClientSide()) {
//             return count;
//         }

//         IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
//         if (knowledge == null) {
//             return count;
//         }

//         ItemInfo info = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(itemStack));
//         long value = IEMCProxy.INSTANCE.getValue(info);
//         if (!knowledge.hasKnowledge(info) || value <= 0) {
//             return count;
//         }

//         BigInteger unitCost = BigInteger.valueOf(value);
//         int extracted = knowledge.getEmc().divide(unitCost).min(BigInteger.valueOf(count)).intValue();
//         if (extracted <= 0) {
//             return count;
//         }

//         knowledge.setEmc(knowledge.getEmc().subtract(unitCost.multiply(BigInteger.valueOf(extracted))));
//         if (player instanceof ServerPlayer serverPlayer) {
//             knowledge.syncEmc(serverPlayer);
//         }
//         return count - extracted;
//     }

//     private static boolean isTransmutationAccess(ItemStack stack) {
//         var itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
//         return itemId.getNamespace().equals(PROJECTE)
//             && (itemId.getPath().equals("transmutation_table") || itemId.getPath().equals("transmutation_tablet"));
//     }
// }
