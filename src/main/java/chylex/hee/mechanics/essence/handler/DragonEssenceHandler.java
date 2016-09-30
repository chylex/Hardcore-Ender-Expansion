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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.enhancements.EnhancementList;
import chylex.hee.mechanics.enhancements.types.EssenceAltarEnhancements;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.essence.handler.dragon.AltarItemRecipe;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C11ParticleAltarOrb;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import chylex.hee.system.abstractions.nbt.NBTList;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.collections.weight.WeightedMap;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEssenceAltar;

public class DragonEssenceHandler extends AltarActionHandler{
	public static final List<AltarItemRecipe> recipes = CollectionUtil.newList(new AltarItemRecipe[]{
		new AltarItemRecipe(new ItemStack(Items.brewing_stand), new ItemStack(ItemList.enhanced_brewing_stand), 20),
		new AltarItemRecipe(new ItemStack(ItemList.ghost_amulet, 1, 0), new ItemStack(ItemList.ghost_amulet, 1, 1), 8)
	});
	
	private AxisAlignedBB itemBoundingBox;
	private final List<Pos> pedestals = new ArrayList<>();
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
			
			int maxPedestals = /* TODO altar.getEnhancements().contains(EssenceAltarEnhancements.RANGE) ? 12 : */8;
			int range = maxPedestals == 12 ? 4 : 3;
			long currentHash = 0L;
			
			if (lastMaxPedestals != maxPedestals){
				lastMaxPedestals = (byte)maxPedestals;
				pedestalAreaHash = 0L;
			}
			
			World world = altar.getWorldObj();
			
			for(int xx = -range, id; xx <= range; xx++){
				for(int zz = -range; zz <= range; zz++){
					id = Block.getIdFromBlock(Pos.at(altar.xCoord+xx, altar.yCoord, altar.zCoord+zz).getBlock(world));
					currentHash += ((4+xx)*7+(4+zz)+id)*262144L+(xx*id)+(zz*id);
				}
			}
			
			if (pedestalAreaHash != currentHash){
				pedestalAreaHash = currentHash;
				
				pedestals.clear();
				IdentityHashMap<Block, Byte> blockCounts = new IdentityHashMap<>();
				Block[][] blocks = new Block[range*2+1][range*2+1];
				
				Pos tilePos = Pos.at(altar);
				
				Pos.forEachBlock(tilePos.offset(-range, 0, -range), tilePos.offset(range, 0, range), pos -> { // TODO rework a bit?
					if (Math.abs(pos.x-tilePos.getX()) <= 1 && Math.abs(pos.z-tilePos.getZ()) <= 1)return;
					if (!(pos.getUp().isAir(world) && hasCollisionBox(altar, pos.getX(), pos.getY(), pos.getZ())))return;
					
					for(Facing4 facing:Facing4.list){
						Pos offset = pos.offset(facing);
						if (!(offset.isAir(world) || !hasCollisionBox(altar, offset.getX(), offset.getY(), offset.getZ())))return;
					}
					
					Block block = pos.getBlock(world);
					if (block.getMaterial() == Material.air)return;
					
					blocks[range+pos.getX()-tilePos.getX()][range+pos.getZ()-tilePos.getZ()] = block;
					
					if (blockCounts.containsKey(block))blockCounts.put(block, (byte)(blockCounts.get(block)+1));
					else blockCounts.put(block, (byte)1);
				});
				
				SortedSet<Entry<Block, Byte>> sorted = CollectionUtil.sortMapByValueDesc(blockCounts);
				
				for(Entry<Block, Byte> entry:sorted){
					if (entry.getValue() > maxPedestals)continue;
					
					for(int xx = -range; xx <= range; xx++){
						for(int zz = -range; zz <= range; zz++){
							if (blocks[range+xx][range+zz] != entry.getKey())continue;
							
							pedestals.add(Pos.at(altar.xCoord+xx, altar.yCoord, altar.zCoord+zz));
						}
					}
					
					break;
				}
			}
			
