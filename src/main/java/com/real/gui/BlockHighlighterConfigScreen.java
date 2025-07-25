package com.real.gui;

import com.real.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BlockHighlighterConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget scanRadiusField;
    private TextFieldWidget refreshIntervalField;
    private boolean enabled;
    
    public BlockHighlighterConfigScreen(Screen parent) {
        super(Text.literal("方块高亮器配置"));
        this.parent = parent;
        this.enabled = ConfigManager.getConfig().blockHighlighter.enabled;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = 60;
        int spacing = 25;
        int currentY = startY;
        
        // 功能开关
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("功能状态: " + (enabled ? "开启" : "关闭"))
                .formatted(enabled ? Formatting.GREEN : Formatting.RED),
            button -> {
                enabled = !enabled;
                ConfigManager.getConfig().blockHighlighter.enabled = enabled;
                button.setMessage(Text.literal("功能状态: " + (enabled ? "开启" : "关闭"))
                    .formatted(enabled ? Formatting.GREEN : Formatting.RED));
            }
        ).dimensions(centerX - 100, currentY, 200, 20).build());
        
        currentY += spacing;
        
        // 扫描半径设置
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("扫描半径设置"),
            button -> {}
        ).dimensions(centerX - 150, currentY, 100, 20).build());
        
        scanRadiusField = new TextFieldWidget(this.textRenderer, centerX - 40, currentY, 60, 20, Text.literal(""));
        scanRadiusField.setText(String.valueOf(ConfigManager.getConfig().blockHighlighter.scanRadius));
        scanRadiusField.setMaxLength(3);
        this.addDrawableChild(scanRadiusField);
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("应用"),
            button -> {
                try {
                    int radius = Integer.parseInt(scanRadiusField.getText());
                    if (radius > 0 && radius <= 100) {
                        ConfigManager.getConfig().blockHighlighter.scanRadius = radius;
                        if (this.client.player != null) {
                            this.client.player.sendMessage(Text.literal("扫描半径设置为: " + radius).formatted(Formatting.GREEN), false);
                        }
                    } else {
                        if (this.client.player != null) {
                            this.client.player.sendMessage(Text.literal("扫描半径必须在1-100之间").formatted(Formatting.RED), false);
                        }
                    }
                } catch (NumberFormatException e) {
                    if (this.client.player != null) {
                        this.client.player.sendMessage(Text.literal("请输入有效数字").formatted(Formatting.RED), false);
                    }
                }
            }
        ).dimensions(centerX + 30, currentY, 50, 20).build());
        
        currentY += spacing;
        
        // 刷新间隔设置
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("刷新间隔(tick)"),
            button -> {}
        ).dimensions(centerX - 150, currentY, 100, 20).build());
        
        refreshIntervalField = new TextFieldWidget(this.textRenderer, centerX - 40, currentY, 60, 20, Text.literal(""));
        refreshIntervalField.setText(String.valueOf(ConfigManager.getConfig().blockHighlighter.refreshInterval));
        refreshIntervalField.setMaxLength(4);
        this.addDrawableChild(refreshIntervalField);
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("应用"),
            button -> {
                try {
                    int interval = Integer.parseInt(refreshIntervalField.getText());
                    if (interval > 0 && interval <= 1200) {
                        ConfigManager.getConfig().blockHighlighter.refreshInterval = interval;
                        if (this.client.player != null) {
                            this.client.player.sendMessage(Text.literal("刷新间隔设置为: " + interval + " tick").formatted(Formatting.GREEN), false);
                        }
                    } else {
                        if (this.client.player != null) {
                            this.client.player.sendMessage(Text.literal("刷新间隔必须在1-1200之间").formatted(Formatting.RED), false);
                        }
                    }
                } catch (NumberFormatException e) {
                    if (this.client.player != null) {
                        this.client.player.sendMessage(Text.literal("请输入有效数字").formatted(Formatting.RED), false);
                    }
                }
            }
        ).dimensions(centerX + 30, currentY, 50, 20).build());
        
        currentY += spacing + 10;
        
        // 关键词管理
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("管理关键词"),
            button -> {
                this.client.setScreen(new KeywordManagerScreen(this));
            }
        ).dimensions(centerX - 100, currentY, 200, 20).build());
        
        currentY += spacing;
        
        // 键绑定配置
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("键绑定配置"),
            button -> {
                this.client.setScreen(new KeyBindingConfigScreen(this));
            }
        ).dimensions(centerX - 100, currentY, 200, 20).build());
        
        // 底部按钮
        this.addDrawableChild(ButtonWidget.builder(Text.literal("返回"), button -> {
            this.client.setScreen(parent);
        }).dimensions(centerX - 50, this.height - 40, 100, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("保存"), button -> {
            ConfigManager.saveConfig();
            if (this.client.player != null) {
                this.client.player.sendMessage(Text.literal("配置已保存").formatted(Formatting.GREEN), false);
            }
        }).dimensions(centerX - 160, this.height - 40, 100, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // 绘制标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // 绘制说明文本
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal("配置方块高亮器的各项设置"), this.width / 2, 35, 0xCCCCCC);
        
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