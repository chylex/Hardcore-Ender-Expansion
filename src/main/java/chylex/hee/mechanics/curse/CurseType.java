package chylex.hee.mechanics.curse;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum CurseType{
	TELEPORTATION(0);
	
	static{
		TELEPORTATION
		.setRecipe(Items.ender_pearl,Items.ender_pearl,Items.ender_pearl,Items.ender_pearl);
	}
	
	public static CurseType getFromDamage(int damage){
		for(CurseType type:values()){
			if (damage == type.damage)return type;
		}
		
		return null;
	}
	
	public final byte damage;
	private ItemStack[] items = new ItemStack[4];
	private byte usesBlockMin, usesBlockMax, usesEntityMin, usesEntityMax, usesPlayerMin, usesPlayerMax;
	
	CurseType(int damage){
		this.damage = (byte)damage;
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
