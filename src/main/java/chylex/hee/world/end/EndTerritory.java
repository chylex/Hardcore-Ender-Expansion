package chylex.hee.world.end;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.world.end.TerritoryGenerator.ITerritoryGeneratorConstructor;
import chylex.hee.world.end.gen.TerritoryTheHub;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.StructureWorldLazy;
import chylex.hee.world.util.BoundingBox;

public enum EndTerritory{
	THE_HUB(24, new TerritorySpawnInfo(128,0), TerritoryTheHub::new), // 384 blocks
	;
	
	public final int chunkSize;
	private final TerritorySpawnInfo info;
	private final ITerritoryGeneratorConstructor constructor;
	
	private EndTerritory(int chunkSize, TerritorySpawnInfo info, ITerritoryGeneratorConstructor constructor){
		this.chunkSize = chunkSize;
		this.info = info;
		this.constructor = constructor;
	}
	
	public StructureWorld createWorld(World world){
		StructureWorldLazy structureWorld = new StructureWorldLazy(world,8*chunkSize,info.getHeight(),8*chunkSize);
		
		if ($debugging){
			structureWorld.setSendToWatchers();
			
			BoundingBox box = structureWorld.getArea();
			
			for(int x = box.x1; x < box.x2; x++){
				for(int z = -box.z1; z < box.z2; z++){
					for(int y = box.y1; y < box.y2; y++){
						structureWorld.setAir(x,y,z);
					}
				}
			}
		}
		
		return structureWorld;
	}

	/**
	 * Finds the top left chunk for the territory using magic. They are generated in this manner:<br>
	 * {@code 7|5} <br>
	 * {@code 3|1} <br>
	 * {@code 2|0} <br>
	 * {@code 6|4} <br>
	 * This was tested using modified values that cannot be used in production. Do not touch this. Ever.
	 */
	public ChunkCoordIntPair getStartPoint(int index){
		int chunkOffX = 0;
		
		if ((index/2)%2 == 0)chunkOffX = IntStream.range(0,ordinal()).map(level -> chunksBetween+values[level].chunkSize).sum(); // positive x
		else chunkOffX = -IntStream.rangeClosed(1,ordinal()).map(level -> chunksBetween+values[level].chunkSize).sum(); // negative x
		
		int distZ = index/4;
		if (index%2 != 0)distZ = -distZ-1; // moves odd indexes to negative z
		
		int chunkOffZ = distZ*(chunksBetween+chunkSize);
		
		return new ChunkCoordIntPair(chunkOffX+chunkOffset,chunkOffZ+chunkOffset);
	}
	
	public void generateTerritory(ChunkCoordIntPair startPoint, World world, Random rand){
		for(int chunkX = 0; chunkX < chunkSize; chunkX++){
			for(int chunkZ = 0; chunkZ < chunkSize; chunkZ++){
				world.getChunkFromChunkCoords(startPoint.chunkXPos+chunkX,startPoint.chunkZPos+chunkZ);
			}
		}
		
		StructureWorld structureWorld = createWorld(world);
		constructor.construct(structureWorld,rand).generate();
		structureWorld.generateInWorld(world,rand,16*startPoint.chunkXPos+structureWorld.getArea().x2,info.getBottomY(rand),16*startPoint.chunkZPos+structureWorld.getArea().z2);
	}
	
	public void generateTerritory(int index, World world, Random rand){
		generateTerritory(getStartPoint(index),world,rand);
	}
	
	public static final int chunksBetween = 64; // 1024 blocks
	public static final int chunkOffset = -THE_HUB.chunkSize/2;
	public static final EndTerritory[] values = values();
	
	private static boolean $debugging = false;
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			$debugging = true;
			THE_HUB.generateTerritory(THE_HUB.getStartPoint(0),world,new Random(world.getSeed()));
		}
	};
}
