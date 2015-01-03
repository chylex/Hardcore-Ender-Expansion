package chylex.hee.entity.block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;
import chylex.hee.world.loot.LootItemStack;
import chylex.hee.world.loot.WeightedLootList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityBlockHomelandCache extends Entity{
	private static final WeightedLootList loot = new WeightedLootList(new LootItemStack[]{
		new LootItemStack(ItemList.arcane_shard).setWeight(220),
		new LootItemStack(ItemList.end_powder).setWeight(15),
		new LootItemStack(ItemList.instability_orb).setWeight(3),
		new LootItemStack(Items.iron_ingot).setWeight(3),
		new LootItemStack(Items.gold_ingot).setWeight(3),
		new LootItemStack(Items.feather).setWeight(3),
		new LootItemStack(Items.flint).setWeight(3),
		new LootItemStack(Items.bone).setWeight(3),
		new LootItemStack(Items.slime_ball).setWeight(3),
		new LootItemStack(Items.string).setWeight(3),
		new LootItemStack(Items.spider_eye).setWeight(3),
		new LootItemStack(Items.glowstone_dust).setWeight(3),
		new LootItemStack(Items.dye).setDamage(0,15).setWeight(3),
		new LootItemStack(Items.paper).setWeight(3),
		new LootItemStack(Items.stick).setWeight(3),
		new LootItemStack(Items.sugar).setWeight(3),
		new LootItemStack(Items.snowball).setWeight(3),
		new LootItemStack(Items.egg).setWeight(3),
		new LootItemStack(Items.arrow).setWeight(3),
		new LootItemStack(Items.diamond).setWeight(2),
		new LootItemStack(Items.emerald).setWeight(2),
		new LootItemStack(Items.gold_nugget).setWeight(2),
		new LootItemStack(Items.quartz).setWeight(2),
		new LootItemStack(Items.blaze_powder).setWeight(2),
		new LootItemStack(Items.ghast_tear).setWeight(2),
		new LootItemStack(Items.book).setWeight(2),
		new LootItemStack(Items.wheat_seeds).setWeight(2),
		new LootItemStack(Items.pumpkin_seeds).setWeight(2),
		new LootItemStack(Items.melon_seeds).setWeight(2),
		new LootItemStack(Items.fire_charge).setWeight(2),
		new LootItemStack(Items.coal).setDamage(0,1).setWeight(2)
	});
	
	public float rotation, prevRotation;
	
	public EntityBlockHomelandCache(World world){
		super(world);
		setSize(0.75F,1.05F);
		preventEntitySpawning = true;
		rotation = prevRotation = rand.nextFloat()*2F*(float)Math.PI;
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		if (worldObj.isRemote){
			prevRotation = rotation;
			rotation += 4F;
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (isEntityInvulnerable())return false;
		
		if (!isDead){
			setDead();
			
			if (worldObj.isRemote){
				worldObj.playSound(posX,posY,posZ,"dig.glass",1F,rand.nextFloat()*0.1F+0.92F,false);
				for(int a = 0; a < 20; a++)worldObj.spawnParticle("largesmoke",posX+(rand.nextDouble()-0.5D)*0.8D,posY+0.05D+rand.nextDouble()*1D,posZ+(rand.nextDouble()-0.5D)*0.8D,0D,0D,0D);
			}
			else{
				for(int a = 0; a < 2+rand.nextInt(2+rand.nextInt(2)); a++){
					EntityItem item = new EntityItem(worldObj,posX+(rand.nextDouble()-0.5D)*0.75D,posY+0.2D+rand.nextDouble()*0.6D,posZ+(rand.nextDouble()-0.5D)*0.75D,loot.generateIS(rand));
					item.delayBeforeCanPickup = 10;
					worldObj.spawnEntityInWorld(item);
				}
			}
		}
		
		return true;
	}
	
	@Override
	protected boolean canTriggerWalking(){
		return false;
	}
	
	@Override
	public boolean canBeCollidedWith(){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize(){
		return 0F;
	}
}
