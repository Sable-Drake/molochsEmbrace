package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatUIAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class molochs_artificial_mothball extends BaseHullMod {
    
    public static final float CREW = 100f;
    public static final float USELESS = 0f;

	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            
		stats.getMinCrewMod().modifyMult(id, 1f - CREW * 0.01f);
        stats.getBallisticRoFMult().modifyMult(id, USELESS);
        stats.getEnergyRoFMult().modifyMult(id, USELESS);
        stats.getMissileRoFMult().modifyMult(id, USELESS);
        stats.getWeaponTurnRateBonus().modifyMult(id, USELESS);
        stats.getFighterWingRange().modifyMult(id, USELESS);
        stats.getFighterRefitTimeMult().modifyMult(id, USELESS);
        stats.getBeamWeaponRangeBonus().modifyMult(id, USELESS);
        stats.getBeamWeaponFluxCostMult().modifyMult(id, USELESS);
        stats.getCargoMod().modifyMult(id, USELESS);
        stats.getFuelMod().modifyMult(id, USELESS);
        stats.getSuppliesPerMonth().modifyMult(id, 0.5f);

    }
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
                  return   !ship.getVariant().getHullMods().contains("safetyoverrides");
	}

	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullMods().contains("safetyoverrides")) {
			return "SO ships are too dangerous to leave sleeping.";
		}
		return null;
	}

    public String getDescriptionParam(int index, HullSize hullSize) {
        return null;
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
        Color p = Misc.getHighlightColor();
        Color w = Misc.getDesignTypeColor("molochs white");
        Color prt = Misc.getDesignTypeColor("Pirate");
        
        Color bad = Misc.getNegativeHighlightColor();



        LabelAPI label = tooltip.addPara("An alternative method of %s developed by Epta engineers. By installing a delta + core to run basic maintenance tasks, the ship can be put back into working order in minutes rather than days. Ships under automated mothball %s, and %s.", opad, t,
        "" + "mothballing ships",
        "" + "do not require any crew",
        "" + "halves maintenance supply cost");
        label.setHighlight("" + "mothballing ships",
                "" + "do not require any crew",
                "" + "halves maintenance supply cost");
        label.setHighlightColors(b, e, m);


        tooltip.addSectionHeading("Modifies:", Alignment.MID, opad);

        label = tooltip.addPara( "No crew %s; ", opad, t,
            "requirement");
        label.setHighlight("requirement");
        label.setHighlightColors(e);

        label = tooltip.addPara( "%s maintenance supply cost; ", opad, b,
        "" + "-50%");
        label.setHighlight("" + "-50%");
        label.setHighlightColors(b);

        label = tooltip.addPara( "%s ship; ", opad, b,
            "" + "Mothballs");
        label.setHighlight("" + "Mothballs");
        label.setHighlightColors(bad);


        tooltip.addSectionHeading("Suggestions", Alignment.MID, opad);


        label = tooltip.addPara("Intended to be used in fleets with %s. However, an automated mothball merely halves maintenance costs, while a %s reduces supply maintenance costs to zero.", opad, l,
            "" + "crew shortages",

            "" + "standard mothball");
        label.setHighlight(
            "" + "crew shortages",

            "" + "standard mothball");
        label.setHighlightColors(b, b);
    }

}

