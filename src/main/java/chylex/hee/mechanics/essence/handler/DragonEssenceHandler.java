package chylex.hee.mechanics.essence.handler;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.essence.handler.dragon.AltarItemRecipe;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C11ParticleAltarOrb;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.collections.weight.ObjectWeightPair;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.CollectionUtil;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import chylex.hee.world.util.BlockLocation;

public class DragonEssenceHandler extends AltarActionHandler{
	public static final List<AltarItemRecipe> recipes = CollectionUtil.newList(new AltarItemRecipe[]{
		new AltarItemRecipe(Items.brewing_stand, ItemList.enhanced_brewing_stand, 20),
		new AltarItemRecipe(Items.ender_eye, ItemList.temple_caller, 50),
		new AltarItemRecipe(ItemList.ghost_amulet, 0, ItemList.ghost_amulet, 1, 8)
	});
	
	private AxisAlignedBB itemBoundingBox;
	private final List<BlockLocation> pedestals = new ArrayList<>();
	private byte updatePedestalTimer = 2;
	private long pedestalAreaHash;
	private byte lastMaxPedestals;
	private short repairCounter;
	
	public DragonEssenceHandler(TileEntityEssenceAltar altar){
		super(altar);
	}
	
	@Override
	public void onUpdate(){
		if (--updatePedestalTimer <= 0){
			updatePedestalTimer = 20;
			
			int maxPedestals = altar.getEnhancements().contains(EssenceAltarEnhancements.RANGE) ? 12 : 8;
			int range = maxPedestals == 12 ? 4 : 3;
			long currentHash = 0L;
			
			if (lastMaxPedestals != maxPedestals){
				lastMaxPedestals = (byte)maxPedestals;
				pedestalAreaHash = 0L;
			}
			
			World world = altar.getWorld();
			BlockPosM testPos = new BlockPosM();
			
			for(int xx = -range,id; xx <= range; xx++){
				for(int zz = -range; zz <= range; zz++){
					id = Block.getStateId(world.getBlockState(testPos.moveTo(xx,pos.y,zz)));
					currentHash += ((4+xx)*7+(4+zz)+id)*262144L+(xx*id)+(zz*id);
				}
			}
			
			if (pedestalAreaHash != currentHash){
				pedestalAreaHash = currentHash;
				
				pedestals.clear();
				IdentityHashMap<Block,Byte> blockCounts = new IdentityHashMap<>();
				Block[][] blocks = new Block[range*2+1][range*2+1];
				
				for(int xx = pos.x-range; xx <= pos.x+range; xx++){
					for(int zz = pos.z-range; zz <= pos.z+range; zz++){
						if (Math.abs(xx-pos.x) <= 1 && Math.abs(zz-pos.z) <= 1)continue;
						
						if (testPos.moveTo(xx,pos.y,zz).isAir(world) && hasCollisionBox(altar,testPos) &&
							(testPos.moveTo(xx-1,pos.y,zz).isAir(world) || !hasCollisionBox(altar,testPos)) &&
							(testPos.moveTo(xx+1,pos.y,zz).isAir(world) || !hasCollisionBox(altar,testPos)) &&
							(testPos.moveTo(xx,pos.y,zz-1).isAir(world) || !hasCollisionBox(altar,testPos)) &&
							(testPos.moveTo(xx,pos.y,zz+1).isAir(world) || !hasCollisionBox(altar,testPos))){
							Block block = testPos.moveTo(xx,pos.y,zz).getBlock(world);
							if (block.getMaterial() == Material.air)continue;
							
							blocks[range+xx-pos.x][range+zz-pos.z] = block;
							
							if (blockCounts.containsKey(block))blockCounts.put(block,(byte)(blockCounts.get(block)+1));
							else blockCounts.put(block,(byte)1);
						}
					}
				}
				
				SortedSet<Entry<Block,Byte>> sorted = CollectionUtil.sortMapByValueDesc(blockCounts);
				for(Entry<Block,Byte> entry:sorted){
					if (entry.getValue() > maxPedestals)continue;
					
					for(int xx = -range; xx <= range; xx++){
						for(int zz = -range; zz <= range; zz++){
							if (blocks[range+xx][range+zz] != entry.getKey())continue;
							
							pedestals.add(new BlockLocation(testPos.moveTo(pos).moveBy(xx,0,zz)));
						}
					}
					
					break;
				}
			}
			
			for(BlockLocation loc:pedestals){
				if (world.rand.nextInt(5) <= 1){
					PacketPipeline.sendToAllAround(altar,64D,new C11ParticleAltarOrb(altar,loc.x+0.5D,loc.y+0.5D,loc.z+0.5D));
				}
			}
		}
		
		if (itemBoundingBox == null){
			BlockPos pos = altar.getPos();
			itemBoundingBox = AxisAlignedBB.fromBounds(pos.getX()+0.5D-4.5D,pos.getY()+0.9D,pos.getZ()+0.5D-4.5D,pos.getX()+0.5D+4.5D,pos.getY()+1.6D,pos.getZ()+0.5D+4.5D);
		}

		World world = altar.getWorld();
		List<EntityItem> thrownItems = world.getEntitiesWithinAABB(EntityItem.class,itemBoundingBox);
		double targX,targY,targZ;
		
		for(EntityItem item:thrownItems){
			for(BlockLocation loc:pedestals){
				targX = loc.x+0.5D;
				targY = loc.y+1.15D;
				targZ = loc.z+0.5D;
				
				if (Math.abs(item.posX-targX) > 0.001D || Math.abs(item.posY-targY) > 0.001D || Math.abs(item.posZ-targZ) > 0.001D){
					if (world.getEntitiesWithinAABB(EntityItemAltar.class,AxisAlignedBB.fromBounds(targX,targY,targZ,targX,targY,targZ)).isEmpty()&&
						Math.sqrt(MathUtil.square(targX-item.posX)+MathUtil.square(targY-item.posY)+MathUtil.square(targZ-item.posZ)) < 0.275D){
						world.spawnEntityInWorld(new EntityItemAltar(world,targX,targY,targZ,item,EssenceType.DRAGON.getId()));
					}
				}
				else if ((updatePedestalTimer&3) == 1 && item instanceof EntityItemAltar){
					EntityItemAltar altarItem = (EntityItemAltar)item;
					altarItem.pedestalUpdate = 0;
					updatePedestalItem(altarItem);
					
					if (world.rand.nextInt(5) == 0){
						PacketPipeline.sendToAllAround(altar.getWorld().provider.getDimensionId(),targX,loc.y+0.5D,targZ,64D,new C11ParticleAltarOrb(targX,loc.y+0.5D,targZ,item.posX,item.posY+0.3D,item.posZ,altar.getEssenceType().getId(),(byte)1));
					}
				}
			}
		}
	}
	
