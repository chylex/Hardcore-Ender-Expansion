package chylex.hee.world;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenEnd;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;
import chylex.hee.world.structure.island.ComponentScatteredFeatureIsland;
import chylex.hee.world.structure.island.StructureIsland;
import chylex.hee.world.structure.tower.ComponentScatteredFeatureTower;
import chylex.hee.world.structure.tower.StructureTower;

public final class DimensionOverride{
	public static void setup(){
		overrideBiome();
		overrideWorldGen();
		
		MapGenStructureIO.registerStructure(StructureTower.class,"hardcoreenderdragon_EndTower");
		MapGenStructureIO.func_143031_a(ComponentScatteredFeatureTower.class,"hardcoreenderdragon_EndTowerC"); // OBFUSCATED register structure component
		MapGenStructureIO.registerStructure(StructureIsland.class,"hardcoreenderdragon_EndIsland");
		MapGenStructureIO.func_143031_a(ComponentScatteredFeatureIsland.class,"hardcoreenderdragon_EndIslandC");
	}
	
	private static void overrideBiome(){
		Field modifiersField = null;
		
	    try{
			BiomeGenBase sky = (new BiomeGenHardcoreEnd(9)).setColor(8421631).setBiomeName("Sky").setDisableRain();
			BiomeGenBase.getBiomeGenArray()[9] = sky;
			
    		modifiersField = Field.class.getDeclaredField("modifiers");
    		modifiersField.setAccessible(true);
    		
    		for(Field field:BiomeGenBase.class.getDeclaredFields()){
    			if (BiomeGenEnd.class.isAssignableFrom(field.getType())){
					modifiersField.setInt(field,modifiersField.getInt(field) & ~Modifier.FINAL);
					field.set(null,sky);
    				break;
    			}
    		}
    	}catch(Exception e){
    		throw new RuntimeException("Could not override the End biome!",e);
    	}
	}
	
	@SuppressWarnings("unchecked")
	private static void overrideWorldGen(){
		try{
			Field f = DimensionManager.class.getDeclaredField("providers");
			f.setAccessible(true); // let it throw NPE if the field isn't found
			
			Hashtable<Integer,Class<? extends WorldProvider>> providers = (Hashtable<Integer,Class<? extends WorldProvider>>)f.get(null);
			providers.put(1,WorldProviderHardcoreEnd.class);
			f.set(null,providers);
		}catch(Exception e){
			throw new RuntimeException("Could not override the DimensionManager providers!",e);
		}
	}
}
