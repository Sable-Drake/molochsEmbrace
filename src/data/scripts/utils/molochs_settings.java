package data.scripts.utils;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

public class molochs_settings {
    private static final String MOD_ID = "molochs_embrace";
    
    /**
     * Check if omega mechanics are enabled
     * @return true if omega mechanics are enabled, false otherwise
     */
    public static boolean isOmegaMechanicEnabled() {
        try {
            return LunaSettings.getBoolean(MOD_ID, "molochs_enableOmegaMechanic");
        } catch (Exception e) {
            // If LunaLib is not available or setting doesn't exist, default to true
            return true;
        }
    }
    
    /**
     * Get the omega core drop chance percentage
     * @return drop chance as a percentage (0-100)
     */
    public static float getOmegaCoreDropChance() {
        try {
            return LunaSettings.getFloat(MOD_ID, "molochs_omegaCoreDropChance");
        } catch (Exception e) {
            // If LunaLib is not available or setting doesn't exist, default to 5.0
            return 5.0f;
        }
    }
}

