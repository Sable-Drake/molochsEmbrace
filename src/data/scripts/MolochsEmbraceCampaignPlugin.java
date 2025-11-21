package data.scripts;

import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.AICoreAdminPlugin;
import com.fs.starfarer.api.campaign.BaseCampaignPlugin;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import data.scripts.utils.molochs_settings;
import data.scripts.utils.molochs_OmegaCoreAdminPlugin;

/**
 * Campaign plugin for Moloch's Embrace mod.
 * Handles AI core admin plugin selection.
 */
public class MolochsEmbraceCampaignPlugin extends BaseCampaignPlugin {
    
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
    
    @Override
    public String getId() {
        return "MolochsEmbraceCampaignPlugin";
    }
}

