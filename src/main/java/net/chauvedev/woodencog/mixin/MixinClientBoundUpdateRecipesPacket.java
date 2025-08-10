package net.chauvedev.woodencog.mixin;

import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

@Mixin(value = ClientboundUpdateRecipesPacket.class, remap = true)
public class MixinClientBoundUpdateRecipesPacket {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static Recipe<?> fromNetwork(FriendlyByteBuf buff) {
        try (FileWriter fw = new FileWriter("client_fromNetwork.log", true)) {
            fw.write("fromNetwork");
            ResourceLocation $$1 = buff.readResourceLocation();
            fw.write("Serializer: " + $$1.toString());
            ResourceLocation $$2 = buff.readResourceLocation();
            fw.write("Recipe: " + $$2.toString());
            fw.write("Buffer: " + Arrays.toString(buff.array()));
            return ((RecipeSerializer)BuiltInRegistries.RECIPE_SERIALIZER.getOptional($$1).orElseThrow(() -> {
                return new IllegalArgumentException("Unknown recipe serializer " + $$1);
            })).fromNetwork($$2, buff);

        } catch (IOException e) {
            WoodenCog.LOGGER.error("Error writing client log", e);
        }
        return null;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static <T extends Recipe<?>> void toNetwork(FriendlyByteBuf buffer, T recipe) {
        ResourceLocation rs = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.getSerializer());

        if(!woodencog$isValidNamespace(rs.getNamespace())) WoodenCog.LOGGER.error("Invalid Namespace " + rs.getNamespace());
        if(!woodencog$isValidPath(rs.getPath())) WoodenCog.LOGGER.error("Invalid Path " + rs.getPath());

        if(!woodencog$isValidNamespace(recipe.getId().getNamespace())) WoodenCog.LOGGER.warn("Invalid Namespace " + recipe.getId().getNamespace());
        if(!woodencog$isValidPath(recipe.getId().getPath())) WoodenCog.LOGGER.warn("Invalid Path  " + recipe.getId().getPath());

        buffer.writeResourceLocation(rs);
        buffer.writeResourceLocation(recipe.getId());
        ((net.minecraft.world.item.crafting.RecipeSerializer<T>) recipe.getSerializer()).toNetwork(buffer, recipe);
    }


    @Unique
    private static boolean woodencog$isValidNamespace(String pNamespace) {
        for(int i = 0; i < pNamespace.length(); ++i) {
            if (!woodencog$valid(pNamespace.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private static boolean woodencog$valid(char pNamespaceChar) {
        return pNamespaceChar == '_' || pNamespaceChar == '-' || pNamespaceChar >= 'a' && pNamespaceChar <= 'z' || pNamespaceChar >= '0' && pNamespaceChar <= '9' || pNamespaceChar == '.';
    }

    @Unique
    private static boolean woodencog$isValidPath(String pPath) {
        for(int i = 0; i < pPath.length(); ++i) {
            if (!woodencog$validPathChar(pPath.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private static boolean woodencog$validPathChar(char pPathChar) {
        return pPathChar == '_' || pPathChar == '-' || pPathChar >= 'a' && pPathChar <= 'z' || pPathChar >= '0' && pPathChar <= '9' || pPathChar == '/' || pPathChar == '.';
    }
}
