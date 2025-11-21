package data.scripts.skills;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.CharacterStatsSkillEffect;
import com.fs.starfarer.api.characters.MarketSkillEffect;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

/**
 * Custom skill for omega core administrators.
 * Provides superior bonuses compared to Industrial Planning.
 */
public class molochs_OmegaAdministration {
	
	// Omega cores provide +2 units to all industry outputs (alpha provides +1)
	public static int SUPPLY_BONUS = 2;
	
	// Omega cores provide +10% accessibility (alpha doesn't provide this)
	public static float ACCESS_BONUS = 10f;
	
	/**
	 * Level 1: Provides +2 units to all industry outputs
	 */
	public static class Level1 implements CharacterStatsSkillEffect {
		@Override
		public void apply(MutableCharacterStatsAPI stats, String id, float level) {
			stats.getDynamic().getMod(Stats.SUPPLY_BONUS_MOD).modifyFlat(id, SUPPLY_BONUS);
		}

		@Override
		public void unapply(MutableCharacterStatsAPI stats, String id) {
			stats.getDynamic().getMod(Stats.SUPPLY_BONUS_MOD).unmodifyFlat(id);
		}
		
		@Override
		public String getEffectDescription(float level) {
			return "All industries supply " + SUPPLY_BONUS + " more units of all the commodities they produce";
		}
		
		@Override
		public String getEffectPerLevelDescription() {
			return null;
		}

		@Override
		public ScopeDescription getScopeDescription() {
			return ScopeDescription.GOVERNED_OUTPOST;
		}
	}
	
	/**
	 * Level 2: Provides +10% accessibility to the colony
	 */
	public static class Level2 implements MarketSkillEffect {
		@Override
		public void apply(MarketAPI market, String id, float level) {
			market.getAccessibilityMod().modifyFlat(id, ACCESS_BONUS * 0.01f, "Omega Administration");
		}

		@Override
		public void unapply(MarketAPI market, String id) {
			market.getAccessibilityMod().unmodifyFlat(id);
		}
		
		@Override
		public String getEffectDescription(float level) {
			return "+" + (int)ACCESS_BONUS + "% accessibility";
		}
		
		@Override
		public String getEffectPerLevelDescription() {
			return null;
		}

		@Override
		public ScopeDescription getScopeDescription() {
			return ScopeDescription.GOVERNED_OUTPOST;
		}
	}
}

