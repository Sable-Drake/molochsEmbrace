package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.PlayerFleetPersonnelTracker;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;

public class molochs_marinesims extends BaseHullMod {

    private static final float TIMEDILATION = 12f;
    private static final float PROPORTION_XP_GAIN = 1/500f;
    private static final float MAX_PROPORTION_XP_GAIN = 1/200f;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getFluxCapacity().modifyMult(id, 0.65f);
    }

    public void advanceInCampaign(FleetMemberAPI member, float amount) {
        if(member.getFleetData() != null && member.getFleetData().getFleet() != null && member.getFleetData().getFleet().equals(Global.getSector().getPlayerFleet())) {
            if(!Global.getSector().getPersistentData().containsKey(member.getId()+"marinetimecheck")) Global.getSector().getPersistentData().put(member.getId()+"marinetimecheck", Global.getSector().getClock().getTimestamp());
            float timeelapsed=Global.getSector().getClock().getElapsedDaysSince((long)Global.getSector().getPersistentData().get(member.getId()+"marinetimecheck"));
            if(timeelapsed>=1f && timeelapsed<=2f) {
                float xp = MathUtils.clamp(member.getMaxCrew()*PROPORTION_XP_GAIN,0f,Global.getSector().getPlayerFleet().getCargo().getMarines()*MAX_PROPORTION_XP_GAIN);
                PlayerFleetPersonnelTracker.getInstance().update();
                PlayerFleetPersonnelTracker.getInstance().getMarineData().addXP(xp);
                Global.getSector().getPersistentData().put(member.getId()+"marinetimecheck", Global.getSector().getClock().getTimestamp());
            }else if(timeelapsed>2f){
                Global.getSector().getPersistentData().put(member.getId()+"marinetimecheck", Global.getSector().getClock().getTimestamp());
            }
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


        LabelAPI label = tooltip.addPara("Dozens of %s are installed to allow marines to train without participating in actual combat. Daily usage of the simulators will improve marine effectiveness over time without any chance of casualties, but the flux requirements to run the simulators effectively reduces ship flux capacity by %s. Grants marine XP equal to %s of maximum crew capacity per day. Marine XP gained per day is limited to a daily maximum of %s of the marines currently in the fleet", opad, t,
                "" + "Full Dive VR Simulator Rigs",
                "" + "35%",
                "" + "2%",
                "" + "5%");
        label.setHighlight(
                "" + "Full Dive VR Simulator Rigs",
                "" + "35%",
                "" + "2%",
                "" + "5%");
        label.setHighlightColors(b, bad, e, e);
    }
}

