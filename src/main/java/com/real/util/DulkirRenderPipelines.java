package com.real.util;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;

public class DulkirRenderPipelines {

    public static final RenderPipeline DULKIR_LINES = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
            .withLocation("pipeline/line_strip")
            .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, DrawMode.LINE_STRIP)
            .withCull(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withDepthWrite(true)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .build()
    );

    public static final RenderPipeline DULKIR_LINES_ESP = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
            .withLocation("pipeline/line_strip")
            .withShaderDefine("shad")
            .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, DrawMode.LINE_STRIP)
            .withCull(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withDepthWrite(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build()
    );

    public static final RenderPipeline DULKIR_TEXT = RenderPipelines.register(
        RenderPipeline.builder(
            RenderPipelines.TEXT_SNIPPET,
            RenderPipelines.FOG_SNIPPET
        )
            .withLocation("pipeline/text")
            .withVertexShader("core/rendertype_text")
            .withFragmentShader("core/rendertype_text")
            .withSampler("Sampler0")
            .withSampler("Sampler2")
            .withoutBlend()
            .withCull(false)
            .withDepthWrite(true)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .build()
    );

    public static final RenderPipeline DULKIR_TEXT_ESP = RenderPipelines.register(
        RenderPipeline.builder(
            RenderPipelines.TEXT_SNIPPET,
            RenderPipelines.FOG_SNIPPET
        )
            .withLocation("pipeline/text")
            .withVertexShader("core/rendertype_text")
            .withFragmentShader("core/rendertype_text")
            .withSampler("Sampler0")
            .withSampler("Sampler2")
            .withoutBlend()
            .withCull(false)
            .withDepthWrite(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build()
    );

    public static final RenderPipeline DULKIR_TRIANGLE_STRIP = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation("pipeline/debug_filled_box")
            .withCull(false)
            .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP)
            .withDepthWrite(true)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .withBlend(BlendFunction.TRANSLUCENT)
            .build()
    );

    public static final RenderPipeline DULKIR_TRIANGLE_STRIP_ESP = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation("pipeline/debug_filled_box")
            .withCull(false)
            .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP)
            .withDepthWrite(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withBlend(BlendFunction.TRANSLUCENT)
            .build()
    );

    public static final RenderPipeline DULKIR_QUADS_ESP = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation("pipeline/debug_quads")
            .withCull(false)
            .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
            .withDepthWrite(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withBlend(BlendFunction.TRANSLUCENT)
            .build()
    );
} 