			for(Pos pos:pedestals){
				if (world.rand.nextInt(5) <= 1){
					PacketPipeline.sendToAllAround(altar, 64D, new C11ParticleAltarOrb(altar, pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D));
				}
			}
		}
		
		if (itemBoundingBox == null){
			itemBoundingBox = AxisAlignedBB.getBoundingBox(altar.xCoord+0.5D-4.5D, altar.yCoord+0.9D, altar.zCoord+0.5D-4.5D, altar.xCoord+0.5+4.5D, altar.yCoord+1.6D, altar.zCoord+0.5D+4.5D);
		}

		World world = altar.getWorldObj();
		List<EntityItem> thrownItems = EntitySelector.type(world, EntityItem.class, itemBoundingBox);
		double targX, targY, targZ;
		
		for(EntityItem item:thrownItems){
			for(Pos pos:pedestals){
				targX = pos.getX()+0.5D;
				targY = pos.getY()+1.15D;
				targZ = pos.getZ()+0.5D;
				
				if (Math.abs(item.posX-targX) > 0.001D || Math.abs(item.posY-targY) > 0.001D || Math.abs(item.posZ-targZ) > 0.001D){
					if (EntitySelector.type(world, EntityItemAltar.class, AxisAlignedBB.getBoundingBox(targX, targY, targZ, targX, targY, targZ)).isEmpty() &&
						Math.sqrt(MathUtil.square(targX-item.posX)+MathUtil.square(targY-item.posY)+MathUtil.square(targZ-item.posZ)) < 0.275D){
						world.spawnEntityInWorld(new EntityItemAltar(world, targX, targY, targZ, item, EssenceType.DRAGON.id));
					}
				}
				else if ((updatePedestalTimer&3) == 1 && item instanceof EntityItemAltar){
					EntityItemAltar altarItem = (EntityItemAltar)item;
					altarItem.pedestalUpdate = 0;
					
					if (altar.getEssenceLevel() > 0){
						updatePedestalItem(altarItem);
						
						if (world.rand.nextInt(5) == 0){
							PacketPipeline.sendToAllAround(altar.getWorldObj().provider.dimensionId, targX, pos.getY()+0.5D, targZ, 64D, new C11ParticleAltarOrb(targX, pos.getY()+0.5D, targZ, item.posX, item.posY+0.3D, item.posZ, altar.getEssenceType().id, (byte)1));
						}
					}
				}
			}
		}
	}
	
	public static boolean hasCollisionBox(TileEntityEssenceAltar altar, int x, int y, int z){
		Block block = altar.getWorldObj().getBlock(x, y, z);
		return block.getMaterial() == Material.air ? false : block.getCollisionBoundingBoxFromPool(altar.getWorldObj(), x, y, z) != null;
	}
	
	private void updatePedestalItem(EntityItemAltar item){
		EnhancementList<EssenceAltarEnhancements> enhancements = altar.getEnhancements();
		
		ItemStack is = item.getEntityItem();
		
		/*
		 * REPAIRING
		 */
		
		if (item.worldObj.rand.nextInt(3) != 0){
			if (is.isItemStackDamageable() && is.getItemDamage() != 0 && is.getItem().isRepairable()){
				for(int a = /* TODO enhancements.contains(EssenceAltarEnhancements.SPEED) ? 2 : */1; a > 0; a--){
					if (++repairCounter > (/* TODO enhancements.contains(EssenceAltarEnhancements.EFFICIENCY) ? 72 : */56)){
						altar.drainEssence(1);
						repairCounter = 0;
					}
					
					if (updateItemCounter(is, "HEE_repair", 1) < 18)continue;
					updateItemCounter(is, "HEE_repair", 0);
					
					int amount = MathUtil.clamp(MathUtil.floor(Math.sqrt(is.getMaxDamage())*0.65D), 1, is.getItemDamage());
					is.setItemDamage(is.getItemDamage()-amount);
					item.setSparkling();
				}
			}
		}
		
		/*
		 * ENCHANTMENT UPGRADES
		 */
		
		else if (is.isItemEnchanted() && is.getItem() != Items.enchanted_book){
			for(int b = /* TODO enhancements.contains(EssenceAltarEnhancements.SPEED) ? 2 : */1; b > 0; b--){
				if (updateItemCounter(is, "HEE_enchant", 1) < 280-is.getItem().getItemEnchantability()*5)return;
				updateItemCounter(is, "HEE_enchant", 0);
				
				NBTList enchants = is.hasTagCompound() ? new NBTList(is.getEnchantmentTagList()) : null;
				if (enchants == null || enchants.isEmpty())return;
				
				for(int attempt = 0; attempt < 3; attempt++){
					WeightedMap<Enchantment> list = new WeightedMap<>();
					
					for(int a = 0; a < enchants.size(); a++){
						Enchantment e = Enchantment.enchantmentsList[enchants.getCompound(a).getShort("id")];
						if (e == null)continue;
						
						list.add(e, e.getWeight());
					}
					
					if (list.isEmpty())continue; // the enchantments are no longer in the game
					
					Enchantment chosenEnchantment = list.getRandomItem(item.worldObj.rand);
					
					for(int a = 0; a < enchants.size(); a++){
						NBTCompound tag = enchants.getCompound(a);
						if (tag.getShort("id") != chosenEnchantment.effectId)continue;
						
						int level = tag.getShort("lvl"), cost = getEnchantmentCost(chosenEnchantment, level+1);
						// TODO if (enhancements.contains(EssenceAltarEnhancements.EFFICIENCY))cost = MathUtil.ceil(cost*0.65F);
						if (level >= chosenEnchantment.getMaxLevel() || altar.getEssenceLevel() < cost)continue;
						
						altar.drainEssence(cost);
						tag.setShort("lvl", (short)(level+1));
						NBT.item(is, true).setList("ench", enchants);

						item.setSparkling();
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
					for(int a = /* TODO enhancements.contains(EssenceAltarEnhancements.SPEED) ? 2 : */1; a > 0; a--){
						if (updateItemCounter(is, "HEE_transform", 1) <= Math.max(MathUtil.ceil(recipe.cost*(/* TODO enhancements.contains(EssenceAltarEnhancements.EFFICIENCY) ? 0.65F : */1F)), recipe.cost>>1)){
							altar.drainEssence(1);
							continue;
						}
						
						updateItemCounter(is, "HEE_transform", 0);
						recipe.doTransaction(item);
						item.setSparkling();
						break;
					}
					
					break;
				}
			}
		}
	}
	
	private int getEnchantmentCost(Enchantment ench, int level){
		return MathUtil.floor(Math.max(1F, 1F+(2F*level*((float)level/ench.getMaxLevel()))+(10-ench.getWeight())*0.2F));
	}
	
	/**
	 * @param operation 0 = reset, 1 = increment
	 * @return current value
	 */
	private short updateItemCounter(ItemStack is, String counterName, int operation){
		NBTCompound tag = NBT.item(is, true);
		
		if (operation == 0){
			tag.removeTag(counterName);
			return 0;
		}

		short counter = tag.getShort(counterName);
		if (operation == 1)tag.setShort(counterName, ++counter);
		
		return counter;
	}
	
	@Override
	public void onTileWriteToNBT(NBTTagCompound nbt){
		nbt.setShort("D_repairCnt", repairCounter);
	}
	
	@Override
	public void onTileReadFromNBT(NBTTagCompound nbt){
		repairCounter = nbt.getShort("D_repairCnt");
	}
}
