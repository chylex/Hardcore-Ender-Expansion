package chylex.hee.mechanics.orb;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.util.SpawnEntry;

public final class OrbSpawnableMobs{
	public static final Set<Class<?>> classList = new HashSet<>();
	
	public static void initialize(){
		for(BiomeGenBase biome:BiomeGenBase.getBiomeGenArray()){
			if (biome == null)continue;
			
			for(EnumCreatureType creatureType:EnumCreatureType.values()){
				List<?> spawnEntries = biome.getSpawnableList(creatureType);
				if (spawnEntries == null)continue;
				
				for(Object o:spawnEntries)classList.add(((SpawnListEntry)o).entityClass);
			}
		}
		
		for(IslandBiomeBase biome:IslandBiomeBase.biomeList){
			for(WeightedList<SpawnEntry> list:biome.spawnEntries.valueCollection()){
				for(SpawnEntry entry:list)classList.add(entry.getMobClass());
			}
		}

		for(Class cls:new Class<?>[]{
			EntityCaveSpider.class, EntityWitch.class, EntitySnowman.class,
			EntityVillager.class, EntitySilverfish.class, EntityIronGolem.class
		})classList.add(cls);
	}
	
	private OrbSpawnableMobs(){}
}
