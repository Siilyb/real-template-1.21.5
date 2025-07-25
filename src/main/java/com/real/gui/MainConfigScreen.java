package com.real.gui;

import com.real.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class MainConfigScreen extends Screen {
    private final Screen parent;
    private final List<FeatureButton> featureButtons = new ArrayList<>();
    
    public MainConfigScreen(Screen parent) {
        super(Text.literal("Real Mod 配置界面"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 标题
        int centerX = this.width / 2;
        int startY = 50;
        
        // 添加功能按钮
        featureButtons.clear();
        
        // 方块高亮功能
        FeatureButton blockHighlighterButton = new FeatureButton(
            centerX - 100, startY + 40, 200, 20,
            Text.literal("方块高亮器"),
            ConfigManager.getConfig().blockHighlighter.enabled,
            () -> {
                this.client.setScreen(new BlockHighlighterConfigScreen(this));
            }
        );

        featureButtons.add(blockHighlighterButton);
        this.addDrawableChild(blockHighlighterButton);
        
        // 实体高亮功能
        FeatureButton entityHighlighterButton = new FeatureButton(
            centerX - 100, startY + 70, 200, 20,
            Text.literal("实体高亮器"),
            ConfigManager.getConfig().entityHighlighter.enabled,
            () -> {
                this.client.setScreen(new EntityHighlighterConfigScreen(this));
            }
        );

        featureButtons.add(entityHighlighterButton);
        this.addDrawableChild(entityHighlighterButton);
        
        // AutoPhanflare功能
        FeatureButton autoPhanflareButton = new FeatureButton(
            centerX - 100, startY + 100, 200, 20,
            Text.literal("AutoPhanflare"),
            com.real.features.AutoPhanflare.isEnabled(),
            () -> {
                com.real.features.AutoPhanflare.toggle();
            }
        );

        featureButtons.add(autoPhanflareButton);
        this.addDrawableChild(autoPhanflareButton);
        
        // 箱子物品记录器
        FeatureButton chestItemLoggerButton = new FeatureButton(
            centerX - 100, startY + 130, 200, 20,
            Text.literal("箱子物品记录器"),
            com.real.features.ChestItemLogger.isEnabled(),
            () -> {
                com.real.features.ChestItemLogger.toggle();
            }
        );

        featureButtons.add(chestItemLoggerButton);
        this.addDrawableChild(chestItemLoggerButton);
        
        // 返回按钮
        this.addDrawableChild(ButtonWidget.builder(Text.literal("返回"), button -> {
            this.client.setScreen(parent);
        }).dimensions(centerX - 50, this.height - 40, 100, 20).build());
        
        // 保存配置按钮
        this.addDrawableChild(ButtonWidget.builder(Text.literal("保存配置"), button -> {
            ConfigManager.saveConfig();
            if (this.client.player != null) {
                this.client.player.sendMessage(Text.literal("配置已保存").formatted(Formatting.GREEN), false);
            }
        }).dimensions(centerX - 160, this.height - 40, 100, 20).build());
        
        // 重新加载配置按钮
        this.addDrawableChild(ButtonWidget.builder(Text.literal("重新加载"), button -> {
            ConfigManager.loadConfig();
            this.init();
            if (this.client.player != null) {
                this.client.player.sendMessage(Text.literal("配置已重新加载").formatted(Formatting.YELLOW), false);
            }
        }).dimensions(centerX + 60, this.height - 40, 100, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // 绘制标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // 绘制功能列表标题
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("功能列表"), this.width / 2, 30, 0xCCCCCC);
        
        super.render(context, mouseX, mouseY, delta);
        
        // 绘制功能状态
        updateFeatureStatus();
    }
    
    private void updateFeatureStatus() {
        for (FeatureButton button : featureButtons) {
            button.updateStatus();
        }
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
    
    @Override
    public void close() {
        this.client.setScreen(parent);
    }
    
    private static class FeatureButton extends ButtonWidget {
        private boolean enabled;
        private final Runnable onPress;
        
        public FeatureButton(int x, int y, int width, int height, Text message, boolean enabled, Runnable onPress) {
            super(x, y, width, height, message, button -> onPress.run(), DEFAULT_NARRATION_SUPPLIER);
            this.enabled = enabled;
            this.onPress = onPress;
        }
        
        public void updateStatus() {
            // 更新状态，这里可以实时获取功能状态
            if (this.getMessage().getString().contains("方块高亮器")) {
                this.enabled = ConfigManager.getConfig().blockHighlighter.enabled;
            } else if (this.getMessage().getString().contains("实体高亮器")) {
                this.enabled = ConfigManager.getConfig().entityHighlighter.enabled;
            } else if (this.getMessage().getString().contains("AutoPhanflare")) {
                this.enabled = com.real.features.AutoPhanflare.isEnabled();
            } else if (this.getMessage().getString().contains("箱子物品记录器")) {
                this.enabled = com.real.features.ChestItemLogger.isEnabled();
            }
        }
        
        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            super.renderWidget(context, mouseX, mouseY, delta);
            
            // 绘制状态指示器
            int statusColor = enabled ? 0x00FF00 : 0xFF0000;
            context.fill(this.getX() + this.width - 15, this.getY() + 3, this.getX() + this.width - 5, this.getY() + 13, statusColor);
            
            // 绘制状态文本
            String statusText = enabled ? "开启" : "关闭";
            context.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                statusText,
                this.getX() + this.width - 35,
                this.getY() + 6,
                statusColor
            );
        }
    }
} 