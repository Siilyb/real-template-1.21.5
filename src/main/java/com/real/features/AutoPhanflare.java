package com.real.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

public class AutoPhanflare {
    private static boolean enabled = false;
    private static long lastUseTime = 0;
    private static final int USE_COOLDOWN = 400; // 0.2秒 = 200毫秒
    private static final double SEARCH_RADIUS = 15.0;
    private static int tickCounter = 0;
    private static final int CHECK_INTERVAL = 10; // 每10tick检查一次
    
    public static void initialize() {
        // 初始化功能
    }
    
    public static void tick() {
        if (!enabled) return;
        
        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        // 检查是否在冷却时间内
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUseTime < USE_COOLDOWN) return;

        // 查找附近的REEL实体

        if (findReelEntity(client)) {
            // 执行右键操作
            performRightClick(client);
            lastUseTime = currentTime;
        }
    }
    
    private static boolean findReelEntity(MinecraftClient client) {
        PlayerEntity player = client.player;
        Vec3d playerPos = player.getPos();
        
        // 搜索附近15格内的实体
        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;
            
            double distance = entity.getPos().distanceTo(playerPos);
            if (distance <= SEARCH_RADIUS) {

                // 检查实体的customname是否为REEL
                if (entity.hasCustomName()) {

                    String customName = entity.getCustomName().getString();

                    if ("REEL".equals(customName)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    private static void performRightClick(MinecraftClient client) {
        if (client.player == null) return;

        // 模拟右键点击
        if (client.interactionManager != null) {
            // 使用主手物品

         /*   HitResult hitResult = client.crosshairTarget;

            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHitResult);
                */
         //   } else {
                // 如果没有方块目标，使用物品
            client.player.sendMessage(Text.literal("CLICK"),false);
          /*  try {
                client.options.useKey.setPressed(true);
                Thread.sleep(90);
                client.options.useKey.setPressed(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
            
          //  }
        }
    }
    
    public static void toggle() {
        enabled = !enabled;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(
                Text.literal("AutoPhanflare: " + (enabled ? "开启" : "关闭"))
                    .formatted(enabled ? Formatting.GREEN : Formatting.RED),
                false
            );
        }
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static void setEnabled(boolean enabled) {
        AutoPhanflare.enabled = enabled;
    }
} 