package chylex.hee.system.abstractions;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class Stack{
	public static final int CMP_ID = 1;
	public static final int CMP_DAMAGE = 2;
	public static final int CMP_AMOUNT = 4;
	public static final int CMP_NBT = 8;
	
	public static final int CMP_ID_DAMAGE = CMP_ID | CMP_DAMAGE;
	public static final int CMP_ID_DAMAGE_AMOUNT = CMP_ID | CMP_DAMAGE | CMP_AMOUNT;
	public static final int CMP_ID_DAMAGE_AMOUNT_NBT = CMP_ID | CMP_DAMAGE | CMP_AMOUNT | CMP_NBT;
	
	// Static constructors
	
	public static final Stack empty(){
		return new Stack(null);
	}
	
	public static final Stack of(ItemStack is){
		return new Stack(is);
	}
	
	public static final Stack ofCopy(ItemStack is){
		return new Stack(is == null ? null : is.copy());
	}
	
	public static final Stack of(BlockInfo blockInfo){
		return of(blockInfo, 1);
	}
	
	public static final Stack of(BlockInfo blockInfo, int amount){
		return new Stack(new ItemStack(blockInfo.block, amount, blockInfo.meta));
	}
	
	public static final Stack of(Block block){
		return of(block, 0, 1);
	}
	
	public static final Stack of(Block block, int meta){
		return of(block, meta, 1);
	}
	
	public static final Stack of(Block block, int meta, int amount){
		return new Stack(new ItemStack(block, amount, meta));
	}
	
	public static final Stack of(Item item){
		return of(item, 0, 1);
	}
	
	public static final Stack of(Item item, int damage){
		return of(item, damage, 1);
	}
	
	public static final Stack of(Item item, int damage, int amount){
		return new Stack(new ItemStack(item, amount, damage));
	}
	
	// Instance
	
	private static final ItemStack ITEM_STACK_EMPTY = new ItemStack((Item)null, 0);
	
	private final @Nonnull ItemStack is;
	
	private Stack(@Nullable ItemStack is){
		this.is = is == null ? ITEM_STACK_EMPTY : is;
	}
	
	public boolean isEmpty(){
		return is == ITEM_STACK_EMPTY;
	}
	
	// Basic getters
	
	public Item getItem(){
		return is.getItem();
	}
	
	public int getDamage(){
		return is.getItemDamage();
	}
	
	public int getAmount(){
		return is.stackSize;
	}
	
	public boolean isBlock(){
		return is.getItem() instanceof ItemBlock;
	}
	
	// Basic setters
	
	public void setDamage(int damage){
		if (!isEmpty()){
			is.setItemDamage(damage);
		}
	}
	
	public void setAmount(int amount){
		if (!isEmpty()){
			is.stackSize = amount;
		}
	}
	
	public void increaseAmount(int amount){
		if (!isEmpty()){
			is.stackSize += amount;
		}
	}
	
	public boolean decreaseAmount(int amount){
		if (!isEmpty()){
			is.stackSize -= amount;
			return is.stackSize <= 0;
		}
		
		return false;
	}
	
	// Special getters
	
	public @Nullable ItemStack is(){
		return isEmpty() ? null : is;
	}
	
	public Optional<ItemBlock> getItemBlock(){
		return isBlock() ? Optional.of((ItemBlock)is.getItem()) : Optional.empty();
	}
	
	public Optional<Block> getBlock(){
		return isBlock() ? Optional.ofNullable(((ItemBlock)is.getItem()).field_150939_a) : Optional.empty();
	}
	
	// NBT
	
	public boolean hasNBT(){
		return is.hasTagCompound();
	}
	
	public NBTCompound nbt(boolean create){
		return isEmpty() ? NBT.dummy() : NBT.item(is, create);
	}
	
	public NBTCompound nbt(String key, boolean create){
		NBTCompound root = nbt(create), sub = root.getCompound(key);
		
		if (!root.hasKey(key) && create){
			root.setCompound(key, sub);
		}
		
		return sub;
	}
	
	// Instance utilities
	
	public Stack copy(){
		return Stack.ofCopy(is);
	}
	
	public boolean equals(Block block, int meta){
		return equals(Stack.of(block, meta));
	}
	
	public boolean equals(Item item, int damage){
		return equals(Stack.of(item, damage));
	}
	
	public boolean equals(ItemStack is, int flags){
		return equals(Stack.of(is), flags);
	}
	
	public boolean equals(Stack stack, int flags){
		if (flags == 0 || isEmpty() != stack.isEmpty()){
			return false;
		}
		else if (isEmpty()){
			return true;
		}
		
		if ((flags & CMP_ID) == CMP_ID){
			if (getItem() != stack.getItem()){
				return false;
			}
		}
		
		if ((flags & CMP_DAMAGE) == CMP_DAMAGE){
			if (getDamage() != stack.getDamage()){
				return false;
			}
		}
		
		if ((flags & CMP_AMOUNT) == CMP_AMOUNT){
			if (getAmount() != stack.getAmount()){
				return false;
			}
		}
		
		if ((flags & CMP_NBT) == CMP_NBT){
			if (!ItemStack.areItemStackTagsEqual(is(), stack.is())){
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString(){
		return isEmpty() ? "{empty}" : is.toString();
	}
	
	// Static utilities
	
	public static boolean isEmpty(ItemStack is){
		return is == null;
	}
}
