package chylex.hee.mechanics.curse;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum CurseType{
	TELEPORTATION(0, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
		}
	}),
	
	CONFUSION(1, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
		}
	}),
	
	TRANQUILITY(2, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
		}
	}),
	
	SLOWNESS(3, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
		}
	}),
	
	WEAKNESS(4, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
		}
	}),
	
	BLINDNESS(5, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
		}
	}),
	
	DEATH(6, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
		}
	}),
	
	DECAY(7, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
		}
	}),
	
	VAMPIRE(8, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
		}
	}),
	
	REBOUND(9, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
		}
	}),
	
	LOSS(10, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
		}
	});
	
	static{
		TELEPORTATION
		.setRecipe(Items.ender_pearl,Items.ender_pearl,Items.ender_pearl,Items.ender_pearl)
		.setUses(EnumCurseUse.BLOCK,22,34).setUses(EnumCurseUse.ENTITY,7,12).setUses(EnumCurseUse.PLAYER,3,6);
		
		CONFUSION
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,11,15).setUses(EnumCurseUse.ENTITY,6,10);
		
		TRANQUILITY
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,35*20,48*20).setUses(EnumCurseUse.ENTITY,150*20,210*20);
		
		SLOWNESS
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,35,42).setUses(EnumCurseUse.ENTITY,16,21).setUses(EnumCurseUse.PLAYER,5,8);
		
		WEAKNESS
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,35,42).setUses(EnumCurseUse.ENTITY,16,21).setUses(EnumCurseUse.PLAYER,5,8);
		
		BLINDNESS
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,30,39).setUses(EnumCurseUse.ENTITY,12,16).setUses(EnumCurseUse.PLAYER,3,5);
		
		DEATH
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,28,36).setUses(EnumCurseUse.ENTITY,12,16).setUses(EnumCurseUse.PLAYER,6,9);
		
		DECAY
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,18,25).setUses(EnumCurseUse.ENTITY,14,19).setUses(EnumCurseUse.PLAYER,9,14);
		
		VAMPIRE
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,15,22).setUses(EnumCurseUse.ENTITY,8,13).setUses(EnumCurseUse.PLAYER,6,9);
		
		REBOUND
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,20,26).setUses(EnumCurseUse.ENTITY,15,22).setUses(EnumCurseUse.PLAYER,12,16);
		
		LOSS
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,14,19).setUses(EnumCurseUse.ENTITY,10,12).setUses(EnumCurseUse.PLAYER,8,11);
	}
	
	public static CurseType getFromDamage(int damage){
		damage = damage&0b11111111;
		
		for(CurseType type:values()){
			if (damage == type.damage)return type;
		}
		
		return null;
	}
	
	public static boolean isEternal(int damage){
		return (damage&0b100000000) != 0;
	}
	
	public final byte damage;
	public final ICurseHandler handler;
	private ItemStack[] items = new ItemStack[4];
	private byte usesBlockMin, usesBlockMax, usesEntityMin, usesEntityMax, usesPlayerMin, usesPlayerMax;
	
	CurseType(int damage, ICurseHandler handler){
		this.damage = (byte)damage;
		this.handler = handler;
	}
	
	private CurseType setRecipe(Object o1, Object o2, Object o3, Object o4){
		Object[] array = new Object[]{ o1, o2, o3, o4 };
		
		for(int a = 0; a < 4; a++){
			if (array[a] instanceof Block)items[a] = new ItemStack((Block)array[a]);
			else if (array[a] instanceof Item)items[a] = new ItemStack((Item)array[a]);
			else if (array[a] instanceof ItemStack)items[a] = (ItemStack)array[a];
			else throw new IllegalArgumentException("Invalid recipe object, accepting only Block, Item and ItemStack, got "+array[a].getClass());
		}
		
		return this;
	}
	
	private CurseType setUses(EnumCurseUse useType, int minUses, int maxUses){
		switch(useType){
			case BLOCK:
				usesBlockMin = (byte)minUses;
				usesBlockMax = (byte)maxUses;
				break;
				
			case ENTITY:
				usesEntityMin = (byte)minUses;
				usesEntityMax = (byte)maxUses;
				break;
				
			case PLAYER:
				usesPlayerMin = (byte)minUses;
				usesPlayerMax = (byte)maxUses;
				break;
				
			default:
		}
		
		return this;
	}
	
	public ItemStack getRecipeItem(int index){
		return items[index];
	}
	
	public int getUses(EnumCurseUse useType, Random rand){
		switch(useType){
			case BLOCK: return usesBlockMin+rand.nextInt(usesBlockMax-usesBlockMin+1);
			case ENTITY: return usesEntityMin+rand.nextInt(usesEntityMax-usesEntityMin+1);
			case PLAYER: return usesPlayerMin+rand.nextInt(usesPlayerMax-usesPlayerMin+1);
			default: return 0;
		}
	}
	
	public enum EnumCurseUse{
		BLOCK, ENTITY, PLAYER
	}
}
