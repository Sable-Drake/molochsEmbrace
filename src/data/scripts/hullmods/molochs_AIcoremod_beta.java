package data.hullmods;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.utils.molochs_util_misc;

import java.awt.*;
import java.util.Map;

public class molochs_AIcoremod_beta extends BaseHullMod {

    public static final float basetimedilation = 0.06f;
    public static final float repmaxtimedilation = 0.06f;

    public static final float repgaincoefficient = 0.002f;
    public static final float decayreduction = 0.1f;

    public static final float DP_INCREASE_MULT = 0.2f;
    public static final float DP_INCREASE_MAX = 6f;

    // Constant extracted from ReactiveField
    public static final float maxfluxtohp = 0.1f;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        // Only apply effects if built-in (check via variant if available)
        boolean isBuiltIn = false;
        if (stats.getVariant() != null) {
            isBuiltIn = stats.getVariant().getPermaMods().contains("molochs_AIcoremod_beta") ||
                       stats.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_beta");
        }
        
        if (!isBuiltIn) {
            return; // Don't apply effects if not built-in
        }
        
        stats.getAutofireAimAccuracy().modifyMult(id,10f);

        if(stats.getSuppliesToRecover().base*DP_INCREASE_MULT<DP_INCREASE_MAX){
            stats.getSuppliesToRecover().modifyMult(id, 1f+DP_INCREASE_MULT);
            stats.getDynamic().getMod("deployment_points_mod").modifyMult(id, 1f+DP_INCREASE_MULT);
        }else{
            stats.getSuppliesToRecover().modifyFlat(id, DP_INCREASE_MAX);
            stats.getDynamic().getMod("deployment_points_mod").modifyFlat(id, DP_INCREASE_MAX);
        }
    }


    public void advanceInCampaign(FleetMemberAPI member, float amount) {
        if(Global.getCurrentState() != GameState.TITLE) {
            // Only process if built-in
            boolean isBuiltIn = member.getVariant().getPermaMods().contains("molochs_AIcoremod_beta") ||
                               member.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_beta");
            
            if (!isBuiltIn) {
                return; // Don't process if not built-in
            }
            
            Map<String, Object> data = Global.getSector().getPersistentData();
            if (!data.containsKey("aiintbeta_check_" + member.getId())) {
                data.put("aiintbeta_check_" + member.getId(), "_");
                if (member.getFleetData() != null && member.getFleetData().getFleet() != null && member.getFleetData().getFleet().equals(Global.getSector().getPlayerFleet())) {
                    molochs_util_misc.removePlayerCommodity("beta_core");
                }
            }
        }
    }

    public void advanceInCombat(ShipAPI ship, float amount){
        if (!ship.isAlive()) return;
        
        // Only apply effects if built-in
        boolean isBuiltIn = ship.getVariant().getPermaMods().contains("molochs_AIcoremod_beta") ||
                           ship.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_beta");
        
        if (!isBuiltIn) {
            return; // Don't apply effects if not built-in
        }
        
        MutableShipStatsAPI stats = ship.getMutableStats();

        float relationshiplevel = 0f;
        PersonAPI AI = null;

        if(Global.getCurrentState() != GameState.TITLE) {
            if (ship.getFleetMember().getFleetData() != null && (Global.getSector().getPlayerFleet().getMembersWithFightersCopy().contains(ship.getFleetMember()) || ship.getFleetMember().getFleetData().equals(Global.getSector().getPlayerFleet().getFleetData()))) {
                if (ship.getCaptain() != null) {
                    if (!ship.getCaptain().isDefault()) {
                        if (!Global.getSector().getPersistentData().containsKey(ship.getCaptain().getId() + " AIIntegrationPartner_beta")) {
                            AI = Misc.getAICoreOfficerPlugin("beta_core").createPerson("beta_core", "player", Misc.random);
                            AI.setName(OfficerManagerEvent.createOfficer(Global.getSector().getFaction("remnant"), 1, true).getName());
                            Global.getSector().getPersistentData().put(ship.getCaptain().getId() + " AIIntegrationPartner_beta", AI);

                            relationshiplevel = 0f;
                            Global.getSector().getPersistentData().put(ship.getCaptain().getId() + " AIIntegrationSyncRate_beta", relationshiplevel);

                        } else {
                            AI = (PersonAPI) Global.getSector().getPersistentData().get(ship.getCaptain().getId() + " AIIntegrationPartner_beta");
                            relationshiplevel = (float) Global.getSector().getPersistentData().get(ship.getCaptain().getId() + " AIIntegrationSyncRate_beta");

                            if (!ship.hasListenerOfClass(molochs_AIcoremod_gamma.aicorechatter.class)) {
                                ship.addListener(new molochs_AIcoremod_gamma.aicorechatter(ship, AI, relationshiplevel));
                            }
                        }
                        if (!ship.hasListenerOfClass(molochs_AIcoremod_gamma.aicoredamagecounter.class)) {
                            ship.addListener(new molochs_AIcoremod_gamma.aicoredamagecounter());
                        }

                    }
                }
            }
        }else{
            ship.getMutableStats().getFluxCapacity().modifyMult(spec.getId(),0.65f);
        }

        float tbonus = 1f + basetimedilation + (relationshiplevel * 0.01f) * repmaxtimedilation;
        ship.getMutableStats().getTimeMult().modifyMult(spec.getId(), tbonus);
        ship.getMutableStats().getPeakCRDuration().modifyMult(spec.getId(), tbonus);

        if(ship.getCustomData().containsKey("shieldhpmax") && (float)ship.getCustomData().get("shieldhpmax") < ship.getMaxFlux() * maxfluxtohp * (1f+decayreduction)){
            ship.setCustomData("shieldhpmax",ship.getMaxFlux() * maxfluxtohp * (1f+decayreduction));
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return false;
        
        boolean isBuiltIn = ship.getVariant().getPermaMods().contains("molochs_AIcoremod_beta") ||
                           ship.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_beta");
        
        if (!isBuiltIn) {
            return false;
        }
        
        boolean hasai = false;
        for(String hullmod:ship.getVariant().getHullMods()){
            if((hullmod.equals("molochs_AIcoremod_alpha") || hullmod.equals("molochs_AIcoremod_gamma") || hullmod.equals("molochs_AIcoremod_omega")) && !hullmod.equals("molochs_AIcoremod_beta")){
                hasai = true;
            }
        }
        
        for(String hullmod:ship.getVariant().getPermaMods()){
            if((hullmod.equals("molochs_AIcoremod_alpha") || hullmod.equals("molochs_AIcoremod_gamma") || hullmod.equals("molochs_AIcoremod_omega")) && !hullmod.equals("molochs_AIcoremod_beta")){
                hasai = true;
            }
        }
        
        return !hasai;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return null;
        
        boolean isBuiltIn = ship.getVariant().getPermaMods().contains("molochs_AIcoremod_beta") ||
                           ship.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_beta");
        
        if (!isBuiltIn) {
            return "This hullmod must be installed using the installation hullmod at a spaceport";
        }

        boolean hasai = false;
        for(String hullmod:ship.getVariant().getHullMods()){
            if((hullmod.equals("molochs_AIcoremod_alpha") || hullmod.equals("molochs_AIcoremod_gamma") || hullmod.equals("molochs_AIcoremod_omega")) && !hullmod.equals("molochs_AIcoremod_beta")){
                hasai = true;
            }
        }
        
        for(String hullmod:ship.getVariant().getPermaMods()){
            if((hullmod.equals("molochs_AIcoremod_alpha") || hullmod.equals("molochs_AIcoremod_gamma") || hullmod.equals("molochs_AIcoremod_omega")) && !hullmod.equals("molochs_AIcoremod_beta")){
                hasai = true;
            }
        }

        if (hasai){
            return "Only one integrated AI is able to be installed at once";
        }

        return null;
    }

    @Override
    public boolean canBeAddedOrRemovedNow(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
        if (ship.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_beta")) {
            return true;
        }
        return false;
    }

    @Override
    public String getCanNotBeInstalledNowReason(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
        return "This hullmod must be installed using the installation hullmod at a spaceport";
    }

    private final Color HL=Global.getSettings().getColor("hColor");
    private final Color TT = Global.getSettings().getColor("buttonBgDark");
    private final Color F = Global.getSettings().getColor("textFriendColor");
    private final Color E = Global.getSettings().getColor("textEnemyColor");
    private final Color def = Global.getSettings().getColor("standardTextColor");
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 10f;
        float padS = 2f;
        Color[] arr ={Misc.getHighlightColor(),F};
        Color[] arrB ={Misc.getHighlightColor(),F,F};
        Color[] arr2 ={Misc.getHighlightColor(),E};
        Color bad = Misc.getNegativeHighlightColor();
        
        // Check if built-in
        boolean isBuiltIn = false;
        if (ship != null) {
            isBuiltIn = ship.getVariant().getPermaMods().contains("molochs_AIcoremod_beta") ||
                       ship.getVariant().getSModdedBuiltIns().contains("molochs_AIcoremod_beta");
        }
        
        // Show inactive warning if not built-in
        if (ship != null && !isBuiltIn) {
            tooltip.addPara("", pad);
            tooltip.addPara("%s This AI integration is not yet fully integrated. Install it using the installation hullmod at a spaceport to activate its effects.", pad, bad, "INACTIVE:");
            return;
        }

        if(ship == null) return;

        float relationshiplevel = 0f;
        PersonAPI AI = null;

        if(Global.getCurrentState() != GameState.TITLE) {
            if (ship.getOriginalOwner() == -1) {
                if (ship.getCaptain() != null) {
                    if (!ship.getCaptain().isDefault()) {
                        if (!Global.getSector().getPersistentData().containsKey(ship.getCaptain().getId() + " AIIntegrationPartner_beta")) {
                            AI = Misc.getAICoreOfficerPlugin("beta_core").createPerson("beta_core", "player", Misc.random);
                            AI.setName(OfficerManagerEvent.createOfficer(Global.getSector().getFaction("remnant"), 1, true).getName());
                            Global.getSector().getPersistentData().put(ship.getCaptain().getId() + " AIIntegrationPartner_beta", AI);

                            relationshiplevel = 0f;
                            Global.getSector().getPersistentData().put(ship.getCaptain().getId() + " AIIntegrationSyncRate_beta", relationshiplevel);

                        } else {
                            AI = (PersonAPI) Global.getSector().getPersistentData().get(ship.getCaptain().getId() + " AIIntegrationPartner_beta");
                            relationshiplevel = (float) Global.getSector().getPersistentData().get(ship.getCaptain().getId() + " AIIntegrationSyncRate_beta");
                        }
                    }
                }
            }


            float tbonus = (basetimedilation + (relationshiplevel * 0.01f) * repmaxtimedilation) * 100f;
            float rbonus = ((relationshiplevel * 0.01f) * repmaxtimedilation) * 100f;

            float dbonus = decayreduction * 100f;

            tooltip.addPara("%s " + "Base time dilation and PPT increased by %s.", pad, arrB, "-", (int) tbonus + "%");
            tooltip.addPara("%s " + "Deployment cost increased by %s", pad, bad, "-", (20f) + "%");

            if (ship.getVariant().hasHullMod("molochs_SEswitcher")) {
                tooltip.addPara("%s " + "Epta Shield Engineering power decay reduced by %s.", padS, arrB, "-", (int) dbonus + "%");
            }
            if (ship.getVariant().hasHullMod("molochs_reactive7s")) {
                tooltip.addPara("%s " + "Epta phase microshield shield capacity increased by %s.", padS, arrB, "-", (int) dbonus + "%");
            }

            if (ship.getCaptain().isDefault()) {
                if (ship.getVariant().hasHullMod("automated")) {
                    tooltip.addSectionHeading("=== AI Network Stats ===", Alignment.MID, 10);

                    tooltip.addPara(
                            "This ship has no AI persona captain which can serve as the base AI for its AI network. Assign an AI officer to the ship to create an AI network."
                            , 10
                            , Misc.getHighlightColor()
                    );
                } else {
                    tooltip.addSectionHeading("=== Human AI Bond Stats ===", Alignment.MID, 10);

                    tooltip.addPara(
                            "This ship has no captain with which its AI persona may form a bond. Assign an officer to the ship to create a Human AI duo."
                            , 10
                            , Misc.getHighlightColor()
                    );
                }
            } else {
                String groupname = "Human AI Duo";
                if (ship.getVariant().hasHullMod("automated")) {
                    tooltip.addSectionHeading("=== AI Network Stats ===", Alignment.MID, 10);

                    tooltip.addPara(
                            "This ship's AI persona captain " +
                                    ship.getCaptain().getNameString()
                                    + " and the ship's integrated AI core persona, "
                                    + AI.getName().getFullName()
                                    + " have formed an AI network with eachother. If this AI persona captain is assigned ship with an integrated AI core then this persona will be transferred over to it and the network with be recreated."
                            , 10
                            , HL
                            , ship.getCaptain().getNameString()
                            , AI.getName().getFullName()
                    );
                    groupname = "AI Network";
                } else {
                    tooltip.addSectionHeading("=== Human AI Bond Stats ===", Alignment.MID, 10);

                    tooltip.addPara(
                            "A strong bond has formed between officer " +
                                    ship.getCaptain().getNameString()
                                    + " and the ship's integrated AI core persona, "
                                    + AI.getName().getFullName()
                                    + ". If this officer is assigned to another ship with an integrated AI core then this persona will be transferred over to it."
                            , 10
                            , HL
                            , ship.getCaptain().getNameString()
                            , AI.getName().getFullName()
                    );
                }

                tooltip.addPara("This " + groupname + "'s Sync Rating is at %s, increasing this ship's time dilation and PPT by %s.", pad, F, (int) relationshiplevel + "%", (int) rbonus + "%");

                tooltip.addSectionHeading(AI.getName().getFullName(), Alignment.MID, 10);
                tooltip.beginImageWithText(AI.getPortraitSprite(), 88);
                tooltip.addImageWithText(8);
                tooltip.addRelationshipBar(relationshiplevel / 100, 5f);
            }
        }
    }
}

