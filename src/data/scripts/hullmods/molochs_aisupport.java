package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class molochs_aisupport extends BaseHullMod {
    
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if (!stats.getVariant().hasHullMod("molochs_AIcoremod_gamma") && !stats.getVariant().hasHullMod("molochs_AIcoremod_beta") && !stats.getVariant().hasHullMod("molochs_AIcoremod_alpha") && !stats.getVariant().hasHullMod("molochs_AIcoremod_ananke")){
            stats.getFluxCapacity().modifyMult(id,0f);
        }else{
            stats.getDynamic().getMod(Stats.FLEET_GROUND_SUPPORT).modifyFlat(id, 350f);
        }
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


        LabelAPI label = tooltip.addPara("A salvaged Tri-Tachyon relic from before the AI Wars. The pilot's mind is directly uploaded to a %s with their AI partner which vastly improves design efficiency. Grants %s, however, if an %s is not installed then the %s.", opad, t,
                "" + "VR Simulation",
                "" + "350 ground support bonus",
                "" + "AI Integration",
                "" + "simulators and the ship's flux grid cannot function");
        label.setHighlight(
                "" + "VR Simulation",
                "" + "350 ground support bonus",
                "" + "AI Integration",
                "" + "simulators and the ship's flux grid cannot function");
        label.setHighlightColors(e, b, b, bad);
    }
}

