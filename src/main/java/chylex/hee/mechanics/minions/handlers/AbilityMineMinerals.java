package chylex.hee.mechanics.minions.handlers;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.mob.EntityMobMinion;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AbilityMineMinerals extends AbstractAbilityHandler{
	private static final ItemStack diamondPickaxe = new ItemStack(Items.diamond_pickaxe);
	
	private int targetX,targetY,targetZ;
	private double tempTargetX,tempTargetY,tempTargetZ;
	private int breakingX,breakingY,breakingZ;
	private Block breakingBlock;
	private short breakingMeta,breakingTime = 0;
	private byte cooldown = 0;
	
	public AbilityMineMinerals(EntityMobMinion minion){
		super(minion);
	}

	@Override
	public void onUpdate(){
		if (cooldown > 0){
			--cooldown;
			return;
		}
		
		/*if (targetX != 0 && targetY != 0 && targetZ != 0){
			if (breakingTime >= 1){
				if (--breakingTime <= 0){
					BreakEvent event = ForgeHooks.onBlockBreakEvent(minion.worldObj,GameType.SURVIVAL,(EntityPlayerMP)minion.getOwner(),breakingX,breakingY,breakingZ);
					if (!event.isCanceled()){
						breakingBlock.dropBlockAsItem(minion.worldObj,breakingX,breakingY,breakingZ,breakingMeta,minionData.getAttributeLevel(MinionAttributes.FORTUNE));
						breakingBlock.dropXpOnBlockBreak(minion.worldObj,breakingX,breakingY,breakingZ,event.getExpToDrop());
						minion.worldObj.setBlockToAir(breakingX,breakingY,breakingZ);
					}
					
					if (targetX == breakingX && targetY == breakingY && targetZ == breakingZ){
						targetX = targetY = targetZ = 0;
						
						for(int attempt = 0,xx,yy,zz,id; attempt < 20; attempt++){ // TODO attribute to raise attempts?
							Block block = minion.worldObj.getBlock(xx = targetX+rand.nextInt(4)-2,yy = targetY+rand.nextInt(4)-2,zz = targetZ+rand.nextInt(4)-2);
							if (block == null || !(block instanceof BlockOre))continue;
							
							targetX = xx;
							targetY = yy;
							targetZ = zz;
							break;
						}
					}
					
					minion.unlockTarget(this);
					cooldown = 4;
					return;
				}
				else if (breakingTime%4 == 0 && minion.worldObj.getBlock(breakingX,breakingY,breakingZ) != breakingBlock){
					breakingTime = 0;
					minion.unlockTarget(this);
				}
			}
			else if (minion.isTargetLockedBy(this)){
				if (minion.getDistance(tempTargetX,tempTargetY,tempTargetZ) < 1.67D){
					minion.unlockTarget(this);
					minion.lockTargetForAbility(this,minion.posX,minion.posY,minion.posZ);
					
					breakingTime = (short)(1000F*(Items.diamond_pickaxe.getDigSpeed(diamondPickaxe.copy(),breakingBlock,breakingMeta)/breakingBlock.getBlockHardness(minion.worldObj,breakingX,breakingY,breakingZ)/30F));
				}
			}
			else if (!minion.isTargetLocked()){
				double dist = 16F+10F*minionData.getAttributeLevel(MinionAttributes.RANGE);
				minion.getLookHelper().setLookPosition(targetX+0.5D,targetY+0.5D,targetZ+0.5D,360F,360F);
				minion.getLookHelper().onUpdateLook();
				minion.rotationYaw = minion.rotationYawHead;
				Vec3 posVec = Vec3.createVectorHelper(minion.posX,minion.posY,minion.posZ);
				Vec3 lookVec = minion.getLookVec();
				
				MovingObjectPosition mop = minion.worldObj.rayTraceBlocks(posVec,posVec.addVector(lookVec.xCoord*dist,lookVec.yCoord*dist,lookVec.zCoord*dist));
				if (mop == null)targetX = targetY = targetZ = 0;
				else{
					breakingX = mop.blockX;
					breakingY = mop.blockY;
					breakingZ = mop.blockZ;
					breakingBlock = minion.worldObj.getBlock(breakingX,breakingY,breakingZ);
					breakingMeta = (short)minion.worldObj.getBlockMetadata(breakingX,breakingY,breakingZ);
					minion.lockTargetForAbility(this,tempTargetX = (mop.blockX+0.5D),tempTargetY = (mop.blockY+0.5D),tempTargetZ = (mop.blockZ+0.5D));
				}
			}
			
			return;
		}
		
		if (minion.isTargetLocked())return;
		
		for(int a = 0,amount = 1+minion.getMinionData().getAttributeLevel(MinionAttributes.SPEED); a < amount; a++){
			int xx = (int)Math.floor(minion.posX+(rand.nextFloat()-0.5F)*(8F+4F*minionData.getAttributeLevel(MinionAttributes.RANGE))),
				yy = (int)Math.floor(minion.posY-1.2F+(rand.nextFloat()-0.5F)*(6F+3F*minionData.getAttributeLevel(MinionAttributes.RANGE))),
				zz = (int)Math.floor(minion.posZ+(rand.nextFloat()-0.5F)*(8F+4F*minionData.getAttributeLevel(MinionAttributes.RANGE)));
			
			Block block = minion.worldObj.getBlock(xx,yy,zz);
			if (block instanceof BlockOre && block.getBlockHardness(minion.worldObj,xx,yy,zz) != -1){
				targetX = xx;
				targetY = yy;
				targetZ = zz;
			}
		}*/
	}

	@Override
	public void onDeath(){}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void render(){
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		int col = (255<<24)|(255<<16)|(255<<8)|255,y = 20;
		
		font.drawString("Target: "+targetX+","+targetY+","+targetZ,80,y,col);
		font.drawString("Breaking: "+breakingX+","+breakingY+","+breakingZ+" ("+breakingBlock.getUnlocalizedName()+":"+breakingMeta+")"+" // "+breakingTime,80,y+16,col);
	}

	@Override
	public void writeDataToNBT(NBTTagCompound nbt){}

	@Override
	public void readDataFromNBT(NBTTagCompound nbt){}
}
