package chylex.hee.world.structure.island.biome.feature.island;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Direction;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockPersegrit;
import chylex.hee.item.ItemKnowledgeNote;
import chylex.hee.item.ItemList;
import chylex.hee.system.weight.ObjectWeightPair;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.loot.IItemPostProcessor;
import chylex.hee.world.loot.LootItemStack;
import chylex.hee.world.loot.WeightedLootList;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;
import chylex.hee.world.util.BlockLocation;

public class StructureHiddenCellar extends AbstractIslandStructure implements ITileEntityGenerator{
	private static final byte[] horCheckX = new byte[]{ -1, 0, 1, -1, 1, -1, 0, 1 },
								horCheckZ = new byte[]{ -1, -1, -1, 0, 0, 1, 1, 1 };
	
	private static final Block[] endermanSolidBlocks = new Block[]{
		Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.gravel, Blocks.tnt, Blocks.clay, Blocks.pumpkin, Blocks.melon_block, Blocks.mycelium
	};
	
	private static final WeightedLootList normalChest = new WeightedLootList(new LootItemStack[]{
		new LootItemStack(Items.ender_pearl).setAmount(2,6).setWeight(20),
		new LootItemStack(ItemList.end_powder).setAmount(3,8).setWeight(20),
		new LootItemStack(ItemList.stardust).setAmount(4,10).setWeight(17),
		new LootItemStack(Blocks.grass).setAmount(1,2).setWeight(10),
		new LootItemStack(Blocks.mycelium).setAmount(1,2).setWeight(10),
		new LootItemStack(ItemList.knowledge_note).setWeight(8),
		new LootItemStack(Blocks.dirt).setAmount(1,4).setWeight(4),
		new LootItemStack(Blocks.sand).setAmount(1,4).setWeight(4),
		new LootItemStack(Blocks.gravel).setAmount(1,4).setWeight(4),
		new LootItemStack(Blocks.clay).setAmount(1,4).setWeight(4)
	}).addItemPostProcessor(new IItemPostProcessor(){
		@Override
		public ItemStack processItem(ItemStack is, Random rand){
			if (is.getItem() == ItemList.knowledge_note)ItemKnowledgeNote.setRandomNote(is,rand,4);
			return is;
		}
	});
	
	private static final WeightedLootList rareChest = new WeightedLootList(new LootItemStack[]{
		new LootItemStack(ItemList.enhanced_ender_pearl).setAmount(3,9).setWeight(20),
		new LootItemStack(ItemList.knowledge_note).setWeight(7),
		new LootItemStack(Items.ender_pearl).setAmount(3,9).setWeight(5),
		new LootItemStack(ItemList.temple_caller).setWeight(5)
		// TODO add stuff to create energy draining items (like Transference Gem)
	}).addItemPostProcessor(new IItemPostProcessor(){
		@Override
		public ItemStack processItem(ItemStack is, Random rand){
			if (is.getItem() == ItemList.knowledge_note)ItemKnowledgeNote.setRandomNote(is,rand,6);
			return is;
		}
	});
	
	public enum EnchantedIslandVariation{
		HOMELAND, LABORATORY
	}
	
	private EnchantedIslandVariation variation;
	private final List<BlockLocation> patternBlocks = new ArrayList<>();
	
	public void setVariation(EnchantedIslandVariation variation){
		this.variation = variation;
	}
	
