package net.chauvedev.woodencog.recipes.heatedRecipes;

import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WoodenCogHeatCondition {

    private final int color;
    private final int temperature;

    public WoodenCogHeatCondition(int temperature){
        this.color = getBlackBodyColor(temperature);
        this.temperature = temperature;
    }

    /**
     * Test the charcoal forge meets the temperature of this heat condition
     */
    public boolean testCharcoalForge(BlockEntity charcoalForge){
        if (charcoalForge instanceof CharcoalForgeBlockEntity) {
            return ((CharcoalForgeBlockEntity) charcoalForge).getTemperature() >= this.temperature;
        }
        return false;
    }

    public String serialize(){
        return this.temperature + "";
    }

    public static WoodenCogHeatCondition deserialize(int temperature){
        return new WoodenCogHeatCondition(temperature);
    }

    public boolean hasTemp(){
        return temperature > 0;
    }

    public int getColor(){
        return this.color;
    }
    public int getTemperature(){
        return this.temperature;
    }

    private int getBlackBodyColor(double temperatureCelsius) {
        double temperature = temperatureCelsius + 273.15;
        double r, g, b;

        temperature = Math.max(1000, Math.min(40000, temperature)) / 100;

        if (temperature <= 66) {
            r = 255;
        } else {
            r = 329.698727446 * Math.pow(temperature - 60, -0.1332047592);
            r = Math.max(0, Math.min(255, r));
        }

        if (temperature <= 66) {
            g = 99.4708025861 * Math.log(temperature) - 161.1195681661;
        } else {
            g = 288.1221695283 * Math.pow(temperature - 60, -0.0755148492);
        }
        g = Math.max(0, Math.min(255, g));

        if (temperature >= 66) {
            b = 255;
        } else if (temperature <= 19) {
            b = 0;
        } else {
            b = 138.5177312231 * Math.log(temperature - 10) - 305.0447927307;
            b = Math.max(0, Math.min(255, b));
        }

        return ((int) r << 16) | ((int) g << 8) | (int) b;
    }
}
