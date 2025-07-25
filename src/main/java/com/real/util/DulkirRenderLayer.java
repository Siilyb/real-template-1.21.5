package com.real.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.*;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.HashSet;

public class DulkirRenderLayer {
    
    public static final RenderLayer DULKIR_LINES = RenderLayer.of(
        "dulkir-lines",
        1536,
        DulkirRenderPipelines.DULKIR_LINES,
        RenderLayer.MultiPhaseParameters.builder()
            .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
            .build(false)
    );

    public static final RenderLayer DULKIR_LINES_ESP = RenderLayer.of(
        "dulkir-lines-esp",
        1536,
        DulkirRenderPipelines.DULKIR_LINES_ESP,
        RenderLayer.MultiPhaseParameters.builder()
            .build(false)
    );

    public static final RenderLayer DULKIR_TEXT = RenderLayer.of(
        "dulkir-text",
        1536,
        DulkirRenderPipelines.DULKIR_TEXT,
        RenderLayer.MultiPhaseParameters.builder()
            .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
            .build(false)
    );

    public static final RenderLayer DULKIR_TEXT_ESP = RenderLayer.of(
        "dulkir_text_esp",
        1536,
        DulkirRenderPipelines.DULKIR_TEXT_ESP,
        RenderLayer.MultiPhaseParameters.builder()
            .build(false)
    );

    public static final RenderLayer.MultiPhase DULKIR_TRIANGLE_STRIP = RenderLayer.of(
        "dulkir_triangle_strip",
        1536,
        false,
        true,
        DulkirRenderPipelines.DULKIR_TRIANGLE_STRIP,
        RenderLayer.MultiPhaseParameters.builder()
            .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
            .build(false)
    );

    public static final RenderLayer.MultiPhase DULKIR_TRIANGLE_STRIP_ESP = RenderLayer.of(
        "dulkir_triangle_strip",
        1536,
        false,
        true,
        DulkirRenderPipelines.DULKIR_TRIANGLE_STRIP_ESP,
        RenderLayer.MultiPhaseParameters.builder()
            .build(false)
    );

    public static final RenderLayer.MultiPhase DULKIR_QUADS_ESP = RenderLayer.of(
        "dulkir_quads",
        1536,
        false,
        true,
        DulkirRenderPipelines.DULKIR_QUADS_ESP,
        RenderLayer.MultiPhaseParameters.builder()
            .build(false)
    );

    public static final Set<RenderLayer> LAYERS = new HashSet<RenderLayer>() {{
        add(DULKIR_LINES);
        add(DULKIR_LINES_ESP);
        add(DULKIR_TEXT);
        add(DULKIR_TEXT_ESP);
        add(DULKIR_TRIANGLE_STRIP);
        add(DULKIR_TRIANGLE_STRIP_ESP);
    }};
}