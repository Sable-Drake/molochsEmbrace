package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class molochs_forced_shield_extender extends BaseHullMod {

	public static final float SHIELD_ARC = 30f;
        public static final float SHIELD_SPEED = 1.5f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getShieldArcBonus().modifyFlat(id, SHIELD_ARC);
                stats.getShieldUnfoldRateMult().modifyMult(id, SHIELD_SPEED);
                stats.getShieldTurnRateMult().modifyMult(id, SHIELD_SPEED);
	}
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
		
            return ship != null && ship.getShield() != null;	
                    

	}
        
        public String getUnapplicableReason(ShipAPI ship) {
		
		if (ship.getHullSpec().getDefenseType() == ShieldAPI.ShieldType.PHASE) {
                return "Ship has no shields";
                }
                
                if (ship.getHullSpec().getDefenseType() == ShieldAPI.ShieldType.NONE) {
                return "Ship has no shields";
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



        LabelAPI label = tooltip.addPara("A sophisticated combination of %s and %s, increasing both %s.", opad, t,
        "" + "Accelerated Shields",
        "" + "Extended Shields",
        "" + "shield unfold speed and extension");
	label.setHighlight(
        "" + "Accelerated Shields",
        "" + "Extended Shields",
        "" + "shield unfold speed and extension");
	label.setHighlightColors(b, b, e);


        tooltip.addSectionHeading("Modifies:", Alignment.MID, opad);
        
        label = tooltip.addPara( "%s of %s; ", opad, t, 
        "" + "+30 Degrees",
        "" + "Shield Unfold Arc");
	label.setHighlight("" + "+30 Degrees",
        "" + "Shield Unfold Arc");
	label.setHighlightColors(b, e);
        
        label = tooltip.addPara( "%s of Shield Unfold and Turn Speed; ", opad, b, 
        "" + "50%");
	label.setHighlight("" + "50%");
	label.setHighlightColors(b);
           

        tooltip.addSectionHeading("Suggestions", Alignment.MID, opad);


        label = tooltip.addPara("This hullmod is designed to fix %s on vulnerable ships such as %s.", opad, l, 
        "" + "poor shield coverages",
        
        "" + "Conquests");
	label.setHighlight(
        "" + "poor shield coverages",
        
        "" + "Conquests");
	label.setHighlightColors(bad, md);
        
        tooltip.addSectionHeading("Interactions with other hullmods:", Alignment.MID, opad);

        label = tooltip.addPara( "%s with %s; ", opad, b, 
        "" + "Stacks",
        "" + "Accelerated Shields");
	label.setHighlight("" + "Stacks",
        "" + "Accelerated Shields");
	label.setHighlightColors(m, b);
        
        label = tooltip.addPara( "%s with %s; ", opad, b, 
        "" + "Stacks",
        "" + "Extended Shields");
	label.setHighlight("" + "Stacks",
        "" + "Extended Shields");
	label.setHighlightColors(m, b);
        
       
    }
}

