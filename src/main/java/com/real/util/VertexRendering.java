package com.real.util;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class VertexRendering {

    /**
     * Draws a filled box using the given buffer builder
     *
     * @param matrices The matrix stack to use for rendering
     * @param buffer The buffer builder to use for rendering
     * @param x The x coordinate of the box
     * @param y The y coordinate of the box
     * @param z The z coordinate of the box
     * @param width The width of the box
     * @param height The height of the box
     * @param depth The depth of the box
     * @param red The red component of the box color (0-1)
     * @param green The green component of the box color (0-1)
     * @param blue The blue component of the box color (0-1)
     * @param alpha The alpha component of the box color (0-1)
     */
    public static void drawFilledBox(MatrixStack matrices, BufferBuilder buffer, 
                                    double x, double y, double z,
                                    double width, double height, double depth,
                                    float red, float green, float blue, float alpha) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float x1 = (float) x;
        float y1 = (float) y;
        float z1 = (float) z;
        float x2 = (float) (x + width);
        float y2 = (float) (y + height);
        float z2 = (float) (z + depth);
        
        // Bottom face
        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha) ;
        
        // Top face
        buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha) ;
        
        // Front face
        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha) ;
        
        // Back face
        buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha) ;
        
        // Left face
        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha) ;
        
        // Right face
        buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha) ;
        buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha) ;
    }
} 