	@Override
	protected boolean generate(Random rand){
		patternBlocks.clear();
		
		int height = 4+rand.nextInt(4);
		
		for(int attempt = 0, x, y, z; attempt < 50; attempt++){
			x = getRandomXZ(rand,32);
			y = 10+rand.nextInt(30);
			z = getRandomXZ(rand,32);
			
			RoomInfo info = genRoom(x,z,3+rand.nextInt(6),y,height,rand);
			if (info == null)continue;
			
			List<RoomInfo> rooms = new ArrayList<RoomInfo>();
			rooms.add(info);
			
			for(int roomAttemptsLeft = 70+rand.nextInt(110), roomsLeft = 5+rand.nextInt(16), side, dist, hWidth, offX, offZ; roomAttemptsLeft >= 0 && roomsLeft > 0; roomAttemptsLeft--){
				RoomInfo room = rooms.get(rand.nextInt(rooms.size()));
				if ((side = room.getUseableSide(rand)) == -1)continue;
				
				offX = Direction.offsetX[side];
				offZ = Direction.offsetZ[side];
				
				hWidth = 3+rand.nextInt(6);
				if (hWidth == 3)hWidth = 2;
				
				dist = room.halfWidth+hWidth+2+rand.nextInt(7);
				
				RoomInfo newRoom = genRoom(room.x+offX*dist,room.z+offZ*dist,hWidth,y,height,rand);
				if (newRoom == null)continue;
				
				room.canUseSide[side] = false;
				newRoom.canUseSide[Direction.rotateOpposite[side]] = false;
				rooms.add(newRoom);
				
				genHall(room.x+(room.halfWidth-1)*offX-offZ*2,room.z+(room.halfWidth-1)*offZ-offX*2,newRoom.x-(hWidth-1)*offX+offZ*2,newRoom.z-(hWidth-1)*offZ+offX*2,y,height,rand);
			}
			
			genPatterns(rand);
			return true;
		}
		
		return false;
	}

	private RoomInfo genRoom(int x, int z, int halfWidth, int bottomY, int height, Random rand){
		if (halfWidth == 3)halfWidth = 2; // too small, turn it into a "hall"
		
		int halfHeight = height>>1;

		if (world.getBlock(x,bottomY+halfHeight*2+2,z) != Blocks.end_stone)return null;
		
		for(int horCheck = 0; horCheck < 8; horCheck++){
			for(int yCheck = -1; yCheck <= 1; yCheck++){
				if (world.getBlock(x+horCheckX[horCheck]*halfWidth,bottomY+halfHeight+yCheck*(halfHeight+1),z+horCheckZ[horCheck]*halfWidth) != Blocks.end_stone)return null;
			}
		}
		
		int x1 = x-halfWidth, z1 = z-halfWidth, x2 = x+halfWidth, z2 = z+halfWidth;
		
		for(int xx = x1; xx <= x2; xx++){
			for(int zz = z1; zz <= z2; zz++){
				for(int yy = bottomY; yy <= bottomY+height; yy++){
					boolean edge = xx == x1 || xx == x2 || zz == z1 || zz == z2 || yy == bottomY || yy == bottomY+height;
					world.setBlock(xx,yy,zz,edge ? BlockList.persegrit : Blocks.air);
					if (edge && rand.nextInt(60) == 0)patternBlocks.add(new BlockLocation(xx,yy,zz));
				}
			}
		}
		
		if (halfWidth != 2)genRoomContent(x,z,halfWidth,bottomY,height,rand);
		return new RoomInfo(x,z,halfWidth);
	}
	
	private enum EnumRoomContent{
		NONE, CONNECTING_LINES, PERSEGRIT_CUBE, LOTS_OF_CHESTS, FLOATING_CUBES
	}
	
	private static final WeightedList<ObjectWeightPair<EnumRoomContent>> roomContentList = new WeightedList<>(
		//ObjectWeightPair.of(EnumRoomContent.NONE, 100),
		ObjectWeightPair.of(EnumRoomContent.CONNECTING_LINES, 10),
		ObjectWeightPair.of(EnumRoomContent.LOTS_OF_CHESTS, 8),
		ObjectWeightPair.of(EnumRoomContent.PERSEGRIT_CUBE, 6),
		ObjectWeightPair.of(EnumRoomContent.FLOATING_CUBES, 6)
	);
	
