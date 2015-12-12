package chylex.hee.world.end;
import java.util.Random;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.ColorUtil;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.end.TerritoryGenerator.ITerritoryGeneratorConstructor;
import chylex.hee.world.end.gen.TerritoryTheHub;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.StructureWorldLazy;
import chylex.hee.world.util.BoundingBox;

public enum EndTerritory{
	THE_HUB(24, color(0,0,0), new TerritorySpawnInfo(128,0), new TerritorySpawnGenerator.Empty(), new TerritoryTheHub.Environment(), TerritoryTheHub::new), // 384 blocks
	DEBUG_TEST(7, color(253), new TerritorySpawnInfo(128,0), new TerritorySpawnGenerator.Test(), new TerritoryTheHub.Environment(), TerritoryTheHub::new),
	DEBUG_TEST_2(20, color(253), new TerritorySpawnInfo(128,0), new TerritorySpawnGenerator.Test(), new TerritoryTheHub.Environment(), TerritoryTheHub::new),
	;
	
	private final int chunkSize;
	private final TerritorySpawnInfo info;
	private final TerritorySpawnGenerator spawn;
	private final ITerritoryGeneratorConstructor constructor;

	public final TerritoryEnvironment environment;
	public final int tokenColor;
	
	private EndTerritory(int chunkSize, int tokenColor, TerritorySpawnInfo info, TerritorySpawnGenerator spawn, TerritoryEnvironment environment, ITerritoryGeneratorConstructor constructor){
		this.chunkSize = chunkSize;
		this.tokenColor = tokenColor;
		this.info = info;
		this.spawn = spawn;
		this.environment = environment;
		this.constructor = constructor;
	}
	
	public boolean canGenerate(){
		return ordinal() != 0;
	}
	
	public Random createRandom(long seed, int index){
		Random rand = new Random(seed);
		rand.setSeed(rand.nextLong()^(66L*index)+ordinal()*rand.nextInt());
		return rand;
	}
	
