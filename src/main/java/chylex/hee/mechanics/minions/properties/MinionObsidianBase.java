package chylex.hee.mechanics.minions.properties;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;

public enum MinionObsidianBase{
	REGULAR_OBSIDIAN(new IRepresentativeItemSelector(){
		private ItemStack isToShow = new ItemStack(Blocks.obsidian);
		
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Item.getItemFromBlock(Blocks.obsidian);
		}
		
		@Override
		public ItemStack getRepresentativeItem(){
			return isToShow;
		}
	}, 1, 5, 1),
	
	SMOOTH_OBSIDIAN(new IRepresentativeItemSelector(){
		private ItemStack isToShow = new ItemStack(BlockList.obsidian_special, 1, 0);
		
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Item.getItemFromBlock(BlockList.obsidian_special) && is.getItemDamage() == 0;
		}
		
		@Override
		public ItemStack getRepresentativeItem(){
			return isToShow;
		}
	}, 4, 5, 1),
	
	CHISELED_OBSIDIAN(new IRepresentativeItemSelector(){
		private ItemStack isToShow = new ItemStack(BlockList.obsidian_special, 1, 1);
		
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Item.getItemFromBlock(BlockList.obsidian_special) && is.getItemDamage() == 1;
		}
		
		@Override
		public ItemStack getRepresentativeItem(){
			return isToShow;
		}
	}, 2, 6, 3),
	
	OBSIDIAN_PILLAR(new IRepresentativeItemSelector(){
		private ItemStack isToShow = new ItemStack(BlockList.obsidian_special, 1, 2);
		
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Item.getItemFromBlock(BlockList.obsidian_special) && (is.getItemDamage() == 2 || is.getItemDamage() == 3);
		}
		
		@Override
		public ItemStack getRepresentativeItem(){
			return isToShow;
		}
	}, 3, 7, 2),
	
	GLOWING_SMOOTH_OBSIDIAN(new IRepresentativeItemSelector(){
		private ItemStack isToShow = new ItemStack(BlockList.obsidian_special_glow, 1, 0);
		
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Item.getItemFromBlock(BlockList.obsidian_special_glow) && is.getItemDamage() == 0;
		}
		
		@Override
		public ItemStack getRepresentativeItem(){
			return isToShow;
		}
	}, 4, 10, 1),
	
	GLOWING_CHISELED_OBSIDIAN(new IRepresentativeItemSelector(){
		private ItemStack isToShow = new ItemStack(BlockList.obsidian_special_glow, 1, 1);
		
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Item.getItemFromBlock(BlockList.obsidian_special_glow) && is.getItemDamage() == 1;
		}
		
		@Override
		public ItemStack getRepresentativeItem(){
			return isToShow;
		}
	}, 2, 12, 3),
	
	GLOWING_OBSIDIAN_PILLAR(new IRepresentativeItemSelector(){
		private ItemStack isToShow = new ItemStack(BlockList.obsidian_special_glow, 1, 2);
		
		@Override
		public boolean isValid(ItemStack is){
			return is.getItem() == Item.getItemFromBlock(BlockList.obsidian_special_glow) && (is.getItemDamage() == 2 || is.getItemDamage() == 3);
		}
		
		@Override
		public ItemStack getRepresentativeItem(){
			return isToShow;
		}
	}, 3, 14, 2);
	
	public final IRepresentativeItemSelector blockSelector;
	public final byte maxModifiers, maxAttributes, maxAbilities;
	
	MinionObsidianBase(IRepresentativeItemSelector blockSelector, int maxModifiers, int maxAttributes, int maxAbilities){
		this.blockSelector = blockSelector;
		this.maxModifiers = (byte)maxModifiers;
		this.maxAttributes = (byte)maxAttributes;
		this.maxAbilities = (byte)maxAbilities;
	}
}
