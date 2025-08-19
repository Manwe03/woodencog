package net.chauvedev.woodencog.ponder.scene;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class BlockScenes {

    public static void generatorWaterWheel(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        BlockPos generator = new BlockPos(3,3,3);
        Vec3 generatorSide = util.vector().blockSurface(generator, Direction.WEST);

        scene.title("wooden_generator.water_wheel", "");

        scene.configureBasePlate(0, 0, 7);

        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(5);

        scene.world().showSection(util.select().layer(1), Direction.DOWN);
        scene.idle(5);

        scene.world().showSection(util.select().position(3,3,4), Direction.DOWN);
        scene.idle(20);

        scene.world().showSection(util.select().position(generator), Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(80).text("").pointAt(generatorSide).placeNearTarget();
        scene.idle(80);

        scene.world().showSection(util.select().position(3,3,2), Direction.DOWN);
        scene.idle(5);

        scene.world().showSection(util.select().position(3,3,1), Direction.DOWN);
        scene.idle(5);

        scene.overlay().showText(80).text("").pointAt(generatorSide).placeNearTarget();
        scene.idle(80);

        scene.overlay().showText(40).text("").colored(PonderPalette.RED).pointAt(generatorSide).placeNearTarget();
        scene.idle(40);
    }

    public static void generatorWindmill(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        BlockPos generatorPos = new BlockPos(3,14,3);

        scene.title("wooden_generator.windmill", "");

        scene.scaleSceneView(0.8f);
        scene.configureBasePlate(0, 0, 7);
        scene.setSceneOffsetY(-13);
        scene.world().showSection(util.select().position(generatorPos.south(1)), Direction.DOWN);
        scene.idle(20);
        scene.world().showSection(util.select().position(generatorPos), Direction.DOWN);
        scene.overlay().showText(80).text("").pointAt(generatorPos.getCenter()).placeNearTarget();
        scene.idle(20);
        scene.world().showSection(util.select().position(generatorPos.north(1)), Direction.DOWN);
        scene.world().showSection(util.select().position(generatorPos.north(2)), Direction.DOWN);
    }

    public static void generatorWindmillRustic(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        BlockPos generatorPos = new BlockPos(3,14,3);

        scene.title("wooden_generator.windmill_rustic", "");

        scene.scaleSceneView(0.8f);
        scene.configureBasePlate(0, 0, 7);
        scene.setSceneOffsetY(-13);
        scene.world().showSection(util.select().position(generatorPos.south(1)), Direction.DOWN);
        scene.idle(20);
        scene.world().showSection(util.select().position(generatorPos), Direction.DOWN);
        scene.overlay().showText(80).text("").pointAt(generatorPos.getCenter()).placeNearTarget();
        scene.idle(20);
        scene.world().showSection(util.select().position(generatorPos.north(1)), Direction.DOWN);
        scene.world().showSection(util.select().position(generatorPos.north(2)), Direction.DOWN);
    }
}
