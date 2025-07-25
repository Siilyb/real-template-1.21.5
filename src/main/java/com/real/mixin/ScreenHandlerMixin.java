package com.real.mixin;

import com.real.features.BeaconSolver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 监听容器界面打开的Mixin
 */

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin{

    @Shadow @Final public DefaultedList<Slot> slots;

    /**
     * 当HandledScreen初始化时，检查是否为箱子界面并记录物品
     */
    @Inject(method ="setStackInSlot",at = @At("RETURN"))

    private void onsetStackInSlot(int slot, int revision, ItemStack stack, CallbackInfo ci) {

//        MinecraftClient.getInstance().player.sendMessage(Text.literal("stackindex%s,\t stackname%s \t itemname%s"
//                .formatted(
//                        slot,
//                        stack.getName().getString(),
//                        stack.getItemName().getString()
//                        )),false);

        if(MinecraftClient.getInstance().currentScreen!=null){
            if(MinecraftClient.getInstance().currentScreen.getNarratedTitle().getString().contains("Upgrade Signal")) {

                BeaconSolver.INSTANCE.beatCalculator(slot,revision,stack);




            }
        }

    }


} 