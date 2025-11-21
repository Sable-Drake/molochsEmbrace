package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.utils.molochs_util_misc;

import java.awt.Color;

/**
 * Installation hullmod for Beta AI Core Integration.
 * Consumes a beta core and adds the actual integration hullmod as built-in.
 */
public class molochs_AIcoremod_install_beta extends BaseHullMod {

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        // Only process in campaign mode, not in combat or title screen
        if (ship == null || ship.getVariant() == null) return;
        if (Global.getCurrentState() == com.fs.starfarer.api.GameState.TITLE) return;
        if (Global.getCurrentState() == com.fs.starfarer.api.GameState.COMBAT) return;
        
        // Only process if ship has a fleet member and it's in player fleet
        if (ship.getFleetMember() == null) return;
        if (ship.getFleetMember().getFleetData() == null || 
            ship.getFleetMember().getFleetData().getFleet() == null ||
            !ship.getFleetMember().getFleetData().getFleet().equals(Global.getSector().getPlayerFleet())) {
            return;
        }
        
        // Check if installation hullmod is still present
        if (!ship.getVariant().hasHullMod("molochs_AIcoremod_install_beta")) {
            return;
        }
        
        // Don't add confirmed automatically - player must install it manually
    }

    @Override
    public void advanceInCampaign(FleetMemberAPI member, float amount) {
        // Only process in campaign mode, not in title screen
        if (Global.getCurrentState() == com.fs.starfarer.api.GameState.TITLE) return;
        
        // Check if installation hullmod is still present
        if (!member.getVariant().hasHullMod("molochs_AIcoremod_install_beta")) {
            return;
        }
        
        // Only process player fleet ships
        if (member.getFleetData() == null || 
            member.getFleetData().getFleet() == null ||
            !member.getFleetData().getFleet().equals(Global.getSector().getPlayerFleet())) {
            return;
        }
        
        // Don't add confirmed automatically - player must install it manually
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return false;
        
        // Don't allow if yunru install hullmods are present
        if (Global.getSettings().getModManager().isModEnabled("yunru_hullmods")) {
            if (ship.getVariant().hasHullMod("yunru_install_alpha") ||
                ship.getVariant().hasHullMod("yunru_install_beta") ||
                ship.getVariant().hasHullMod("yunru_install_gamma")) {
                return false;
            }
        }
        
        // Don't allow if other Moloch install hullmods are present
        if (ship.getVariant().hasHullMod("molochs_AIcoremod_install_alpha") ||
            ship.getVariant().hasHullMod("molochs_AIcoremod_install_gamma") ||
            ship.getVariant().hasHullMod("molochs_AIcoremod_install_omega")) {
            return false;
        }
        
        for (String hullmod : ship.getVariant().getHullMods()) {
            if (hullmod.equals("molochs_AIcoremod_alpha") || 
                hullmod.equals("molochs_AIcoremod_beta") ||
                hullmod.equals("molochs_AIcoremod_gamma") ||
                hullmod.equals("molochs_AIcoremod_omega")) {
                return false;
            }
        }
        
        for (String hullmod : ship.getVariant().getPermaMods()) {
            if (hullmod.equals("molochs_AIcoremod_alpha") || 
                hullmod.equals("molochs_AIcoremod_beta") ||
                hullmod.equals("molochs_AIcoremod_gamma") ||
                hullmod.equals("molochs_AIcoremod_omega")) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return null;
        
        // Check if yunru install hullmods are present
        if (Global.getSettings().getModManager().isModEnabled("yunru_hullmods")) {
            if (ship.getVariant().hasHullMod("yunru_install_alpha") ||
                ship.getVariant().hasHullMod("yunru_install_beta") ||
                ship.getVariant().hasHullMod("yunru_install_gamma")) {
                return "Can only integrate one AI core at once";
            }
        }
        
        for (String hullmod : ship.getVariant().getHullMods()) {
            if (hullmod.equals("molochs_AIcoremod_alpha") || 
                hullmod.equals("molochs_AIcoremod_beta") ||
                hullmod.equals("molochs_AIcoremod_gamma") ||
                hullmod.equals("molochs_AIcoremod_omega")) {
                return "Only one integrated AI can be installed at once";
            }
        }
        
        for (String hullmod : ship.getVariant().getPermaMods()) {
            if (hullmod.equals("molochs_AIcoremod_alpha") || 
                hullmod.equals("molochs_AIcoremod_beta") ||
                hullmod.equals("molochs_AIcoremod_gamma") ||
                hullmod.equals("molochs_AIcoremod_omega")) {
                return "Only one integrated AI can be installed at once";
            }
        }
        
        return null;
    }

    @Override
    public boolean canBeAddedOrRemovedNow(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
        return molochs_util_misc.playerHasCommodity("beta_core") && 
               super.canBeAddedOrRemovedNow(ship, marketOrNull, mode);
    }

    @Override
    public String getCanNotBeInstalledNowReason(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
        if (!molochs_util_misc.playerHasCommodity("beta_core")) {
            return "You do not have the required Beta AI core";
        }
        return super.getCanNotBeInstalledNowReason(ship, marketOrNull, mode);
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 10f;
        Color highlight = Misc.getHighlightColor();
        
        tooltip.addPara("Consumes a Beta AI core from your inventory and permanently integrates it into the ship as a built-in modification. The integration cannot be removed once installed.", pad, highlight, "permanently integrates", "built-in modification", "cannot be removed");
    }
}

