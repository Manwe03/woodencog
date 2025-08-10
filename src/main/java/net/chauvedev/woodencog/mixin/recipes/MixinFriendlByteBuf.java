package net.chauvedev.woodencog.mixin.recipes;

import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Mixin(value = FriendlyByteBuf.class, remap = true)
public abstract class MixinFriendlByteBuf {

    @Shadow public abstract String readUtf(int pMaxLength);

    @Shadow public abstract int readerIndex();

    @Shadow public abstract int readableBytes();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public ResourceLocation readResourceLocation() {
        FriendlyByteBuf buf = (FriendlyByteBuf)(Object)this;
        int startIndex = buf.readerIndex();
        int readable = buf.readableBytes();

        writeLog("readResourceLocation start readerIndex=" + startIndex + ", readableBytes=" + readable);

        String srl = buf.readUtf(32767);

        writeLog("FriendlyByteBuf readResourceLocation: " + srl);

        int endIndex = buf.readerIndex();
        writeLog("readResourceLocation end readerIndex=" + endIndex + ", consumed bytes=" + (endIndex - startIndex));

        return new ResourceLocation(srl);
    }

    @Inject(method = "writeResourceLocation", at = @At("HEAD"))
    public void writeResourceLocation(ResourceLocation pResourceLocation, CallbackInfoReturnable<FriendlyByteBuf> cir) {
        FriendlyByteBuf buf = (FriendlyByteBuf)(Object)this;
        int startIndex = buf.writerIndex();

        writeLog("writeResourceLocation at writerIndex=" + startIndex + ", ResourceLocation: " + pResourceLocation.toString());
    }

    @Unique
    private void writeLog(String message) {
        File logFile = new File("logs/woodencog_resourcelocations.log");
        // Asegúrate de que el directorio exista
        logFile.getParentFile().mkdirs();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        String line = timestamp + " - " + message + System.lineSeparator();

        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