	private void genRoomContent(int x, int z, int halfWidth, int bottomY, int height, Random rand){
		EnumRoomContent type = roomContentList.getRandomItem(rand).getObject();
		if (type == EnumRoomContent.NONE)return;
		
		switch(type){
			case CONNECTING_LINES:
				for(int lines = 3+((halfWidth*(height-2))>>1)+rand.nextInt(6+halfWidth*2+height), width = halfWidth*2-2, dir, xx, yy, zz, addX = 0, addY = 0, addZ = 0, a, b; lines >= 0; lines--){
					dir = rand.nextInt(3);
					
					if (dir == 0)addX = rand.nextBoolean() ? -1 : 1;
					else if (dir == 1)addZ = rand.nextBoolean() ? -1 : 1;
					else addY = rand.nextBoolean() ? -1 : 1;
					
					for(a = 0; a < 10; a++){
						xx = x+rand.nextInt(width)-rand.nextInt(width);
						yy = bottomY+1+rand.nextInt(height-1);
						zz = z+rand.nextInt(width)-rand.nextInt(width);
						
						if (world.getBlock(xx-1,yy,zz) == BlockList.persegrit || world.getBlock(xx+1,yy,zz) == BlockList.persegrit ||
							world.getBlock(xx,yy-1,zz) == BlockList.persegrit || world.getBlock(xx,yy+1,zz) == BlockList.persegrit ||
							world.getBlock(xx,yy,zz-1) == BlockList.persegrit || world.getBlock(xx,yy,zz+1) == BlockList.persegrit ||
							world.getBlock(xx,yy,zz) == BlockList.persegrit)continue;
						
						boolean found = false;
						
						for(b = 0; b <= width+2; b++){
							if (world.getBlock(xx -= addX,yy -= addY,zz -= addZ) == BlockList.persegrit){
								if (Math.abs(xx-x) <= halfWidth && Math.abs(zz-z) <= halfWidth)found = true;
								break;
							}
						}
						
						if (!found)continue;
						found = false;
						
						for(b = 0; b <= width+2; b++){
							if (world.getBlock(xx += addX,yy += addY,zz += addZ) == BlockList.persegrit){
								if (Math.abs(xx-x) <= halfWidth && Math.abs(zz-z) <= halfWidth)found = true;
								break;
							}
						}
						
						if (!found)continue;
						
						for(b = 0; b <= width+2; b++){
							if (world.getBlock(xx -= addX,yy -= addY,zz -= addZ) == BlockList.persegrit)break;
							world.setBlock(xx,yy,zz,BlockList.persegrit);
						}
						
						break;
					}
				}
				
				break;
				
			case PERSEGRIT_CUBE:
				int cubeHalfWidth = Math.max(1,halfWidth-2-rand.nextInt(2));
				
				for(int yy = bottomY+1; yy <= bottomY+height-1; yy++){
					for(int a = -cubeHalfWidth; a <= cubeHalfWidth; a++){
						world.setBlock(x+a,yy,z-cubeHalfWidth,BlockList.persegrit);
						world.setBlock(x+a,yy,z+cubeHalfWidth,BlockList.persegrit);
						world.setBlock(x-cubeHalfWidth,yy,z+a,BlockList.persegrit);
						world.setBlock(x+cubeHalfWidth,yy,z+a,BlockList.persegrit);
					}
				}
				
				break;
				
			case LOTS_OF_CHESTS:
				for(int chests = 15+rand.nextInt(10+3*halfWidth)+5*halfWidth, xx, yy, zz, width = halfWidth*2-2, attempt, yTest, dir, normal, trapped; chests >= 0; chests--){
					for(attempt = 0; attempt < 4; attempt++){
						xx = x+rand.nextInt(width)-rand.nextInt(width);
						zz = z+rand.nextInt(width)-rand.nextInt(width);
						yy = bottomY+1;
						
						if (!world.isAir(xx,yy,zz)){
							for(yTest = 0; yTest < height-3; yTest++){
								if (world.isAir(xx,++yy,zz))break;
							}
							
							if (!world.isAir(xx,yy,zz))continue;
						}
						
						normal = 0;
						trapped = 0;
						
						for(dir = 0; dir < 4; dir++){
							Block block = world.getBlock(xx+Direction.offsetX[dir],yy,zz+Direction.offsetZ[dir]);
							
							if (block == Blocks.chest)++normal;
							else if (block == Blocks.trapped_chest)++trapped;
						}
						
						if (normal == 0 && trapped == 0)world.setBlock(xx,yy,zz,rand.nextBoolean() ? Blocks.chest : Blocks.trapped_chest);
						else if (normal == 1 && trapped == 0)world.setBlock(xx,yy,zz,Blocks.trapped_chest);
						else if (normal == 0 && trapped == 1)world.setBlock(xx,yy,zz,Blocks.chest);
						else continue;
						
						if (rand.nextInt(10) == 0){
							if (rand.nextInt(6) == 0)world.setTileEntityGenerator(xx,yy,zz,"CellarChestRare",this);
							else world.setTileEntityGenerator(xx,yy,zz,"CellarChestNormal",this);
						}
						
						break;
					}
				}
				
				break;
				
			case FLOATING_CUBES:
				for(int cubes = 5+rand.nextInt(10), size, attempt, xx, yy, zz, px, py, pz, xzSpace, ySpace; cubes >= 0; cubes--){
					Block randBlock = endermanSolidBlocks[rand.nextInt(endermanSolidBlocks.length)];
					size = 2+rand.nextInt(3+rand.nextInt(2));
					
					if ((xzSpace = halfWidth*2-1-size) < 1)continue;
					if ((ySpace = height-1-size) < 1)continue;
					
					for(attempt = 0; attempt < 20; attempt++){
						xx = x-halfWidth+1+rand.nextInt(xzSpace);
						yy = bottomY+1+rand.nextInt(ySpace);
						zz = z-halfWidth+1+rand.nextInt(xzSpace);
						
						boolean canGenerate = true;
						
						for(px = xx-1; px <= xx+size+1; px++){
							for(py = yy-1; py <= yy+size+1; py++){
								for(pz = zz-1; zz <= zz+size+1; pz++){
									if (world.getBlock(px,py,pz) != BlockList.persegrit){
										canGenerate = false;
										px += 99;
										py += 99;
										pz += 99;
									}
								}
							}
						}
						
						if (canGenerate){
							for(px = xx; px <= xx+size; px++){
								for(py = yy; py <= yy+size; py++){
									for(pz = zz; zz <= zz+size; pz++){
										world.setBlock(px,py,pz,randBlock);
									}
								}
							}
							
							break;
						}
					}
				}
				
				break;
		}
	}
	