	public static boolean hasCollisionBox(TileEntityEssenceAltar altar, BlockPos pos){
		Block block = altar.getWorld().getBlockState(pos).getBlock();
		return block.getMaterial() == Material.air ? false : block.getCollisionBoundingBox(altar.getWorld(),pos,altar.getWorld().getBlockState(pos)) != null;
	}
	
	private void updatePedestalItem(EntityItemAltar item){
		List<Enum> enhancements = altar.getEnhancements();
		
		ItemStack is = item.getEntityItem();
		
		/*
		 * REPAIRING
		 */
		
		if (item.worldObj.rand.nextInt(3) != 0){
			if (is.isItemStackDamageable() && is.getItemDamage() != 0 && is.getItem().isRepairable()){
				for(int a = enhancements.contains(EssenceAltarEnhancements.SPEED) ? 2 : 1; a > 0; a--){
					if (++repairCounter > (enhancements.contains(EssenceAltarEnhancements.EFFICIENCY) ? 66 : 56)){
						altar.drainEssence(1);
						repairCounter = 0;
					}
					
					if (updateItemCounter(is,"HEE_repair",1) < 18)continue;
					updateItemCounter(is,"HEE_repair",0);
					
					int amount = MathUtil.clamp(MathUtil.floor(Math.sqrt(is.getMaxDamage())*0.65D),1,is.getItemDamage());
					is.setItemDamage(is.getItemDamage()-amount);
					item.hasChanged = true;
				}
			}
		}
		
		/*
		 * ENCHANTMENT UPGRADES
		 */
		
		else if (is.isItemEnchanted() && is.getItem() != Items.enchanted_book){
			for(int b = enhancements.contains(EssenceAltarEnhancements.SPEED) ? 2 : 1; b > 0; b--){
				if (updateItemCounter(is,"HEE_enchant",1) < 400-is.getItem().getItemEnchantability()*4)return;
				updateItemCounter(is,"HEE_enchant",0);
				
				NBTTagList enchants = is.getEnchantmentTagList();
				if (enchants == null || enchants.tagCount() == 0)return;
				
				for(int attempt = 0; attempt < 3; attempt++){
					WeightedList<ObjectWeightPair<Enchantment>> list = new WeightedList<>();
					
					for(int a = 0; a < enchants.tagCount(); a++){
						Enchantment e = Enchantment.getEnchantmentById(enchants.getCompoundTagAt(a).getShort("id"));
						if (e == null)continue;
						
						list.add(ObjectWeightPair.of(e,e.getWeight()));
					}
					
					Enchantment chosenEnchantment = list.getRandomItem(item.worldObj.rand).getObject();
					
					for(int a = 0; a < enchants.tagCount(); a++){
						NBTTagCompound tag = enchants.getCompoundTagAt(a);
						if (tag.getShort("id") != chosenEnchantment.effectId)continue;
						
						int level = tag.getShort("lvl"),cost = getEnchantmentCost(chosenEnchantment,level+1);
						if ((socketEffects&EFFECT_LOWER_COST) == EFFECT_LOWER_COST)cost = (int)Math.max(cost>>2,cost*(1F-socketBoost/42F));
						if (level >= chosenEnchantment.getMaxLevel() || altar.getEssenceLevel() < cost)continue;
						
						altar.drainEssence(cost);
						tag.setShort("lvl",(short)(level+1));
						ItemUtil.getNBT(is,true).setTag("ench",enchants);

						item.hasChanged = true;
						attempt = 999;
						break;
					}
				}
			}
		}
		
		/*
		 * ITEM TRANSFORMATIONS
		 */
		
		else if (item.worldObj.rand.nextInt(5) == 0){
			for(AltarItemRecipe recipe:recipes){
				if (recipe.isApplicable(is)){
					for(int a = enhancements.contains(EssenceAltarEnhancements.SPEED) ? 2 : 1; a > 0; a--){
						if (updateItemCounter(is,"HEE_transform",1) <= Math.max(MathUtil.ceil(recipe.getCost()*(enhancements.contains(EssenceAltarEnhancements.EFFICIENCY) ? 0.75F : 1F)),recipe.getCost()>>1)){
							altar.drainEssence(1);
							continue;
						}
						
						updateItemCounter(is,"HEE_transform",0);
						recipe.doTransaction(item);
						item.hasChanged = true;
						break;
					}
					
					break;
				}
			}
		}
	}
	
	private int getEnchantmentCost(Enchantment ench, int level){
		return MathUtil.floor(Math.max(1F,1F+(1.7F*level*((float)level/ench.getMaxLevel()))+(10-ench.getWeight())*0.2F));
	}
	
	/**
	 * @param operation 0 = reset, 1 = increment
	 * @return current value
	 */
	private short updateItemCounter(ItemStack is, String counterName, int operation){
		NBTTagCompound nbt = ItemUtil.getNBT(is,true);
		
		if (operation == 0){
			nbt.removeTag(counterName);
			return 0;
		}

		short counter = nbt.getShort(counterName);
		if (operation == 1)nbt.setShort(counterName,++counter);
		
		return counter;
	}
	
	@Override
	public void onTileWriteToNBT(NBTTagCompound nbt){
		nbt.setShort("D_repairCnt",repairCounter);
	}
	
	@Override
	public void onTileReadFromNBT(NBTTagCompound nbt){
		repairCounter = nbt.getShort("D_repairCnt");
	}
}
