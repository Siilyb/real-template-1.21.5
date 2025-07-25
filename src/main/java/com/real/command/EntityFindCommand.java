package com.real.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class EntityFindCommand {
    
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("entityfind")
            .executes(EntityFindCommand::execute));
    }
    
    private static int execute(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.player == null || client.world == null) {
            context.getSource().sendError(Text.literal("命令只能在游戏中使用"));
            return 0;
        }
        
        PlayerEntity player = client.player;
        Vec3d playerPos = player.getPos();
        
        // 查找附近15格内的实体
        List<Entity> nearbyEntities = new ArrayList<>();
        double searchRadius = 15.0;
        
        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue; // 跳过玩家自己
            
            double distance = entity.getPos().distanceTo(playerPos);
            if (distance <= searchRadius) {
                nearbyEntities.add(entity);
            }
        }
        
        // 发送结果
        if (nearbyEntities.isEmpty()) {
            player.sendMessage(Text.literal("附近15格内没有找到任何实体")
                .formatted(Formatting.YELLOW), false);
        } else {
            player.sendMessage(Text.literal("附近15格内找到 " + nearbyEntities.size() + " 个实体:")
                .formatted(Formatting.GREEN), false);
            
            // 按距离排序
            nearbyEntities.sort((e1, e2) -> {
                double dist1 = e1.getPos().distanceTo(playerPos);
                double dist2 = e2.getPos().distanceTo(playerPos);
                return Double.compare(dist1, dist2);
            });
            
            // 显示实体信息
            for (Entity entity : nearbyEntities) {
                double distance = entity.getPos().distanceTo(playerPos);
                String entityName = entity.getType().getName().getString();
                String customName = entity.hasCustomName() ? entity.getCustomName().getString() : null;


                Text entityInfo;
                if (customName != null) {
                    entityInfo = Text.literal(String.format("  • %s (%s) - %.1f格", 
                        entityName, customName, distance))
                        .formatted(Formatting.AQUA);
                } else {
                    entityInfo = Text.literal(String.format("  • %s - %.1f格", 
                        entityName, distance))
                        .formatted(Formatting.AQUA);
                }
                
                player.sendMessage(entityInfo, false);
            }
        }
        
        return 1;
    }
} 