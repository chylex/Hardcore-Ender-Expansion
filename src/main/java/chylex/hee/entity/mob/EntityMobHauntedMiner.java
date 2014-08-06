package chylex.hee.entity.mob;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;

public class EntityMobHauntedMiner extends EntityFlying implements IMob{
	public EntityMobHauntedMiner(World world){
		super(world);
		setSize(1.6F,1.2F);
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(45D);
	}
	
	@Override
	protected void updateEntityActionState(){
		if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL)setDead();
		despawnEntity();
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (worldObj.isRemote){
			for(int a = 0; a < 2; a++)worldObj.spawnParticle("flame",posX+(rand.nextDouble()-0.5D)*0.15D,posY,posZ+(rand.nextDouble()-0.5D)*0.15D,0D,-0.05D,0D);
		}
	}
	
	@Override
	public void dropFewItems(boolean recentlyHit, int looting){
		for(int a = 0; a < rand.nextInt(2+rand.nextInt(2)+looting); a++)dropItem(ItemList.fire_shard,1);
	}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal("entity.hauntedMiner.name");
	}
}
