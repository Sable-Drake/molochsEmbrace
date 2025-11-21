package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import data.scripts.utils.molochs_util_misc;

import java.util.Map;

public class molochs_AIcoreRefunder_betacore extends BaseHullMod {

    @Override
    public void advanceInCampaign(FleetMemberAPI member, float amount) {

        boolean hasAICoreHullmod = false;

        if (member.getVariant().hasHullMod("molochs_AIcoremod_beta")) {
            hasAICoreHullmod = true;
        }

        if (!hasAICoreHullmod) {
            Map<String, Object> data = Global.getSector().getPersistentData();
            if (data.containsKey("aiintbeta_check_" + member.getId())) {
                data.remove("aiintbeta_check_" + member.getId());
            }
            if(member.getFleetData().getFleet().equals(Global.getSector().getPlayerFleet())) {
                molochs_util_misc.addPlayerCommodity("beta_core", 1);
            }
            member.getVariant().getHullMods().remove("molochs_AIcoreRefunder_betacore");
        }
    }
}

