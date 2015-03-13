package chylex.hee.world.structure.sanctuary;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockSacredStone;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.WorldGenSavefile;
import chylex.hee.system.savedata.types.WorldGenSavefile.WorldGenElement;
import chylex.hee.system.util.CycleProtection;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.ComponentLargeStructureWorld;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;

public class ComponentSanctuary extends ComponentLargeStructureWorld implements ITileEntityGenerator{
	/**
	 * Required for reflection.
	 */
	public ComponentSanctuary(){}
	
	protected ComponentSanctuary(Random rand, int x, int z){
		super(rand,x,128,z,112,256,112);
		WorldDataHandler.<WorldGenSavefile>get(WorldGenSavefile.class).addElementAt(getStartX()>>4,getStartZ()>>4,WorldGenElement.INSIDIOUS_SANCTUARY);
	}
	
	@Override
	protected int setupStructure(long seed){
		Random rand = new Random(((getStartX()/10)*52260739421L+(getStartZ()/10)*704913L)^seed);
		
		int roomsX = 0, roomsZ = 0;
		
		while(!MathUtil.inRangeIncl(roomsX*roomsZ,24,36) && CycleProtection.proceed()){
			roomsX = 4+rand.nextInt(4);
			roomsZ = 4+rand.nextInt(4);
			if (roomsX == roomsZ && rand.nextInt(5) <= 2)roomsX = roomsZ = 0;
		}
		
		if (CycleProtection.failed())roomsX = roomsZ = 5;
		
		int roomWidth = 8, roomDepth = 8;
		int floorHeight = 11, height = floorHeight*2-1, bottom = 10;
		int structureWidth = roomWidth*roomsX+2, structureDepth = roomDepth*roomsZ+2;
		
		int tlX = (getSizeX()>>1)-(structureWidth>>1), tlZ = (getSizeZ()>>1)-(structureDepth>>1);
		
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
			
			for(int roomX = 0; roomX < roomsX; roomX++){
				for(int roomZ = 0; roomZ < roomsZ; roomZ++){
					int px = tlX+1+roomX*roomWidth, pz = tlZ+1+roomZ*roomDepth;
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
			
			for(int room1 = 0; room1 < roomsX-1; room1++){
				for(int room2 = 0; room2 < roomsZ; room2++){
					int px = tlX+1+(room1+1)*roomWidth-1;
					int sizeY = 1+rand.nextInt(4), sizeZ = 1+rand.nextInt(4);
					int yy = startY+1+rand.nextInt(floorHeight-3-sizeY), zz = tlZ+1+room2*roomDepth+rand.nextInt(roomDepth-sizeZ-1)+1;
					
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
					int pz = tlZ+1+(room1+1)*roomDepth-1;
					int sizeY = 1+rand.nextInt(4), sizeX = 1+rand.nextInt(4);
					int yy = startY+1+rand.nextInt(floorHeight-3-sizeY), xx = tlX+1+room2*roomWidth+rand.nextInt(roomWidth-sizeX-1)+1;
					
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
		
		// hairy chest
		/*for(int attempt = 0, px, py, pz, dir; attempt < 500000; attempt++){
			px = centerX+rand.nextInt(halfWidth+10)-rand.nextInt(halfWidth+10);
			py = rand.nextInt(bottom+height+10);
			pz = centerZ+rand.nextInt(halfWidth+10)-rand.nextInt(halfWidth+10);
			
			if (structure.getBlock(px,py,pz) == BlockList.sacred_stone){
				if (rand.nextInt(6) <= 1){
					py += rand.nextInt(2)*2-1;
				}
				else{
					dir = rand.nextInt(4);
					px += Direction.offsetX[dir];
					pz += Direction.offsetZ[dir];
				}
				
				if (structure.isAir(px,py,pz))structure.setBlock(px,py,pz,BlockList.sacred_stone,rand.nextInt(BlockSacredStone.metaAmount));
			}
		}*/
		
		return rand.nextInt(64);
	}
	
	private void genMain(int x, int y, int z, int rad){
		
	}
	
	@Override
	public void onTileEntityRequested(String key, TileEntity tile, Random rand){
		
	}
}