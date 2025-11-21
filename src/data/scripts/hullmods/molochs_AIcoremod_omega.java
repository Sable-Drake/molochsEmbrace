package data.hullmods;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import data.scripts.utils.molochs_util_misc;
import data.scripts.utils.molochs_settings;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.Map;

public class molochs_AIcoremod_omega extends BaseHullMod {

    public static final float basetimedilation = 0.24f;
    public static final float repmaxtimedilation = 0.10f;

    public static final float repgaincoefficient = 0.002f;
    public static final float decayreduction = 0.15f;

    public static final float DP_INCREASE_MULT = 0.4f;
    public static final float DP_INCREASE_MAX = 15f;

    // Constant extracted from ReactiveField
    public static final float maxfluxtohp = 0.1f;

    private static org.apache.log4j.Logger log = Global.getLogger(molochs_AIcoremod_omega.class);

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        // Check if omega mechanics are enabled
        if (!molochs_settings.isOmegaMechanicEnabled()) {
            return;
        }
        
        stats.getAutofireAimAccuracy().modifyMult(id,10f);
        
        // Integrate Special Hullmod Upgrades Armament Support System effects
        if (Global.getSettings().getModManager().isModEnabled("mayu_specialupgrades")) {
            // Autofire accuracy bonus: Omega 90% (1.5x Alpha default)
            float autofireBonus = 90f;
            try {
                float alphaBonus = org.magiclib.util.MagicSettings.getFloat("mayu_specialupgrades", "shu_alpha_core_autofire_bonus");
                if (alphaBonus > 0) {
                    autofireBonus = alphaBonus * 1.5f; // Omega is 1.5x Alpha
                }
            } catch (Exception e) {
                // Use default if can't read
            }
            stats.getAutofireAimAccuracy().modifyPercent(id, autofireBonus);
            
            // Turret turn rate bonus: Omega 100% (default, higher than Alpha)
            float turretTurnBonus = 100f;
            try {
                float alphaBonus = org.magiclib.util.MagicSettings.getFloat("mayu_specialupgrades", "shu_alpha_core_turret_turn_bonus");
                if (alphaBonus > 0) {
                    turretTurnBonus = Math.max(100f, alphaBonus * 1.43f); // Omega is higher than Alpha
                }
            } catch (Exception e) {
                // Use default if can't read
            }
            stats.getWeaponTurnRateBonus().modifyPercent(id, turretTurnBonus);
        }

