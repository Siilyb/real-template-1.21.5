package com.real.mixin;

import com.real.features.BeaconSolver;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SoundManager.class)
public class SoundManagerMixin {
   @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V",at=@At("RETURN"))
    public void play(SoundInstance soundInstance,CallbackInfo ci) {
       if(soundInstance.getId().toString().contains("bass")){
           BeaconSolver.INSTANCE.soundAnalyze(soundInstance);

       }
   }
}
