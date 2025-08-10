package net.chauvedev.woodencog.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

@Mixin(value = ResourceLocation.class, remap = true)
public class MixinResourceLocation {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean isValidNamespace(String pNamespace) {
        for(int i = 0; i < pNamespace.length(); ++i) {
            if (!woodencog$valid(pNamespace.charAt(i))) {
                logStackTraceToFile(pNamespace);
                throw new RuntimeException(pNamespace);
                //return false;
            }
        }

        return true;
    }

    @Unique
    private static boolean woodencog$valid(char pNamespaceChar) {
        return pNamespaceChar == '_' || pNamespaceChar == '-' || pNamespaceChar >= 'a' && pNamespaceChar <= 'z' || pNamespaceChar >= '0' && pNamespaceChar <= '9' || pNamespaceChar == '.';
    }


    @Unique
    private static void logStackTraceToFile(String nameSpace) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("woodencog-stacktrace.log", true))) {
            writer.println("=== DumpStack at " + Instant.now() + " ===");
            writer.println("namespace: " + nameSpace);
            new Exception("Manual stack trace").printStackTrace(writer);
            writer.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