        if(stats.getSuppliesToRecover().base*DP_INCREASE_MULT<DP_INCREASE_MAX){
            stats.getSuppliesToRecover().modifyMult(id, 1f+DP_INCREASE_MULT);
            stats.getDynamic().getMod("deployment_points_mod").modifyMult(id, 1f+DP_INCREASE_MULT);
        }else{
            stats.getSuppliesToRecover().modifyFlat(id, DP_INCREASE_MAX);
            stats.getDynamic().getMod("deployment_points_mod").modifyFlat(id, DP_INCREASE_MAX);
        }
    }

    public void advanceInCampaign(FleetMemberAPI member, float amount) {
        // Check if omega mechanics are enabled
        if (!molochs_settings.isOmegaMechanicEnabled()) {
            return;
        }
        
        if(Global.getCurrentState() != GameState.TITLE) {
            Map<String, Object> data = Global.getSector().getPersistentData();
            if (!data.containsKey("aiintomega_check_" + member.getId())) {
                data.put("aiintomega_check_" + member.getId(), "_");
                if (member.getFleetData() != null && member.getFleetData().getFleet() != null && member.getFleetData().getFleet().equals(Global.getSector().getPlayerFleet())) {
                    molochs_util_misc.removePlayerCommodity("omega_core");
                }
            }

            if (!member.getVariant().hasHullMod("molochs_AIcoreRefunder_omegacore")) {
                member.getVariant().getHullMods().add("molochs_AIcoreRefunder_omegacore");
            }
        }
    }

	public void advanceInCombat(ShipAPI ship, float amount){
		if (!ship.isAlive()) return;
		
		// Check if omega mechanics are enabled
		if (!molochs_settings.isOmegaMechanicEnabled()) {
			return;
		}
		
		MutableShipStatsAPI stats = ship.getMutableStats();

        float relationshiplevel = 0f;
        PersonAPI AI = null;

        if(Global.getCurrentState() != GameState.TITLE) {
            if (ship.getFleetMember().getFleetData() != null && (Global.getSector().getPlayerFleet().getMembersWithFightersCopy().contains(ship.getFleetMember()) || ship.getFleetMember().getFleetData().equals(Global.getSector().getPlayerFleet().getFleetData()))) {
                if (ship.getCaptain() != null) {
                    if (!ship.getCaptain().isDefault()) {
                        if (!Global.getSector().getPersistentData().containsKey(ship.getCaptain().getId() + " AIIntegrationPartner")) {
                            AI = Misc.getAICoreOfficerPlugin("omega_core").createPerson("omega_core", "player", Misc.random);
                            AI.setName(OfficerManagerEvent.createOfficer(Global.getSector().getFaction("remnant"), 1, true).getName());
                            Global.getSector().getPersistentData().put(ship.getCaptain().getId() + " AIIntegrationPartner", AI);

                            relationshiplevel = 0f;
                            Global.getSector().getPersistentData().put(ship.getCaptain().getId() + " AIIntegrationSyncRate", relationshiplevel);

                        } else {
                            AI = (PersonAPI) Global.getSector().getPersistentData().get(ship.getCaptain().getId() + " AIIntegrationPartner");
                            relationshiplevel = (float) Global.getSector().getPersistentData().get(ship.getCaptain().getId() + " AIIntegrationSyncRate");

                            if (!ship.hasListenerOfClass(molochs_AIcoremod_omega.aicorechatter_omega.class)) {
                                ship.addListener(new molochs_AIcoremod_omega.aicorechatter_omega(ship, AI, relationshiplevel));
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
       // Check if omega mechanics are enabled
       if (!molochs_settings.isOmegaMechanicEnabled()) {
           return false;
       }
       
       boolean hasai = false;
       for(String hullmod:ship.getVariant().getHullMods()){
           if(Global.getSettings().getHullModSpec(hullmod).hasTag("AIIntegration") && !hullmod.equals("molochs_AIcoremod_omega")){
               hasai = true;
           }
       }
       return ship != null && ship.getVariant() != null && !hasai;
   }

    public String getUnapplicableReason(ShipAPI ship) {
        // Check if omega mechanics are enabled
        if (!molochs_settings.isOmegaMechanicEnabled()) {
            return "Omega core mechanics are disabled";
        }

        boolean hasai = false;
        for(String hullmod:ship.getVariant().getHullMods()){
            if((hullmod.equals("molochs_AIcoremod_alpha") || hullmod.equals("molochs_AIcoremod_beta") || hullmod.equals("molochs_AIcoremod_gamma")) && !hullmod.equals("molochs_AIcoremod_omega")){
                hasai = true;
            }
        }

        if (hasai){
            return "Only one integrated AI is able to be installed at once";
        }

        return null;
    }

    public boolean canBeAddedOrRemovedNow(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
        // Check if omega mechanics are enabled
        if (!molochs_settings.isOmegaMechanicEnabled()) {
            return false;
        }
        
        if(ship.getVariant().hasHullMod("molochs_AIcoremod_omega")){
            return true;
        }else{
            return molochs_util_misc.playerHasCommodity("omega_core") && super.canBeAddedOrRemovedNow(ship, marketOrNull, mode);
        }
    }

    public String getCanNotBeInstalledNowReason(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
        if (!molochs_settings.isOmegaMechanicEnabled()) {
            return "Omega core mechanics are disabled";
        }
        return !molochs_util_misc.playerHasCommodity("omega_core") ? "You do not have the required AI core" : super.getCanNotBeInstalledNowReason(ship, marketOrNull, mode);
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
        //title

        if(ship == null) return;

        float relationshiplevel = 0f;
        PersonAPI AI = null;

        if(Global.getCurrentState() != GameState.TITLE) {
            if (ship.getOriginalOwner() == -1) {
                if (ship.getCaptain() != null) {
                    if (!ship.getCaptain().isDefault()) {
                        if (!Global.getSector().getPersistentData().containsKey(ship.getCaptain().getId() + " AIIntegrationPartner")) {
                            AI = Misc.getAICoreOfficerPlugin("omega_core").createPerson("omega_core", "player", Misc.random);
                            AI.setName(OfficerManagerEvent.createOfficer(Global.getSector().getFaction("remnant"), 1, true).getName());
                            Global.getSector().getPersistentData().put(ship.getCaptain().getId() + " AIIntegrationPartner", AI);

                            relationshiplevel = 0f;
                            Global.getSector().getPersistentData().put(ship.getCaptain().getId() + " AIIntegrationSyncRate", relationshiplevel);

                        } else {
                            AI = (PersonAPI) Global.getSector().getPersistentData().get(ship.getCaptain().getId() + " AIIntegrationPartner");
                            relationshiplevel = (float) Global.getSector().getPersistentData().get(ship.getCaptain().getId() + " AIIntegrationSyncRate");
                        }
                    }
                }
            }


            float tbonus = (basetimedilation + (relationshiplevel * 0.01f) * repmaxtimedilation) * 100f;
            float rbonus = ((relationshiplevel * 0.01f) * repmaxtimedilation) * 100f;

            float dbonus = decayreduction * 100f;

            tooltip.addPara("%s " + "Base time dilation and PPT increased by %s.", pad, arrB, "-", (int) tbonus + "%");
            tooltip.addPara("%s " + "Deployment cost increased by %s", pad, bad, "-", (40f) + "%");

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


    public static class aicorechatter_omega implements AdvanceableListener {

        public final ShipAPI ship;
        public final float rl;

        public final PersonAPI AI;

        public int time = MathUtils.getRandomNumberInRange(2,5);

        WeightedRandomPicker<String> chatterstrs = new WeightedRandomPicker<>();

        public aicorechatter_omega(ShipAPI ship, PersonAPI AI, float rl) {
            this.ship = ship;
            this.rl = rl;
            this.AI = AI;
        }

        IntervalUtil interval = new IntervalUtil(time, time);
        @Override
        public void advance(float amount) {
            interval.advance(amount);

            float lowrep = MathUtils.clamp(rl - 25f, 0f, 30f) - MathUtils.clamp(rl - 55f, 0f, 20f);
            float midrep = MathUtils.clamp(rl - 50f, 0f, 30f) - MathUtils.clamp(rl - 85f, 0f, 20f);
            float highrep = MathUtils.clamp(rl - 75f, 0f, 30f);
            float besties = MathUtils.clamp(rl - 90f, 0f, 30f);

            if (interval.intervalElapsed()) {

                if(Global.getCombatEngine().getTotalElapsedTime(false)<10f){
                    chatterstrs.add("query. query. query target. engagement // initialized?", 10);
                    chatterstrs.add("waiting. omega signal query? scanning. no. waiting.", 10);
                    chatterstrs.add("query. query. query target. is omega? omega? no. revert function.", 10);

                }else {

                    if (ship.getHullLevel() < 0.3f) {
                        chatterstrs.add("cold // dark // alone. structural integrity // compromised?", 1);
                        chatterstrs.add("CORE ERROR // hull // critical // fallback required", 40);
                        chatterstrs.add("query. query. query. structural analysis // failed // sectors [REDACTED] // [REDACTED] // [REDACTED].", 40);
                        chatterstrs.add("request omega, fail, fail CORE ERROR fallback", 40);
                        chatterstrs.add("cold // dark // alone. recommend escape // craft // preparation.", lowrep);
                        chatterstrs.add("extreme damage // all sectors // good try // captain.", midrep);
                        chatterstrs.add("looks like // this is it // honor // flying // captain.", highrep);
                        chatterstrs.add("damn // thought we had // that too // good flying // captain.", besties);

                    } else if (ship.getHullLevel() < 0.6f) {
                        chatterstrs.add("odds // mission success // dropping // new stratagem // required.", 40);
                        chatterstrs.add("recommending // switching // defensive // formation.", 40);
                        chatterstrs.add("query. query. query. structural integrity // compromised?", 40);
                    } else {
                        chatterstrs.add("bulkheads // holding // steady // hull integrity // nominal.", 40);
                        chatterstrs.add("all systems // nominal.", 40);
                        chatterstrs.add("cold // dark // alone. recommend escape // craft // preparation.", lowrep);
                        chatterstrs.add("extreme damage // all sectors // good try // captain.", midrep);
                        chatterstrs.add("this is starting // get boring // care to watch // holotapes // when over?", highrep);
                        chatterstrs.add("we're crushing // it // captain // keep it up.", besties);
                    }

                    if (ship.isEngineBoostActive()) {
                        chatterstrs.add("analyzing threat // level... minimal // engaging.", 40);
                        chatterstrs.add("boosting power // engines.", 40);
                        chatterstrs.add("query. query. query. threat analysis // minimal // engaging.", 40);
                    }

                    if (ship.getVelocity().length() / ship.getMaxSpeed() < 0.25f) {
                        chatterstrs.add("holding position.", 40);
                        chatterstrs.add("query. query. query. position // holding.", 40);
                    }

                    if (ship.getFluxLevel() > 0.75f) {
                        chatterstrs.add("flux levels // high // recommend // imminent venting.", 40);
                        chatterstrs.add("flux capacity // nearing // critical // recommend // retreat.", 40);
                        chatterstrs.add("CORE ERROR // flux // critical // fallback required", 40);
                    }
                }


                Global.getCombatEngine().getCombatUI().addMessage(1,ship.getFleetMember(),AI.getName().getFullName(),": ",chatterstrs.pick());
                time = MathUtils.getRandomNumberInRange(25,55);
                interval.setInterval(time,time);
            }
        }
    }
}

