package chylex.hee.entity.technical;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.entity.fx.FXType;
import chylex.hee.init.BlockList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.util.MathUtil;

public class EntityTechnicalPuzzleSolved extends EntityTechnicalBase{
	private int minX, minZ, maxX, maxZ;
	private final List<Pos> locs = new ArrayList<>();
	private byte appearTimer;
	
	public EntityTechnicalPuzzleSolved(World world){
		super(world);
	}
	
	public EntityTechnicalPuzzleSolved(World world, int x, int y, int z, int minX, int minZ, int maxX, int maxZ){
		super(world);
		setPosition(x+0.5D,y+0.5D,z+0.5D);
		this.minX = minX;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxZ = maxZ;
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote)return;
		
		if (ticksExisted == 1 && appearTimer == 0){
			Pos.forEachBlock(Pos.at(minX,posY,minZ),Pos.at(maxX,posY,maxZ),pos -> {
				if (pos.getBlock(worldObj) == BlockList.dungeon_puzzle && pos.getMetadata(worldObj) != BlockDungeonPuzzle.metaDisabled){
					locs.add(pos.immutable());
				}
			});
		}
		else if (!locs.isEmpty() && ticksExisted%4 == 0){
			for(int a = 0; a < 1+rand.nextInt(3) && !locs.isEmpty(); a++){
				Pos loc = locs.remove(rand.nextInt(locs.size()));
				loc.setMetadata(worldObj,BlockDungeonPuzzle.metaDisabled);
				worldObj.addBlockEvent(loc.getX(),loc.getY(),loc.getZ(),BlockList.dungeon_puzzle,69,0);
			}
		}
		else if (locs.isEmpty() && appearTimer < 12 && ++appearTimer == 12){
			Pos.at(this).setMetadata(worldObj,BlockDungeonPuzzle.metaPortal);
			worldObj.addBlockEvent(MathUtil.floor(posX),MathUtil.floor(posY),MathUtil.floor(posZ),BlockList.dungeon_puzzle,69,1);
			appearTimer = 69;
		}
		else if (appearTimer == 69){
			if (Pos.at(this).getBlock(worldObj) != BlockList.dungeon_puzzle)setDead();
			else if (worldObj.getClosestPlayerToEntity(this,1D) != null){
				for(EntityPlayer player:EntitySelector.players(worldObj,AxisAlignedBB.getBoundingBox(minX,posY,minZ,maxX,posY+3D,maxZ))){
					Pos pos = Pos.at(player.posX,posY,player.posZ);
					
					if (pos.getBlock(worldObj) == BlockList.dungeon_puzzle){
						if (player.isRiding())player.mountEntity(null);
						
						double prevX = player.posX, prevY = player.posY, prevZ = player.posZ;
						player.setPositionAndUpdate(pos.getX()+0.5D,worldObj.getTopSolidOrLiquidBlock(pos.getX(),pos.getZ()),pos.getZ()+0.5D);
						player.fallDistance = 0F;
						PacketPipeline.sendToAllAround(this,64D,new C22EffectLine(FXType.Line.DUNGEON_PUZZLE_TELEPORT,player.posX,player.posY,player.posZ,prevX,prevY,prevZ));
					}
				}
				
				Pos pos = Pos.at(this);
				
				EntityMiniBossFireFiend fiend = new EntityMiniBossFireFiend(worldObj);
				fiend.setLocationAndAngles(pos.getX()+0.5D+(rand.nextDouble()-0.5D)*18D,worldObj.getTopSolidOrLiquidBlock(pos.getX(),pos.getZ())+10,pos.getZ()+0.5D+(rand.nextDouble()-0.5D)*18D,rand.nextFloat()*360F,0F);
				worldObj.spawnEntityInWorld(fiend);
				
				pos.setMetadata(worldObj,BlockDungeonPuzzle.metaDisabled);
				setDead();
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		nbt.setInteger("x1",minX);
		nbt.setInteger("z1",minZ);
		nbt.setInteger("x2",maxX);
		nbt.setInteger("z2",maxZ);
		nbt.setByte("appearTim",appearTimer);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		minX = nbt.getInteger("x1");
		minZ = nbt.getInteger("z1");
		maxX = nbt.getInteger("x2");
		maxZ = nbt.getInteger("z2");
		appearTimer = nbt.getByte("appearTim");
	}
}
