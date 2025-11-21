package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class molochs_er_device extends BaseHullMod
{   
    public static float BONUS_MAX_1;


    @Override
    public void applyEffectsBeforeShipCreation(final ShipAPI.HullSize hullSize, final MutableShipStatsAPI stats, final String id) {


    }
    
    @Override
    public void applyEffectsAfterShipCreation(final ShipAPI ship, final String id) {
    }

    public void advanceInCombat(ShipAPI ship, float amount){

        if(ship.getVelocity().length()<ship.getMaxSpeed()*0.15f){
            ship.getMutableStats().getBallisticWeaponRangeBonus().modifyFlat(spec.getId(), 150);
            ship.getMutableStats().getEnergyWeaponRangeBonus().modifyFlat(spec.getId(), 150);
        } else {
            ship.getMutableStats().getBallisticWeaponRangeBonus().unmodifyFlat(spec.getId());
            ship.getMutableStats().getEnergyWeaponRangeBonus().unmodifyFlat(spec.getId());
        }
    }
    
    @Override
	public boolean isApplicableToShip(ShipAPI ship) {
        return !ship.getVariant().getHullMods().contains("supercomputer") && !ship.getVariant().getHullMods().contains("advancedcore");

	}
	
	
	public String getUnapplicableReason(ShipAPI ship) {
		
		
		if (ship.getVariant().getHullMods().contains("supercomputer")) {
			return "The ER targeting device cannot further boost the range of weapons augmented by station-grade supercomputers.";
		}

		if (ship.getVariant().getHullMods().contains("advancedcore")) {
			return "The ER targeting device cannot further boost the range of weapons augmented by an Advanced Targeting Core.";
        }

        if (ship.getVariant().getHullMods().contains("molochs_energy_scatter")) {
            return "This ship already has a hullmod that affects energy weapon range.";
		}
		
		return null;
	}
    
    public static class ERdevice implements WeaponBaseRangeModifier
    {
        public float small;
        public float medium;
        public float max;
        
        public ERdevice(final float small, final float medium, final float max) {
            this.small = small;
            this.medium = medium;
            this.max = max;
        }
        
        public float getWeaponBaseRangePercentMod(final ShipAPI ship, final WeaponAPI weapon) {
            return 0.0f;
        }
        
        public float getWeaponBaseRangeMultMod(final ShipAPI ship, final WeaponAPI weapon) {
            return 1.0f;
        }
        
        public float getWeaponBaseRangeFlatMod(final ShipAPI ship, final WeaponAPI weapon) {
            if (weapon.getSlot().getWeaponType() != WeaponAPI.WeaponType.ENERGY) {
				if (!(weapon.getSpec().getMountType() == WeaponAPI.WeaponType.ENERGY )) {
					return 0f;
				}
			}
            
            float bonus = 0.0f;
            if (weapon.getSize() == WeaponAPI.WeaponSize.SMALL) {
                bonus = this.small;
            }
            else if (weapon.getSize() == WeaponAPI.WeaponSize.MEDIUM) {
                bonus = this.small;
            }
            else if (weapon.getSize() == WeaponAPI.WeaponSize.LARGE) {
                bonus = this.small;
            }
            
            if (bonus == 0.0f) {
                return 0.0f;
            }
            final float base = weapon.getSpec().getMaxRange();
            if (base + bonus > this.max) {
                bonus = this.max - base;
            }
            if (bonus < 0.0f) {
                bonus = 0.0f;
            }
            return bonus;
        }
    }
    
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        return null;
    }

    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return false;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color m = Misc.getMissileMountColor();
        Color e = Misc.getEnergyMountColor();
        Color b = Misc.getHighlightColor();
        Color t = Misc.getDesignTypeColor("Takeshido");
        Color l = Misc.getDesignTypeColor("Low Tech");
        Color md = Misc.getDesignTypeColor("Midline");
        Color h = Misc.getDesignTypeColor("High Tech");
        Color et = Misc.getDesignTypeColor("Epta Tech");
        Color p = Misc.getDesignTypeColor("molochs phase");
        Color w = Misc.getDesignTypeColor("molochs white");
        Color prt = Misc.getDesignTypeColor("Pirate");
        
        Color bad = Misc.getNegativeHighlightColor();



        LabelAPI label = tooltip.addPara("A targetting sensor which increases the %s as long as the ship is travelling below %s of its maximum speed.", opad, t,
        "" + "base range of all weapons by 150",
        "" + "15%");
	label.setHighlight(
        "" + "base range of all weapons by 150",
        "" + "15%");
	label.setHighlightColors(b, bad);

    }
}

