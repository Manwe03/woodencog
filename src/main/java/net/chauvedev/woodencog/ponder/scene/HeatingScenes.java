package net.chauvedev.woodencog.ponder.scene;

import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class HeatingScenes {
    public static void heating(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        BlockPos lavaDepotPos = new BlockPos(1,1,1);
        Vec3 lavaDepotText = util.vector().blockSurface(new BlockPos(1,1,1), Direction.WEST);
        BlockPos fireDepotPos = new BlockPos(1,1,3);
        Vec3 fireDepotText = util.vector().blockSurface(new BlockPos(1,1,3), Direction.WEST);

        scene.title("heat", "");

        scene.configureBasePlate(0, 0, 5);

        scene.world().showSection(util.select().everywhere(), Direction.DOWN);
        scene.idle(20);

        scene.world().modifyBlockEntity(lavaDepotPos, DepotBlockEntity.class, be -> be.setHeldItem(new ItemStack(Items.IRON_INGOT)));
        scene.overlay().showText(80).text("").pointAt(lavaDepotText).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.world().modifyBlockEntity(lavaDepotPos, DepotBlockEntity.class, be -> be.setHeldItem(new ItemStack(Items.AIR)));

        scene.world().modifyBlockEntity(fireDepotPos, DepotBlockEntity.class, be -> be.setHeldItem(new ItemStack(Items.IRON_INGOT)));
        scene.overlay().showText(80).text("").pointAt(fireDepotText).placeNearTarget().attachKeyFrame();
        scene.idle(80);
    }

    public static void cooling(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        Vec3 waterDepot = util.vector().blockSurface(new BlockPos(1,1,2), Direction.WEST);

        scene.title("cool", "");

        scene.configureBasePlate(0, 0, 5);

        scene.world().showSection(util.select().everywhere(), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(80).text("").pointAt(waterDepot).placeNearTarget().attachKeyFrame();
        scene.idle(80);
    }
}