	public StructureWorld createWorld(World world){
		StructureWorldLazy structureWorld = new StructureWorldLazy(world,8*chunkSize,info.getHeight(),8*chunkSize);
		
		if ($debugging){
			structureWorld.setSendToWatchers();
			
			BoundingBox box = structureWorld.getArea();
			
			for(int x = box.x1; x < box.x2; x++){
				for(int z = box.z1; z < box.z2; z++){
					for(int y = box.y1; y < box.y2; y++){
						structureWorld.setAir(x,y,z);
					}
				}
			}
			
			for(Entity entity:EntitySelector.any(world,box.toAABB())){
				if (!(entity instanceof EntityPlayer))entity.setDead();
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
	
	public Pos generateTerritory(ChunkCoordIntPair startPoint, World world, Random rand){
		for(int chunkX = 0; chunkX < chunkSize; chunkX++){
			for(int chunkZ = 0; chunkZ < chunkSize; chunkZ++){
				world.getChunkFromChunkCoords(startPoint.chunkXPos+chunkX,startPoint.chunkZPos+chunkZ);
			}
		}
		
		StructureWorld structureWorld = createWorld(world);
		constructor.construct(this,structureWorld,rand).generate();
		
		int startX = 16*startPoint.chunkXPos+structureWorld.getArea().x2;
		int startZ = 16*startPoint.chunkZPos+structureWorld.getArea().z2;
		int bottomY = info.getBottomY(rand);
		
		Pos spawnPos = spawn.createSpawnPoint(structureWorld,rand,this).offset(startX,bottomY,startZ);
		structureWorld.generateInWorld(world,rand,startX,bottomY,startZ);
		return spawnPos;
	}
	
	public Pos generateTerritory(int index, World world, Random rand){
		return generateTerritory(getStartPoint(index),world,rand);
	}
	
	public static final int chunksBetween = 64; // 1024 blocks
	public static final int chunkOffset = -THE_HUB.chunkSize/2;
	public static final EndTerritory[] values = values();
	
	/**
	 * If you touch this, your pet fish will die.
	 */
	private static @Nullable Pair<Pos,EndTerritory> findTerritoryCenter(double posX, double posZ){
		int middleX = chunkOffset*16;
		
		for(EndTerritory territory:values){
			if (Math.abs(Math.abs(posX)-(middleX+territory.chunkSize*8)) < (territory.chunkSize+chunksBetween)*8){
				final int gridSize = (territory.chunkSize+chunksBetween)*16;
				final int offset = chunkOffset*16+territory.chunkSize*8;
				
				int xStart = (posX >= 0D ? 1 : -1)*(middleX+territory.chunkSize*8);
				int zStart = posZ >= chunkOffset*16 ? gridSize*((MathUtil.floor(posZ)+gridSize/2-offset)/gridSize)+offset :
				                                      gridSize*((MathUtil.floor(posZ)-gridSize/2-offset)/gridSize)+offset;
				
				return Pair.of(Pos.at(xStart,0,zStart),territory);
			}
			
			middleX += (territory.chunkSize+chunksBetween)*16;
		}
		
		return null;
	}
	
	/**
	 * Do you wanna have a bad time? No? Then don't touch this.
	 */
	public static @Nullable EndTerritory fromPosition(double posX){
		int middlePoint = chunkOffset*16;
		
		for(EndTerritory territory:values){
			if (Math.abs(Math.abs(posX)-(middlePoint+territory.chunkSize*8)) < (territory.chunkSize+chunksBetween)*8){
				return territory;
			}
			
			middlePoint += (territory.chunkSize+chunksBetween)*16;
		}
		
		return null;
	}
	
	/**
	 * Returns how close the entity is to the void, starting from 0 with no upper bound.
	 * Values around 1 should show negative effects, eventually trying to kill the entity more violently as the value continues to increase.
	 */
	public static double getVoidFactor(Entity entity){
		Pair<Pos,EndTerritory> info = findTerritoryCenter(entity.posX,entity.posZ);
		if (info == null)return 0D;
		
		double diffX = Math.abs(info.getKey().getX()-entity.posX);
		double diffZ = Math.abs(info.getKey().getZ()-entity.posZ);
		
		double distXZ = Math.pow(Math.pow(diffX,2.5D)+Math.pow(diffZ,2.5D),0.4D)/((info.getValue().chunkSize+2)*8);
		double offY = Math.pow(Math.abs(128D-entity.posY)/164D,6D);
		
		return Math.max(0D,distXZ*3.5D-3D)+Math.max(0D,offY);
	}
	
	private static int color(int hue){
		float[] rgb = ColorUtil.hsvToRgb(hue/360F,0.32F,0.76F);
		return (MathUtil.floor(rgb[0]*255F)<<16)|(MathUtil.floor(rgb[1]*255F)<<8)|MathUtil.floor(rgb[2]*255F);
	}
	
	private static int color(int red, int green, int blue){
		return (red<<16)|(green<<8)|blue;
	}
	
	private static boolean $debugging = false;
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			$debugging = true;
			
			if (args.length == 0){
				THE_HUB.generateTerritory(0,world,new Random(world.getSeed()));
			}
			else if (args[0].equals("spawn") && args.length >= 3){
				values[DragonUtil.tryParse(args[1],0)].generateTerritory(DragonUtil.tryParse(args[2],0),world,new Random(world.getSeed()));
			}
			else if (args[0].equals("seed") && args.length >= 4){
				values[DragonUtil.tryParse(args[1],0)].generateTerritory(DragonUtil.tryParse(args[2],0),world,new Random(DragonUtil.tryParse(args[3],0)));
			}
			else if (args[0].equals("loc") && args.length >= 3){
				EndTerritory territory = values[DragonUtil.tryParse(args[1],0)];
				ChunkCoordIntPair coords = territory.getStartPoint(DragonUtil.tryParse(args[2],0));
				HardcoreEnderExpansion.notifications.report("Start: "+(coords.chunkXPos*16)+", "+(coords.chunkZPos*16));
				HardcoreEnderExpansion.notifications.report("Center: "+(coords.chunkXPos*16+territory.chunkSize*8)+", "+(coords.chunkZPos*16+territory.chunkSize*8));
			}
			else if (args[0].equals("find") && args.length >= 3){
				int x = DragonUtil.tryParse(args[1],0), z = DragonUtil.tryParse(args[2],0);
				Log.reportedDebug("Found: $0",findTerritoryCenter(x,z));
			}
		}
	};
}
