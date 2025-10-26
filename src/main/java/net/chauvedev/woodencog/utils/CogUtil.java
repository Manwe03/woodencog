package net.chauvedev.woodencog.utils;

import net.chauvedev.woodencog.WoodenCog;
import org.jetbrains.annotations.Contract;

public class CogUtil {
    /**
     * Runs the guard on condition
     * @param condition to run guard
     * @param onTrue guard
     * @return is executed
     */
    public static boolean guard(boolean condition, Runnable onTrue) {
        if (condition) {
            onTrue.run();
            return true;
        }
        return false;
    }

    /**
     * Logs if on condition
     * @param condition to run log
     * @param c class
     * @param message error message
     * @return id logged
     */
    @Contract("true, _, _ -> true; false, _, _ -> false")
    public static boolean logConditional(boolean condition, Class<?> c, String message){
        return guard(condition,()-> WoodenCog.LOGGER.error(c.getName()+" : "+message));
    }
}
