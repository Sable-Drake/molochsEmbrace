package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class molochs_polarized_shields extends BaseHullMod {


    public static final float FLUX_MANAGEMENT = 35f;
    public static final float FLUX_MANAGEMENT_V2 = 25f;
    public static final float FLUX_MANAGEMENT_V3 = 75f;
    public static final float FLUX_MANAGEMENT_V4 = 35f;
    public static final float SHIELD_PENALTIES = 0.1f;
    public static final float DEGRADE_INCREASE_PERCENT = 125f;
        
   public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
    
       stats.getShieldDamageTakenMult().modifyMult(id,1f + SHIELD_PENALTIES);
   }
	public void advanceInCombat(ShipAPI ship, float amount){
		if (!ship.isAlive()) return;
		MutableShipStatsAPI stats = ship.getMutableStats();
        stats.getFluxDissipation().modifyMult(spec.getId(), 1 + FLUX_MANAGEMENT_V2 * 0.01f * ship.getHardFluxLevel());
     

		if (ship == Global.getCombatEngine().getPlayerShip())
			Global.getCombatEngine().maintainStatusForPlayerShip("molochs_polarized_shields", "graphics/icons/hullsys/damper_field.png", "polarized shield improved flux grid", Math.round(FLUX_MANAGEMENT_V4 * ship.getHardFluxLevel()) + "%", false);
	}

        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
		
            return ship != null && ship.getShield() != null;
                    

	}
        
        public String getUnapplicableReason(ShipAPI ship) {
		
		if (ship.getHullSpec().getDefenseType() == ShieldAPI.ShieldType.PHASE) {
                return "Phase ships have no shields";
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

        LabelAPI label = tooltip.addPara("An experimental %s hullmod that applies polarized armor techniques to shield generators. %s as %s. As a downside, reduces %s.", opad, t,
        "" + "Epta",
        "" + "Increases flux dissipation",
        "" + "hardflux rises",
        "" + "shield efficiency");
		label.setHighlight(
        "" + "Epta",
        "" + "Increases flux dissipation and max speed",
        "" + "hardflux rises",
        "" + "shield efficiency");
		label.setHighlightColors(et, e, b, bad);


        tooltip.addSectionHeading("Modifies:", Alignment.MID, opad);
        
        label = tooltip.addPara( "increases %s up to %s, in proportion to %s; ", opad, t,

        "" + "flux dissipation",
        "" + "25%",
        "" + "hard flux level");
	label.setHighlight(
        "" + "flux dissipation",
            "" + "25%",
        "" + "hard flux level");
	label.setHighlightColors(e, b, e);
        
        label = tooltip.addPara( "%s %s; ", opad, b,
        "" + "-10%",
        "" + "Shield Efficiency");
	label.setHighlight("" + "-10%",
        "" + "Shield Efficiency");
	label.setHighlightColors(bad, e);
        
        
        
        tooltip.addSectionHeading("Suggestions", Alignment.MID, opad);

        label = tooltip.addPara("This configuration improves %s as %s from projectile damage taken. Can significantly increase effective firepower and flux recovering for a daring captain. Not recommended for bad grade shields, as it reduces %s.", opad, l,
        "" + "flux dissipation",
        
        "" + "hard flux rises",

        "" + "shield efficiency");
	label.setHighlight(
        "" + "flux dissipation",
        
        "" + "hard flux rises",

        "" + "shield efficiency");
	label.setHighlightColors(e, e, bad);
    }
}

