package com.real.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TargetingBlockPosCommand {
    
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("targetingBlockPos")
            .executes(TargetingBlockPosCommand::execute));
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
        
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            player.sendMessage(Text.literal("你没有指向任何方块")
                .formatted(Formatting.YELLOW), false);
            return 0;
        }
        
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = client.world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        
        // 获取方块名称
        String blockName = block.getName().getString();
        
        // 计算距离
        Vec3d blockCenter = Vec3d.ofCenter(blockPos);
        double distance = blockCenter.distanceTo(player.getPos());
        
        // 发送坐标信息
        Text blockInfo = Text.literal(String.format("指向方块: %s", blockName))
            .formatted(Formatting.GREEN);
        player.sendMessage(blockInfo, false);
        
        Text posInfo = Text.literal(String.format("坐标: X=%d, Y=%d, Z=%d", 
            blockPos.getX(), blockPos.getY(), blockPos.getZ()))
            .formatted(Formatting.AQUA);
        player.sendMessage(posInfo, false);
        
        Text distanceInfo = Text.literal(String.format("距离: %.2f格", distance))
            .formatted(Formatting.GOLD);
        player.sendMessage(distanceInfo, false);
        
        // 显示击中面的信息
        String side = blockHitResult.getSide().toString();

        Text sideInfo = Text.literal(String.format("击中面: %s", side))
            .formatted(Formatting.GRAY);
        player.sendMessage(sideInfo, false);
        
        return 1;
    }
} 