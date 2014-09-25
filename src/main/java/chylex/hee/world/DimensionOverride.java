package chylex.hee.world;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenEnd;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.island.StructureIsland;
import chylex.hee.world.structure.tower.ComponentTower;
import chylex.hee.world.structure.tower.StructureTower;

public final class DimensionOverride{
	public static void setup(){
		Stopwatch.time("DimensionOverride");
		
		overrideBiome();
		overrideWorldGen();
		
		MapGenStructureIO.registerStructure(StructureTower.class,"hardcoreenderdragon_EndTower");
		MapGenStructureIO.func_143031_a(ComponentTower.class,"hardcoreenderdragon_EndTowerC"); // OBFUSCATED register structure component
		MapGenStructureIO.registerStructure(StructureIsland.class,"hardcoreenderdragon_EndIsland");
		MapGenStructureIO.func_143031_a(ComponentIsland.class,"hardcoreenderdragon_EndIslandC");
		
		Stopwatch.finish("DimensionOverride");
	}
	
	public static void postInit(){
		Stopwatch.time("DimensionOverride - PostInit");
		
		if (!(BiomeGenBase.sky instanceof BiomeGenHardcoreEnd))throw new RuntimeException("End biome class mismatch, Hardcore Ender Expansion cannot proceed! Biome class: "+BiomeGenBase.sky.getClass().getName());
		
		try{
			Field f = DimensionManager.class.getDeclaredField("providers");
			f.setAccessible(true); // let it throw NPE if the field isn't found
			
			Class<?> cls = ((Hashtable<Integer,Class<? extends WorldProvider>>)f.get(null)).get(1);
			if (cls != WorldProviderHardcoreEnd.class)throw new RuntimeException("End world provider class mismatch, Hardcore Ender Expansion cannot proceed! Provider class: "+cls.getName());
		}catch(NullPointerException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e){
			throw new RuntimeException("End world provider check failed!",e);
		}
		
		((BiomeGenHardcoreEnd)BiomeGenBase.getBiome(9)).overrideMobLists();
		
		Stopwatch.finish("DimensionOverride - PostInit");
	}
	
	private static void overrideBiome(){
		Stopwatch.time("DimensionOverride - Biome");
		
		try{
			BiomeGenBase sky = new BiomeGenHardcoreEnd(9).setColor(8421631).setBiomeName("Sky").setDisableRain();
			BiomeGenBase.getBiomeGenArray()[9] = sky;

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			
			for(Field field:BiomeGenBase.class.getDeclaredFields()){
				if (!BiomeGenBase.class.isAssignableFrom(field.getType()))continue;
				
				field.setAccessible(true);
				modifiersField.setInt(field,field.getModifiers() & ~Modifier.FINAL);
				
				if (field.get(null) instanceof BiomeGenEnd){
					field.set(null,sky);
					break;
				}
			}
		}catch(NoSuchFieldException | IllegalArgumentException | IllegalAccessException e){
			throw new RuntimeException("Could not override the End biome!",e);
		}
		
		Stopwatch.finish("DimensionOverride - Biome");
	}
	
	@SuppressWarnings("unchecked")
	private static void overrideWorldGen(){
		Stopwatch.time("DimensionOverride - WorldProvider");
		
		DimensionManager.unregisterProviderType(1);
		DimensionManager.registerProviderType(1,WorldProviderHardcoreEnd.class,false);

		Stopwatch.finish("DimensionOverride - WorldProvider");
	}
}
