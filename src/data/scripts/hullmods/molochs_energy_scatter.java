package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class molochs_energy_scatter extends BaseHullMod {


    
    public static final float DAMAGE = 1.15f;
    public static final float RANGE = 0.75f;
    
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
            stats.getEnergyWeaponDamageMult().modifyMult(id, 1f * DAMAGE);
            stats.getEnergyWeaponRangeBonus().modifyMult(id, 1f * RANGE);
            
        }
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
		
            return 		
                    
                    !ship.getVariant().getHullMods().contains("molochs_er_device");

	}
        
        public String getUnapplicableReason(ShipAPI ship) {
		
		
		if (ship.getVariant().getHullMods().contains("molochs_er_device")) {
			return "This ship already has a range increasing hullmod similar to this one.";
                        
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
        Color p = Misc.getDesignTypeColor("molochs phase");
        Color w = Misc.getDesignTypeColor("molochs white");
        Color prt = Misc.getDesignTypeColor("Pirate");
        
        Color bad = Misc.getNegativeHighlightColor();



        LabelAPI label = tooltip.addPara("A sidegrade for %s that %s at the cost of %s. Is incompatible with %s.", opad, t,
        "" + "energy weapons",
        "" + "increases damage per shot",
        "" + "range",
        "" + "ER Targeting Device");
	label.setHighlight("" + "energy weapons",
        "" + "increases damage per shot",
        "" + "range",
        "" + "ER Targeting Device");
	label.setHighlightColors(b, b, bad, et);


        tooltip.addSectionHeading("Modifies:", Alignment.MID, opad);
        
        label = tooltip.addPara( "%s by %s; ", opad, t,
        "" + "Increases energy weapon damage",
        "" + "15%");
	label.setHighlight("" + "Increases energy weapon damage",
            "" + "15%");
	label.setHighlightColors(b, b);
        
        label = tooltip.addPara( "%s by %s; ", opad, b,
                "" + "Decreases energy weapon range",
                "" + "25%");
	label.setHighlight("" + "Decreases energy weapon range",
            "" + "25%");
	label.setHighlightColors(b, bad);
    }
}

