package net.chauvedev.woodencog.compat;

import net.chauvedev.woodencog.compat.createaddition.CCAIntegrationImpl;
import net.chauvedev.woodencog.compat.createaddition.EmptyCCAIntegration;
import net.chauvedev.woodencog.compat.createaddition.ICCAIntegration;
import net.chauvedev.woodencog.compat.createlowheated.CLHIntegrationImpl;
import net.chauvedev.woodencog.compat.createlowheated.EmptyCLHIntegration;
import net.chauvedev.woodencog.compat.createlowheated.ICLHIntegration;
import net.chauvedev.woodencog.compat.createmoreburners.CMBIntegration;
import net.chauvedev.woodencog.compat.createmoreburners.CMBIntegrationImpl;
import net.chauvedev.woodencog.compat.createmoreburners.EmptyCMBIntegration;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;

public class Compat {

    public static final String CCA_MOD_ID = "createaddition"; // Create Crafts & Additions
    public static final String CLH_MOD_ID = "createlowheated"; // Create Low Heated
    public static final String CMB_MOD_ID = "moreburners"; // Create More Burners

    public static boolean isCCALoaded() {
        return ModList.get().isLoaded(CCA_MOD_ID);
    }
    public static boolean isCCAInstalled(){
        return LoadingModList.get().getModFileById(CCA_MOD_ID) != null;
    }
    public static boolean isCLHLoaded() {
        return ModList.get().isLoaded(CLH_MOD_ID);
    }
    public static boolean isCLHInstalled(){
        return LoadingModList.get().getModFileById(CLH_MOD_ID) != null;
    }
    public static boolean isCMBLoaded() {
        return ModList.get().isLoaded(CMB_MOD_ID);
    }
    public static boolean isCMBInstalled() {
        return LoadingModList.get().getModFileById(CMB_MOD_ID) != null;
    }

    public static ICCAIntegration CCA_INSTANCE;
    public static ICLHIntegration CLH_INSTANCE;
    public static CMBIntegration CMB_INSTANCE;

    public static void init(){
        CCA_INSTANCE = isCCALoaded() ? new CCAIntegrationImpl() : new EmptyCCAIntegration();
        CLH_INSTANCE = isCLHLoaded() ? new CLHIntegrationImpl() : new EmptyCLHIntegration();
        CMB_INSTANCE = isCMBLoaded() ? new CMBIntegrationImpl() : new EmptyCMBIntegration();
    }
}