	private void genHall(int x1, int z1, int x2, int z2, int bottomY, int height, Random rand){
		for(int xx = Math.min(x1,x2), xMax = Math.max(x1,x2); xx <= xMax; xx++){
			for(int zz = Math.min(z1,z2), zMax = Math.max(z1,z2); zz <= zMax; zz++){
				for(int yy = bottomY; yy <= bottomY+height; yy++){
					boolean edge = xx == x1 || xx == x2 || zz == z1 || zz == z2 || yy == bottomY || yy == bottomY+height;
					
					if (!edge || world.getBlock(xx,yy,zz) != Blocks.air){
						world.setBlock(xx,yy,zz,edge ? BlockList.persegrit : Blocks.air);
						if (edge && rand.nextInt(60) == 0)patternBlocks.add(new BlockLocation(xx,yy,zz));
					}
				}
			}
		}
	}
	
	private void genPatterns(Random rand){
		List<BlockLocation> connections = new ArrayList<>();
		
		for(BlockLocation loc:patternBlocks){
			int x = loc.x, y = loc.y, z = loc.z, addX = 0, addY = 0, addZ = 0, iterations = 6+rand.nextInt(30+rand.nextInt(60));
			boolean wall = true;
			
			if (!isWall(x,y,z)){
				if (rand.nextBoolean())addX = rand.nextInt(2)*2-1;
				else addZ = rand.nextInt(2)*2-1;
				
				wall = false;
			}
			else if (world.getBlock(x-1,y,z) != BlockList.persegrit || world.getBlock(x+1,y,z) != BlockList.persegrit){
				if (rand.nextInt(3) != 0)addY = rand.nextInt(2)*2-1;
				else addZ = rand.nextInt(2)*2-1;
			}
			else if (world.getBlock(x,y,z-1) != BlockList.persegrit || world.getBlock(x,y,z+1) != BlockList.persegrit){
				if (rand.nextInt(3) != 0)addY = rand.nextInt(2)*2-1;
				else addX = rand.nextInt(2)*2-1;
			}
			
			for(int iteration = 0; iteration <= iterations; iteration++){
				x += addX;
				y += addY;
				z += addZ;
				
				if (isCorner(x,y,z)){
					if (addX != 0){
						addZ = world.getBlock(x,y,z-1) == BlockList.persegrit ? -1 : 1;
						addX = 0;
					}
					else if (addZ != 0){
						addX = world.getBlock(x-1,y,z) == BlockList.persegrit ? -1 : 1;
						addZ = 0;
					}
				}
				else if (rand.nextInt(12) == 0){
					int newAddX = 0, newAddY = 0, newAddZ = 0;
					
					if (!isWall(x,y,z)){
						if (rand.nextBoolean())newAddX = rand.nextBoolean() ? -1 : 1;
						else newAddZ = rand.nextBoolean() ? -1 : 1;
					}
					else if (world.getBlock(x-1,y,z) != BlockList.persegrit || world.getBlock(x+1,y,z) != BlockList.persegrit){
						if (rand.nextBoolean())newAddY = rand.nextInt(2)*2-1;
						else newAddZ = rand.nextInt(2)*2-1;
					}
					else if (world.getBlock(x,y,z-1) != BlockList.persegrit || world.getBlock(x,y,z+1) != BlockList.persegrit){
						if (rand.nextBoolean())newAddX = rand.nextInt(2)*2-1;
						else newAddY = rand.nextInt(2)*2-1;
					}
					
					if (!isCorner(x+newAddX,y+newAddY,z+newAddZ)){
						addX = newAddX;
						addY = newAddY;
						addZ = newAddZ;
					}
				}
				
				if (world.getBlock(x,y,z) == BlockList.persegrit){
					world.setBlock(x,y,z,BlockList.persegrit,15);
					connections.add(new BlockLocation(x,y,z));
				}
				else break;
			}
		}
		
		for(BlockLocation loc:connections){
			world.setBlock(loc.x,loc.y,loc.z,BlockList.persegrit,BlockPersegrit.getConnectionMeta(world,rand,loc.x,loc.y,loc.z));
		}
	}
	
