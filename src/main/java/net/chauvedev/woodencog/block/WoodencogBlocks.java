package net.chauvedev.woodencog.block;

import com.simibubi.create.content.kinetics.transmission.ClutchBlock;
import com.simibubi.create.foundation.data.*;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class WoodencogBlocks {
    private static final CreateRegistrate REGISTRATE = WoodenCog.registrate();

    public static final BlockEntry<CTTransformerBlock> CT_TRANSFORMER = REGISTRATE.block("ct_transformer", CTTransformerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .transform(TagGen.axeOrPickaxe())
            .blockstate(BlockStateGen.directionalBlockProvider(true))
            .addLayer(() -> RenderType::cutoutMipped) //Not sure
            .item()
            .transform(ModelGen.customItemModel())
            .register();

    public static void register() {}
}
