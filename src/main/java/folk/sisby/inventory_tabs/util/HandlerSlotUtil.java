package folk.sisby.inventory_tabs.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.SlotClickC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Map;

public class HandlerSlotUtil {
    public static int stashSlot = -1;
    public static int mainHandSwapSlot = -1;

    public static void push(ClientPlayerEntity player, ScreenHandler handler) {
        if (!handler.getCursorStack().isEmpty()) {
            stashSlot = player.getInventory().getEmptySlot();
            if (stashSlot != -1) {
                handler.getSlotIndex(player.getInventory(), stashSlot).ifPresent((screenSlot) -> player.networkHandler.sendPacket(new SlotClickC2SPacket(
                        handler.syncId,
                        handler.getRevision(),
                        screenSlot,
                        0,
                        SlotActionType.PICKUP,
                        handler.getSlot(screenSlot).getStack().copy(),
                        new Int2ObjectOpenHashMap<>(Map.of(screenSlot, handler.getCursorStack().copy()))
                )));
            }
        }
    }

    public static void tryPop(ClientPlayerEntity player, ClientPlayerInteractionManager manager, ScreenHandler handler) {
        if (stashSlot != -1) {
            handler.getSlotIndex(player.getInventory(), stashSlot).ifPresent((screenSlot) -> manager.clickSlot(
                    handler.syncId,
                    screenSlot,
                    0, // Mouse Left Click
                    SlotActionType.PICKUP,
                    player
            ));
            stashSlot = -1;
        }
        if (mainHandSwapSlot != -1) {
            handler.getSlotIndex(player.getInventory(), mainHandSwapSlot).ifPresent((screenSlot) -> manager.clickSlot(
                    handler.syncId,
                    screenSlot,
                    player.getInventory().selectedSlot,
                    SlotActionType.SWAP,
                    player
            ));
            mainHandSwapSlot = -1;
        }
    }
}
