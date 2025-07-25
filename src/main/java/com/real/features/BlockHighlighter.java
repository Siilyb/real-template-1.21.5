package com.real.features;

import com.real.config.ConfigManager;
import com.real.util.WorldRenderUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockHighlighter {
    private static KeyBinding toggleKey;
    private static final List<HighlightedBlock> highlightedBlocks = new ArrayList<>();
    private static final float LINE_THICKNESS = 2.0f;
    
    public static void toggle() {
        ConfigManager.getConfig().blockHighlighter.enabled = !ConfigManager.getConfig().blockHighlighter.enabled;
        if (!ConfigManager.getConfig().blockHighlighter.enabled) {
            highlightedBlocks.clear();
        }
    }
    
    public static boolean isEnabled() {
        return ConfigManager.getConfig().blockHighlighter.enabled;
    }
    
    public static void setEnabled(boolean enabled) {
        ConfigManager.getConfig().blockHighlighter.enabled = enabled;
        if (!enabled) {
            highlightedBlocks.clear();
        }
    }

    public static void initialize() {
        // 注册按键绑定
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.real.toggleblockhighlight",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.real.features"
        ));

        // 注册按键监听器
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                toggle();
                
                if (client.player != null) {
                    if (isEnabled()) {
                        client.player.sendMessage(Text.literal("方块高亮功能已 ").append(
                                Text.literal("开启").formatted(Formatting.GREEN)), true);
                        scanForBlocks(client);
                    } else {
                        client.player.sendMessage(Text.literal("方块高亮功能已 ").append(
                                Text.literal("关闭").formatted(Formatting.RED)), true);
                    }
                }
            }
        });

        // 注册世界渲染事件
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            if (isEnabled()) {
                renderHighlightedBlocks(context);
            }
        });

        // 注册tick事件来周期性扫描方块
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (isEnabled() && client.player != null && client.world != null) {
                if (client.player.age % ConfigManager.getConfig().blockHighlighter.refreshInterval == 0) {
                    scanForBlocks(client);
                }
            }
        });
    }

    private static void scanForBlocks(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        
        highlightedBlocks.clear();
        BlockPos playerPos = client.player.getBlockPos();
        int scanRadius = ConfigManager.getConfig().blockHighlighter.scanRadius;
        Map<String, String> keywords = ConfigManager.getConfig().blockHighlighter.keywords;
        
        for (int x = -scanRadius; x <= scanRadius; x++) {
            for (int y = -scanRadius; y <= scanRadius; y++) {
                for (int z = -scanRadius; z <= scanRadius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    Block block = client.world.getBlockState(pos).getBlock();
                    String blockName = block.getTranslationKey().toLowerCase();
                    
                    for (Map.Entry<String, String> entry : keywords.entrySet()) {
                        String keyword = entry.getKey();
                        String colorHex = entry.getValue();
                        
                        if (blockName.contains(keyword)) {
                            Color color = ConfigManager.getColorFromHex(colorHex);
                            highlightedBlocks.add(new HighlightedBlock(pos, blockName, color, keyword));
                            break;
                        }
                    }
                }
            }
        }
        
        if (client.player != null) {
            client.player.sendMessage(Text.literal("找到 ")
                    .append(Text.literal(String.valueOf(highlightedBlocks.size()))
                            .formatted(Formatting.YELLOW))
                    .append(" 个匹配的方块"), false);
        }
    }

    private static void renderHighlightedBlocks(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext context) {
        for (HighlightedBlock block : highlightedBlocks) {
            BlockPos pos = block.pos;
            Box box = new Box(pos).expand(0.002); // 略微扩大以确保可见
            
            // 使用半透明填充颜色
            Color fillColor = new Color(block.color.getRed(), block.color.getGreen(), block.color.getBlue(), 20);
            // 使用较亮的轮廓颜色
            Color outlineColor = new Color(
                Math.min(255, block.color.getRed() + 60), 
                Math.min(255, block.color.getGreen() + 60), 
                Math.min(255, block.color.getBlue() + 60), 
                200
            );
            
            // 绘制填充方块
            WorldRenderUtils.drawBox(
                    context,
                    pos.getX(), pos.getY(), pos.getZ(),
                    1.0, 1.0, 1.0,
                    fillColor,
                    false
            );
            
            // 绘制线框
            WorldRenderUtils.drawWireFrame(
                    context,
                    box,
                    outlineColor,
                    LINE_THICKNESS,
                    false
            );
        }
    }

    private static class HighlightedBlock {
        final BlockPos pos;
        final String name;
        final Color color;
        final String keyword;

        HighlightedBlock(BlockPos pos, String name, Color color, String keyword) {
            this.pos = pos;
            this.name = name;
            this.color = color;
            this.keyword = keyword;
        }
    }
} 