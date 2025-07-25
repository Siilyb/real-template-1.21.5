package com.real.features;



import com.real.mixin.HandledScreenMixin;
import com.real.util.ContainerScreenHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.mixin.screen.ScreenAccessor;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

public class BeaconSolver {
    private static Screen screen;

    private static MinecraftClient client=MinecraftClient.getInstance();
    private  double lastGlassPanelMovementEnchanted=100L;
    private  double lastGlassPanelMovement=100L;
    private  double glassPanelMovementIntervalEnchanted=0l;
    private  double glassPanelMovementInterval=0l;
    private  float pitchEnchanted=0;
    private  float pitch;
    private  double lastSound=100L;
    private  int stackCountEnchanted=0;
    private  int stackCount=0;
    private  int lastGlassPanelSlotEnchanted=999;
    private  int lastGlassPanelSlot=999;
    private  int[] targets=new int[]{2750,2250,1750,1250,750};
    private  boolean[] hasFound=new boolean[2];
    //Light = lightblue
    private  String[] colors=new String[]{"Brown","Green","Red","White","Orange","Magenta","Light Blue","Yellow","Lime","Pink","Cyan","Purple","Blue"};
    private  int speed[]=new int[2];
    private  boolean hasEnteredEnchanted=false;
    private  boolean hasEntered=false;
    private  boolean isReady=false;

    private ItemStack glassPanelEnchanted =null;
    private ItemStack glassPanel = null;
    //current
    private int glassPanelSlot=999;
    private int glassPanelSlotEnchanted=999;
    public static BeaconSolver INSTANCE=new BeaconSolver();


    public static void click(int id,int slot){
        client.interactionManager.clickSlot(id,slot,0, SlotActionType.PICKUP_ALL, client.player);
    }

    public static void initialize(){

    }

