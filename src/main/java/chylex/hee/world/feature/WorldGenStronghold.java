package chylex.hee.world.feature;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;

public class WorldGenStronghold implements IWorldGenerator{
	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider){
		if (world.provider.dimensionId != 0)return;
		
		// TODO
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			MapGenStronghold stronghold = new MapGenStronghold();
			int x = MathUtil.floor(player.posX)>>4, z = MathUtil.floor(player.posZ)>>4;
			StructureBoundingBox box = new StructureBoundingBox(-99999,0,-99999,99999,256,99999);
			
			try{
				Field f = MapGenStructure.class.getDeclaredField("field_143029_e");
				Method m = MapGenStronghold.class.getDeclaredMethod("getStructureStart",int.class,int.class);
				
				f.setAccessible(true);
				f.set(stronghold,new MapGenStructureData("TMPSTRONGHOLD"));
				
				m.setAccessible(true);
				StructureStart start = (StructureStart)m.invoke(stronghold,x,z);
				
				f = MapGenStructure.class.getDeclaredField("structureMap");
				f.setAccessible(true);
				((Map)f.get(stronghold)).put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(x,z)),start);
				start.generateStructure(world,world.rand,box);
			}catch(Throwable t){
				t.printStackTrace();
			}
		}
	};
}