	private boolean isCorner(int x, int y, int z){
		Block pg = BlockList.persegrit;
		
		Block l = world.getBlock(x-1,y,z), r = world.getBlock(x+1,y,z),
			  u = world.getBlock(x,y,z-1), d = world.getBlock(x,y,z+1),
			  b = world.getBlock(x,y-1,z), t = world.getBlock(x,y+1,z);
		
		return (((l == pg && r == pg) || (u == pg && d == pg)) && ((b == pg && t != pg) || (b != pg && t == pg))) ||
				(t == pg && b == pg && ((l == pg && r != pg) || (u == pg && d != pg) || (l != pg && r == pg) || (u != pg && d == pg)));
	}
	
	private boolean isWall(int x, int y, int z){
		return world.getBlock(x,y-1,z) == BlockList.persegrit || world.getBlock(x,y+1,z) == BlockList.persegrit;
	}
	
	private final class RoomInfo{
		final int x, z;
		final byte halfWidth;
		final boolean[] canUseSide = new boolean[]{ true, true, true, true };
		
		RoomInfo(int x, int z, int halfWidth){
			this.x = x;
			this.z = z;
			this.halfWidth = (byte)halfWidth;
		}
		
		int getUseableSide(Random rand){
			if (!canUseSide[0] && !canUseSide[1] && !canUseSide[2] && !canUseSide[3])return -1;
			
			for(int attempt = 0, side; attempt < 8; attempt++){
				if (canUseSide[side = rand.nextInt(4)])return side;
			}
			
			for(int side = 0; side < 4; side++){
				if (canUseSide[side])return side;
			}
			
			return -1; // should never happen
		}
	}

	@Override
	public void onTileEntityRequested(String key, TileEntity tile, Random rand){
		if (key.equals("CellarChestNormal")){
			TileEntityChest chest = (TileEntityChest)tile;
			
			for(int amount = 0; amount < 1+rand.nextInt(4+rand.nextInt(3)); amount++){
				chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),normalChest.generateIS(rand));
			}
		}
		else if (key.equals("CellarChestRare")){
			TileEntityChest chest = (TileEntityChest)tile;
			
			for(int amount = 0; amount < 3+rand.nextInt(6); amount++){
				chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),rareChest.generateIS(rand));
			}
		}
	}
}
