package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class molochs_AIConstruction extends BaseHullMod {

    public static Color LIGHTNING_FRINGE_COLOR2 = new Color(155, 41, 217, 90);
    public static Color FRINGE_COLOR2 = new Color( 19, 26, 14, 250);

    public static final float SMOD_MAINTENANCE_MULT = 0.50f;
    public static final float SMOD_FLUXCOST_EMULT = 0.68f;
    public static final float SMOD_FLUXCOST_BMULT = 0.78f;
    public static final float SMOD_BALLRANGE_MULT = 0.80f;

    public static final float MAINTENANCE_MULT = 0.65f;
    public static final float FLUXCOST_EMULT = 0.75f;
    public static final float FLUXCOST_BMULT = 0.85f;
    public static final float BALLRANGE_MULT = 0.80f;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

        boolean sMod = isSMod(stats);
        if (sMod) {
            stats.getSuppliesPerMonth().modifyMult(id, SMOD_MAINTENANCE_MULT);
            stats.getFuelUseMod().modifyMult(id, SMOD_MAINTENANCE_MULT);
            stats.getBallisticWeaponFluxCostMod().modifyMult(id,SMOD_FLUXCOST_BMULT);
            stats.getEnergyWeaponFluxCostMod().modifyMult(id,SMOD_FLUXCOST_EMULT);
            stats.getMissileWeaponFluxCostMod().modifyMult(id,SMOD_FLUXCOST_EMULT);
            stats.getBallisticWeaponRangeBonus().modifyMult(id,SMOD_BALLRANGE_MULT);
        }else{
            stats.getSuppliesPerMonth().modifyMult(id, MAINTENANCE_MULT);
            stats.getFuelUseMod().modifyMult(id, MAINTENANCE_MULT);
            stats.getBallisticWeaponFluxCostMod().modifyMult(id,FLUXCOST_BMULT);
            stats.getEnergyWeaponFluxCostMod().modifyMult(id,FLUXCOST_EMULT);
            stats.getMissileWeaponFluxCostMod().modifyMult(id,FLUXCOST_EMULT);
            stats.getBallisticWeaponRangeBonus().modifyMult(id,BALLRANGE_MULT);
        }
    }

    public String getSModDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) ((1f-SMOD_MAINTENANCE_MULT)*100f) + "%";
        if (index == 1) return "" + (int) ((1f-SMOD_FLUXCOST_EMULT)*100f) + "%";
        if (index == 2) return "" + (int) ((1f-SMOD_FLUXCOST_BMULT)*100f) + "%";
        return null;
    }

    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;
        MutableShipStatsAPI stats = ship.getMutableStats();
    }


    @Override
    public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return false;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color m = Misc.getMissileMountColor();
        Color e = Misc.getHighlightColor();
        Color b = Misc.getHighlightColor();
        Color t = Misc.getHighlightColor();
        Color l = Misc.getDesignTypeColor("Low Tech");
        Color md = Misc.getDesignTypeColor("Midline");
        Color h = Misc.getDesignTypeColor("High Tech");
        Color et = Misc.getDesignTypeColor("Epta Tech");
        Color p = Misc.getDesignTypeColor("molochs phase");
        Color w = Misc.getDesignTypeColor("molochs white");
        Color prt = Misc.getDesignTypeColor("Pirate");
        
        Color bad = Misc.getNegativeHighlightColor();


        LabelAPI label = tooltip.addPara("Epta ship designs are immensely efficient for their size. AI assisted hull architecture drastically reduces the amount of components that need regular replacements and next gen flux grid designs drastically reduce the flux requirements of most fitted weapons. Unfortunately said flux grids are ill suited to powering lower tech weapons, reducing ballistic weapon range and flux cost reduction." +
                "" +
                "Decreases maintenance costs by %s and decreases the flux cost to fire energy weapons by %s and ballistic weapons by %s. Decreases ballistic weapon range by %s.", opad, t,
                "" + "35%",
                "" + "25%",
                "" + "15%",
                "" + "20%");
        label.setHighlight(
                "" + "35%",
                "" + "25%",
                "" + "15%",
                "" + "20%");
        label.setHighlightColors(b, b, b, b);
    }
}

