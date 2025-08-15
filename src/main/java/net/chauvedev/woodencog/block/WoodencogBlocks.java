package net.chauvedev.woodencog.block;

import com.simibubi.create.foundation.data.*;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.config.WStress;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.MapColor;

public class WoodencogBlocks {
    private static final CreateRegistrate REGISTRATE = WoodenCog.registrate();

    public static final BlockEntry<CTTransformerBlock> CT_TRANSFORMER;

    static {
        CT_TRANSFORMER = REGISTRATE.block("ct_transformer", CTTransformerBlock::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .transform(WStress.setImpact(32))
                .transform(TagGen.axeOrPickaxe())
                .blockstate((c, p) -> {
                    BlockStateGen.axisBlock(c, p, AssetLookup.forPowered(c, p));
                })
                .addLayer(() -> RenderType::cutoutMipped) //Not sure
                .item()
                .transform(ModelGen.customItemModel())
                .register();
    }

    public static void register() {}
}
