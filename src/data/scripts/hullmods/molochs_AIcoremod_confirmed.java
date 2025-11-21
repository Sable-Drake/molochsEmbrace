package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import data.scripts.utils.molochs_util_misc;

/**
 * Confirmation hullmod that triggers the AI core integration process.
 * Player installs this after selecting which AI core to integrate.
 * 
 * IMPORTANT: This hullmod uses ONLY applyEffectsAfterShipCreation() for integration logic,
 * NOT advanceInCampaign(). This matches Yunru's implementation pattern.
 * 
 * DO NOT add advanceInCampaign() logic here - it will cause issues with immediate application
 * of the integration. The integration must happen immediately when the hullmod is applied,
 * which applyEffectsAfterShipCreation() handles correctly.
 */
public class molochs_AIcoremod_confirmed extends BaseHullMod {
    
    /**
     * Integration logic runs here - NOT in advanceInCampaign().
     * This ensures immediate application when the hullmod is installed.
     */
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship == null || ship.getVariant() == null) return;
        
        // Check if confirmed hullmod is still present
        if (!ship.getVariant().hasHullMod("molochs_AIcoremod_confirmed")) {
            return;
        }
        
        // Check which install hullmod is present and process accordingly
        if (ship.getVariant().hasHullMod("molochs_AIcoremod_install_alpha")) {
            processAlphaIntegration(ship);
        } else if (ship.getVariant().hasHullMod("molochs_AIcoremod_install_beta")) {
            processBetaIntegration(ship);
        } else if (ship.getVariant().hasHullMod("molochs_AIcoremod_install_gamma")) {
            processGammaIntegration(ship);
        } else if (ship.getVariant().hasHullMod("molochs_AIcoremod_install_omega")) {
            processOmegaIntegration(ship);
        }
    }
    
    private void processAlphaIntegration(ShipAPI ship) {
        // Don't process if already integrated
        if (ship.getVariant().getPermaMods().contains("molochs_AIcoremod_alpha") ||
            ship.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_alpha")) {
            return;
        }
        
        if (!molochs_util_misc.playerHasCommodity("alpha_core")) return;
        
        // Remove lower-tier integrations and refund cores
        if (ship.getVariant().getPermaMods().contains("molochs_AIcoremod_gamma")) {
            ship.getVariant().removePermaMod("molochs_AIcoremod_gamma");
            molochs_util_misc.addPlayerCommodity("gamma_core", 1);
        }
        if (ship.getVariant().getPermaMods().contains("molochs_AIcoremod_beta")) {
            ship.getVariant().removePermaMod("molochs_AIcoremod_beta");
            molochs_util_misc.addPlayerCommodity("beta_core", 1);
        }
        
        molochs_util_misc.removePlayerCommodity("alpha_core");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_alpha");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_beta");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_gamma");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_omega");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_confirmed");
        ship.getVariant().addPermaMod("molochs_AIcoremod_alpha", false);
    }
    
    private void processBetaIntegration(ShipAPI ship) {
        // Don't process if already integrated
        if (ship.getVariant().getPermaMods().contains("molochs_AIcoremod_beta") ||
            ship.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_beta")) {
            return;
        }
        
        if (!molochs_util_misc.playerHasCommodity("beta_core")) return;
        
        if (ship.getVariant().getPermaMods().contains("molochs_AIcoremod_gamma")) {
            ship.getVariant().removePermaMod("molochs_AIcoremod_gamma");
            molochs_util_misc.addPlayerCommodity("gamma_core", 1);
        }
        
        molochs_util_misc.removePlayerCommodity("beta_core");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_alpha");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_beta");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_gamma");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_omega");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_confirmed");
        ship.getVariant().addPermaMod("molochs_AIcoremod_beta", false);
    }
    
    private void processGammaIntegration(ShipAPI ship) {
        // Don't process if already integrated
        if (ship.getVariant().getPermaMods().contains("molochs_AIcoremod_gamma") ||
            ship.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_gamma")) {
            return;
        }
        
        if (!molochs_util_misc.playerHasCommodity("gamma_core")) return;
        
        molochs_util_misc.removePlayerCommodity("gamma_core");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_alpha");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_beta");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_gamma");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_omega");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_confirmed");
        ship.getVariant().addPermaMod("molochs_AIcoremod_gamma", false);
    }
    
    private void processOmegaIntegration(ShipAPI ship) {
        // Don't process if already integrated
        if (ship.getVariant().getPermaMods().contains("molochs_AIcoremod_omega") ||
            ship.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_omega")) {
            return;
        }
        
        if (!molochs_util_misc.playerHasCommodity("omega_core")) return;
        
        if (ship.getVariant().getPermaMods().contains("molochs_AIcoremod_gamma")) {
            ship.getVariant().removePermaMod("molochs_AIcoremod_gamma");
            molochs_util_misc.addPlayerCommodity("gamma_core", 1);
        }
        if (ship.getVariant().getPermaMods().contains("molochs_AIcoremod_beta")) {
            ship.getVariant().removePermaMod("molochs_AIcoremod_beta");
            molochs_util_misc.addPlayerCommodity("beta_core", 1);
        }
        if (ship.getVariant().getPermaMods().contains("molochs_AIcoremod_alpha")) {
            ship.getVariant().removePermaMod("molochs_AIcoremod_alpha");
            molochs_util_misc.addPlayerCommodity("alpha_core", 1);
        }
        
        molochs_util_misc.removePlayerCommodity("omega_core");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_alpha");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_beta");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_gamma");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_install_omega");
        ship.getVariant().getHullMods().remove("molochs_AIcoremod_confirmed");
        ship.getVariant().addPermaMod("molochs_AIcoremod_omega", false);
    }
    
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return false;
        
        // Only applicable if one of the install hullmods is present
        return ship.getVariant().hasHullMod("molochs_AIcoremod_install_alpha") ||
               ship.getVariant().hasHullMod("molochs_AIcoremod_install_beta") ||
               ship.getVariant().hasHullMod("molochs_AIcoremod_install_gamma") ||
               ship.getVariant().hasHullMod("molochs_AIcoremod_install_omega");
    }
    
    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return null;
        
        if (!ship.getVariant().hasHullMod("molochs_AIcoremod_install_alpha") &&
            !ship.getVariant().hasHullMod("molochs_AIcoremod_install_beta") &&
            !ship.getVariant().hasHullMod("molochs_AIcoremod_install_gamma") &&
            !ship.getVariant().hasHullMod("molochs_AIcoremod_install_omega")) {
            return "Select an AI core integration hullmod first";
        }
        
        return null;
    }
}

