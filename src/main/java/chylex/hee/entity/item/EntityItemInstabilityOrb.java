package chylex.hee.entity.item;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.fx.FXType;
import chylex.hee.mechanics.orb.OrbAcquirableItems;
import chylex.hee.mechanics.orb.OrbSpawnableMobs;
import chylex.hee.mechanics.orb.WeightedItem;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.system.abstractions.Explosion;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.RandUtil;

public class EntityItemInstabilityOrb extends EntityItem{
	public EntityItemInstabilityOrb(World world){
		super(world);
	}
	
	public EntityItemInstabilityOrb(World world, double x, double y, double z, ItemStack is){
		super(world,x,y,z,is);
		
		for(int a = 0; a < is.stackSize-1; a++){
			ItemStack newIS = is.copy();
			newIS.stackSize = 1;
			EntityItem item = new EntityItemInstabilityOrb(world,x,y,z,newIS);
			item.delayBeforeCanPickup = 40;
			world.spawnEntityInWorld(item);
		}
		
		is.stackSize = 1;
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (!worldObj.isRemote){
			age += 1+rand.nextInt(2+rand.nextInt(4))*rand.nextInt(3);
			if (age >= 666)detonate();
		}
		else{
			int chance = age/17-7;
			for(int a = 0; a < Math.min(7,(chance <= 0 ? (rand.nextInt(Math.abs(chance)+1) == 0 ? 1 : 0) : chance)); a++){
				HardcoreEnderExpansion.fx.instability(this);
			}
			
			if (rand.nextInt(3+Math.max(0,(int)((680F-age)/70F))) == 0)worldObj.playSound(posX,posY,posZ,"random.pop",0.15F+rand.nextFloat()*0.1F,0.7F+rand.nextFloat()*0.6F,false);
		}
	}
	
	private void detonate(){
		if (rand.nextInt(6) == 0){
			Explosion explosion = new Explosion(worldObj,posX,posY,posZ,2.8F+rand.nextFloat()*0.8F,this){
				@Override
				protected void onDamageEntity(Entity entity, double blastPower, DamageSource source){
					if (!(entity instanceof EntityLiving))super.onDamageEntity(entity,blastPower,source);
				}
			};
			
			explosion.trigger();
		}
		else if (rand.nextInt(6) == 0){
			Class<?> cls = null;
			int ele = rand.nextInt(OrbSpawnableMobs.classList.size());
			Iterator<Class<?>> iter = OrbSpawnableMobs.classList.iterator();
			
			while(iter.hasNext()){
				cls = iter.next();
				if (--ele < 0)break;
			}
			
			try{
				Entity e = (Entity)cls.getConstructor(World.class).newInstance(worldObj);
				e.setPositionAndRotation(posX,posY,posZ,rand.nextFloat()*360F-180F,0F);
				worldObj.spawnEntityInWorld(e);
				
				PacketPipeline.sendToAllAround(this,64D,new C21EffectEntity(FXType.Entity.ORB_TRANSFORMATION,e));
			}catch(Exception ex){
				Log.throwable(ex,"Error spawning entity $0 in EntityItemInstabilityOrb",cls == null ? "<null>" : cls.getSimpleName());
			}
		}
		else{
			WeightedItem item = OrbAcquirableItems.idList.getRandomItem(rand);
			
			if (item == null){ // list is empty
				String[] list = new String[]{
					ChestGenHooks.DUNGEON_CHEST, ChestGenHooks.BONUS_CHEST, ChestGenHooks.MINESHAFT_CORRIDOR,
					ChestGenHooks.VILLAGE_BLACKSMITH, ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST,
					ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.STRONGHOLD_CORRIDOR
				};
				
				WeightedRandomChestContent[] content = ChestGenHooks.getItems(list[rand.nextInt(list.length)],rand);
				if (content.length == 0)return;
				
				ItemStack is = content[rand.nextInt(content.length)].theItemId;
				item = new WeightedItem(is.getItem(),is.getItemDamage(),1);
			}
			
			int meta = RandUtil.anyOf(rand,item.getDamageValues());
			if (meta == 32767)meta = 0;
			
			EntityItem entityitem = new EntityItem(worldObj,posX,posY,posZ,new ItemStack(item.getItem(),1,meta));
			entityitem.motionX = entityitem.motionY = entityitem.motionZ = 0D;
			entityitem.delayBeforeCanPickup = 10;
			worldObj.spawnEntityInWorld(entityitem);
			
			PacketPipeline.sendToAllAround(this,64D,new C21EffectEntity(FXType.Entity.ORB_TRANSFORMATION,posX,posY,posZ,0.25F,0.4F));
		}
		
		setDead();
	}
	
	@Override
	public boolean combineItems(EntityItem item){
		return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source.isExplosion()){
			if (rand.nextInt(6) != 0){
				age -= 10-rand.nextInt(80);
				return false;
			}
		}
		
		return super.attackEntityFrom(source,amount);
	}
}
