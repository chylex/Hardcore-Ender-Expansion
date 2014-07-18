package chylex.hee.mechanics.knowledge.util;
import static chylex.hee.mechanics.knowledge.KnowledgeRegistrations.*;
import java.util.Random;
import chylex.hee.mechanics.knowledge.data.KnowledgeCategory;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;
import chylex.hee.system.weight.WeightedList;

public final class FragmentWeightLists{
	public static final FragmentWeightList overworldGeneral = new FragmentWeightList(),
										   overworldMineshaft = new FragmentWeightList(),
										   overworldStronghold = new FragmentWeightList(),
										   villagerTrades = new FragmentWeightList(),
										   endTower = new FragmentWeightList(),
										   endSilverfishDungeons = new FragmentWeightList();
										   
	static{
		overworldMineshaft.add(ENDER_DRAGON,8)
						  .add(DRAGON_LAIR,5)
						  .add(ANGRY_ENDERMAN,4)
						  .add(BASIC_ESSENCE_ALTAR,4)
						  .add(FALLING_OBSIDIAN,3)
						  .add(ALTAR_NEXUS,2)
						  .add(DRAGON_ESSENCE,1)
						  .add(VAMPIRIC_BAT,1);
		
		overworldStronghold.add(ENDER_DRAGON,9)
						   .add(DRAGON_LAIR,7)
						   .add(ALTAR_NEXUS,6)
						   .add(BASIC_ESSENCE_ALTAR,5)
						   .add(DRAGON_ESSENCE,4)
						   .add(DRAGON_ESSENCE_ALTAR,3)
						   .add(TEMPLE_CALLER,3);
		
		overworldGeneral.add(END_POWDER_ORE,7)
						.add(STARDUST_ORE,7)
						.add(DRAGON_LAIR,6)
						.add(ENDSTONE_BLOB,6)
						.add(END_POWDER,5)
						.add(IGNEOUS_ROCK_ORE,5)
						.add(STARDUST,4)
						.add(ENDER_PEARLS_ENH,4)
						.add(IGNEOUS_ROCK,3)
						.add(DEATH_FLOWER,3)
						.add(DUNGEON_TOWER,2);
		
		villagerTrades.add(DRAGON_LAIR,8)
					  .add(DUNGEON_TOWER,8)
					  .add(ENDSTONE_BLOB,7)
					  .add(ENDER_GOO,6)
					  .add(INFESTED_FOREST_BIOME,5)
					  .add(BURNING_MOUNTAINS_BIOME,5)
					  .add(ENCHANTED_ISLAND_BIOME,5)
					  .add(INSTABILITY_ORB_ORE,4)
					  .add(ENDER_EYE,4)
					  .add(ENDER_GUARDIAN,4)
					  .add(BABY_ENDERMAN,4)
					  .add(SCORCHING_LENS,4)
					  .add(FIRE_GOLEM,4)
					  .add(METEOROID,3)
					  .add(SPATIAL_DASH_GEM,3)
					  .add(TRANSFERENCE_GEM,3)
					  .add(TEMPLE_CALLER,3);
		
		endTower.add(ENDSTONE_BLOB,7)
				.add(TRANSFERENCE_GEM,6)
				.add(TEMPLE_CALLER,6)
				.add(INFESTED_FOREST_BIOME,6)
				.add(BURNING_MOUNTAINS_BIOME,6)
				.add(ENCHANTED_ISLAND_BIOME,6)
				.add(ENDER_EYE,5)
				.add(DEATH_FLOWER,4)
				.add(TRANSFERENCE_GEM_ENH,4)
				.add(OBSIDIAN_VARIATIONS,3);
		
		endSilverfishDungeons.add(GHOST_AMULET,5)
							 .add(SPOOKY_TREES,5)
							 .add(ECTOPLASM,3)
							 .add(SILVERFISH_BLOOD,3)
							 .add(SPECTRAL_WAND,3)
							 .add(DRY_SPLINTER,3)
							 .add(INFESTATION_REMEDY,2)
							 .add(INFESTED_BAT,1);
	}
	
	public static class FragmentWeightList extends WeightedList<WeightedRegistrationWrapper>{
		private FragmentWeightList add(KnowledgeRegistration registration, int weight){
			this.add(new WeightedRegistrationWrapper(registration,weight));
			return this;
		}
		
		private FragmentWeightList addCategory(KnowledgeCategory category, int weight){
			for(KnowledgeRegistration registration:category.registrations)this.add(new WeightedRegistrationWrapper(registration,weight));
			return this;
		}
		
		public KnowledgeRegistration getRandomRegistration(Random rand){
			return super.getRandomItem(rand).getRegistration();
		}
	}
}
