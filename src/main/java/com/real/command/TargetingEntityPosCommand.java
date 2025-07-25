package com.real.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class TargetingEntityPosCommand {
    
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("targetingEntityPos")
            .executes(TargetingEntityPosCommand::execute));
    }
    
    private static int execute(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.player == null || client.world == null) {
            context.getSource().sendError(Text.literal("命令只能在游戏中使用"));
            return 0;
        }
        
        PlayerEntity player = client.player;
        
        // 获取玩家的十字准星目标
        HitResult hitResult = client.crosshairTarget;
        
        if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
            player.sendMessage(Text.literal("你没有指向任何实体")
                .formatted(Formatting.YELLOW), false);
            return 0;
        }
        
        EntityHitResult entityHitResult = (EntityHitResult) hitResult;
        Entity targetEntity = entityHitResult.getEntity();
        Vec3d entityPos = targetEntity.getPos();
        
        // 获取实体名称
        String entityName = targetEntity.getType().getName().getString();
        String customName = targetEntity.hasCustomName() ? targetEntity.getCustomName().getString() : null;
        
        // 计算距离
        double distance = entityPos.distanceTo(player.getPos());
        
        // 发送坐标信息
        Text entityInfo;
        if (customName != null) {
            entityInfo = Text.literal(String.format("指向实体: %s (%s)", entityName, customName))
                .formatted(Formatting.GREEN);
        } else {
            entityInfo = Text.literal(String.format("指向实体: %s", entityName))
                .formatted(Formatting.GREEN);
        }
        
        player.sendMessage(entityInfo, false);
        
        Text posInfo = Text.literal(String.format("坐标: X=%.2f, Y=%.2f, Z=%.2f", 
            entityPos.x, entityPos.y, entityPos.z))
            .formatted(Formatting.AQUA);
        player.sendMessage(posInfo, false);
        
        Text distanceInfo = Text.literal(String.format("距离: %.2f格", distance))
            .formatted(Formatting.GOLD);
        player.sendMessage(distanceInfo, false);
        
        return 1;
    }
} 