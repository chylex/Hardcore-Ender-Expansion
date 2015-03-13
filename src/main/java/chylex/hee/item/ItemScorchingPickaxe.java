package chylex.hee.item;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import chylex.hee.block.BlockList;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.util.MathUtil;
import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemScorchingPickaxe extends Item{
	private static final Pattern oreRegex = Pattern.compile("(?:^ore[A-Z].+$)|(?:^ore_.+$)|(?:.+_ore$)|(?:.+Ore$)");
	private static final Map<Block,Boolean> cachedBlocks = new IdentityHashMap<>();
	private static final List<Block> cachedOres = new ArrayList<>();
	private static final Random cacheRand = new Random(0);
	
	public static final boolean isBlockValid(Block block){
		if (cachedBlocks.containsKey(block))return cachedBlocks.get(block).booleanValue();
		
		if (FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(block)) != null){
			cachedBlocks.put(block,true);
			return true;
		}
		
		UniqueIdentifier name = GameRegistry.findUniqueIdentifierFor(block);
		if (name == null){
			cachedBlocks.put(block,false);
			return false;
		}
		
		if (oreRegex.matcher(name.name).find()){
			Item drop = block.getItemDropped(0,cacheRand,0);
			
			if (drop != null && !(drop instanceof ItemBlock)){
				int testAmt = 0;
				for(int a = 0; a < 50; a++)testAmt += block.quantityDroppedWithBonus(10,cacheRand);
				boolean isValid = testAmt > 50;
				
				cachedBlocks.put(block,isValid);
				cachedOres.add(block);
				return isValid;
			}
		}
		
		cachedBlocks.put(block,false);
		return false;
	}
	
	public static final boolean isBlockOre(Block block){
		return isBlockValid(block) && cachedOres.contains(block);
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack is, World world, Block block, int x, int y, int z, EntityLivingBase owner){
		if (block.getBlockHardness(world,x,y,z) != 0D)is.damageItem(1,owner);
		return true;
	}
	
	@Override
	public boolean hitEntity(ItemStack is, EntityLivingBase hitEntity, EntityLivingBase owner){
		is.damageItem(2,owner);
		return true;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta){
		if (isBlockValid(block))return 8F;
		else if (block == BlockList.ravaged_brick)return 16F;
		else return super.getDigSpeed(stack,block,meta);
	}
	
	@Override
	public boolean canHarvestBlock(Block block, ItemStack is){
		return isBlockValid(block) || block == BlockList.ravaged_brick;
	}

	@Override
	public int getHarvestLevel(ItemStack is, String toolClass){
		return toolClass.equals("pickaxe") ? 100 : -1;
	}

	@Override
	public Set<String> getToolClasses(ItemStack is){
		return ImmutableSet.of("pickaxe");
	}
	
	@Override
	public void onCreated(ItemStack is, World world, EntityPlayer player){
		player.addStat(AchievementManager.SCORCHING_PICKAXE,1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D(){
		return true;
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBlockDropItems(HarvestDropsEvent e){
		if (e.harvester == null)return;
		
		ItemStack heldItem = e.harvester.getHeldItem();
		if (heldItem == null || heldItem.getItem() != this)return;
		
		if (isBlockValid(e.block) && !e.drops.isEmpty()){
			e.dropChance = 1F;
			
			ItemStack drop = e.drops.get(0);
			drop.stackSize = 1;
			
			for(ItemStack blockDrop:e.drops){
				if (drop.getItem() != blockDrop.getItem())return;
			}
			
			e.drops.clear();
			Random rand = e.world.rand;
			
			if (drop.getItem() instanceof ItemBlock){
				ItemStack result = null;
				ItemStack smelted = FurnaceRecipes.smelting().getSmeltingResult(drop);
				
				if (smelted == null)return;
				else if (smelted.getItem() instanceof ItemBlock)result = smelted.copy();
				else{
					result = smelted.copy();
					
					int fortune = 0;
					for(int a = 0; a < 5; a++)fortune += 1+rand.nextInt(3+rand.nextInt(3));
					fortune = 1+MathUtil.floor(fortune*(0.35D*rand.nextDouble()*rand.nextDouble()*Math.pow(FurnaceRecipes.smelting().func_151398_b(result),2.6D)+(fortune*0.06D))/6.4D); // OBFUSCATED getExperience
					result.stackSize = fortune;
				}
				
				e.drops.add(result);
			}
			else{
				int fortune = 0;
				for(int a = 0; a < 4; a++)fortune += e.block.quantityDropped(e.blockMetadata,3+rand.nextInt(3)-rand.nextInt(2),rand);
				for(int a = 0; a < 4; a++)fortune += e.block.quantityDropped(e.blockMetadata,0,rand);
				fortune = 1+MathUtil.floor((fortune+e.block.getExpDrop(e.world,e.blockMetadata,0)/2D)*(rand.nextDouble()+(rand.nextDouble()*0.5D)+0.35D)/6D);
				
				drop.stackSize = fortune;
				e.drops.add(drop);
			}
		}
	}
}
