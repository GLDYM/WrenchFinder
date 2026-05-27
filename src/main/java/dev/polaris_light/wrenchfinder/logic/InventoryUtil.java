package dev.polaris_light.wrenchfinder.logic;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class InventoryUtil {
    private InventoryUtil() {
    }

    public static boolean stackEquals(ItemStack stackA, ItemStack stackB) {
        return ItemStack.isSameItemSameComponents(stackA, stackB);
    }

    public static List<ItemStack> getArmor(Player player) {
        ArrayList<ItemStack> armor = new ArrayList<>(4);
        int inventorySize = Inventory.INVENTORY_SIZE;
        armor.add(player.getInventory().getItem(EquipmentSlot.FEET.getIndex(inventorySize)));
        armor.add(player.getInventory().getItem(EquipmentSlot.LEGS.getIndex(inventorySize)));
        armor.add(player.getInventory().getItem(EquipmentSlot.CHEST.getIndex(inventorySize)));
        armor.add(player.getInventory().getItem(EquipmentSlot.HEAD.getIndex(inventorySize)));
        return armor;
    }

    public static List<ItemStack> getCuriosInv(Player player) {
        List<ItemStack> result = new ArrayList<>();
        if (ModList.get().isLoaded("curios")) {
            CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
                Map<String, ICurioStacksHandler> curios = handler.getCurios();
                for (ICurioStacksHandler slotHandler : curios.values()) {
                    for (int i = 0; i < slotHandler.getSlots(); i++) {
                        ItemStack stack = slotHandler.getStacks().getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            result.add(stack);
                        }
                    }
                }
            });
        }
        return result;
    }

    public static List<ItemStack> getFullInv(Player player) {
        ArrayList<ItemStack> inventory = new ArrayList<>(player.getInventory().offhand);
        inventory.addAll(player.getInventory().items);
        inventory.addAll(getArmor(player));
        inventory.addAll(getCuriosInv(player));
        return inventory;
    }
}
