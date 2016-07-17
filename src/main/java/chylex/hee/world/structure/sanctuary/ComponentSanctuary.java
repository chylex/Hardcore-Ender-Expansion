package chylex.hee.world.structure.sanctuary;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.WorldGenSavefile;
import chylex.hee.system.savedata.types.WorldGenSavefile.WorldGenElement;
import chylex.hee.tileentity.TileEntitySanctuaryBrain;
import chylex.hee.world.structure.ComponentLargeStructureWorld;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;

public class ComponentSanctuary extends ComponentLargeStructureWorld implements ITileEntityGenerator{
	private NBTTagCompound brainNBT;
	
	/**
	 * Required for reflection.
	 */
	public ComponentSanctuary(){}
	
	protected ComponentSanctuary(World world, Random rand, int x, int z){
		super(world,rand,x,128,z,112,128,112);
		WorldDataHandler.<WorldGenSavefile>get(WorldGenSavefile.class).addElementAt(getStartX()>>4,getStartZ()>>4,WorldGenElement.INSIDIOUS_SANCTUARY);
	}
	// TODO SANCTUARY
	@Override
	protected int setupStructure(long seed){return 0;
		/*brainNBT = new NBTTagCompound();
		Random rand = new Random(((getStartX()/10)*52260739421L+(getStartZ()/10)*704913L)^seed);
		
		int roomsX = 0, roomsZ = 0;
		
		while(!MathUtil.inRangeIncl(roomsX*roomsZ,24,36) && CycleProtection.proceed()){
			roomsX = 4+rand.nextInt(4);
			roomsZ = 4+rand.nextInt(4);
			if (roomsX == roomsZ && rand.nextInt(5) <= 2)roomsX = roomsZ = 0;
		}
		
		if (CycleProtection.failed())roomsX = roomsZ = 5;
		
		// data
		SanctuaryMazeGen maze = SanctuaryMazeGen.generate(rand,roomsX,roomsZ);
		
		if (maze == null){
			Log.error("Failed generating maze (x: $0, y: $1, seed: $2)",getStartX(),getStartZ(),seed);
			return 0;
		}
		
		SanctuaryConquerPointGen conquerPts = SanctuaryConquerPointGen.generate(rand,roomsX,roomsZ,3);
		
		// prepare
		
		final int roomWidth = 8, roomDepth = 8;
		final int floorHeight = 11, height = floorHeight*2-1, bottom = 10;
		int structureWidth = (roomWidth+1)*roomsX+1, structureDepth = (roomDepth+1)*roomsZ+1;
		
		int tlX = (getSizeX()>>1)-(structureWidth>>1), tlZ = (getSizeZ()>>1)-(structureDepth>>1);
		
		// outside box
		for(int xx = tlX; xx <= tlX+structureWidth; xx++){
			for(int zz = tlZ; zz <= tlZ+structureDepth; zz++){
				structure.setBlock(xx,bottom,zz,BlockList.sacred_stone,0);
				structure.setBlock(xx,bottom+height,zz,BlockList.sacred_stone,0);
			}
		}
		
		for(int yy = bottom+1; yy < bottom+height; yy++){
			for(int xx = tlX; xx <= tlX+structureWidth; xx++){
				structure.setBlock(xx,yy,tlZ,BlockList.sacred_stone,0);
				structure.setBlock(xx,yy,tlZ+structureDepth,BlockList.sacred_stone,0);
			}
			
			for(int zz = tlZ; zz <= tlZ+structureDepth; zz++){
				structure.setBlock(tlX,yy,zz,BlockList.sacred_stone,0);
				structure.setBlock(tlX+structureWidth,yy,zz,BlockList.sacred_stone,0);
			}
		}
		
		// rooms
		for(int floor = 0; floor < 2; floor++){
			int startY = bottom+1+floor*(floorHeight-1);
			
			// room layout
			for(int roomX = 0; roomX < roomsX; roomX++){
				for(int roomZ = 0; roomZ < roomsZ; roomZ++){
					int px = tlX+1+roomX*(roomWidth+1), pz = tlZ+1+roomZ*(roomDepth+1);
					int meta = 1+rand.nextInt(BlockSacredStone.metaAmount-1);
					
					for(int xx = 0; xx <= roomWidth; xx++){
						for(int zz = 0; zz <= roomDepth; zz++){
							structure.setBlock(px+xx,startY,pz+zz,BlockList.sacred_stone,rand.nextInt(3) == 0 ? meta : 0);
							structure.setBlock(px+xx,startY+floorHeight-2,pz+zz,BlockList.sacred_stone,rand.nextInt(3) == 0 ? meta : 0);
						}
					}
					
					for(int yy = startY+1; yy <= startY+floorHeight-3; yy++){
						for(int xx = 0; xx <= roomWidth; xx++){
							structure.setBlock(px+xx,yy,pz,BlockList.sacred_stone,rand.nextInt(3) == 0 ? meta : 0);
							structure.setBlock(px+xx,yy,pz+roomDepth,BlockList.sacred_stone,rand.nextInt(3) == 0 ? meta : 0);
						}
	
						for(int zz = 0; zz <= roomDepth; zz++){
							structure.setBlock(px,yy,pz+zz,BlockList.sacred_stone,rand.nextInt(3) == 0 ? meta : 0);
							structure.setBlock(px+roomWidth,yy,pz+zz,BlockList.sacred_stone,rand.nextInt(3) == 0 ? meta : 0);
						}
					}
				}
			}
			
			// room holes
			for(int room1 = 0; room1 < roomsX-1; room1++){
				for(int room2 = 0; room2 < roomsZ; room2++){
					if (!maze.isOpen(room1,room2,Facing.EAST_POSX))continue;
					
					int px = tlX+(room1+1)*(roomWidth+1);
					int sizeY = 2+rand.nextInt(3), sizeZ = 1+rand.nextInt(4);
					int yy = startY+1+rand.nextInt(floorHeight-3-sizeY), zz = tlZ+2+room2*(roomDepth+1)+rand.nextInt(roomDepth-sizeZ-1);
					
					for(int xx = px; xx <= px+1; xx++){
						for(int py = yy; py <= yy+sizeY; py++){
							for(int pz = zz; pz <= zz+sizeZ; pz++){
								structure.setBlock(xx,py,pz,Blocks.air);
							}
						}
					}
				}
			}
			
			for(int room1 = 0; room1 < roomsZ-1; room1++){
				for(int room2 = 0; room2 < roomsX; room2++){
					if (!maze.isOpen(room2,room1,Facing.SOUTH_POSZ))continue;
					
					int pz = tlZ+(room1+1)*(roomDepth+1);
					int sizeY = 2+rand.nextInt(3), sizeX = 1+rand.nextInt(4);
					int yy = startY+1+rand.nextInt(floorHeight-3-sizeY), xx = tlX+2+room2*(roomWidth+1)+rand.nextInt(roomWidth-sizeX-1);
					
					for(int zz = pz; zz <= pz+1; zz++){
						for(int py = yy; py <= yy+sizeY; py++){
							for(int px = xx; px <= xx+sizeX; px++){
								structure.setBlock(px,py,zz,Blocks.air);
							}
						}
					}
				}
			}
		}
		
		// outside decoration
		for(int attempt = 0, px = 0, py = 0, pz = 0, addX, addY, addZ, dir; attempt < 20000 && false; attempt++){
			dir = rand.nextInt(6);
			addX = addY = addZ = 0;
			
			if (dir == 0 || dir == 1){
				px = tlX+rand.nextInt(structureWidth+1);
				pz = tlZ+rand.nextInt(structureWidth+1);
				py = dir == 0 ? bottom : bottom+height;
				addY = dir == 0 ? -1 : 1;
			}
			else if (dir == 2 || dir == 3){
				px = tlX+rand.nextInt(structureWidth+1);
				py = rand.nextInt(bottom+height+1);
				pz = dir == 2 ? tlZ : tlZ+structureDepth;
				addZ = dir == 2 ? -1 : 1;
			}
			else if (dir == 4 || dir == 5){
				pz = tlZ+rand.nextInt(structureDepth+1);
				py = rand.nextInt(bottom+height+1);
				px = dir == 4 ? tlX : tlX+structureWidth;
				addX = dir == 4 ? -1 : 1;
			}
			
			if (structure.getBlock(px,py,pz) == BlockList.sacred_stone && structure.isAir(px+addX,py+addY,pz+addZ)){
				for(int a = 0; a < 1+rand.nextInt(3+rand.nextInt(4)*rand.nextInt(3)); a++){
					structure.setBlock(px += addX,py += addY,pz += addZ,BlockList.sacred_stone,rand.nextInt(BlockSacredStone.metaAmount));
				}
			}
		}
		
		// the brainz
		int bottomY = rand.nextInt(64);
		
		int x1 = tlX+roomWidth, z1 = tlZ+roomDepth, x2 = x1+structureWidth, z2 = z1+structureDepth;
		brainNBT.setIntArray("p1",new int[]{ getXWithOffset(x1,z1), bottomY+getYWithOffset(bottom), getZWithOffset(x1,z1) });
		brainNBT.setIntArray("p2",new int[]{ getXWithOffset(x2,z2), bottomY+getYWithOffset(bottom+height), getZWithOffset(x2,z2) });
		
		NBTTagList ptsTag = new NBTTagList();
		
		for(byte[] point:conquerPts.getPoints()){
			NBTTagCompound pointTag = new NBTTagCompound();
			x1 = tlX+roomWidth+point[0]*(roomWidth+1)+2;
			x2 = x1+roomWidth-2;
			z1 = tlZ+roomDepth+point[1]*(roomDepth+1)+2;
			z2 = z1+roomDepth-2;
			pointTag.setIntArray("p1",new int[]{ getXWithOffset(x1,z1), bottomY+getYWithOffset(bottom+1+floorHeight), getZWithOffset(x1,z1) });
			pointTag.setIntArray("p2",new int[]{ getXWithOffset(x2,z2), bottomY+getYWithOffset(bottom+height-2), getZWithOffset(x2,z2) });
			ptsTag.appendTag(pointTag);
		}
		
		brainNBT.setTag("conquer",ptsTag);
		
		structure.setBlock(tlX+2+roomWidth,bottom+1+floorHeight,tlZ+2+roomDepth,BlockList.sanctuary_brain);
		structure.setTileEntityGenerator(tlX+2+roomWidth,bottom+1+floorHeight,tlZ+2+roomDepth,"Brain",this);
		
		return bottomY;*/
	}
	
	@Override
	public void onTileEntityRequested(String key, TileEntity tile, Random rand){
		if (key.equals("Brain"))((TileEntitySanctuaryBrain)tile).readCustomData(brainNBT);
	}
}