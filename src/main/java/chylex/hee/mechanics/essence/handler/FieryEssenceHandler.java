package chylex.hee.mechanics.essence.handler;
import static chylex.hee.mechanics.essence.SocketManager.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityFurnace;
import chylex.hee.api.interfaces.IAcceptFieryEssence;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C11ParticleAltarOrb;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEssenceAltar;

public class FieryEssenceHandler extends AltarActionHandler{
	private byte essenceUsageCounter;
	
	public FieryEssenceHandler(TileEntityEssenceAltar altar){
		super(altar);
	}
	
	@Override
	public void onUpdate(){
		int level = altar.getEssenceLevel();
		int n = 35+Math.min(60,level>>3);
		boolean drained = false;
		
		byte socketEffects = getSocketEffects(altar),socketBoost = getSocketBoost(altar);
		
		int range = 12+((socketEffects&EFFECT_RANGE_INCREASE) == EFFECT_RANGE_INCREASE?MathUtil.floor(Math.sqrt(socketBoost*3D))*2:0);
		n += (range-12)*2;
		
		if ((socketEffects&EFFECT_SPEED_BOOST) == EFFECT_SPEED_BOOST)n += socketBoost*3;
		
		for(int a = 0,xx,yy,zz; a < n; a++){
			xx = altar.xCoord+rand.nextInt(range)-(range>>1);
			yy = altar.yCoord+rand.nextInt(5)-2;
			zz = altar.zCoord+rand.nextInt(range)-(range>>1);
			
			Block block = altar.getWorldObj().getBlock(xx,yy,zz);
			TileEntity tile = altar.getWorldObj().getTileEntity(xx,yy,zz);
			drained = false;
			
			if (block == Blocks.lit_furnace || tile instanceof TileEntityFurnace){
				TileEntityFurnace furnace = (TileEntityFurnace)tile;
				
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
			else if (tile instanceof IAcceptFieryEssence){
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
	
	private boolean tryDrainEssence(){
		if (++essenceUsageCounter > 60+((getSocketEffects(altar)&EFFECT_LOWER_COST) == EFFECT_LOWER_COST ? 3+MathUtil.floor(getSocketBoost(altar)*0.7D) : 0)){
			essenceUsageCounter = 0;
			altar.drainEssence(1);
			return true;
		}
		
		return false;
	}
	
	private void createOrbParticle(int targetX, int targetY, int targetZ){
		PacketPipeline.sendToAllAround(altar,64D,new C11ParticleAltarOrb(altar,targetX+0.5D,targetY+0.5D,targetZ+0.5D));
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
