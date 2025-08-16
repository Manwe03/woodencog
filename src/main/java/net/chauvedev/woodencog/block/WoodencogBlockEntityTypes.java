package net.chauvedev.woodencog.block;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.bell.HauntedBellBlockEntity;
import com.simibubi.create.content.kinetics.transmission.ClutchBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftVisual;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.chauvedev.woodencog.WoodenCog;

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

}
