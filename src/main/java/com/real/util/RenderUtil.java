package com.real.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.BufferAllocator;

import java.nio.Buffer;

public class RenderUtil {
    
    /**
     * Gets a BufferBuilder for a specific render layer.
     * 
     * @param layer The render layer to get a buffer for
     * @return A BufferBuilder ready to be used with the specified render layer
     */
    public static BufferBuilder getBufferFor(RenderLayer layer) {
/*        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(layer.getDrawMode(), layer.getVertexFormat());

        return bufferBuilder;*/

        BufferBuilder bufferBuilder=new BufferBuilder(new BufferAllocator(layer.getExpectedBufferSize()),
                layer.getPipeline().getVertexFormatMode(),
                layer.getPipeline().getVertexFormat());

        return bufferBuilder;

    }
} 