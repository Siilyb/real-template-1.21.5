package com.real.util;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TextExtractor {
    
    /**
     * 从ChatHudLine.Visible中提取文本内容
     */
    public static String extractText(ChatHudLine.Visible line) {
        System.out.println("[TextExtractor] 开始提取文本...");
        
        // 方法1：尝试直接获取content并转换
        try {
            OrderedText content = line.content();
            if (content != null) {
                System.out.println("[TextExtractor] 方法1 - content类型: " + content.getClass().getName());
                
                // 尝试使用OrderedText的visit方法
                StringBuilder textBuilder = new StringBuilder();
                content.accept((index, style, codePoint) -> {
                    textBuilder.append(Character.toString(codePoint));
                    return true;
                });
                
                String result = textBuilder.toString();
                if (result != null && !result.isEmpty() && !result.contains("Lambda")) {
                    System.out.println("[TextExtractor] 方法1成功: " + result);
                    return result;
                }
            }
        } catch (Exception e) {
            System.out.println("[TextExtractor] 方法1失败: " + e.getMessage());
        }
        
        // 方法2：通过反射获取ChatHudLine的字段
        try {
            System.out.println("[TextExtractor] 方法2 - 尝试反射获取字段...");
            Field[] fields = line.getClass().getDeclaredFields();
            
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(line);
                System.out.println("[TextExtractor] 字段 " + field.getName() + ": " + 
                    (value != null ? value.getClass().getSimpleName() : "null"));
                
                if (value instanceof Text) {
                    String result = ((Text) value).getString();
                    System.out.println("[TextExtractor] 方法2成功从字段 " + field.getName() + ": " + result);
                    return result;
                }
            }
        } catch (Exception e) {
            System.out.println("[TextExtractor] 方法2失败: " + e.getMessage());
        }
        
        // 方法3：尝试获取ChatHudLine的父类字段
        try {
            System.out.println("[TextExtractor] 方法3 - 尝试获取父类字段...");
            Class<?> parentClass = line.getClass().getSuperclass();
            if (parentClass != null) {
                Field[] parentFields = parentClass.getDeclaredFields();
                for (Field field : parentFields) {
                    field.setAccessible(true);
                    Object value = field.get(line);
                    System.out.println("[TextExtractor] 父类字段 " + field.getName() + ": " + 
                        (value != null ? value.getClass().getSimpleName() : "null"));
                    
                    if (value instanceof Text) {
                        String result = ((Text) value).getString();
                        System.out.println("[TextExtractor] 方法3成功从父类字段 " + field.getName() + ": " + result);
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[TextExtractor] 方法3失败: " + e.getMessage());
        }
        
        // 方法4：尝试调用可能的方法
        try {
            System.out.println("[TextExtractor] 方法4 - 尝试调用方法...");
            Method[] methods = line.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getParameterCount() == 0 && method.getReturnType() == Text.class) {
                    method.setAccessible(true);
                    Text result = (Text) method.invoke(line);
                    if (result != null) {
                        String text = result.getString();
                        System.out.println("[TextExtractor] 方法4成功从方法 " + method.getName() + ": " + text);
                        return text;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[TextExtractor] 方法4失败: " + e.getMessage());
        }
        
        System.out.println("[TextExtractor] 所有方法都失败了");
        return "无法提取文本内容";
    }
} 