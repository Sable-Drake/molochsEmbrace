package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import data.scripts.utils.molochs_util_misc;

import java.util.Map;

public class molochs_AIcoreRefunder_gammacore extends BaseHullMod {

    @Override
    public void advanceInCampaign(FleetMemberAPI member, float amount) {

        boolean hasAICoreHullmod = false;

        if (member.getVariant().hasHullMod("molochs_AIcoremod_gamma")) {
            hasAICoreHullmod = true;
        }

        if (!hasAICoreHullmod) {
            Map<String, Object> data = Global.getSector().getPersistentData();
            if (data.containsKey("aiintgamma_check_" + member.getId())) {
                data.remove("aiintgamma_check_" + member.getId());
            }
            if(member.getFleetData().getFleet().equals(Global.getSector().getPlayerFleet())){
                molochs_util_misc.addPlayerCommodity("gamma_core", 1);
            }
            member.getVariant().getHullMods().remove("molochs_AIcoreRefunder_gammacore");
        }
    }
}

