package chylex.hee.world.feature.stronghold.rooms.traps;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.EntityMobSilverfish;
import chylex.hee.entity.technical.EntityTechnicalTrigger;
import chylex.hee.entity.technical.EntityTechnicalTrigger.TriggerBase;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.Skull;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.logging.Log;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.structure.util.IStructureTileEntity;
import chylex.hee.world.util.Size;
import chylex.hee.world.util.BoundingBox;

public class StrongholdRoomPrisonTrap extends StrongholdRoom{
	public static StrongholdRoomPrisonTrap[] generatePrisons(){
		return Arrays.stream(Facing4.list).map(facing -> new StrongholdRoomPrisonTrap(facing)).toArray(StrongholdRoomPrisonTrap[]::new);
	}
	
	private final Facing4 facing;
	
	public StrongholdRoomPrisonTrap(Facing4 facing){
		super(new Size(facing.getX() != 0 ? 10 : 11,5,facing.getZ() != 0 ? 10 : 11),null);
		
		if (facing.getZ() != 0){
			addConnection(Facing4.NORTH_NEGZ,5-3*facing.getZ(),0,0,fromRoom);
			addConnection(Facing4.SOUTH_POSZ,5-3*facing.getZ(),0,9,fromRoom);
		}
		else{
			addConnection(Facing4.WEST_NEGX,0,0,5+3*facing.getX(),fromRoom);
			addConnection(Facing4.EAST_POSX,9,0,5+3*facing.getX(),fromRoom);
		}
		
		this.facing = facing;
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);

		PosMutable posStart = new PosMutable(), posEnd = new PosMutable();
		Connection connection = connections.stream().filter(testConnection -> testConnection.facing.opposite() == this.facing).findFirst().get();
		
		// prison wall
		posStart.set(x+connection.offsetX,0,z+connection.offsetZ).move(facing).move(facing.rotateLeft(),2);
		posEnd.set(posStart).move(facing,7);
		placeLine(world,rand,placeStoneBrick,posStart.x,y+4,posStart.z,posEnd.x,y+4,posEnd.z); // top line
		placeLine(world,rand,placeStoneBrick,posStart.x,y+1,posStart.z,posStart.x,y+3,posStart.z); // first part of arch
		
