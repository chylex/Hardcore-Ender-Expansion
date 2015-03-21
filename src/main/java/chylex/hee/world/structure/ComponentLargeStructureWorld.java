package chylex.hee.world.structure;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public abstract class ComponentLargeStructureWorld extends ComponentScatteredFeatureCustom{
	protected final LargeStructureWorld structure;
	private boolean isSetup;
	private int startX, startZ;
	private int bottomY;
	
	public ComponentLargeStructureWorld(){
		structure = new LargeStructureWorld(null);
	}
	
	public ComponentLargeStructureWorld(Random rand, int x, int y, int z, int sizeX, int sizeY, int sizeZ){
		super(rand,x,y,z,sizeX,sizeY,sizeZ);
		structure = new LargeStructureWorld(this);
		startX = x;
		startZ = z;
	}
	
	@Override
	public final boolean addComponentParts(World world, Random rand, StructureBoundingBox bb){
		if (!isSetup){
			Stopwatch.time("Structure setup");
			bottomY = setupStructure(world.getWorldInfo().getSeed());
			Stopwatch.finish("Structure setup");
			isSetup = true;
		}
		
		for(int chunkX = 0, chunkXAmount = (getSizeX()>>4); chunkX < chunkXAmount; chunkX++){
			for(int chunkZ = 0, chunkZAmount = (getSizeZ()>>4); chunkZ < chunkZAmount; chunkZ++){
				structure.getChunkFromChunkCoords(chunkX,chunkZ).generateInStructure(this,world,bb,8,bottomY,8);
			}
		}
		
		return true;
	}
	
	protected final int getStartX(){
		return startX;
	}
	
	protected final int getStartZ(){
		return startZ;
	}
	
	protected final int getBottomY(){
		return bottomY;
	}
	
	protected abstract int setupStructure(long seed);
	
	@Override
	protected void func_143012_a(NBTTagCompound nbt){ // OBFUSCATED writeToNBT
		super.func_143012_a(nbt);
		nbt.setInteger("startX",startX);
		nbt.setInteger("startZ",startZ);
		nbt.setByte("bottomY",(byte)bottomY);
		nbt.setTag("structure",structure.saveToNBT());
	}

	@Override
	protected void func_143011_b(NBTTagCompound nbt){ // OBFUSCATED readFromNBT
		super.func_143011_b(nbt);
		startX = nbt.getInteger("startX");
		startZ = nbt.getInteger("startZ");
		bottomY = nbt.getByte("bottomY");
		structure.loadFromNBT(nbt.getCompoundTag("structure"));
	}
}
