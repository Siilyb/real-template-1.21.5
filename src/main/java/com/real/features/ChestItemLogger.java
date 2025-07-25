package com.real.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import java.util.List;

public class ChestItemLogger {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static boolean enabled = true;

    public static void initialize() {

    }

    /**
     * 切换功能开关
     */
    public static void toggle() {
        enabled = !enabled;
        String status = enabled ? "启用" : "禁用";
        if (client.player != null) {
            client.player.sendMessage(Text.literal("§a箱子物品记录器已" + status),false);
        }
        System.out.println("[ChestItemLogger] 箱子物品记录器已" + status);
    }


    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * 当打开箱子界面时调用此方法
     */
    public static void onChestScreenOpen(ScreenHandler screenHandler) {
        if (!enabled || screenHandler == null) return;

        logItems(screenHandler);
    }
    
    /**
     * 记录箱子中的物品
     */
    private static void logItems(ScreenHandler screenHandler) {
        List<Slot> slots = screenHandler.slots;
        int containerSize = screenHandler.slots.size();

        for (int i = 0; i < containerSize && i < slots.size(); i++) {
            Slot slot = slots.get(i);
            ItemStack stack = slot.getStack();
            System.out.println("位置"+i+"的物品是"+stack.getName()+"****"+stack.getFormattedName());

        }
        

    }
} 