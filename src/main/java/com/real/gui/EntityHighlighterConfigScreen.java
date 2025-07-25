package com.real.gui;

import com.real.config.ConfigManager;
import com.real.features.EntityHighlighter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EntityHighlighterConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget scanRadiusField;
    private TextFieldWidget refreshIntervalField;
    private ButtonWidget toggleEnabledButton;
    private ButtonWidget toggleLabelsButton;
    private ButtonWidget manageEntityTypesButton;
    private ButtonWidget keyBindingButton;
    
    public EntityHighlighterConfigScreen(Screen parent) {
        super(Text.literal("实体高亮器配置"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = 50;
        
        ConfigManager.EntityHighlighterConfig config = ConfigManager.getConfig().entityHighlighter;
        
        // 功能开关
        toggleEnabledButton = ButtonWidget.builder(
            Text.literal("功能状态: " + (config.enabled ? "开启" : "关闭")),
            button -> {
                config.enabled = !config.enabled;
                EntityHighlighter.setEnabled(config.enabled);
                button.setMessage(Text.literal("功能状态: " + (config.enabled ? "开启" : "关闭")));
            }
        ).dimensions(centerX - 100, startY, 200, 20).build();
        this.addDrawableChild(toggleEnabledButton);
        
        // 标签显示开关
        toggleLabelsButton = ButtonWidget.builder(
            Text.literal("显示标签: " + (config.showLabels ? "开启" : "关闭")),
            button -> {
                config.showLabels = !config.showLabels;
                button.setMessage(Text.literal("显示标签: " + (config.showLabels ? "开启" : "关闭")));
            }
        ).dimensions(centerX - 100, startY + 25, 200, 20).build();
        this.addDrawableChild(toggleLabelsButton);
        
        // 扫描半径设置
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("扫描半径"),
            button -> {}
        ).dimensions(centerX - 100, startY + 55, 80, 20).build());
        
        scanRadiusField = new TextFieldWidget(this.textRenderer, centerX - 10, startY + 55, 110, 20, Text.literal("扫描半径"));
        scanRadiusField.setText(String.valueOf(config.scanRadius));
        scanRadiusField.setChangedListener(text -> {
            try {
                int radius = Integer.parseInt(text);
                if (radius >= 1 && radius <= 100) {
                    config.scanRadius = radius;
                }
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(scanRadiusField);
        
        // 刷新间隔设置
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("刷新间隔"),
            button -> {}
        ).dimensions(centerX - 100, startY + 80, 80, 20).build());
        
        refreshIntervalField = new TextFieldWidget(this.textRenderer, centerX - 10, startY + 80, 110, 20, Text.literal("刷新间隔"));
        refreshIntervalField.setText(String.valueOf(config.refreshInterval));
        refreshIntervalField.setChangedListener(text -> {
            try {
                int interval = Integer.parseInt(text);
                if (interval >= 1 && interval <= 1200) {
                    config.refreshInterval = interval;
                }
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(refreshIntervalField);
        
        // 实体类型管理
        manageEntityTypesButton = ButtonWidget.builder(
            Text.literal("管理实体类型"),
            button -> this.client.setScreen(new EntityTypeManagerScreen(this))
        ).dimensions(centerX - 100, startY + 120, 200, 20).build();
        this.addDrawableChild(manageEntityTypesButton);
        
        // 键绑定配置
        keyBindingButton = ButtonWidget.builder(
            Text.literal("键绑定配置"),
            button -> this.client.setScreen(new KeyBindingConfigScreen(this))
        ).dimensions(centerX - 100, startY + 145, 200, 20).build();
        this.addDrawableChild(keyBindingButton);
        
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
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // 绘制说明文本
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal("配置实体高亮器的设置"), this.width / 2, 35, 0xCCCCCC);
        
        // 绘制设置说明
     /*   context.drawTextWithShadow(this.textRenderer,
            Text.literal("扫描半径: 1-100格"), this.width / 2 - 100, 58, 0xAAAAA);
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal("刷新间隔: 1-1200 tick"), this.width / 2 - 100, 88, 0xAAAAA);*/
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
    
    @Override
    public void close() {
        this.client.setScreen(parent);
    }
} 