    public static BeaconSolver createInstance(){
        return INSTANCE = new BeaconSolver();
    }
    public void beaconSolver(ScreenHandler screenHandler){

        /*全清空
         * 1.先关闭两个需要控制的玻璃板
         * 2.获取需要校准的两个玻璃板，记录名称，+是否附魔。当其中含有两个玻璃板的时候再进入判断逻辑
         *
         * 3.获取两个声音，应该是不同的，
         *
        */
        new Thread(()->{
            try {
                hasEnteredEnchanted=false;
                hasEntered=false;

                hasFound= new boolean[]{false, false};
                Thread.sleep(400);
                client.interactionManager.clickSlot(screenHandler.syncId,43,0, SlotActionType.PICKUP_ALL, client.player);
                Thread.sleep(400);
                client.interactionManager.clickSlot(screenHandler.syncId,52,0, SlotActionType.PICKUP_ALL, client.player);
                Thread.sleep(1000);
                /** 记得结束时候加上isready false*/
                isReady=true;
                //等待直到获得两个speed
                while(!hasFound[0] || !hasFound[1]){
                    Thread.sleep(100);
                }
                client.player.sendMessage(Text.literal("找到了间隔"),false);
                //处理附魔颜色
                while (MinecraftClient.getInstance().currentScreen.getNarratedTitle().getString().contains("Upgrade Signal")
                        &&findColor(glassPanelEnchanted.getItemName().getString())!=null
                        &&!client.player.currentScreenHandler.slots.get(46).getStack().getTooltip(Item.TooltipContext.DEFAULT, client.player,TooltipType.ADVANCED).get(4).getString().contains(findColor(glassPanelEnchanted.getItemName().getString()))){

                    client.interactionManager.clickSlot(screenHandler.syncId,46,0, SlotActionType.PICKUP_ALL, client.player);
                    client.player.sendMessage(Text.literal("附魔:点击一次，当前颜色为%s,所需为%s".formatted(client.player.currentScreenHandler.slots.get(46).getStack().getTooltip(Item.TooltipContext.DEFAULT, client.player,TooltipType.ADVANCED).get(4).getString(),glassPanelEnchanted.getItemName().getString())),false);
                    Thread.sleep(300);

                }
                client.player.sendMessage(Text.literal("附魔，当前颜色为%s,所需为%s".formatted(client.player.currentScreenHandler.slots.get(46).getStack().getTooltip(Item.TooltipContext.DEFAULT, client.player,TooltipType.ADVANCED).get(4).getString(),glassPanelEnchanted.getItemName().getString())),false);

                //处理非附魔颜色
                while (MinecraftClient.getInstance().currentScreen.getNarratedTitle().getString().contains("Upgrade Signal")
                        &&findColor(glassPanel.getItemName().getString())!=null
                        &&!client.player.currentScreenHandler.slots.get(37).getStack().getTooltip(Item.TooltipContext.DEFAULT, client.player,TooltipType.ADVANCED).get(4).getString().contains(findColor(glassPanel.getItemName().getString()))){

                    client.interactionManager.clickSlot(screenHandler.syncId,37,0, SlotActionType.PICKUP_ALL, client.player);
                    client.player.sendMessage(Text.literal("非附魔：点击一次，当前颜色为%s,所需为%s".formatted(client.player.currentScreenHandler.slots.get(37).getStack().getTooltip(Item.TooltipContext.DEFAULT, client.player,TooltipType.ADVANCED).get(4).getString(),glassPanel.getItemName().getString())),false);
                    Thread.sleep(300);

                }
                client.player.sendMessage(Text.literal("非附魔：当前颜色为%s,所需为%s".formatted(client.player.currentScreenHandler.slots.get(37).getStack().getTooltip(Item.TooltipContext.DEFAULT, client.player,TooltipType.ADVANCED).get(4).getString(),glassPanel.getItemName().getString())),false);

                //处理附魔速度
                while (MinecraftClient.getInstance().currentScreen.getNarratedTitle().getString().contains("Upgrade Signal")
                        &&!client.player.currentScreenHandler.slots.get(48).getStack().getTooltip(Item.TooltipContext.DEFAULT, client.player,TooltipType.ADVANCED).get(4).getString().contains(Integer.toString(speed[0]))){
                    client.interactionManager.clickSlot(screenHandler.syncId,48,0, SlotActionType.PICKUP_ALL, client.player);
                    client.player.sendMessage(Text.literal("附魔：点击一次，当前速度为%s,所需为%s".formatted(client.player.currentScreenHandler.slots.get(48).getStack().getTooltip(Item.TooltipContext.DEFAULT, client.player,TooltipType.ADVANCED).get(4).getString(),speed[0])),false);
                    Thread.sleep(300);
                }

                //处理普通速度
                while (MinecraftClient.getInstance().currentScreen.getNarratedTitle().getString().contains("Upgrade Signal")
                        &&!client.player.currentScreenHandler.slots.get(39).getStack().getTooltip(Item.TooltipContext.DEFAULT, client.player,TooltipType.ADVANCED).get(4).getString().contains(Integer.toString(speed[1]))){
                    client.interactionManager.clickSlot(screenHandler.syncId,39,0, SlotActionType.PICKUP_ALL, client.player);
                    client.player.sendMessage(Text.literal("非附魔：点击一次，当前速度为%s,所需为%s".formatted(client.player.currentScreenHandler.slots.get(39).getStack().getTooltip(Item.TooltipContext.DEFAULT, client.player,TooltipType.ADVANCED).get(4).getString(),speed[1])),false);
                    Thread.sleep(300);
                }


                //检查是不是有两个玻璃板 不是就让普通玻璃板往前走一点
                while(!screenCheck()){
                    client.player.sendMessage(Text.literal("没有两个玻璃板"),false);

                    client.interactionManager.clickSlot(screenHandler.syncId,43,0, SlotActionType.PICKUP_ALL, client.player);
                    Thread.sleep((long) (glassPanelMovementInterval+150));
                    client.interactionManager.clickSlot(screenHandler.syncId,43,0, SlotActionType.PICKUP_ALL, client.player);
                }
                //准备对齐普通玻璃板 从上对齐下
                if(client.player.currentScreenHandler.slots.get(glassPanelSlot+18).getStack().getItemName()==glassPanel.getItemName()){
                    client.interactionManager.clickSlot(screenHandler.syncId,43,0, SlotActionType.PICKUP_ALL, client.player);
                }
                while(client.player.currentScreenHandler.slots.get(glassPanelSlot+18).getStack().getItemName()!=glassPanel.getItemName()){
                    Thread.sleep(100);
                    client.player.sendMessage(Text.literal("普通没有对齐"),false);
                    if(client.player.currentScreenHandler.slots.get(glassPanelSlot+18).getStack().getItemName()==glassPanel.getItemName()){
                        client.interactionManager.clickSlot(screenHandler.syncId,43,0, SlotActionType.PICKUP_ALL, client.player);
                    }

                }
                client.player.sendMessage(Text.literal("普通已经对齐"),false);

                if(client.player.currentScreenHandler.slots.get(glassPanelSlotEnchanted+18).getStack().getItemName()==glassPanelEnchanted.getItemName()){
                    client.interactionManager.clickSlot(screenHandler.syncId,52,0, SlotActionType.PICKUP_ALL, client.player);
                }
                //准备对齐附魔玻璃板
                while(client.player.currentScreenHandler.slots.get(glassPanelSlotEnchanted+18).getStack().getItemName()!=glassPanelEnchanted.getItemName()){
                    Thread.sleep(100);
                    client.player.sendMessage(Text.literal("附魔没有对齐"),false);
                    if(client.player.currentScreenHandler.slots.get(glassPanelSlotEnchanted+18).getStack().getItemName()==glassPanelEnchanted.getItemName()){
                        client.interactionManager.clickSlot(screenHandler.syncId,52,0, SlotActionType.PICKUP_ALL, client.player);
                    }

                }

                client.player.sendMessage(Text.literal("附魔已经对齐"),false);
                //穷举声音 穷举两轮
//                for(int j=0;j<2;j++){
//                    Thread.sleep(300);
//                    for(int n=0;n<3;n++){
//
//                        click(screenHandler.syncId,50);
//                        Thread.sleep(300);
//                    }
//
//                    click(screenHandler.syncId,41);
//
//                }
                hasFound[0]=false;
                hasFound[1]=false;
                isReady=false;















            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


    }


    public void soundAnalyze(SoundInstance soundInstance){
        lastSound=System.currentTimeMillis();
        //client.player.sendMessage(Text.literal(soundInstance.getId().toString()+"pitch"+soundInstance.getPitch()+"volume"+soundInstance.getVolume()+""),false);
//        if(client.player!=null&&client!=null){
//            if(client.player.squaredDistanceTo(new Vec3d(soundInstance.getX(),soundInstance.getY(),soundInstance.getZ()))<=25){
//                    client.player.sendMessage(Text.literal("pitch"+soundInstance.getPitch()+"volume"+soundInstance.getVolume()+""),false);
//                if(Math.abs(System.currentTimeMillis()-lastGlassPanelMovement)<=100){
//                    client.player.sendMessage(Text.literal("间隔%s对应的是普通玻璃板".formatted(glassPanelMovementInterval)),false);
//                }
//                if(Math.abs(System.currentTimeMillis()-lastGlassPanelMovementEnchanted)<=100){
//                    client.player.sendMessage(Text.literal("间隔%s对应的是附魔玻璃板".formatted(glassPanelMovementIntervalEnchanted)),false);
//                }
//
//            }
//
//
//        }
    }

    public void beatCalculator(int slot, int revision, ItemStack stack){

        if(Boolean.TRUE.equals(stack.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))
                &&slot>=10&&slot<=16
                &&findColor(stack.getItemName().getString())!=null
                &&lastGlassPanelSlotEnchanted!=slot)
        {
            glassPanelSlotEnchanted=slot;}

        if(!Boolean.TRUE.equals(stack.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))
                &&slot>=10&&slot<=16
                &&findColor(stack.getItemName().getString())!=null
                &&lastGlassPanelSlot!=slot)
        {
            glassPanelSlot=slot;}
        if(isReady){
            if(slot>=10&&slot<=15&&Boolean.TRUE.equals(stack.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))
                    &&findColor(stack.getItemName().getString())!=null
                    &&lastGlassPanelSlotEnchanted!=slot){
                lastGlassPanelSlotEnchanted=slot;
                new Thread(()->{
                    if(!hasEnteredEnchanted){
                        client.player.sendMessage(Text.literal("在slot%s检测到附魔玻璃板，进入监听".formatted(slot)),false);
                        lastGlassPanelMovementEnchanted=System.currentTimeMillis();
                        hasEnteredEnchanted=true;
                        while(client.player.currentScreenHandler.slots.get(slot+1).getStack().getItemName()!=stack.getItemName()&&MinecraftClient.getInstance().currentScreen.getNarratedTitle().getString().contains("Upgrade Signal")){
                            try {
                                client.player.sendMessage(Text.literal(Integer.toString(slot+1)+"没找到"),false);

                                if(findColor(client.player.currentScreenHandler.slots.get(slot+1).getStack().getItemName().getString())!=null){
                                    client.player.sendMessage(Text.literal("找到了非附魔玻璃板，直接拒绝。"+findColor(client.player.currentScreenHandler.slots.get(slot+1).getStack().getItemName().getString())),false);
                                    hasEnteredEnchanted=false;
                                    return;
                                }

                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        glassPanelMovementIntervalEnchanted=System.currentTimeMillis()-lastGlassPanelMovementEnchanted;
                        if(findClosestSpeed(glassPanelMovementIntervalEnchanted)>0){
                            client.player.sendMessage(Text.literal("间隔%s对应的是附魔玻璃板,对应速度%sSpeed".formatted(glassPanelMovementIntervalEnchanted,findClosestSpeed(glassPanelMovementIntervalEnchanted))),false);
                            hasFound[0]=true;
                            speed[0]=findClosestSpeed(glassPanelMovementIntervalEnchanted);
                            glassPanelEnchanted=stack;
                        }
                        else {
                            hasFound[0]=false;
                            hasEnteredEnchanted=false;
                        }

                    }




                }).start();
            }
//普通玻璃板的逻辑
            if(slot>=10&&slot<=15&&!Boolean.TRUE.equals(stack.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))
                    &&findColor(stack.getItemName().getString())!=null
                    &&lastGlassPanelSlot!=slot){
                lastGlassPanelSlot=slot;
                new Thread(()->{
                    if(!hasEntered){
                        client.player.sendMessage(Text.literal("在slot%s检测到普通玻璃板，进入监听".formatted(slot)),false);
                        lastGlassPanelMovement=System.currentTimeMillis();
                        hasEntered=true;
                        while(client.player.currentScreenHandler.slots.get(slot+1).getStack().getItemName()!=stack.getItemName()){
                            try {
                                client.player.sendMessage(Text.literal(Integer.toString(slot+1)+"没找到"),false);

                                if(findColor(client.player.currentScreenHandler.slots.get(slot+1).getStack().getItemName().getString())!=null){
                                    client.player.sendMessage(Text.literal("找到了附魔玻璃板，直接拒绝。"+findColor(client.player.currentScreenHandler.slots.get(slot+1).getStack().getItemName().getString())),false);
                                    hasEntered=false;
                                    return;
                                }

                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        glassPanelMovementInterval=System.currentTimeMillis()-lastGlassPanelMovement;
                        if(findClosestSpeed(glassPanelMovementInterval)>0){
                            client.player.sendMessage(Text.literal("间隔%s对应的是普通玻璃板,对应速度%sSpeed".formatted(glassPanelMovementInterval,findClosestSpeed(glassPanelMovementInterval))),false);
                            hasFound[1]=true;
                            speed[1]=findClosestSpeed(glassPanelMovementInterval);
                            glassPanel=stack;
                        }else {
                            hasEntered=false;
                            hasFound[1]=false;
                        }

                    }




                }).start();
            }
        }


//        //如果两个有一个没找到就进入判断
//        if(!hasFound[0]||!hasFound[1]){
//            if(findColor(stack.getItemName().getString())!=null){
//
//                if(screenCheck()){
//                    //client.player.sendMessage(Text.literal("一层判断"),false);
//                    if(slot>=10&&slot<=16
//                            &&lastGlassPanelSlotEnchanted!=slot
//                            &&Boolean.TRUE.equals(stack.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))){
//                        stackCountEnchanted++;
//
//                        client.player.sendMessage(Text.literal("enchant++"),false);
//                    }
//                    else if(slot>=10&&slot<=16 &&lastGlassPanelSlot!=slot) {
//                        client.player.sendMessage(Text.literal("UNenchant++"),false);
//                        stackCount++;
//                    }
//
//                    switch (stackCountEnchanted){
//                        case 1: {
//                            // client.player.sendMessage(Text.literal("计时一次"),false);
//                            if(!hasEnteredEnchanted){
//                                glassPanelEnchanted = stack;
//                                lastGlassPanelSlotEnchanted=slot;
//                                lastGlassPanelMovementEnchanted = System.currentTimeMillis();
//                                hasEnteredEnchanted=true;
//                            }
//
////                            client.player.sendMessage(Text.literal("附魔玻璃板移动一次"),false);
//
//                            break;
//                        }
//                        case 2:{
//                            hasEnteredEnchanted=false;
//                            if(System.currentTimeMillis()-lastGlassPanelMovementEnchanted>=500){
//                                glassPanelMovementIntervalEnchanted=System.currentTimeMillis()-lastGlassPanelMovementEnchanted;
////                                client.player.sendMessage(Text.literal("附魔玻璃板移动二次"),false);
//
//                                if(findClosestSpeed(glassPanelMovementIntervalEnchanted)>0){
//                                    client.player.sendMessage(Text.literal("间隔%s对应的是附魔玻璃板,对应速度%sSpeed".formatted(glassPanelMovementIntervalEnchanted,findClosestSpeed(glassPanelMovementIntervalEnchanted))),false);
//                                    hasFound[0]=true;
//                                    speed[0]=findClosestSpeed(glassPanelMovementIntervalEnchanted);
//
//                                }
//
//                            }
//                            //else  client.player.sendMessage(Text.literal("报错4444"),false);
//                        }
//                        default: {
//                            client.player.sendMessage(Text.literal("EN清零+当前"+stackCountEnchanted),false);
//                            stackCountEnchanted = 0;
//                        }
//
//                    }
//                    switch (stackCount){
//                        case 1: {
//                            // client.player.sendMessage(Text.literal("计时一次"),false);
//                            if(!hasEntered){
//                                glassPanel = stack;
//                                lastGlassPanelSlot=slot;
//                                lastGlassPanelMovement = System.currentTimeMillis();
//                                break;
//                            }
//
//                        }
//                        case 2:{
//                            hasEntered=false;
//                            if(System.currentTimeMillis()-lastGlassPanelMovement>=500){
//                                glassPanelMovementInterval=System.currentTimeMillis()-lastGlassPanelMovement;
//
//                                if(findClosestSpeed(glassPanelMovementInterval)>0){
//                                    client.player.sendMessage(Text.literal("间隔%s对应的是普通玻璃板,对应速度%sSpeed".formatted(glassPanelMovementInterval,findClosestSpeed(glassPanelMovementInterval))),false);
//                                    hasFound[1]=true;
//                                    speed[1]=findClosestSpeed(glassPanelMovementInterval);
//
//                                    break;
//                                }
//
//                            }
//
//                        }
//
//                        default: {
//                            client.player.sendMessage(Text.literal("UN清零+当前"+stackCount),false);
//                            stackCount = 0;
//                        }
//                    }
//
//
//
//
//
//
//
//
//                }else{
////                    client.player.sendMessage(Text.literal(stack.getItemName().getString()),false);
////                    client.player.sendMessage(Text.literal("清零111"),false);
//                    client.player.sendMessage(Text.literal("因为没有两个玻璃板而清零"),false);
//                    stackCount=0;
//                    stackCountEnchanted=0;//没有两个玻璃板要清零
//                }
//
//
//
//            }
//
//        }







//
//        if(Boolean.TRUE.equals(stack.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))
//                &&slot>=10&&slot<=16
//                &&!stack.getItemName().getString().contains("Black Stained Glass Pane")
//                ) {
//            glassPanelEnchanted=stack;
//            glassPanelMovementIntervalEnchanted=System.currentTimeMillis()-lastGlassPanelMovementEnchanted;
//            lastGlassPanelMovementEnchanted=System.currentTimeMillis();
//         //   if(Math.abs(glassPanelMovementIntervalEnchanted-750)<=)
//
//
//        } else if(slot>=10&&slot<=16
//                &&!stack.getItemName().getString().contains("Black Stained Glass Pane")) {
//            glassPanel=stack;
//            glassPanelMovementInterval=System.currentTimeMillis()-lastGlassPanelMovement;
//            client.player.sendMessage(Text.literal(glassPanelMovementInterval+"normal"),false);
//            lastGlassPanelMovement=System.currentTimeMillis();
//        }

//
//           if(Boolean.TRUE.equals(stack.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))) {
//               client.player.sendMessage(Text.literal("slot "+slot+"stackname"+stack.getItemName().getString()+"ENCHANTMENT_GLINT"), false);
//
//           }
//           else{
//               client.player.sendMessage(Text.literal("slot "+slot+"stackname"+stack.getItemName().getString()), false);
//
//           }



    }

    //检测这两栏是不是有两个玻璃板
    public static boolean screenCheck(){
        int count=0;
        for(int i=10;i<=16;i++){
            if(!client.player.currentScreenHandler.slots.get(i).getStack().getItemName().getString().contains("Gray Stained Glass Pane")){
                count++;
                //client.player.sendMessage(Text.literal("第%s个   Name:%s".formatted(count,client.player.currentScreenHandler.slots.get(i).getStack().getItemName().getString())),false);
            }
        }
        if(count==1){
            //client.player.sendMessage(Text.literal("此时有两个玻璃板"),false);
            return false;
        }
        else {

            return true;
        }
    }

    private int findClosestSpeed(double Interval) {

        int closestSpeed=999;
        double minDiff=Integer.MAX_VALUE;
        double currentDiff;
        for (int i = 0; i < targets.length; i++) {
            currentDiff = Math.abs(Interval - targets[i]);
            if (currentDiff < minDiff) {
                minDiff = currentDiff;
                closestSpeed = i;
            }
        }
        if(minDiff>=50){
            return -1;
        }

        return closestSpeed+1;
    }
    private String findColor(String color){
        for (int i = 0; i < colors.length; i++) {
            if(color.contains(colors[i])){
                return colors[i];
            }
        }
//        client.player.sendMessage(Text.literal(color),false);
//        client.player.sendMessage(Text.literal("没有找到颜色"),false);
        return null;
    }






}
