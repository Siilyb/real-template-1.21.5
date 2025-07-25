package com.real;

import com.real.command.EntityFindCommand;
import com.real.command.TargetingBlockPosCommand;
import com.real.command.TargetingEntityPosCommand;
import com.real.config.ConfigManager;
import com.real.features.*;
import com.real.gui.MainConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class realClient implements ClientModInitializer {
    
    // 键绑定
    private static KeyBinding blockHighlighterKey;
    private static KeyBinding entityHighlighterKey;
    private static KeyBinding configMenuKey;
    
    @Override
    public void onInitializeClient() {
        // 加载配置
        ConfigManager.loadConfig();
        
        // 初始化功能
        BlockHighlighter.initialize();
        EntityHighlighter.initialize();
        AutoPhanflare.initialize();
        ChatCopy.initialize();
        BeaconSolver.initialize();
        ChestItemLogger.initialize();

        
        // 初始化键绑定
        initializeKeyBindings();
        
        // 注册客户端命令
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            EntityFindCommand.register(dispatcher);
            TargetingEntityPosCommand.register(dispatcher);
            TargetingBlockPosCommand.register(dispatcher);
        });
        
        // 注册客户端tick事件
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // 检查按键
            checkKeyBindings();
            
            // 运行AutoPhanflare
            AutoPhanflare.tick();
        });
        
        real.LOGGER.info("Real Mod 客户端已初始化");
    }
    
    private void initializeKeyBindings() {
        ConfigManager.KeyBindingConfig config = ConfigManager.getConfig().keyBindings;
        
        // 创建键绑定
        blockHighlighterKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.real.toggle_block_highlighter",
            InputUtil.Type.KEYSYM,
            getKeyCode(config.toggleBlockHighlighter),
            "category.real.general"
        ));
        
        entityHighlighterKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.real.toggle_entity_highlighter",
            InputUtil.Type.KEYSYM,
            getKeyCode(config.toggleEntityHighlighter),
            "category.real.general"
        ));
        
        configMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.real.open_config_menu",
            InputUtil.Type.KEYSYM,
            getKeyCode(config.openConfigMenu),
            "category.real.general"
        ));
    }
    
    private void checkKeyBindings() {
        while (blockHighlighterKey.wasPressed()) {
            BlockHighlighter.toggle();
            ConfigManager.getConfig().blockHighlighter.enabled = BlockHighlighter.isEnabled();
            if (net.minecraft.client.MinecraftClient.getInstance().player != null) {
                net.minecraft.client.MinecraftClient.getInstance().player.sendMessage(
                    Text.literal("方块高亮器: " + (BlockHighlighter.isEnabled() ? "开启" : "关闭"))
                        .formatted(BlockHighlighter.isEnabled() ? Formatting.GREEN : Formatting.RED),
                    false
                );
            }
        }
        
        while (entityHighlighterKey.wasPressed()) {
            EntityHighlighter.toggle();
            ConfigManager.getConfig().entityHighlighter.enabled = EntityHighlighter.isEnabled();
            if (net.minecraft.client.MinecraftClient.getInstance().player != null) {
                net.minecraft.client.MinecraftClient.getInstance().player.sendMessage(
                    Text.literal("实体高亮器: " + (EntityHighlighter.isEnabled() ? "开启" : "关闭"))
                        .formatted(EntityHighlighter.isEnabled() ? Formatting.GREEN : Formatting.RED),
                    false
                );
            }
        }
        
        while (configMenuKey.wasPressed()) {
            net.minecraft.client.MinecraftClient.getInstance().setScreen(new MainConfigScreen(null));
        }
    }
    
    private int getKeyCode(String keyName) {
        switch (keyName.toUpperCase()) {
            case "SPACE": return GLFW.GLFW_KEY_SPACE;
            case "ENTER": return GLFW.GLFW_KEY_ENTER;
            case "TAB": return GLFW.GLFW_KEY_TAB;
            case "BACKSPACE": return GLFW.GLFW_KEY_BACKSPACE;
            case "DELETE": return GLFW.GLFW_KEY_DELETE;
            case "INSERT": return GLFW.GLFW_KEY_INSERT;
            case "HOME": return GLFW.GLFW_KEY_HOME;
            case "END": return GLFW.GLFW_KEY_END;
            case "PAGE_UP": return GLFW.GLFW_KEY_PAGE_UP;
            case "PAGE_DOWN": return GLFW.GLFW_KEY_PAGE_DOWN;
            case "LEFT": return GLFW.GLFW_KEY_LEFT;
            case "RIGHT": return GLFW.GLFW_KEY_RIGHT;
            case "UP": return GLFW.GLFW_KEY_UP;
            case "DOWN": return GLFW.GLFW_KEY_DOWN;
            case "LEFT_SHIFT": return GLFW.GLFW_KEY_LEFT_SHIFT;
            case "RIGHT_SHIFT": return GLFW.GLFW_KEY_RIGHT_SHIFT;
            case "LEFT_CONTROL": return GLFW.GLFW_KEY_LEFT_CONTROL;
            case "RIGHT_CONTROL": return GLFW.GLFW_KEY_RIGHT_CONTROL;
            case "LEFT_ALT": return GLFW.GLFW_KEY_LEFT_ALT;
            case "RIGHT_ALT": return GLFW.GLFW_KEY_RIGHT_ALT;
            case "ESCAPE": return GLFW.GLFW_KEY_ESCAPE;
            default:
                if (keyName.length() == 1) {
                    char c = keyName.charAt(0);
                    if (c >= 'A' && c <= 'Z') {
                        return GLFW.GLFW_KEY_A + (c - 'A');
                    } else if (c >= '0' && c <= '9') {
                        return GLFW.GLFW_KEY_0 + (c - '0');
                    }
                } else if (keyName.startsWith("F") && keyName.length() <= 3) {
                    try {
                        int fNum = Integer.parseInt(keyName.substring(1));
                        if (fNum >= 1 && fNum <= 12) {
                            return GLFW.GLFW_KEY_F1 + (fNum - 1);
                        }
                    } catch (NumberFormatException ignored) {}
                }
                return GLFW.GLFW_KEY_UNKNOWN;
        }
    }
} 