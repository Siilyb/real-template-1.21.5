package com.real.mixin;

import com.real.features.ChatCopy;
import com.real.util.ChatHudHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
       // System.out.println("[ChatHudMixin] 检测到ChatHud鼠标点击事件 - X: " + mouseX + ", Y: " + mouseY);
        
        if (ChatCopy.isEnabled()) {
            // 这里可以添加更精确的聊天消息点击检测逻辑
            List<ChatHudLine.Visible> visibleMessages = ChatHudHelper.getVisibleMessages((ChatHud) (Object) this);
           // System.out.println("[ChatHudMixin] ChatCopy功能已启用，可见聊天消息数量: " + visibleMessages.size());
            
            // 输出调试信息
            for (int i = 0; i < Math.min(visibleMessages.size(), 3); i++) {
                ChatHudLine.Visible line = visibleMessages.get(i);
                //System.out.println("[ChatHudMixin] 聊天消息 " + i + ": " + line.content().toString());
            }
        }
    }
    
    @Inject(method = "getTextStyleAt", at = @At("HEAD"))
    private void onGetTextStyleAt(double x, double y, CallbackInfoReturnable<Style> cir) {
       // System.out.println("[ChatHudMixin] 获取文本样式 - X: " + x + ", Y: " + y);
    }
} 