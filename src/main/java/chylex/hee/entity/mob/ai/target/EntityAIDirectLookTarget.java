package chylex.hee.entity.mob.ai.target;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.util.MathUtil;

public class EntityAIDirectLookTarget extends EntityAITarget{
	private final ITargetOnDirectLook lookHandler;
	private double maxDistance = 32D;
	
	private EntityLivingBase currentTarget;
	private int tickLimiter, lookTimer;
	
	public EntityAIDirectLookTarget(EntityCreature owner, ITargetOnDirectLook lookHandler){
		super(owner,true,false);
		this.lookHandler = lookHandler;
	}
	
	public EntityAIDirectLookTarget setMaxDistance(double maxDistance){
		this.maxDistance = maxDistance;
		return this;
	}
	
	@Override
	public boolean shouldExecute(){
		if (++tickLimiter < 2)return false;
		tickLimiter = 0;
		
		for(EntityPlayer player:EntitySelector.players(taskOwner.worldObj,taskOwner.boundingBox.expand(maxDistance,maxDistance,maxDistance))){
			double dist = MathUtil.distance(player.posX-taskOwner.posX,player.posY-taskOwner.posY,player.posZ-taskOwner.posZ);
			
			if (dist <= maxDistance && isPlayerLookingIntoEyes(player) && lookHandler.canTargetOnDirectLook(player,dist)){
				if (++lookTimer == 4){
					currentTarget = player;
					return true;
				}
				else return false;
			}
		}
		
		lookTimer = 0;
		return false;
	}
	
	@Override
	public void startExecuting(){
		taskOwner.setAttackTarget(currentTarget);
		super.startExecuting();
	}
	
	private boolean isPlayerLookingIntoEyes(EntityPlayer target){
		ItemStack headIS = target.inventory.armorInventory[3];
		if (headIS != null && headIS.getItem() == Item.getItemFromBlock(Blocks.pumpkin))return false;
		
		Vec posDiff = Vec.between(target,taskOwner);
		posDiff.y += taskOwner.height*0.9D-target.getEyeHeight();
		
		double dist = posDiff.length();
		double dot = Vec.look(target).normalized().dotProduct(posDiff.normalized());
		
		return Math.abs(dot-1D) < 0.05D/MathUtil.square(dist) && target.canEntityBeSeen(taskOwner);
	}
	
	@FunctionalInterface
	public static interface ITargetOnDirectLook{
		boolean canTargetOnDirectLook(EntityPlayer target, double distance);
	}
}
