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
import net.minecraft.world.World;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.enhancements.types.EssenceAltarEnhancements;
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

public class DragonEssenceHandler extends AltarActionHandler{
	public static final List<AltarItemRecipe> recipes = CollectionUtil.newList(new AltarItemRecipe[]{
		new AltarItemRecipe(new ItemStack(Items.brewing_stand), new ItemStack(ItemList.enhanced_brewing_stand), 20),
		new AltarItemRecipe(new ItemStack(Items.ender_eye), new ItemStack(ItemList.temple_caller), 50),
		new AltarItemRecipe(new ItemStack(ItemList.ghost_amulet,1,0), new ItemStack(ItemList.ghost_amulet,1,1), 8)
	});
	
	private AxisAlignedBB itemBoundingBox;
	private final List<BlockPosM> pedestals = new ArrayList<>();
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
			
			World world = altar.getWorldObj();
			
			for(int xx = -range,id; xx <= range; xx++){
				for(int zz = -range; zz <= range; zz++){
					id = Block.getIdFromBlock(BlockPosM.tmp(altar.xCoord+xx,altar.yCoord,altar.zCoord+zz).getBlock(world));
					currentHash += ((4+xx)*7+(4+zz)+id)*262144L+(xx*id)+(zz*id);
				}
			}
			
			if (pedestalAreaHash != currentHash){
				pedestalAreaHash = currentHash;
				
				pedestals.clear();
				IdentityHashMap<Block,Byte> blockCounts = new IdentityHashMap<>();
				Block[][] blocks = new Block[range*2+1][range*2+1];
				
				BlockPosM tmpPos = BlockPosM.tmp();
				
				for(int xx = altar.xCoord-range; xx <= altar.xCoord+range; xx++){
					for(int zz = altar.zCoord-range; zz <= altar.zCoord+range; zz++){
						if (Math.abs(xx-altar.xCoord) <= 1 && Math.abs(zz-altar.zCoord) <= 1)continue;
						
						if (tmpPos.set(xx,altar.yCoord+1,zz).isAir(world) &&
							(tmpPos.set(xx-1,altar.yCoord,zz).isAir(world) || !hasCollisionBox(altar,xx-1,altar.yCoord,zz)) &&
							(tmpPos.set(xx+1,altar.yCoord,zz).isAir(world) || !hasCollisionBox(altar,xx+1,altar.yCoord,zz)) &&
							(tmpPos.set(xx,altar.yCoord,zz-1).isAir(world) || !hasCollisionBox(altar,xx,altar.yCoord,zz-1)) &&
							(tmpPos.set(xx,altar.yCoord,zz+1).isAir(world) || !hasCollisionBox(altar,xx,altar.yCoord,zz+1)) &&
							hasCollisionBox(altar,xx,altar.yCoord,zz)){
							Block block = tmpPos.set(xx,altar.yCoord,zz).getBlock(world);
							if (block.getMaterial() == Material.air)continue;
							
							blocks[range+xx-altar.xCoord][range+zz-altar.zCoord] = block;
							
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
							
							pedestals.add(new BlockPosM(altar.xCoord+xx,altar.yCoord,altar.zCoord+zz));
						}
					}
					
					break;
				}
			}
			
