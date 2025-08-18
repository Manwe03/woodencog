package net.chauvedev.woodencog.utils;

public class RotationUtil {

    public static float toRadPerTick(float rpm) {
        return (float) (rpm * (2 * Math.PI / 1200.0));
    }

    public static float toRPM(double radPerTick) {
        return (float) (radPerTick * 1200.0 / (2 * Math.PI));
    }

    public static float roundUpToPowerOfTwo(float rpm) {
        float absrpm = Math.abs(rpm);
        if (absrpm == 0) return 0;

        int value = (int) Math.ceil(absrpm);
        int highestOneBit = Integer.highestOneBit(value);

        if (value == highestOneBit) {
            return Math.copySign(value, rpm);
        }

        return Math.copySign(highestOneBit << 1, rpm);
    }
}
