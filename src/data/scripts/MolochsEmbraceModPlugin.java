package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.AICoreAdminPlugin;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.listeners.ShowLootListener;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.procgen.themes.DerelictShipEntityPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.utils.molochs_settings;
import data.scripts.utils.molochs_OmegaCoreAdminPlugin;

import java.util.Random;

public class MolochsEmbraceModPlugin extends BaseModPlugin {
    
    private OmegaCoreDropListener omegaDropListener;
    
    @Override
    public void onApplicationLoad() {
        // Check for incompatible mods
        if (Global.getSettings().getModManager().isModEnabled("Planetace_AstralAscension")) {
            throw new RuntimeException(
                "Moloch's Embrace is incompatible with Astral Ascension.\n\n" +
                "Both mods modify omega core mechanics, which causes conflicts.\n" +
                "Please disable one of these mods to continue."
            );
        }
        
        // Register omega core drop listener if omega mechanics are enabled
        if (molochs_settings.isOmegaMechanicEnabled()) {
            omegaDropListener = new OmegaCoreDropListener();
            Global.getSector().getListenerManager().addListener(omegaDropListener);
        }
    }
    
    @Override
    public void onGameLoad(boolean newGame) {
        // Re-register listener on game load if needed
        if (molochs_settings.isOmegaMechanicEnabled()) {
            if (omegaDropListener == null) {
                omegaDropListener = new OmegaCoreDropListener();
            }
            if (!Global.getSector().getListenerManager().hasListenerOfClass(OmegaCoreDropListener.class)) {
                Global.getSector().getListenerManager().addListener(omegaDropListener);
            }
        } else {
            // Remove listener if omega mechanics disabled
            if (omegaDropListener != null && Global.getSector().getListenerManager().hasListenerOfClass(OmegaCoreDropListener.class)) {
                Global.getSector().getListenerManager().removeListener(omegaDropListener);
            }
        }
    }
    
    /**
     * Allow omega cores to be used as administrators
     * Only if omega mechanics are enabled
     */
    @Override
    public PluginPick<AICoreAdminPlugin> pickAICoreAdminPlugin(String commodityId) {
        if (molochs_settings.isOmegaMechanicEnabled() && "omega_core".equals(commodityId)) {
            // Use our custom omega core admin plugin
            return new PluginPick<AICoreAdminPlugin>(
                new molochs_OmegaCoreAdminPlugin(),
                CampaignPlugin.PickPriority.MOD_SET
            );
        }
        return null;
    }
    
    /**
     * Listener for adding omega core drops to Tesseract ship salvage
     */
    private static class OmegaCoreDropListener implements ShowLootListener {
        private static final String[] TESSERACT_VARIANTS = {
            "tesseract_Attack",
            "tesseract_Attack2",
            "tesseract_Strike",
            "tesseract_Disruptor",
            "tesseract_Shieldbreaker",
            "tesseract_Defense"
        };
        
        @Override
        public void reportAboutToShowLootToPlayer(CargoAPI loot, InteractionDialogAPI dialog) {
            if (loot == null || dialog == null) return;
            if (dialog.getInteractionTarget() == null) return;
            
            // Check if omega mechanics are enabled
            if (!molochs_settings.isOmegaMechanicEnabled()) {
                return;
            }
            
            SectorEntityToken target = dialog.getInteractionTarget();
            
            // Check if this is a derelict ship
            if (target.getCustomPlugin() instanceof DerelictShipEntityPlugin) {
                DerelictShipEntityPlugin plugin = (DerelictShipEntityPlugin) target.getCustomPlugin();
                if (plugin.getData() != null && plugin.getData().ship != null) {
                    String variantId = plugin.getData().ship.variantId;
                    String hullId = plugin.getData().ship.getVariant().getHullSpec().getHullId();
                    
                    // Check if this is a Tesseract variant
                    boolean isTesseract = false;
                    if (hullId != null && hullId.contains("tesseract")) {
                        isTesseract = true;
                    } else if (variantId != null) {
                        for (String tesseractVariant : TESSERACT_VARIANTS) {
                            if (variantId.contains(tesseractVariant)) {
                                isTesseract = true;
                                break;
                            }
                        }
                    }
                    
                    if (isTesseract) {
                        // Check if we've already added an omega core to this entity
                        if (!target.getMemoryWithoutUpdate().contains("$molochs_omegaCoreDropped")) {
                            // Check drop chance
                            float dropChance = molochs_settings.getOmegaCoreDropChance();
                            long seed = Misc.getSalvageSeed(target);
                            Random random = new Random(seed);
                            if (random.nextFloat() * 100.0f < dropChance) {
                                loot.addCommodity("omega_core", 1);
                                target.getMemoryWithoutUpdate().set("$molochs_omegaCoreDropped", true);
                            }
                        }
                    }
                }
            }
            
            // Also check ShipRecoverySpecial for recoverable ships
            if (Misc.getSalvageSpecial(target) instanceof ShipRecoverySpecial.ShipRecoverySpecialData) {
                ShipRecoverySpecial.ShipRecoverySpecialData data = (ShipRecoverySpecial.ShipRecoverySpecialData) Misc.getSalvageSpecial(target);
                if (data.ships != null && !data.ships.isEmpty()) {
                    for (ShipRecoverySpecial.PerShipData shipData : data.ships) {
                        if (shipData.variant != null) {
                            String variantId = shipData.variantId;
                            String hullId = shipData.variant.getHullSpec().getHullId();
                            
                            boolean isTesseract = false;
                            if (hullId != null && hullId.contains("tesseract")) {
                                isTesseract = true;
                            } else if (variantId != null) {
                                for (String tesseractVariant : TESSERACT_VARIANTS) {
                                    if (variantId.contains(tesseractVariant)) {
                                        isTesseract = true;
                                        break;
                                    }
                                }
                            }
                            
                            if (isTesseract) {
                                if (!target.getMemoryWithoutUpdate().contains("$molochs_omegaCoreDropped")) {
                                    float dropChance = molochs_settings.getOmegaCoreDropChance();
                                    long seed = Misc.getSalvageSeed(target);
                                    Random random = new Random(seed);
                                    if (random.nextFloat() * 100.0f < dropChance) {
                                        loot.addCommodity("omega_core", 1);
                                        target.getMemoryWithoutUpdate().set("$molochs_omegaCoreDropped", true);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}

