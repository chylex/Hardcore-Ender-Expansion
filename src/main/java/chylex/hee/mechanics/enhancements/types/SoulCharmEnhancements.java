package chylex.hee.mechanics.enhancements.types;
import java.util.EnumMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.item.ItemList;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;

public enum SoulCharmEnhancements{
	SPEED("Speed",6,new IRepresentativeItemSelector(){
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Items.gold_nugget;
		}

		@Override
		public ItemStack getRepresentativeItem(){
			return new ItemStack(Items.gold_nugget);
		}
	}),
	
	RANGE("Range",6,new IRepresentativeItemSelector(){
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Items.ender_pearl || is.getItem() == ItemList.enhanced_ender_pearl;
		}

		@Override
		public ItemStack getRepresentativeItem(){
			return new ItemStack(Items.ender_pearl);
		}
	}),
	
	EFFICIENCY("Efficiency",6,new IRepresentativeItemSelector(){
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Items.quartz;
		}

		@Override
		public ItemStack getRepresentativeItem(){
			return new ItemStack(Items.quartz);
		}
	}),
	
	DAMAGE("Damage",6,new IRepresentativeItemSelector(){
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Items.flint;
		}

		@Override
		public ItemStack getRepresentativeItem(){
			return new ItemStack(Items.flint);
		}
	}),
	
	FIRE("Fire",6,new IRepresentativeItemSelector(){
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Items.blaze_powder;
		}

		@Override
		public ItemStack getRepresentativeItem(){
			return new ItemStack(Items.blaze_powder);
		}
	});
	
	public static EnumMap<SoulCharmEnhancements,Byte> getEnhancements(ItemStack is){
		EnumMap<SoulCharmEnhancements,Byte> map = new EnumMap<>(SoulCharmEnhancements.class);
		
		NBTTagCompound enhancements = is.stackTagCompound == null?new NBTTagCompound():is.stackTagCompound.getCompoundTag("charmEnhancements");
		for(SoulCharmEnhancements enhancement:values())map.put(enhancement,enhancements.getByte(enhancement.name));
		
		return map;
	}
	
	public final String name;
	public final byte maxLevel;
	public final IRepresentativeItemSelector itemSelector;
	
	private SoulCharmEnhancements(String name, int maxLevel, IRepresentativeItemSelector itemSelector){
		this.name = name;
		this.maxLevel = (byte)maxLevel;
		this.itemSelector = itemSelector;
	}
	
	public boolean canIncreaseLevel(ItemStack is){
		return getEnhancements(is).get(this) < maxLevel;
	}
	
	public void increaseLevel(ItemStack is){
		setLevel(is,(byte)(getEnhancements(is).get(this)+1));
	}
	
	public ItemStack setLevel(ItemStack is, byte level){
		if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
		
		NBTTagCompound enhancements = is.stackTagCompound.getCompoundTag("charmEnhancements");
		enhancements.setByte(name,level);
		
		is.stackTagCompound.setTag("charmEnhancements",enhancements);
		return is;
	}
}
