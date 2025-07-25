package com.real.mixin;

import com.real.features.BeaconSolver;
import com.real.features.ChestItemLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



/**
 * 监听容器界面打开的Mixin
 */

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    @Shadow protected ScreenHandler handler;

    @Unique protected volatile int amount = 0;
    protected HandledScreenMixin(Text title) {
        super(title);
    }

    /**
     * 当HandledScreen初始化时，检查是否为箱子界面并记录物品
     */
    @Inject(method = "init()V",at = @At("RETURN"))

    private void onInit(CallbackInfo ci) {
        if(amount>0){
            return;
        }
        new Thread(()->{
            amount++;
            if (handler != null) {
                System.out.println("11111");
                try{
                    if(MinecraftClient.getInstance()!=null){
                        if(MinecraftClient.getInstance().currentScreen.getNarratedTitle().getString().contains("Upgrade Signal")){
                            BeaconSolver.createInstance().beaconSolver(handler);
                        }
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
        // 检查是否是箱子类型界面
    }



}