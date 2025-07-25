package com.real.gui;

import com.real.config.ConfigManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class KeyBindingConfigScreen extends Screen {
    private final Screen parent;
    private ButtonWidget blockHighlighterButton;
    private ButtonWidget entityHighlighterButton;
    private ButtonWidget configMenuButton;
    private String waitingForKey = null;
    
    public KeyBindingConfigScreen(Screen parent) {
        super(Text.literal("键绑定配置"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = 60;
        
        ConfigManager.KeyBindingConfig config = ConfigManager.getConfig().keyBindings;
        
        // 方块高亮器按键
        blockHighlighterButton = ButtonWidget.builder(
            Text.literal("方块高亮器: " + config.toggleBlockHighlighter),
            button -> {
                if (waitingForKey == null) {
                    waitingForKey = "blockHighlighter";
                    button.setMessage(Text.literal("方块高亮器: [按任意键...]").formatted(Formatting.YELLOW));
                }
            }
        ).dimensions(centerX - 100, startY, 200, 20).build();
        this.addDrawableChild(blockHighlighterButton);
        
        // 实体高亮器按键
        entityHighlighterButton = ButtonWidget.builder(
            Text.literal("实体高亮器: " + config.toggleEntityHighlighter),
            button -> {
                if (waitingForKey == null) {
                    waitingForKey = "entityHighlighter";
                    button.setMessage(Text.literal("实体高亮器: [按任意键...]").formatted(Formatting.YELLOW));
                }
            }
        ).dimensions(centerX - 100, startY + 30, 200, 20).build();
        this.addDrawableChild(entityHighlighterButton);
        
        // 配置菜单按键
        configMenuButton = ButtonWidget.builder(
            Text.literal("配置菜单: " + config.openConfigMenu),
            button -> {
                if (waitingForKey == null) {
                    waitingForKey = "configMenu";
                    button.setMessage(Text.literal("配置菜单: [按任意键...]").formatted(Formatting.YELLOW));
                }
            }
        ).dimensions(centerX - 100, startY + 60, 200, 20).build();
        this.addDrawableChild(configMenuButton);
        
        // 重置按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("重置为默认"),
            button -> {
                ConfigManager.KeyBindingConfig keyConfig = ConfigManager.getConfig().keyBindings;
                keyConfig.toggleBlockHighlighter = "P";
                keyConfig.toggleEntityHighlighter = "E";
                keyConfig.openConfigMenu = "O";
                updateButtonTexts();
            }
        ).dimensions(centerX - 100, startY + 110, 200, 20).build());
        
        // 返回按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("返回"),
            button -> this.client.setScreen(parent)
        ).dimensions(centerX - 100, this.height - 40, 200, 20).build());
        
        // 保存配置按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("保存配置"),
            button -> {
                ConfigManager.saveConfig();
                if (this.client.player != null) {
                    this.client.player.sendMessage(Text.literal("配置已保存").formatted(Formatting.GREEN), false);
                }
            }
        ).dimensions(centerX - 100, this.height - 70, 200, 20).build());
    }
    
    private void updateButtonTexts() {
        ConfigManager.KeyBindingConfig config = ConfigManager.getConfig().keyBindings;
        blockHighlighterButton.setMessage(Text.literal("方块高亮器: " + config.toggleBlockHighlighter));
        entityHighlighterButton.setMessage(Text.literal("实体高亮器: " + config.toggleEntityHighlighter));
        configMenuButton.setMessage(Text.literal("配置菜单: " + config.openConfigMenu));
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForKey != null) {
            String keyName = getKeyName(keyCode);
            ConfigManager.KeyBindingConfig config = ConfigManager.getConfig().keyBindings;
            
            switch (waitingForKey) {
                case "blockHighlighter":
                    config.toggleBlockHighlighter = keyName;
                    break;
                case "entityHighlighter":
                    config.toggleEntityHighlighter = keyName;
                    break;
                case "configMenu":
                    config.openConfigMenu = keyName;
                    break;
            }
            
            waitingForKey = null;
            updateButtonTexts();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private String getKeyName(int keyCode) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_SPACE: return "SPACE";
            case GLFW.GLFW_KEY_ENTER: return "ENTER";
            case GLFW.GLFW_KEY_TAB: return "TAB";
            case GLFW.GLFW_KEY_BACKSPACE: return "BACKSPACE";
            case GLFW.GLFW_KEY_DELETE: return "DELETE";
            case GLFW.GLFW_KEY_INSERT: return "INSERT";
            case GLFW.GLFW_KEY_HOME: return "HOME";
            case GLFW.GLFW_KEY_END: return "END";
            case GLFW.GLFW_KEY_PAGE_UP: return "PAGE_UP";
            case GLFW.GLFW_KEY_PAGE_DOWN: return "PAGE_DOWN";
            case GLFW.GLFW_KEY_LEFT: return "LEFT";
            case GLFW.GLFW_KEY_RIGHT: return "RIGHT";
            case GLFW.GLFW_KEY_UP: return "UP";
            case GLFW.GLFW_KEY_DOWN: return "DOWN";
            case GLFW.GLFW_KEY_LEFT_SHIFT: return "LEFT_SHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT: return "RIGHT_SHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL: return "LEFT_CONTROL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL: return "RIGHT_CONTROL";
            case GLFW.GLFW_KEY_LEFT_ALT: return "LEFT_ALT";
            case GLFW.GLFW_KEY_RIGHT_ALT: return "RIGHT_ALT";
            case GLFW.GLFW_KEY_ESCAPE: return "ESCAPE";
            default:
                if (keyCode >= GLFW.GLFW_KEY_A && keyCode <= GLFW.GLFW_KEY_Z) {
                    return String.valueOf((char) keyCode);
                } else if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
                    return String.valueOf((char) keyCode);
                } else if (keyCode >= GLFW.GLFW_KEY_F1 && keyCode <= GLFW.GLFW_KEY_F12) {
                    return "F" + (keyCode - GLFW.GLFW_KEY_F1 + 1);
                }
                return "KEY_" + keyCode;
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal("点击按钮然后按下新的按键来更改绑定"), this.width / 2, 35, 0xCCCCCC);
        
        if (waitingForKey != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, 
                Text.literal("等待按键输入...").formatted(Formatting.YELLOW), this.width / 2, 45, 0xFFFF00);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        if (waitingForKey != null) {
            waitingForKey = null;
            updateButtonTexts();
            return false;
        }
        return true;
    }
    
    @Override
    public void close() {
        this.client.setScreen(parent);
    }
} 