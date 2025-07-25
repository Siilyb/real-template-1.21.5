package com.real.gui;

import com.real.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityTypeManagerScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget newEntityTypeField;
    private TextFieldWidget newColorField;
    private final List<EntityTypeEntry> entityTypeEntries = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int ENTRIES_PER_PAGE = 8;
    
    public EntityTypeManagerScreen(Screen parent) {
        super(Text.literal("实体类型管理"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = 50;
        
        // 添加新实体类型区域
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("新实体类型:"),
            button -> {}
        ).dimensions(centerX - 200, startY, 90, 20).build());
        
        newEntityTypeField = new TextFieldWidget(this.textRenderer, centerX - 100, startY, 100, 20, Text.literal(""));
        newEntityTypeField.setMaxLength(50);
        newEntityTypeField.setPlaceholder(Text.literal("例如: zombie"));
        this.addDrawableChild(newEntityTypeField);
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("颜色:"),
            button -> {}
        ).dimensions(centerX + 10, startY, 40, 20).build());
        
        newColorField = new TextFieldWidget(this.textRenderer, centerX + 60, startY, 80, 20, Text.literal(""));
        newColorField.setMaxLength(7);
        newColorField.setPlaceholder(Text.literal("#FF0000"));
        newColorField.setText("#FF0000");
        this.addDrawableChild(newColorField);
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("添加"),
            button -> {
                addEntityType();
            }
        ).dimensions(centerX + 150, startY, 50, 20).build());
        
        // 滚动按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("↑"),
            button -> {
                if (scrollOffset > 0) {
                    scrollOffset--;
                    refreshEntityTypeEntries();
                }
            }
        ).dimensions(centerX + 180, startY + 40, 20, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("↓"),
            button -> {
                if (scrollOffset < Math.max(0, ConfigManager.getConfig().entityHighlighter.entityTypes.size() - ENTRIES_PER_PAGE)) {
                    scrollOffset++;
                    refreshEntityTypeEntries();
                }
            }
        ).dimensions(centerX + 180, startY + 70, 20, 20).build());
        
        // 底部按钮
        this.addDrawableChild(ButtonWidget.builder(Text.literal("返回"), button -> {
            this.client.setScreen(parent);
        }).dimensions(centerX - 50, this.height - 40, 100, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("保存"), button -> {
            ConfigManager.saveConfig();
            if (this.client.player != null) {
                this.client.player.sendMessage(Text.literal("实体类型配置已保存").formatted(Formatting.GREEN), false);
            }
        }).dimensions(centerX - 160, this.height - 40, 100, 20).build());
        
        refreshEntityTypeEntries();
    }
    
    private void addEntityType() {
        String entityType = newEntityTypeField.getText().trim().toLowerCase();
        String colorHex = newColorField.getText().trim();
        
        if (entityType.isEmpty()) {
            if (this.client.player != null) {
                this.client.player.sendMessage(Text.literal("实体类型不能为空").formatted(Formatting.RED), false);
            }
            return;
        }
        
        if (!colorHex.startsWith("#") || colorHex.length() != 7) {
            if (this.client.player != null) {
                this.client.player.sendMessage(Text.literal("颜色格式错误，请使用#RRGGBB格式").formatted(Formatting.RED), false);
            }
            return;
        }
        
        try {
            Color.decode(colorHex); // 验证颜色格式
            ConfigManager.getConfig().entityHighlighter.entityTypes.put(entityType, colorHex);
            newEntityTypeField.setText("");
            refreshEntityTypeEntries();
            if (this.client.player != null) {
                this.client.player.sendMessage(Text.literal("已添加实体类型: " + entityType).formatted(Formatting.GREEN), false);
            }
        } catch (NumberFormatException e) {
            if (this.client.player != null) {
                this.client.player.sendMessage(Text.literal("无效的颜色代码").formatted(Formatting.RED), false);
            }
        }
    }
    
    private void refreshEntityTypeEntries() {
        // 清除现有的实体类型条目
        for (EntityTypeEntry entry : entityTypeEntries) {
            this.remove(entry);
        }
        entityTypeEntries.clear();
        
        Map<String, String> entityTypes = ConfigManager.getConfig().entityHighlighter.entityTypes;
        List<String> entityTypeList = new ArrayList<>(entityTypes.keySet());
        
        int startY = 90;
        int currentY = startY;
        int index = 0;
        
        for (String entityType : entityTypeList) {
            if (index < scrollOffset) {
                index++;
                continue;
            }
            
            if (index >= scrollOffset + ENTRIES_PER_PAGE) {
                break;
            }
            
            EntityTypeEntry entry = new EntityTypeEntry(entityType, entityTypes.get(entityType), this.width / 2 - 180, currentY);
            entityTypeEntries.add(entry);
            this.addDrawableChild(entry);
            
            currentY += 25;
            index++;
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // 绘制标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // 绘制说明
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal("管理实体高亮的实体类型和颜色"), this.width / 2, 35, 0xCCCCCC);
        
        // 绘制实体类型列表标题
        context.drawTextWithShadow(this.textRenderer, "实体类型", this.width / 2 - 180, 75, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "颜色", this.width / 2 - 80, 75, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "预览", this.width / 2 - 20, 75, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "操作", this.width / 2 + 80, 75, 0xFFFFFF);
        
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
    
    private class EntityTypeEntry extends ButtonWidget {
        private final String entityType;
        private final String colorHex;
        private final TextFieldWidget colorField;
        private final ButtonWidget deleteButton;
        private final ButtonWidget updateButton;
        
        public EntityTypeEntry(String entityType, String colorHex, int x, int y) {
            super(x, y, 360, 20, Text.literal(entityType), button -> {}, DEFAULT_NARRATION_SUPPLIER);
            this.entityType = entityType;
            this.colorHex = colorHex;
            
            // 颜色编辑字段
            this.colorField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x + 100, y, 60, 20, Text.literal(""));
            this.colorField.setText(colorHex);
            this.colorField.setMaxLength(7);
            
            // 更新按钮
            this.updateButton = ButtonWidget.builder(Text.literal("更新"), button -> {
                updateEntityTypeColor();
            }).dimensions(x + 200, y, 40, 20).build();
            
            // 删除按钮
            this.deleteButton = ButtonWidget.builder(Text.literal("删除"), button -> {
                deleteEntityType();
            }).dimensions(x + 250, y, 40, 20).build();
        }
        
        private void updateEntityTypeColor() {
            String newColor = colorField.getText().trim();
            if (!newColor.startsWith("#") || newColor.length() != 7) {
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(
                        Text.literal("颜色格式错误").formatted(Formatting.RED), false);
                }
                return;
            }
            
            try {
                Color.decode(newColor);
                ConfigManager.getConfig().entityHighlighter.entityTypes.put(entityType, newColor);
                refreshEntityTypeEntries();
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(
                        Text.literal("已更新实体类型颜色: " + entityType).formatted(Formatting.GREEN), false);
                }
            } catch (NumberFormatException e) {
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(
                        Text.literal("无效的颜色代码").formatted(Formatting.RED), false);
                }
            }
        }
        
        private void deleteEntityType() {
            ConfigManager.getConfig().entityHighlighter.entityTypes.remove(entityType);
            refreshEntityTypeEntries();
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(
                    Text.literal("已删除实体类型: " + entityType).formatted(Formatting.YELLOW), false);
            }
        }
        
        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            // 绘制实体类型名称
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, 
                entityType, this.getX(), this.getY() + 5, 0xFFFFFF);
            
            // 绘制颜色预览
            try {
                Color color = Color.decode(colorField.getText());
                int colorValue = (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue() | 0xFF000000;
                context.fill(this.getX() + 170, this.getY() + 2, this.getX() + 190, this.getY() + 18, colorValue);
            } catch (NumberFormatException e) {
                // 如果颜色无效，显示红色
                context.fill(this.getX() + 170, this.getY() + 2, this.getX() + 190, this.getY() + 18, 0xFFFF0000);
            }
            
            // 渲染子组件
            colorField.render(context, mouseX, mouseY, delta);
            updateButton.render(context, mouseX, mouseY, delta);
            deleteButton.render(context, mouseX, mouseY, delta);
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (colorField.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (updateButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (deleteButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        
        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (colorField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        
        @Override
        public boolean charTyped(char chr, int modifiers) {
            if (colorField.charTyped(chr, modifiers)) {
                return true;
            }
            return super.charTyped(chr, modifiers);
        }
    }
} 