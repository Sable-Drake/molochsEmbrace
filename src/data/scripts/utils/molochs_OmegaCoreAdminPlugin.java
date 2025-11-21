package data.scripts.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreAdminPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.util.Misc;

import java.util.Random;

/**
 * Custom admin plugin for omega cores.
 * Omega cores provide superior bonuses compared to alpha cores.
 */
public class molochs_OmegaCoreAdminPlugin implements AICoreAdminPlugin {
    
    @Override
    public PersonAPI createPerson(String aiCoreId, String factionId, long seed) {
        // Use alpha core admin plugin as a base, then modify for omega
        AICoreAdminPlugin alphaPlugin = Misc.getAICoreAdminPlugin(Commodities.ALPHA_CORE);
        PersonAPI person = alphaPlugin.createPerson(Commodities.ALPHA_CORE, factionId, seed);
        
        // Modify for omega core
        person.setAICoreId(Commodities.OMEGA_CORE);
        person.getName().setFirst("Omega Core");
        person.getName().setLast("");
        
        // Remove Industrial Planning (alpha core skill)
        person.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 0f);
        
        // Add our custom Omega Administration skill
        person.getStats().setSkillLevel("molochs_omega_administration", 1f);
        
        return person;
    }
}

