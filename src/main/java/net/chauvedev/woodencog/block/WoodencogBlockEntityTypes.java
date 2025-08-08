package net.chauvedev.woodencog.block;

import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftVisual;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.chauvedev.woodencog.WoodenCog;

public class WoodencogBlockEntityTypes {

    private static final CreateRegistrate REGISTRATE = WoodenCog.registrate();

    public static final BlockEntityEntry<CTTransformerBlockEntity> CT_TRANSFORMER;

    public WoodencogBlockEntityTypes(){}

    public static void register() {}

    static {
        CT_TRANSFORMER = REGISTRATE.blockEntity("ct_transformer", CTTransformerBlockEntity::new)
                .validBlocks(WoodencogBlocks.CT_TRANSFORMER::get)
                .renderer(() -> {
                    return CTTransformerRenderer::new;
                }).register();
    }
}
