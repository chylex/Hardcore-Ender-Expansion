/*package chylex.hee.world.structure.island.biome.feature.island;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import chylex.hee.block.BlockPersegrit;
import chylex.hee.init.BlockList;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.collections.weight.ObjectWeightPair;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;
import chylex.hee.world.util.Direction;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

public class StructureHiddenCellar extends AbstractIslandStructure implements ITileEntityGenerator{
	public enum EnchantedIslandVariation{
		HOMELAND, LABORATORY
	}
	
	private static final byte[] horCheckX = new byte[]{ -1, 0, 1, -1, 1, -1, 0, 1 },
								horCheckZ = new byte[]{ -1, -1, -1, 0, 0, 1, 1, 1 };
	
	private static final Block[] endermanSolidBlocks = new Block[]{
		Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.gravel, Blocks.tnt, Blocks.clay, Blocks.pumpkin, Blocks.melon_block, Blocks.mycelium
	};
	
	// TODO
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
	}).addItemPostProcessor((is, rand) -> {
		if (is.getItem() == ItemList.knowledge_note)ItemKnowledgeNote.setRandomNote(is,rand,4);
		return is;
	});
	
	public static final WeightedLootList[] normalChestVariation = new WeightedLootList[]{
		normalChest.copy().addAll(new LootItemStack[]{
			new LootItemStack(ItemList.arcane_shard).setAmount(1,2).setWeight(8)
		}),
		
		normalChest.copy().addAll(new LootItemStack[]{
			new LootItemStack(ItemList.obsidian_fragment).setAmount(1,3).setWeight(9),
			new LootItemStack(ItemList.auricion).setAmount(1,2).setWeight(6)
		})
	};
	
	private static final WeightedLootList rareChest = new WeightedLootList(new LootItemStack[]{
		new LootItemStack(ItemList.enhanced_ender_pearl).setAmount(3,9).setWeight(20),
		new LootItemStack(ItemList.end_powder).setAmount(5,12).setWeight(12),
		new LootItemStack(Items.diamond).setAmount(2,5).setWeight(10),
		new LootItemStack(ItemList.knowledge_note).setWeight(7),
		new LootItemStack(Items.ender_pearl).setAmount(3,9).setWeight(5),
		new LootItemStack(ItemList.temple_caller).setWeight(5)
	}).addItemPostProcessor((is, rand) -> {
		if (is.getItem() == ItemList.knowledge_note)ItemKnowledgeNote.setRandomNote(is,rand,6);
		else if (is.getItem() == ItemList.enhanced_ender_pearl){
			List<EnderPearlEnhancements> availableTypes = CollectionUtil.newList(EnderPearlEnhancements.values());
			
			for(int a = 0; a < 1+Math.abs(Math.round(rand.nextDouble()*rand.nextGaussian()*3.2D)); a++){
				is = EnhancementHandler.addEnhancement(is,availableTypes.remove(rand.nextInt(availableTypes.size())));
				if (availableTypes.isEmpty())break;
			}
		}
		
		return is;
	});
	
	public static final WeightedLootList[] rareChestVariation = new WeightedLootList[]{
		rareChest.copy().addAll(new LootItemStack[]{
			
		}),
		
		rareChest.copy().addAll(new LootItemStack[]{
			new LootItemStack(ItemList.auricion).setWeight(5)
		})
	};
	
	private EnchantedIslandVariation variation;
	private final List<BlockPosM> patternBlocks = new ArrayList<>();
	
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
			
			for(int roomAttemptsLeft = 70+rand.nextInt(110), roomsLeft = 5+rand.nextInt(16), side, dist, hWidth, offX, offZ; roomAttemptsLeft > 0 && roomsLeft > 0; roomAttemptsLeft--){
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
			
			for(RoomInfo room:rooms)genRoomContent(room,y,height,rand);
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
					if (edge && rand.nextInt(60) == 0)patternBlocks.add(new BlockPosM(xx,yy,zz));
				}
			}
		}
		
		return new RoomInfo(x,z,halfWidth);
	}
	
	private enum EnumRoomContent{
		NONE, CONNECTING_LINES, SPIKES, PERSEGRIT_CUBE, LOTS_OF_CHESTS, FLOATING_CUBES, CHAOTIC_PERSEGRIT,
		CHEST_PILLARS
	}
	
	private static final WeightedList<ObjectWeightPair<EnumRoomContent>> roomContentList = new WeightedList<>(
		ObjectWeightPair.of(EnumRoomContent.NONE, 55),
		ObjectWeightPair.of(EnumRoomContent.CONNECTING_LINES, 9),
		ObjectWeightPair.of(EnumRoomContent.SPIKES, 9),
		ObjectWeightPair.of(EnumRoomContent.PERSEGRIT_CUBE, 8),
		ObjectWeightPair.of(EnumRoomContent.LOTS_OF_CHESTS, 7),
		ObjectWeightPair.of(EnumRoomContent.FLOATING_CUBES, 5),
		ObjectWeightPair.of(EnumRoomContent.CHEST_PILLARS, 4),
		ObjectWeightPair.of(EnumRoomContent.CHAOTIC_PERSEGRIT, 4)
	);
	
	private void genRoomContent(RoomInfo room, int bottomY, int height, Random rand){
		int x = room.x, z = room.z, halfWidth = room.halfWidth;
		
		if (halfWidth == 2)return;
		
		EnumRoomContent type = roomContentList.getRandomItem(rand).getObject();
		
		switch(type){
			case CONNECTING_LINES:
				List<BlockPosM> toPersegrit = new ArrayList<BlockPosM>();
				
				for(int lines = 3+((halfWidth*(height-2))>>1)+rand.nextInt(6+halfWidth*2+height), width = halfWidth*2-2, dir, xx, yy, zz, addX = 0, addY = 0, addZ = 0, a, b; lines > 0; lines--){
					dir = rand.nextInt(4);
					addX = addY = addZ = 0;
					
					if (dir == 0)addX = rand.nextBoolean() ? -1 : 1;
					else if (dir == 1)addZ = rand.nextBoolean() ? -1 : 1;
					else addY = rand.nextBoolean() ? -1 : 1;
					
					for(a = 0; a < 10; a++){
						xx = x+rand.nextInt(width)-(width>>1);
						yy = bottomY+1+rand.nextInt(height-1);
						zz = z+rand.nextInt(width)-(width>>1);
						
						if (world.getBlock(xx-1,yy,zz) == Blocks.bedrock || world.getBlock(xx+1,yy,zz) == Blocks.bedrock ||
							world.getBlock(xx,yy-1,zz) == Blocks.bedrock || world.getBlock(xx,yy+1,zz) == Blocks.bedrock ||
							world.getBlock(xx,yy,zz-1) == Blocks.bedrock || world.getBlock(xx,yy,zz+1) == Blocks.bedrock ||
							!world.isAir(xx,yy,zz))continue;
						
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
							world.setBlock(xx,yy,zz,Blocks.bedrock);
							toPersegrit.add(new BlockPosM(xx,yy,zz));
						}
						
						break;
					}
				}
				
				for(BlockPosM loc:toPersegrit){
					world.setBlock(loc.x,loc.y,loc.z,BlockList.persegrit);
					if (rand.nextInt(60) == 0)patternBlocks.add(loc);
				}
				
				break;
				
			case SPIKES:
				for(int spikes = 2+halfWidth+rand.nextInt(4+3*halfWidth), xx, yy, zz, width = halfWidth*2-2, addY, spikeHeight; spikes > 0; spikes--){
					xx = x+rand.nextInt(width)-(width>>1);
					yy = bottomY+(rand.nextBoolean() ? 1 : height-1);
					zz = z+rand.nextInt(width)-(width>>1);
					
					if (world.isAir(xx,yy,zz)){
						addY = yy == bottomY+1 ? 1 : -1;
						
						for(spikeHeight = 1+rand.nextInt(height-3); spikeHeight > 0; spikeHeight--){
							if ((xx == x-(halfWidth-1) || world.isAir(xx-1,yy+addY,zz)) && (xx == x-(halfWidth+1) || world.isAir(xx+1,yy+addY,zz)) &&
								(zz == z-(halfWidth-1) || world.isAir(xx,yy+addY,zz-1)) && (zz == z-(halfWidth-1) || world.isAir(xx,yy+addY,zz+1)) &&
								world.isAir(xx,yy+addY,zz)){
								world.setBlock(xx,yy,zz,BlockList.persegrit);
								yy += addY;
							}
							else break;
						}
					}
				}
				
				break;
				
			case PERSEGRIT_CUBE:
				int cubeHalfWidth = Math.max(1,halfWidth-3-rand.nextInt(2));
				
				for(int yy = bottomY+1; yy <= bottomY+height-1; yy++){
					for(int a = -cubeHalfWidth; a <= cubeHalfWidth; a++){
						world.setBlock(x+a,yy,z-cubeHalfWidth,BlockList.persegrit);
						if (rand.nextInt(60) == 0)patternBlocks.add(new BlockPosM(x+a,yy,z-cubeHalfWidth));
						
						world.setBlock(x+a,yy,z+cubeHalfWidth,BlockList.persegrit);
						if (rand.nextInt(60) == 0)patternBlocks.add(new BlockPosM(x+a,yy,z+cubeHalfWidth));
						
						world.setBlock(x-cubeHalfWidth,yy,z+a,BlockList.persegrit);
						if (rand.nextInt(60) == 0)patternBlocks.add(new BlockPosM(x-cubeHalfWidth,yy,z+a));
						
						world.setBlock(x+cubeHalfWidth,yy,z+a,BlockList.persegrit);
						if (rand.nextInt(60) == 0)patternBlocks.add(new BlockPosM(x+cubeHalfWidth,yy,z+a));
					}
				}
				
				break;
				
			case LOTS_OF_CHESTS:
				for(int chests = 10+rand.nextInt(8+3*halfWidth)+4*halfWidth, xx, yy, zz, width = halfWidth*2-2, attempt, yTest, dir, normal, trapped; chests > 0; chests--){
					for(attempt = 0; attempt < 4; attempt++){
						xx = x+rand.nextInt(width)-(width>>1);
						zz = z+rand.nextInt(width)-(width>>1);
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
						
						if (normal == 0 && trapped == 0)world.setBlock(xx,yy,zz,rand.nextBoolean() ? Blocks.chest : Blocks.trapped_chest,rand.nextInt(4));
						else if (normal == 1 && trapped == 0)world.setBlock(xx,yy,zz,Blocks.trapped_chest,rand.nextInt(4));
						else if (normal == 0 && trapped == 1)world.setBlock(xx,yy,zz,Blocks.chest,rand.nextInt(4));
						else continue;
						
						if (rand.nextInt(10) == 0){
							if (rand.nextInt(6) == 0)world.setTileEntityGenerator(xx,yy,zz,"CellarChestRare|"+variation.ordinal(),this);
							else world.setTileEntityGenerator(xx,yy,zz,"CellarChestNormal|"+variation.ordinal(),this);
						}
						
						break;
					}
				}
				
				break;
				
			case FLOATING_CUBES:
				for(int cubes = 5+rand.nextInt(10), size, attempt, xx, yy, zz, px, py, pz, xzSpace, ySpace; cubes > 0; cubes--){
					Block randBlock = endermanSolidBlocks[rand.nextInt(endermanSolidBlocks.length)];
					size = 1+rand.nextInt(3+rand.nextInt(2));
					
					if ((xzSpace = halfWidth*2-size) < 1)continue;
					if ((ySpace = height-size) < 1)continue;
					
					for(attempt = 0; attempt < 20; attempt++){
						xx = x-halfWidth+1+rand.nextInt(xzSpace);
						yy = bottomY+1+rand.nextInt(ySpace);
						zz = z-halfWidth+1+rand.nextInt(xzSpace);
						
						boolean canGenerate = true;
						
						for(px = xx-1; px <= xx+size; px++){
							for(py = yy-1; py <= yy+size; py++){
								for(pz = zz-1; pz <= zz+size; pz++){
									Block block = world.getBlock(px,py,pz);
									
									if (block != Blocks.air && block != BlockList.persegrit){
										canGenerate = false;
										px += 99;
										py += 99;
										pz += 99;
									}
								}
							}
						}
						
						if (canGenerate){
							for(px = xx; px < xx+size; px++){
								for(py = yy; py < yy+size; py++){
									for(pz = zz; pz < zz+size; pz++){
										world.setBlock(px,py,pz,randBlock);
									}
								}
							}
							
							break;
						}
					}
				}
				
				break;
			
			case CHEST_PILLARS:
				for(int amount = 2+room.halfWidth*(room.halfWidth-2)+rand.nextInt(5+room.halfWidth*3), width = halfWidth*2-2, xx, yy, zz; amount > 0; amount--){
					xx = x-halfWidth+1+rand.nextInt(halfWidth*2-2);
					zz = z-halfWidth+1+rand.nextInt(halfWidth*2-2);
					yy = bottomY+2+rand.nextInt(height-3);
					
					if (world.isAir(xx-1,bottomY+1,zz) && world.isAir(xx+1,bottomY+1,zz) &&
						world.isAir(xx,bottomY+1,zz-1) && world.isAir(xx,bottomY+1,zz+1)){
						world.setBlock(xx,yy,zz,Blocks.chest);
						if (rand.nextInt(5) <= 1)world.setTileEntityGenerator(xx,yy,zz,(rand.nextInt(4) == 0 ? "CellarChestRare" : "CellarChestNormal")+"|"+variation.ordinal(),this);
						for(--yy; yy >= bottomY+1; yy--)world.setBlock(xx,yy,zz,BlockList.persegrit);
					}
				}
				
				break;
				
			case CHAOTIC_PERSEGRIT:
				for(int amount = MathUtil.ceil(Math.sqrt(room.halfWidth*room.halfWidth*(height-1))*(0.85D+rand.nextDouble()*0.5D)), width = halfWidth*2-2; amount > 0; amount--){
					world.setBlock(x+rand.nextInt(width)-(width>>1),bottomY+1+rand.nextInt(height-1),z+rand.nextInt(width)-(width>>1),BlockList.persegrit);
				}
				
				break;
				
			default:
		}
	}
	
	private void genHall(int x1, int z1, int x2, int z2, int bottomY, int height, Random rand){
		for(int xx = Math.min(x1,x2), xMax = Math.max(x1,x2); xx <= xMax; xx++){
			for(int zz = Math.min(z1,z2), zMax = Math.max(z1,z2); zz <= zMax; zz++){
				for(int yy = bottomY; yy <= bottomY+height; yy++){
					boolean edge = xx == x1 || xx == x2 || zz == z1 || zz == z2 || yy == bottomY || yy == bottomY+height;
					
					if (!edge || world.getBlock(xx,yy,zz) != Blocks.air){
						world.setBlock(xx,yy,zz,edge ? BlockList.persegrit : Blocks.air);
						if (edge && rand.nextInt(60) == 0)patternBlocks.add(new BlockPosM(xx,yy,zz));
					}
				}
			}
		}
	}
	
	private void genPatterns(Random rand){
		List<BlockPosM> connections = new ArrayList<>();
		
		for(BlockPosM loc:patternBlocks){
			int x = loc.x, y = loc.y, z = loc.z, addX = 0, addY = 0, addZ = 0, iterations = 6+rand.nextInt(30+rand.nextInt(60));
			
			if (!isWall(x,y,z)){
				if (rand.nextBoolean())addX = rand.nextInt(2)*2-1;
				else addZ = rand.nextInt(2)*2-1;
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
					connections.add(new BlockPosM(x,y,z));
				}
				else break;
			}
		}
		
		for(BlockPosM loc:connections){
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
		// TODO
		if (key.startsWith("CellarChestNormal|")){
			WeightedLootList loot = normalChestVariation[DragonUtil.tryParse(key.split("\\|")[1],0)];
			TileEntityChest chest = (TileEntityChest)tile;
			
			for(int amount = 0; amount < 1+rand.nextInt(4+rand.nextInt(3)); amount++){
				chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),loot.generateIS(rand));
			}
		}
		else if (key.startsWith("CellarChestRare|")){
			WeightedLootList loot = rareChestVariation[DragonUtil.tryParse(key.split("\\|")[1],0)];
			TileEntityChest chest = (TileEntityChest)tile;
			
			for(int amount = 0; amount < 3+rand.nextInt(6); amount++){
				chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),loot.generateIS(rand));
			}
		}
	}
}
*/