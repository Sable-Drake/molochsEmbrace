package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

/**
 * Omega tier extension for SHU's Armament Support System.
 * This hullmod extends SHU's system to support Omega tier within Moloch's Embrace mod.
 * It applies the same bonuses as Alpha tier (matching our Omega AI integration implementation).
 */
public class specialsphmod_omega_core_upgrades extends BaseHullMod {

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        // Check if omega core is present (via yunru_omegacore marker or molochs_AIcoremod_omega)
        boolean hasYunruOmega = false;
        boolean hasOmegaCore = false;
        
        if (stats.getVariant() != null) {
            hasYunruOmega = stats.getVariant().hasHullMod("yunru_omegacore");
            hasOmegaCore = stats.getVariant().hasHullMod("molochs_AIcoremod_omega") ||
                          stats.getVariant().getPermaMods().contains("molochs_AIcoremod_omega") ||
                          stats.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_omega");
            
            if (!hasOmegaCore && !hasYunruOmega) {
                return; // Don't apply effects if omega core is not present
            }
        }
        
        // Apply Alpha-tier SHU bonuses (same as Alpha gets, matching our Omega implementation)
        // Autofire accuracy bonus: Alpha 60% (default)
        float autofireBonus = 60f;
        try {
            autofireBonus = org.magiclib.util.MagicSettings.getFloat("mayu_specialupgrades", "shu_alpha_core_autofire_bonus");
        } catch (Exception e) {
            // Use default if can't read
        }
        
        // Turret turn rate bonus: Alpha 70% (default)
        float turretTurnBonus = 70f;
        try {
            turretTurnBonus = org.magiclib.util.MagicSettings.getFloat("mayu_specialupgrades", "shu_alpha_core_turret_turn_bonus");
        } catch (Exception e) {
            // Use default if can't read
        }
        
        // OP bonus instead of cost reduction - apply as flat bonus to available OP
        float opBonus = 6f; // Default bonus for Alpha
        try {
            float smallReduction = org.magiclib.util.MagicSettings.getFloat("mayu_specialupgrades", "shu_alpha_core_cost_reduction_small_bonus");
            float mediumReduction = org.magiclib.util.MagicSettings.getFloat("mayu_specialupgrades", "shu_alpha_core_cost_reduction_medium_bonus");
            float largeReduction = org.magiclib.util.MagicSettings.getFloat("mayu_specialupgrades", "shu_alpha_core_cost_reduction_large_bonus");
            // Use average as OP bonus
            opBonus = (smallReduction + mediumReduction + largeReduction) / 3f;
        } catch (Exception e) {
            // Use default if can't read
        }
        
        // Apply synergy bonuses when yunru_omegacore is detected (matching SHU's pattern)
        // Omega tier gets enhanced synergy bonuses (1.5x base bonuses)
        if (hasYunruOmega) {
            autofireBonus *= 1.5f;
            turretTurnBonus *= 1.5f;
            opBonus *= 1.5f;
        }
        
        stats.getAutofireAimAccuracy().modifyPercent(id, autofireBonus);
        stats.getWeaponTurnRateBonus().modifyPercent(id, turretTurnBonus);
        stats.getDynamic().getMod("ordnance_points_mod").modifyFlat(id, opBonus);
    }
    
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return false;
        
        // Only applicable if omega core is present
        return ship.getVariant().hasHullMod("yunru_omegacore") ||
               ship.getVariant().hasHullMod("molochs_AIcoremod_omega") ||
               ship.getVariant().getPermaMods().contains("molochs_AIcoremod_omega") ||
               ship.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_omega");
    }
    
    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return null;
        
        if (!ship.getVariant().hasHullMod("yunru_omegacore") &&
            !ship.getVariant().hasHullMod("molochs_AIcoremod_omega") &&
            !ship.getVariant().getPermaMods().contains("molochs_AIcoremod_omega") &&
            !ship.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_omega")) {
            return "Requires an Omega AI core integration";
        }
        
        return null;
    }
}