			for(BlockPosM loc:pedestals){
				if (world.rand.nextInt(5) <= 1){
					PacketPipeline.sendToAllAround(altar,64D,new C11ParticleAltarOrb(altar,loc.x+0.5D,altar.yCoord+0.5D,loc.z+0.5D));
				}
			}
		}
		
		if (itemBoundingBox == null){
			itemBoundingBox = AxisAlignedBB.getBoundingBox(altar.xCoord+0.5D-4.5D,altar.yCoord+0.9D,altar.zCoord+0.5D-4.5D,altar.xCoord+0.5+4.5D,altar.yCoord+1.6D,altar.zCoord+0.5D+4.5D);
		}

		World world = altar.getWorldObj();
		List<EntityItem> thrownItems = world.getEntitiesWithinAABB(EntityItem.class,itemBoundingBox);
		double targX,targY,targZ;
		
		for(EntityItem item:thrownItems){
			for(BlockPosM loc:pedestals){
				targX = loc.x+0.5D;
				targY = loc.y+1.15D;
				targZ = loc.z+0.5D;
				
				if (Math.abs(item.posX-targX) > 0.001D || Math.abs(item.posY-targY) > 0.001D || Math.abs(item.posZ-targZ) > 0.001D){
					if (world.getEntitiesWithinAABB(EntityItemAltar.class,AxisAlignedBB.getBoundingBox(targX,targY,targZ,targX,targY,targZ)).isEmpty()&&
						Math.sqrt(MathUtil.square(targX-item.posX)+MathUtil.square(targY-item.posY)+MathUtil.square(targZ-item.posZ)) < 0.275D){
						world.spawnEntityInWorld(new EntityItemAltar(world,targX,targY,targZ,item,EssenceType.DRAGON.id));
					}
				}
				else if ((updatePedestalTimer&3) == 1 && item instanceof EntityItemAltar){
					EntityItemAltar altarItem = (EntityItemAltar)item;
					altarItem.pedestalUpdate = 0;
					updatePedestalItem(altarItem);
					
					if (world.rand.nextInt(5) == 0){
						PacketPipeline.sendToAllAround(altar.getWorldObj().provider.dimensionId,targX,loc.y+0.5D,targZ,64D,new C11ParticleAltarOrb(targX,loc.y+0.5D,targZ,item.posX,item.posY+0.3D,item.posZ,altar.getEssenceType().id,(byte)1));
					}
				}
			}
		}
	}
	
	public static boolean hasCollisionBox(TileEntityEssenceAltar altar, int x, int y, int z){
		Block block = altar.getWorldObj().getBlock(x,y,z);
		return block.getMaterial() == Material.air ? false : block.getCollisionBoundingBoxFromPool(altar.getWorldObj(),x,y,z) != null;
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
					if (++repairCounter > (enhancements.contains(EssenceAltarEnhancements.EFFICIENCY) ? 72 : 56)){
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
				if (updateItemCounter(is,"HEE_enchant",1) < 280-is.getItem().getItemEnchantability()*5)return;
				updateItemCounter(is,"HEE_enchant",0);
				
				NBTTagList enchants = is.getEnchantmentTagList();
				if (enchants == null || enchants.tagCount() == 0)return;
				
				for(int attempt = 0; attempt < 3; attempt++){
					WeightedList<ObjectWeightPair<Enchantment>> list = new WeightedList<>();
					
					for(int a = 0; a < enchants.tagCount(); a++){
						Enchantment e = Enchantment.enchantmentsList[enchants.getCompoundTagAt(a).getShort("id")];
						if (e == null)continue;
						
						list.add(ObjectWeightPair.of(e,e.getWeight()));
					}
					
					if (list.isEmpty())continue; // the enchantments are no longer in the game
					
					Enchantment chosenEnchantment = list.getRandomItem(item.worldObj.rand).getObject();
					
					for(int a = 0; a < enchants.tagCount(); a++){
						NBTTagCompound tag = enchants.getCompoundTagAt(a);
						if (tag.getShort("id") != chosenEnchantment.effectId)continue;
						
						int level = tag.getShort("lvl"), cost = getEnchantmentCost(chosenEnchantment,level+1);
						if (enhancements.contains(EssenceAltarEnhancements.EFFICIENCY))cost = MathUtil.ceil(cost*0.65F);
						if (level >= chosenEnchantment.getMaxLevel() || altar.getEssenceLevel() < cost)continue;
						
						altar.drainEssence(cost);
						tag.setShort("lvl",(short)(level+1));
						ItemUtil.getTagRoot(is,true).setTag("ench",enchants);

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
						if (updateItemCounter(is,"HEE_transform",1) <= Math.max(MathUtil.ceil(recipe.cost*(enhancements.contains(EssenceAltarEnhancements.EFFICIENCY) ? 0.65F : 1F)),recipe.cost>>1)){
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
		return MathUtil.floor(Math.max(1F,1F+(2F*level*((float)level/ench.getMaxLevel()))+(10-ench.getWeight())*0.2F));
	}
	
	/**
	 * @param operation 0 = reset, 1 = increment
	 * @return current value
	 */
	private short updateItemCounter(ItemStack is, String counterName, int operation){
		NBTTagCompound nbt = ItemUtil.getTagRoot(is,true);
		
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