		posStart.move(facing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.iron_door,Meta.getDoor(facing.rotateLeft(),false)),posStart.x,y+1,posStart.z); // bottom door
		placeBlock(world,rand,IBlockPicker.basic(Blocks.iron_door,Meta.getDoor(facing.rotateLeft(),true)),posStart.x,y+2,posStart.z); // top door
		placeBlock(world,rand,placeStoneBrick,posStart.x,y+3,posStart.z); // top part of arch
		
		posStart.move(facing);
		placeLine(world,rand,placeStoneBrick,posStart.x,y+1,posStart.z,posStart.x,y+3,posStart.z); // second part of arch
		
		posStart.move(facing.rotateRight());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_button,Meta.getButton(facing.rotateLeft())),posStart.x,y+2,posStart.z); // door button
		posStart.move(facing.rotateLeft());
		
		posStart.move(facing);
		posEnd.set(posStart).move(facing,4);
		placeLine(world,rand,IBlockPicker.basic(Blocks.iron_bars),posStart.x,y+1,posStart.z,posEnd.x,y+3,posEnd.z); // prison bars
		
		// inside prison
		posStart.set(x+connection.offsetX,y+1,z+connection.offsetZ).move(facing).move(facing.rotateLeft(),3);
		posEnd.set(posStart).move(facing,7).move(facing.rotateLeft(),4).moveUp().moveUp();
		
		BoundingBox prison = new BoundingBox(posStart,posEnd);
		
		// spawner
		world.addEntity(new EntityTechnicalTrigger(null,0,0,0,new TriggerPrisonSilverfish(prison)));
		
		// redstone blood
		IBlockPicker placeRedstone = IBlockPicker.basic(Blocks.redstone_wire);
		
		for(int blood = 5+rand.nextInt(5+rand.nextInt(6)); blood > 0; blood--){
			placeBlock(world,rand,placeRedstone,prison.x1+rand.nextInt(1+prison.x2-prison.x1),y+1,prison.z1+rand.nextInt(1+prison.z2-prison.z1));
		}
		
		// skull
		boolean skullCorner = rand.nextBoolean();
		posStart.move(facing.rotateLeft(),4).move(facing,skullCorner ? 0 : 7);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.skull,Meta.skullGround),posStart.x,y+1,posStart.z);
		
		IStructureTileEntity skullTile = Meta.generateSkullGround(rand.nextInt(3) == 0 ? Skull.ZOMBIE : Skull.SKELETON,posStart,posStart.offset(facing.rotateRight()).offset(skullCorner ? facing : facing.opposite()));
		world.setTileEntity(posStart.x,y+1,posStart.z,skullTile);
	}
	
	public static class TriggerPrisonSilverfish extends TriggerBase{
		private BoundingBox checkBox;
		private byte checkTimer = 0, spawnsLeft = -1;
		
		public TriggerPrisonSilverfish(){}
		
		public TriggerPrisonSilverfish(BoundingBox checkBox){
			this.checkBox = checkBox;
		}
		
		@Override
		protected void update(EntityTechnicalTrigger entity, World world, Random rand){
			if (world.difficultySetting == EnumDifficulty.PEACEFUL)return;
			
			if (checkBox == null){
				Log.reportedError("Could not update TriggerPrisonSilverfish entity because the detection bounding box is null.");
				entity.setDead();
				return;
			}
			
			if (++checkTimer >= 14){
				checkTimer = 0;
				
				List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class,checkBox.toAABB().offset(entity.posX,entity.posY,entity.posZ));
				if (players.isEmpty())return;
				
				if (spawnsLeft == -1)spawnsLeft = (byte)(2+world.difficultySetting.getDifficultyId()/2);
				else if (spawnsLeft > 0){
					PosMutable mpos = new PosMutable();
					
					for(int attempt = 0, spawned = 2+rand.nextInt(2); attempt < 8; attempt++){
						mpos.set(entity);
						mpos.move(checkBox.x1+rand.nextInt(1+checkBox.x2-checkBox.x1),checkBox.y2+1,checkBox.z1+rand.nextInt(1+checkBox.z2-checkBox.z1));
						
						Block block = mpos.getBlock(world);
						
						if (block == Blocks.stonebrick || block == Blocks.monster_egg){
							EntityMobSilverfish silverfish = new EntityMobSilverfish(entity.worldObj);
							silverfish.setLocationAndAngles(mpos.x+0.5D,mpos.y,mpos.z+0.5D,rand.nextFloat()*360F-180F,0F);
							silverfish.setAttackTarget(players.get(rand.nextInt(players.size())));
							silverfish.setCanHideInBlocks(false);
							silverfish.setCanSummonSilverfish(true);
							entity.worldObj.spawnEntityInWorld(silverfish);
							
							mpos.breakBlock(entity.worldObj,false);
							PacketPipeline.sendToAllAround(silverfish,64D,new C21EffectEntity(FXType.Entity.ENTITY_EXPLOSION_PARTICLE,silverfish));
							
							if (--spawned == 0)break;
						}
					}
					
					--spawnsLeft;
				}
				else entity.setDead();
			}
		}
		
		@Override
		protected void save(NBTTagCompound nbt){
			nbt.setLong("bbTL",checkBox.getTopLeft().toLong());
			nbt.setLong("bbBR",checkBox.getBottomRight().toLong());
		}
		
		@Override
		protected void load(NBTTagCompound nbt){
			this.checkBox = new BoundingBox(Pos.at(nbt.getLong("bbTL")),Pos.at(nbt.getLong("bbBR")));
		}
	}
}
