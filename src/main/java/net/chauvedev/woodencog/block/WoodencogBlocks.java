package net.chauvedev.woodencog.block;

import com.simibubi.create.foundation.data.*;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.block.generator.WoodenGeneratorBlock;
import net.chauvedev.woodencog.block.transformer.CTTransformerBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.MapColor;

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

    public static final BlockEntry<WoodenGeneratorBlock> WOODEN_GENERATOR = REGISTRATE.block("wooden_generator", WoodenGeneratorBlock::new)
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
