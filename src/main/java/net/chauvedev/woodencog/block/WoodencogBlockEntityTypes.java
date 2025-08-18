package net.chauvedev.woodencog.block;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.transmission.SplitShaftVisual;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.block.generator.WoodenGeneratorBlockEntity;
import net.chauvedev.woodencog.block.generator.WoodenGeneratorRenderer;
import net.chauvedev.woodencog.block.transformer.CTTransformerBlockEntity;
import net.chauvedev.woodencog.block.transformer.CTTransformerRenderer;

public class WoodencogBlockEntityTypes {

    private static final CreateRegistrate REGISTRATE = WoodenCog.registrate();

    public WoodencogBlockEntityTypes(){}

    public static void register() {}

    public static final BlockEntityEntry<CTTransformerBlockEntity> CT_TRANSFORMER = REGISTRATE
        .blockEntity("ct_transformer", CTTransformerBlockEntity::new)
        .visual(() -> SplitShaftVisual::new, false)
        .validBlocks(WoodencogBlocks.CT_TRANSFORMER::get)
        .renderer(() -> CTTransformerRenderer::new)
        .register();

    public static final BlockEntityEntry<WoodenGeneratorBlockEntity> WOODEN_GENERATOR = REGISTRATE
        .blockEntity("wooden_generator", WoodenGeneratorBlockEntity::new)
        .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF), false)
        .validBlocks(WoodencogBlocks.WOODEN_GENERATOR::get)
        .renderer(() -> WoodenGeneratorRenderer::new)
        .register();
}
