package com.real.features;

import com.real.util.ChatHudHelper;
import com.real.util.TextExtractor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.util.List;

public class ChatCopy {
    private static boolean enabled = true; // 默认启用
    private static final MinecraftClient client = MinecraftClient.getInstance();
    
    public static void initialize() {

    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static void setEnabled(boolean enabled) {
        ChatCopy.enabled = enabled;

    }
    
    public static void toggle() {
        setEnabled(!enabled);
    }
    
    /**
     * 处理聊天界面的鼠标点击事件
     * @param screen 聊天界面实例
     * @param mouseX 鼠标X坐标
     * @param mouseY 鼠标Y坐标
     * @param button 鼠标按钮
     * @return 是否处理了点击事件
     */
    public static boolean handleChatClick(ChatScreen screen, double mouseX, double mouseY, int button) {
        if (!enabled) {
           // System.out.println("[ChatCopy] 功能未启用，跳过处理");
            return false;
        }
        
        // 只处理左键点击
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
           // System.out.println("[ChatCopy] 非左键点击，跳过处理");
            return false;
        }
        
        // 检查是否按下了Ctrl键
        long window = client.getWindow().getHandle();
        boolean ctrlPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS ||
                             GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
        
        System.out.println("[ChatCopy] 鼠标点击检测 - X: " + mouseX + ", Y: " + mouseY + ", Ctrl: " + ctrlPressed);
        
        if (!ctrlPressed) {
           // System.out.println("[ChatCopy] 未按下Ctrl键，跳过处理");
            return false;
        }
        
        // 获取聊天消息
        String chatMessage = getChatMessageAtPosition(mouseX, mouseY);
        if (chatMessage != null && !chatMessage.isEmpty()) {
           // System.out.println("[ChatCopy] 找到聊天消息: " + chatMessage);
            copyToClipboard(chatMessage);
            showCopyFeedback();
            return true;
        } else {
            System.out.println("[ChatCopy] 未找到聊天消息");
        }
        
        return false;
    }
    
    /**
     * 获取指定位置的聊天消息
     */
    private static String getChatMessageAtPosition(double mouseX, double mouseY) {
        if (client.inGameHud == null) {

            return null;
        }
        
        ChatHud chatHud = client.inGameHud.getChatHud();
        if (chatHud == null) {

            return null;
        }
        
        try {
            // 获取聊天界面的可见行数
            List<ChatHudLine.Visible> visibleLines = ChatHudHelper.getVisibleMessages(chatHud);



            
            if (visibleLines.isEmpty()) {

                return null;
            }
            
            // 获取窗口尺寸
            int windowHeight = client.getWindow().getScaledHeight();
            int windowWidth = client.getWindow().getScaledWidth();
            

            
            // 聊天区域配置
            int lineHeight = 9; // 每行高度
            int chatMarginBottom = 40; // 聊天区域底部边距
            int chatHeight = chatHud.getHeight(); // 聊天区域高度

            
            // 计算聊天区域的Y坐标范围
            int chatBottom = windowHeight - chatMarginBottom;
            int chatTop = chatBottom - chatHeight;
            

            
            // 检查鼠标是否在聊天区域内
            if (mouseY < chatTop || mouseY > chatBottom) {

                return null;
            }
            
            // 计算点击的行索引（从底部开始计算）
            int clickedLine = (int) ((chatBottom - mouseY) / lineHeight);

            
            if (clickedLine >= 0 && clickedLine < visibleLines.size()) {
                ChatHudLine.Visible line = visibleLines.get(clickedLine);
                
                // 使用TextExtractor提取文本
                String message = TextExtractor.extractText(line);
                

                return message;
            } else {

                
                // 输出所有可见消息以供调试

                for (int i = 0; i < visibleLines.size(); i++) {
                    ChatHudLine.Visible line = visibleLines.get(i);
                    String debugMessage = TextExtractor.extractText(line);

                }
            }
            
        } catch (Exception e) {
            System.out.println("[ChatCopy] 获取聊天消息时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 将文本复制到剪贴板
     */
    private static void copyToClipboard(String text) {
        try {
            // 方法1：使用Minecraft客户端的剪贴板功能
            if (client != null) {
                client.keyboard.setClipboard(text);

                return;
            }
        } catch (Exception e) {

        }
        
        try {
            // 方法2：尝试使用系统剪贴板
            if (!java.awt.GraphicsEnvironment.isHeadless()) {
                StringSelection selection = new StringSelection(text);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);

                return;
            } else {
                //System.out.println("[ChatCopy] 检测到无头环境，跳过系统剪贴板");
            }
        } catch (Exception e) {
            System.out.println("[ChatCopy] 使用系统剪贴板失败: " + e.getMessage());
        }
        
        // 方法3：如果都失败了，至少在控制台输出
        System.out.println("[ChatCopy] 复制到剪贴板失败，文本内容: " + text);
    }
    
    /**
     * 显示复制成功的反馈
     */
    private static void showCopyFeedback() {
        if (client.player != null) {

        }
    }
} 