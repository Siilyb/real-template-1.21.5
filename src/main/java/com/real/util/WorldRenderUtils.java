package com.real.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.awt.Color;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

public class WorldRenderUtils {
    
    private static void line(
            MatrixStack.Entry matrix, BufferBuilder buffer,
            Number x1, Number y1, Number z1,
            Number x2, Number y2, Number z2,
            float lineWidth
    ) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.cameraEntity == null) return;
        
        RenderSystem.lineWidth(lineWidth / (float)Math.pow(client.cameraEntity.getPos().squaredDistanceTo(
                new Vec3d(x1.doubleValue(), y1.doubleValue(), z1.doubleValue())
        ), 0.25));
        
        line(
            matrix,
            buffer,
            new Vector3f(x1.floatValue(), y1.floatValue(), z1.floatValue()),
            new Vector3f(x2.floatValue(), y2.floatValue(), z2.floatValue())
        );
    }

    private static void line(MatrixStack.Entry matrix, BufferBuilder buffer, Vector3f from, Vector3f to) {
        Vector3f normal = new Vector3f(to).sub(from).mul(-1F);
        buffer.vertex(matrix.getPositionMatrix(), from.x(), from.y(), from.z())
              .normal(matrix, normal.x(), normal.y(), normal.z())
              .color(0xFFFFFFFF)
              ;
        buffer.vertex(matrix.getPositionMatrix(), to.x(), to.y(), to.z())
              .normal(matrix, normal.x(), normal.y(), normal.z())
              .color(0xFFFFFFFF)
             ;
    }

    public static void drawWireFrame(
            WorldRenderContext context,
            Box box,
            Color color,
            float thickness,
            boolean depthTest
    ) {
        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;
        
        matrices.push();
        RenderLayer layer = depthTest ? DulkirRenderLayer.DULKIR_LINES : DulkirRenderLayer.DULKIR_LINES_ESP;
        RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        matrices.translate(-context.camera().getPos().x, -context.camera().getPos().y, -context.camera().getPos().z);
        BufferBuilder buf = RenderUtil.getBufferFor(layer);
        MatrixStack.Entry me = matrices.peek();

        buf.color(255, 255, 255, 255);

        // bottom
        line(me, buf, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, thickness);
        line(me, buf, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, thickness);
        line(me, buf, box.maxX, box.minY, box.maxZ, box.minX, box.minY, box.maxZ, thickness);
        line(me, buf, box.minX, box.minY, box.maxZ, box.minX, box.minY, box.minZ, thickness);

        line(me, buf, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, thickness);

        // top
        line(me, buf, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, thickness);
        line(me, buf, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, thickness);
        line(me, buf, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, thickness);
        line(me, buf, box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ, thickness);

        // some redraws (blame strips) and getting the rest of the vertical columns
        line(me, buf, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, thickness);
        line(me, buf, box.maxX, box.maxY, box.minZ, box.maxX, box.minY, box.minZ, thickness);
        line(me, buf, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, thickness);
        line(me, buf, box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, thickness);
        line(me, buf, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, thickness);
        line(me, buf, box.minX, box.maxY, box.maxZ, box.minX, box.minY, box.maxZ, thickness);

        layer.draw(buf.end());

        matrices.pop();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    public static void drawLine(WorldRenderContext context, Vec3d startPos, Vec3d endPos, Color color, float thickness, boolean depthTest) {
        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;
        
        matrices.push();
        RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        RenderLayer layer = depthTest ? DulkirRenderLayer.DULKIR_LINES : DulkirRenderLayer.DULKIR_LINES_ESP;
        
        matrices.translate(-context.camera().getPos().x, -context.camera().getPos().y, -context.camera().getPos().z);
        BufferBuilder buf = RenderUtil.getBufferFor(layer);
        MatrixStack.Entry me = matrices.peek();

        buf.color(255, 255, 255, 255);

        line(me, buf, startPos.x, startPos.y, startPos.z, endPos.x, endPos.y, endPos.z, thickness);

        layer.draw(buf.end());

        matrices.pop();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    public static void drawLineArray(WorldRenderContext context, List<Vec3d> posArr, Color color, float thickness, boolean depthTest) {
        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;
        
        matrices.push();
        RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        RenderLayer layer = depthTest ? DulkirRenderLayer.DULKIR_LINES : DulkirRenderLayer.DULKIR_LINES_ESP;
        
        matrices.translate(-context.camera().getPos().x, -context.camera().getPos().y, -context.camera().getPos().z);
        BufferBuilder buf = RenderUtil.getBufferFor(layer);
        MatrixStack.Entry me = matrices.peek();

        buf.color(255, 255, 255, 255);

        for (int i = 0; i < posArr.size() - 1; i++) {
            Vec3d startPos = posArr.get(i);
            Vec3d endPos = posArr.get(i + 1);
            line(me, buf, startPos.x, startPos.y, startPos.z, endPos.x, endPos.y, endPos.z, thickness);
        }

        layer.draw(buf.end());

        matrices.pop();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    /**
     * If you intend to show with distance, use a waypoint I think. If you're looking at this
     * statement and screaming at me for forgetting some use case, either let me know or compile
     * a method that accomplishes your goals based off of this example code. Neither of these
     * things should be incredibly difficult.
     */
    /**
     *

     * @author silk
     */
    public static void drawText(
            Text text,
            WorldRenderContext context,
            Vec3d pos,
            boolean depthTest,
            float scale
    ) {
        RenderLayer layer = DulkirRenderLayer.DULKIR_QUADS_ESP;

        // Minecraft vertex consumer because we still hook into their renderer and do immediate text rendering
        VertexConsumerProvider vertexConsumer = context.consumers();//context.worldRenderer().getBufferBuilders().getEntityVertexConsumers();




        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;
        
        matrices.push();
        matrices.translate(
            pos.x - context.camera().getPos().x,
            pos.y - context.camera().getPos().y,
            pos.z - context.camera().getPos().z
        );
        matrices.multiply(context.camera().getRotation());
        matrices.scale(.025f * scale, -.025f * scale, 1F);
        
        org.joml.Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int j = ((int) (.25 * 255.0f)) << 24;
        
        BufferBuilder buf = RenderUtil.getBufferFor(layer);
        buf.vertex(matrix4f, -1.0f - textRenderer.getWidth(text) / 2, -1.0f, 0.0f)
           .color(j)
           .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
           ;
        buf.vertex(matrix4f, -1.0f - textRenderer.getWidth(text) / 2, textRenderer.fontHeight, 0.0f)
           .color(j)
           .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
           ;
        buf.vertex(matrix4f, textRenderer.getWidth(text) / 2, textRenderer.fontHeight, 0.0f)
           .color(j)
           .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
           ;
        buf.vertex(matrix4f, textRenderer.getWidth(text) / 2, -1.0f, 0.0f)
           .color(j)
           .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
          ;

        matrices.translate(0F, 0F, 0.01F);
        layer.draw(buf.end());
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        
        textRenderer.draw(
            text, -textRenderer.getWidth(text) / 2, 0f, 0xFFFFFF, false, matrix4f, vertexConsumer,
            TextRenderer.TextLayerType.SEE_THROUGH,
            0, LightmapTextureManager.MAX_LIGHT_COORDINATE
        );

        vertexConsumer.getBuffer(layer);
        //vertexConsumer.draw();
        matrices.pop();
    }

    public static void renderWaypoint(
            Text text,
            WorldRenderContext context,
            Vec3d pos
    ) {
        RenderLayer layer = DulkirRenderLayer.DULKIR_QUADS_ESP;
        double d = pos.distanceTo(MinecraftClient.getInstance().player.getPos());
        Text distText = Text.literal(((int) d) + "m").setStyle(Style.EMPTY.withColor(Formatting.YELLOW));
        
        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;

        VertexConsumerProvider vertexConsumer = context.consumers();//context.worldRenderer().getBufferBuilders().getEntityVertexConsumers();
        matrices.push();
        
        double magnitude = sqrt(Math.pow(pos.x - context.camera().getPos().x, 2) +
                              Math.pow(pos.y - context.camera().getPos().y, 2) +
                              Math.pow(pos.z - context.camera().getPos().z, 2));
                              
        if (magnitude < 20) {
            matrices.translate(
                pos.x - context.camera().getPos().x,
                pos.y - context.camera().getPos().y,
                pos.z - context.camera().getPos().z
            );
        } else {
            matrices.translate(
                (pos.x - context.camera().getPos().x) / magnitude * 20,
                (pos.y - context.camera().getPos().y) / magnitude * 20,
                (pos.z - context.camera().getPos().z) / magnitude * 20
            );
        }
        
        matrices.multiply(context.camera().getRotation());
        float scale = max((float) d / 7f, 1f);
        
        if (magnitude < 20) {
            matrices.scale(.025f * scale, -.025f * scale, 1F);
        } else {
            matrices.scale(.025f * 20 / 7f, -.025f * 20 / 7f, .1F);
        }
        
        org.joml.Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int j = ((int) (.25 * 255.0f)) << 24;
        
        BufferBuilder buf = RenderUtil.getBufferFor(layer);
        buf.vertex(matrix4f, -1.0f - textRenderer.getWidth(text) / 2, -1.0f, 0.0f)
           .color(j)
           .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
           ;
        buf.vertex(matrix4f, -1.0f - textRenderer.getWidth(text) / 2, textRenderer.fontHeight, 0.0f)
           .color(j)
           .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
           ;
        buf.vertex(matrix4f, textRenderer.getWidth(text) / 2, textRenderer.fontHeight, 0.0f)
           .color(j)
           .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
           ;
        buf.vertex(matrix4f, textRenderer.getWidth(text) / 2, -1.0f, 0.0f)
           .color(j)
           .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
          ;

        matrices.translate(0F, 0F, 0.01F);
        layer.draw(buf.end());

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        textRenderer.draw(
            text, -textRenderer.getWidth(text) / 2, 0f, 0xFFFFFF, false, matrix4f, vertexConsumer,
            TextRenderer.TextLayerType.SEE_THROUGH,
            0, LightmapTextureManager.MAX_LIGHT_COORDINATE
        );

        textRenderer.draw(
            distText, -textRenderer.getWidth(distText) / 2, 10f, 0xFFFFFF, false, matrix4f, vertexConsumer,
            TextRenderer.TextLayerType.SEE_THROUGH,
            0, LightmapTextureManager.MAX_LIGHT_COORDINATE
        );
       // vertexConsumer.drawCurrentLayer();
        vertexConsumer.getBuffer(layer);

        matrices.pop();
    }

    /**
     * Draws a filled box at a given position
     */
    public static void drawBox(
            WorldRenderContext context,
            double x,
            double y,
            double z,
            double width,
            double height,
            double depth,
            Color color,
            boolean depthTest
    ) {
        RenderLayer layer = depthTest ? 
            DulkirRenderLayer.DULKIR_TRIANGLE_STRIP : 
            DulkirRenderLayer.DULKIR_TRIANGLE_STRIP_ESP;

        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;
        
        BufferBuilder buf = RenderUtil.getBufferFor(layer);
        matrices.push();
        matrices.translate(x - context.camera().getPos().x, y - context.camera().getPos().y, z - context.camera().getPos().z);
        
        VertexRendering.drawFilledBox(matrices, buf, 0.0, 0.0, 0.0, width, height, depth,
            color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            
        layer.draw(buf.end());
        matrices.pop();
    }
} 