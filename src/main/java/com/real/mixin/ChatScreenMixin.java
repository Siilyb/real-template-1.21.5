package com.real.mixin;

import com.real.features.ChatCopy;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
       // System.out.println("[ChatScreenMixin] 检测到鼠标点击事件 - X: " + mouseX + ", Y: " + mouseY + ", Button: " + button);
        
        ChatScreen screen = (ChatScreen) (Object) this;
        
        // 尝试处理聊天复制功能
        boolean handled = ChatCopy.handleChatClick(screen, mouseX, mouseY, button);
        
        if (handled) {
           // System.out.println("[ChatScreenMixin] 聊天复制功能已处理点击事件");
            cir.setReturnValue(true);
            cir.cancel();
        } else {
           // System.out.println("[ChatScreenMixin] 聊天复制功能未处理点击事件，继续正常处理");
        }
    }
} 