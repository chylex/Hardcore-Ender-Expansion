package chylex.hee.mechanics.essence.handler;
import static chylex.hee.mechanics.essence.SocketManager.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityFurnace;
import chylex.hee.api.interfaces.IAcceptFieryEssence;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.essence.SocketManager;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C17ParticleAltarOrb;
import chylex.hee.tileentity.TileEntityEssenceAltar;

public class FieryEssenceHandler extends AltarActionHandler{
	private byte essenceUsageCounter = 0;
	
	public FieryEssenceHandler(TileEntityEssenceAltar altar){
		super(altar);
	}
	
	@Override
	public void onUpdate(){
		short level = altar.getEssenceLevel();
		int n = 35+Math.min(60,level>>3);
		boolean drained = false;
		
		byte socketEffects = SocketManager.getSocketEffects(altar),socketBoost = SocketManager.getSocketBoost(altar);
		
		int range = 12+((socketEffects&EFFECT_RANGE_INCREASE) == EFFECT_RANGE_INCREASE?(int)Math.floor(Math.sqrt(socketBoost*3D))*2:0);
		n += (range-12)*2;
		
		if ((socketEffects&EFFECT_SPEED_BOOST) == EFFECT_SPEED_BOOST)n += socketBoost*3;
		
		for(int a = 0,xx,yy,zz; a < n; a++){
			xx = altar.xCoord+rand.nextInt(range)-(range>>1);
			yy = altar.yCoord+rand.nextInt(5)-2;
			zz = altar.zCoord+rand.nextInt(range)-(range>>1);
			
			Block block = altar.getWorldObj().getBlock(xx,yy,zz);
			drained = false;
			
			if (block == Blocks.lit_furnace){
				TileEntityFurnace furnace = (TileEntityFurnace)altar.getWorldObj().getTileEntity(xx,yy,zz);
				
				if (furnace != null && furnace.isBurning() && canFurnaceSmelt(furnace)){
					n = 1+Math.min(8,level>>6)+((socketEffects&EFFECT_SPEED_BOOST) == EFFECT_SPEED_BOOST?(socketBoost>>2):0);
					for(int b = 0; b < n; b++){
						if (furnace.furnaceCookTime < 199){
							++furnace.furnaceCookTime;
							
							if (tryDrainEssence()){
								drained = true;
								if (--level <= 0)break;
							}
						}
						else break;
					}
					
					if (drained && rand.nextInt(6+(n>>1)) <= 4)createOrbParticle(xx,yy,zz);
					return;
				}
			}
			/*else if (block == BlockList.infestation_cauldron && CauldronState.getByMeta(altar.getWorldObj().getBlockMetadata(xx,yy,zz)).isCooking&&
					 altar.getWorldObj().getBlock(xx,yy-1,zz) == Blocks.fire && rand.nextInt(Math.max(5,30-level>>4)) == 0){
				BlockList.infestation_cauldron.updateTick(altar.getWorldObj(),xx,yy,zz,rand);
				
				for(int drain = 0; drain < 32; drain++){
					if (tryDrainEssence())break;
				}
				
				createOrbParticle(xx,yy,zz);
				return;
			}*/
			else if (block == Blocks.brewing_stand){
				TileEntityBrewingStand stand = (TileEntityBrewingStand)altar.getWorldObj().getTileEntity(xx,yy,zz);
				
				if (stand != null && stand.getBrewTime() > 1 && stand.getBrewTime() != 400){
					n = 1+Math.min(5,level>>6);
					for(int b = 0; b < n; b++){
						stand.updateEntity();
						
						if (tryDrainEssence()){
							drained = true;
							if (--level <= 0)break;
						}
						
						if (stand.getBrewTime() <= 1)break;
					}
				}
			}
			else{
				TileEntity tile = altar.getWorldObj().getTileEntity(xx,yy,zz);
				
				if (tile != null && tile instanceof IAcceptFieryEssence){
					IAcceptFieryEssence acceptor = (IAcceptFieryEssence)tile;
					n = acceptor.getBoostAmount(level);
					
					for(int b = 0; b < n; b++){
						acceptor.boost();
						
						if (tryDrainEssence()){
							drained = true;
							if (--level <= 0)break;
						}
					}
				}
			}
		}
	}
	
	private boolean tryDrainEssence(){
		if (++essenceUsageCounter > 60+((SocketManager.getSocketEffects(altar)&EFFECT_LOWER_COST) == EFFECT_LOWER_COST?3+(int)Math.floor(SocketManager.getSocketBoost(altar)*0.7D):0)){
			essenceUsageCounter = 0;
			altar.drainEssence(1);
			return true;
		}
		
		return false;
	}
	
	private void createOrbParticle(int targetX, int targetY, int targetZ){
		PacketPipeline.sendToAllAround(altar,64D,new C17ParticleAltarOrb(altar,targetX+0.5D,targetY+0.5D,targetZ+0.5D));
		
		if (rand.nextInt(17) == 0){
			for(EntityPlayer observer:ObservationUtil.getAllObservers(altar.getWorldObj(),altar.xCoord+0.5D,altar.yCoord+0.5D,altar.zCoord+0.5D,16D)){
				EssenceType.FIERY.getAltarRegistration().tryUnlockFragment(observer,0.15F);
			}
		}
	}
	
	@Override
	public void onTileWriteToNBT(NBTTagCompound nbt){
		nbt.setByte("F_essenceUsageCnt",essenceUsageCounter);
	}
	
	@Override
	public void onTileReadFromNBT(NBTTagCompound nbt){
		essenceUsageCounter = nbt.getByte("F_essenceUsageCnt");
	}
	
	// TileEntityFurnace.canSmelt()

	private boolean canFurnaceSmelt(TileEntityFurnace furnace){
		if (furnace.getStackInSlot(0) == null)return false;
		
		ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(furnace.getStackInSlot(0));
		if (itemstack == null)return false;
		
		ItemStack input = furnace.getStackInSlot(2);
		if (input == null)return true;
		if (!input.isItemEqual(itemstack))return false;
		int result = input.stackSize+itemstack.stackSize;
		return result <= furnace.getInventoryStackLimit() && result <= input.getMaxStackSize();
	}
}
