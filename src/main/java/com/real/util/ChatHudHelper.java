package com.real.util;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ChatHudHelper {
    private static Field visibleMessagesField;
    private static Field scrolledLinesField;
    private static boolean initialized = false;
    
    /**
     * 初始化反射字段
     */
    private static void initialize() {
        if (initialized) return;
        
        try {
            // 尝试不同的字段名
            String[] possibleFieldNames = {"visibleMessages", "field_2064", "f_93768_"};
            
            for (String fieldName : possibleFieldNames) {
                try {
                    visibleMessagesField = ChatHud.class.getDeclaredField(fieldName);
                    visibleMessagesField.setAccessible(true);
                    System.out.println("[ChatHudHelper] 找到visibleMessages字段: " + fieldName);
                    break;
                } catch (NoSuchFieldException e) {
                    System.out.println("[ChatHudHelper] 未找到字段: " + fieldName);
                }
            }
            
            // 如果还没找到，尝试遍历所有字段
            if (visibleMessagesField == null) {
                System.out.println("[ChatHudHelper] 尝试遍历所有字段...");
                java.lang.reflect.Field[] fields = ChatHud.class.getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    field.setAccessible(true);
                    System.out.println("[ChatHudHelper] 字段: " + field.getName() + ", 类型: " + field.getType().getName());
                    if (field.getType().getName().contains("List")) {
                        visibleMessagesField = field;
                        System.out.println("[ChatHudHelper] 选择字段: " + field.getName());
                        break;
                    }
                }
            }
            
            if (visibleMessagesField != null) {
                initialized = true;
                System.out.println("[ChatHudHelper] 反射字段初始化成功");
            } else {
                System.out.println("[ChatHudHelper] 反射字段初始化失败：未找到visibleMessages字段");
            }
        } catch (Exception e) {
            System.out.println("[ChatHudHelper] 反射字段初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取可见的聊天消息列表
     */
    @SuppressWarnings("unchecked")
    public static List<ChatHudLine.Visible> getVisibleMessages(ChatHud chatHud) {
        initialize();
        
        if (visibleMessagesField == null) {
            System.out.println("[ChatHudHelper] visibleMessagesField为null，返回空列表");
            return new ArrayList<>();
        }
        
        try {
            Object value = visibleMessagesField.get(chatHud);
            if (value instanceof List) {
                return (List<ChatHudLine.Visible>) value;
            }
        } catch (IllegalAccessException e) {
            System.out.println("[ChatHudHelper] 获取visibleMessages失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new ArrayList<>();
    }
    
    /**
     * 获取滚动行数
     */
    public static int getScrolledLines(ChatHud chatHud) {
        initialize();
        
        if (scrolledLinesField == null) {
            System.out.println("[ChatHudHelper] scrolledLinesField为null，返回0");
            return 0;
        }
        
        try {
            Object value = scrolledLinesField.get(chatHud);
            if (value instanceof Integer) {
                return (Integer) value;
            }
        } catch (IllegalAccessException e) {
            System.out.println("[ChatHudHelper] 获取scrolledLines失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
} 