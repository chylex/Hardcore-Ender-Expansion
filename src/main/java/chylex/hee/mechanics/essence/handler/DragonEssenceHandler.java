package chylex.hee.mechanics.essence.handler;
import static chylex.hee.mechanics.essence.SocketManager.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.essence.SocketManager;
import chylex.hee.mechanics.essence.handler.dragon.AltarItemRecipe;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C17ParticleAltarOrb;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.weight.ObjectWeightPair;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import chylex.hee.world.util.BlockLocation;

public class DragonEssenceHandler extends AltarActionHandler{
	public static final List<AltarItemRecipe> recipes = new ArrayList<>(Arrays.asList(new AltarItemRecipe[]{
		new AltarItemRecipe(Items.brewing_stand, ItemList.enhanced_brewing_stand, 20),
		new AltarItemRecipe(Items.ender_eye, ItemList.temple_caller, 50)/*,
		
		new AltarSpawnEggRecipe(Items.rotten_flesh, EntityZombie.class, 5),
		new AltarSpawnEggRecipe(Items.arrow, EntitySkeleton.class, 6),
		new AltarSpawnEggRecipe(Items.bone, EntitySkeleton.class, 6),
		new AltarSpawnEggRecipe(Items.string, EntitySpider.class, 5),
		new AltarSpawnEggRecipe(Items.ender_pearl, EntityEnderman.class, 4),
		new AltarSpawnEggRecipe(Items.slime_ball, EntitySlime.class, 6),
		new AltarSpawnEggRecipe(Items.potionitem, EntityWitch.class, 8),
		new AltarSpawnEggRecipe(Items.nether_wart, EntityWitch.class, 8),
		
		new AltarSpawnEggRecipe(Items.ghast_tear, EntityGhast.class, 10),
		new AltarSpawnEggRecipe(Items.blaze_rod, EntityBlaze.class, 8),
		new AltarSpawnEggRecipe(Items.gold_nugget, EntityPigZombie.class, 9),
		new AltarSpawnEggRecipe(Items.magma_cream, EntityMagmaCube.class, 6),

		new AltarSpawnEggRecipe(Items.porkchop, EntityPig.class, 1),
		new AltarSpawnEggRecipe(Items.beef, EntityCow.class, 2),
		new AltarSpawnEggRecipe(Items.leather, EntityCow.class, 2),
		new AltarSpawnEggRecipe(Items.chicken, EntityChicken.class, 2),
		new AltarSpawnEggRecipe(Items.feather, EntityChicken.class, 2),
		new AltarSpawnEggRecipe(Item.getItemFromBlock(Blocks.wool), EntitySheep.class, 3),
		new AltarSpawnEggRecipe(Items.fish, EntityOcelot.class, 3),
		new AltarSpawnEggRecipe(Items.carrot, EntityHorse.class, 8),
		new AltarSpawnEggRecipe(Items.sugar, EntityHorse.class, 8),
		new AltarSpawnEggRecipe(Items.wheat, EntityHorse.class, 8),
		new AltarSpawnEggRecipe(Item.getItemFromBlock(Blocks.red_mushroom), EntityMooshroom.class, 6),
		new AltarSpawnEggRecipe(Item.getItemFromBlock(Blocks.brown_mushroom), EntityMooshroom.class, 6),
		new AltarSpawnEggRecipe(Items.mushroom_stew, EntityMooshroom.class, 6),
		new AltarSpawnEggRecipe(Items.dye, EntitySquid.class, 2),
		
		new AltarSpawnEggRecipe(Items.emerald, EntityVillager.class, 10)*/
	}));
	
	private AxisAlignedBB itemBoundingBox;
	private List<BlockLocation> pedestals = new ArrayList<>();
	private byte updatePedestalTimer = 2;
	private long pedestalAreaHash;
	private byte lastMaxPedestals;
	private short repairCounter = 0;
	
	public DragonEssenceHandler(TileEntityEssenceAltar altar){
		super(altar);
	}
	
	@Override
	public void onUpdate(){
		if (--updatePedestalTimer <= 0){
			updatePedestalTimer = 20;
			
			int maxPedestals = Math.min(12,8+((SocketManager.getSocketEffects(altar)&EFFECT_RANGE_INCREASE) == EFFECT_RANGE_INCREASE ? (int)Math.ceil(4F*SocketManager.getSocketBoost(altar)/26F) : 0));
			int range = maxPedestals > 8 ? 4 : 3;
			long currentHash = 0L;
			
			if (lastMaxPedestals != maxPedestals){
				lastMaxPedestals = (byte)maxPedestals;
				pedestalAreaHash = 0L;
			}
			
			World world = altar.getWorldObj();
			for(int xx = -range,id; xx <= range; xx++){
				for(int zz = -range; zz <= range; zz++){
					id = Block.getIdFromBlock(world.getBlock(altar.xCoord+xx,altar.yCoord,altar.zCoord+zz));
					currentHash += ((4+xx)*7+(4+zz)+id)*262144L+(xx*id)+(zz*id);
				}
			}
			
			if (pedestalAreaHash != currentHash){
				pedestalAreaHash = currentHash;
				
				pedestals.clear();
				IdentityHashMap<Block,Byte> blockCounts = new IdentityHashMap<>();
				Block[][] blocks = new Block[range*2+1][range*2+1];
				
				for(int xx = altar.xCoord-range; xx <= altar.xCoord+range; xx++){
					for(int zz = altar.zCoord-range; zz <= altar.zCoord+range; zz++){
						if (Math.abs(xx-altar.xCoord) <= 1 && Math.abs(zz-altar.zCoord) <= 1)continue;
						
						if (world.isAirBlock(xx,altar.yCoord+1,zz)&&
						    (world.isAirBlock(xx-1,altar.yCoord,zz) || !hasCollisionBox(altar,xx-1,altar.yCoord,zz)) &&
						    (world.isAirBlock(xx+1,altar.yCoord,zz) || !hasCollisionBox(altar,xx+1,altar.yCoord,zz)) &&
						    (world.isAirBlock(xx,altar.yCoord,zz-1) || !hasCollisionBox(altar,xx,altar.yCoord,zz-1)) &&
						    (world.isAirBlock(xx,altar.yCoord,zz+1) || !hasCollisionBox(altar,xx,altar.yCoord,zz+1)) &&
						    hasCollisionBox(altar,xx,altar.yCoord,zz)){
						    Block block = world.getBlock(xx,altar.yCoord,zz);
						    if (block.getMaterial() == Material.air)continue;
						    
						    blocks[range+xx-altar.xCoord][range+zz-altar.zCoord] = block;
						    
						    if (blockCounts.containsKey(block))blockCounts.put(block,(byte)(blockCounts.get(block)+1));
						    else blockCounts.put(block,(byte)1);
						}
					}
				}
				
				SortedSet<Entry<Block,Byte>> sorted = DragonUtil.sortMapByValueDescending(blockCounts);
				for(Entry<Block,Byte> entry:sorted){
					if (entry.getValue() > maxPedestals)continue;
					
					for(int xx = -range; xx <= range; xx++){
						for(int zz = -range; zz <= range; zz++){
							if (blocks[range+xx][range+zz] != entry.getKey())continue;
							
							pedestals.add(new BlockLocation(altar.xCoord+xx,altar.yCoord,altar.zCoord+zz));
						}
					}
					
					break;
				}
			}
			
			for(BlockLocation loc:pedestals){
				if (rand.nextInt(5) <= 1){
					PacketPipeline.sendToAllAround(altar,64D,new C17ParticleAltarOrb(altar,loc.x+0.5D,altar.yCoord+0.5D,loc.z+0.5D));
				}
			}
		}
		
		if (itemBoundingBox == null){
			itemBoundingBox = AxisAlignedBB.getBoundingBox(altar.xCoord+0.5D-4.5D,altar.yCoord+0.9D,altar.zCoord+0.5D-4.5D,altar.xCoord+0.5+4.5D,altar.yCoord+1.6D,altar.zCoord+0.5D+4.5D);
		}

		World world = altar.getWorldObj();
		List thrownItems = world.getEntitiesWithinAABB(EntityItem.class,itemBoundingBox);
		double targX,targY,targZ;
		
		for(Object o:thrownItems){
			EntityItem item = (EntityItem)o;
			
			for(BlockLocation loc:pedestals){
				targX = loc.x+0.5D;
				targY = loc.y+1.15D;
				targZ = loc.z+0.5D;
				
				if (Math.abs(item.posX-targX) > 0.001D || Math.abs(item.posY-targY) > 0.001D || Math.abs(item.posZ-targZ) > 0.001D){
					if (world.getEntitiesWithinAABB(EntityItemAltar.class,AxisAlignedBB.getBoundingBox(targX,targY,targZ,targX,targY,targZ)).size() == 0&&
						Math.sqrt(MathUtil.square(targX-item.posX)+MathUtil.square(targY-item.posY)+MathUtil.square(targZ-item.posZ)) < 0.275D){
						world.spawnEntityInWorld(new EntityItemAltar(world,targX,targY,targZ,item,EssenceType.DRAGON.id));
					}
				}
				else if ((updatePedestalTimer&3) == 1 && item instanceof EntityItemAltar){
					EntityItemAltar altarItem = (EntityItemAltar)item;
					altarItem.pedestalUpdate = 0;
					updatePedestalItem(altarItem);
					
					if (rand.nextInt(5) == 0){
						PacketPipeline.sendToAllAround(altar.getWorldObj().provider.dimensionId,targX,loc.y+0.5D,targZ,64D,new C17ParticleAltarOrb(targX,loc.y+0.5D,targZ,item.posX,item.posY+0.3D,item.posZ,altar.getEssenceType().id,(byte)1));
					}
				}
			}
		}
	}
	
	public static boolean hasCollisionBox(TileEntityEssenceAltar altar, int x, int y, int z){
		Block block = altar.getWorldObj().getBlock(x,y,z);
		return block.getMaterial() == Material.air?false:block.getCollisionBoundingBoxFromPool(altar.getWorldObj(),x,y,z) != null;
	}
	
	private static final byte[] newEnchantChances = new byte[]{
		100, 45, 18, 5, 0
	};
	
	private void updatePedestalItem(EntityItemAltar item){
		byte socketEffects = SocketManager.getSocketEffects(altar),socketBoost = SocketManager.getSocketBoost(altar);
		
		ItemStack is = item.getEntityItem();
		
		/*
		 * REPAIRING
		 */
		
		if (rand.nextInt(3) != 0){
			if (is.isItemStackDamageable() && is.getItemDamage() != 0){
				for(int a = 0; a < 1+((socketEffects&EFFECT_SPEED_BOOST) == EFFECT_SPEED_BOOST?(1+(socketBoost>>2)):0); a++){
					if (++repairCounter > 56+((socketEffects&EFFECT_LOWER_COST) == EFFECT_LOWER_COST?1+Math.floor(socketBoost*0.6D):0)){
						altar.drainEssence(1);
						repairCounter = 0;
					}
					
					if (updateItemCounter(is,"HEE_repair",1) < 18)continue;
					updateItemCounter(is,"HEE_repair",0);
					
					int amount = Math.min(is.getItemDamage(),Math.max(1,(int)Math.floor(Math.sqrt(is.getMaxDamage())*0.65D)));
					is.setItemDamage(is.getItemDamage()-amount);
					item.hasChanged = true;
				}
			}
		}
		
		/*
		 * ENCHANTING
		 */
		
		else if ((is.isItemEnchantable() || is.isItemEnchanted()) && is.getItem() != Items.book && is.getItem() != Items.enchanted_book){
			for(int b = 0; b < 1+((socketEffects&EFFECT_SPEED_BOOST) == EFFECT_SPEED_BOOST?1+Math.floor(socketBoost*0.4D):0); b++){
				if (updateItemCounter(is,"HEE_enchant",1) < 400-is.getItem().getItemEnchantability()*4)return;
				updateItemCounter(is,"HEE_enchant",0);
				
				NBTTagList enchants = is.getEnchantmentTagList();
				if (enchants == null)enchants = new NBTTagList();
				
				for(int attempt = 0; attempt < 3; attempt++){
					if (rand.nextInt(100) < newEnchantChances[Math.min(4,enchants.tagCount())]){ // NEW ENCHANT
						List enchList = EnchantmentHelper.buildEnchantmentList(rand,is,29);
						if (enchList == null)return; // shouldn't happen!
						
						for(Object o:enchList){
							EnchantmentData ench = (EnchantmentData)o;
							short cost = getEnchantmentCost(ench.enchantmentobj,1);
							if ((socketEffects&EFFECT_LOWER_COST) == EFFECT_LOWER_COST)cost = (short)Math.max(cost>>2,cost*(1F-socketBoost/42F));
							
							if (!hasEnchantment(enchants,ench.enchantmentobj) && altar.getEssenceLevel() >= cost){
								boolean incompatible = false;
								for(int a = 0; a < enchants.tagCount(); a++){
									Enchantment testEnchantment = Enchantment.enchantmentsList[enchants.getCompoundTagAt(a).getShort("id")];
									if (testEnchantment != null && !ench.enchantmentobj.canApplyTogether(testEnchantment)){
										incompatible = true;
										break;
									}
								}
								if (incompatible)continue;
								
								is.addEnchantment(ench.enchantmentobj,1);
								altar.drainEssence(cost);
								item.hasChanged = true;
								
								attempt = 999;
								break;
							}
						}
					}
					else{ // UPGRADE ENCHANT
						WeightedList<ObjectWeightPair<Enchantment>> list = new WeightedList<>();
						
						for(int a = 0; a < enchants.tagCount(); a++){
							Enchantment e = Enchantment.enchantmentsList[enchants.getCompoundTagAt(a).getShort("id")];
							if (e == null)continue;
							
							list.add(new ObjectWeightPair<Enchantment>(e,e.getWeight()));
						}
						
						Enchantment chosenEnchantment = list.getRandomItem(rand).getObject();
						for(int a = 0; a < enchants.tagCount(); a++){
							NBTTagCompound tag = enchants.getCompoundTagAt(a);
							if (tag.getShort("id") != chosenEnchantment.effectId)continue;
							
							short level = tag.getShort("lvl"),cost = getEnchantmentCost(chosenEnchantment,level+1);
							if ((socketEffects&EFFECT_LOWER_COST) == EFFECT_LOWER_COST)cost = (short)Math.max(cost>>2,cost*(1F-socketBoost/42F));
							if (level >= chosenEnchantment.getMaxLevel() || altar.getEssenceLevel() < cost)continue;
							
							altar.drainEssence(cost);
							tag.setShort("lvl",(short)(level+1));
							if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
							is.stackTagCompound.setTag("ench",enchants);

							item.hasChanged = true;
							attempt = 999;
							break;
						}
					}
				}
			}
		}
		
		/*
		 * ITEM TRANSFORMATIONS
		 */
		
		else if (rand.nextInt(5) == 0){
			for(AltarItemRecipe recipe:recipes){
				if (recipe.isApplicable(is)){
					for(int a = 0; a < 1+((socketEffects&EFFECT_SPEED_BOOST) == EFFECT_SPEED_BOOST?Math.ceil(socketBoost*0.15D):0); a++){
						if (updateItemCounter(is,"HEE_transform",1) <= Math.max(recipe.getCost()*((socketEffects&EFFECT_LOWER_COST) == EFFECT_LOWER_COST?1F-(socketBoost/40F):1F),recipe.getCost()>>1)){
							altar.drainEssence(1);
							continue;
						}
						updateItemCounter(is,"HEE_transform",0);
						
						recipe.doTransaction(item);
						item.hasChanged = true;
						for(EntityPlayer observer:ObservationUtil.getAllObservers(item,8D))KnowledgeRegistrations.DRAGON_ESSENCE_ALTAR.tryUnlockFragment(observer,0.3F);
						break;
					}
					
					break;
				}
			}
		}
	}
	
	private boolean hasEnchantment(NBTTagList enchantmentList, Enchantment ench){
		for(int a = 0; a < enchantmentList.tagCount(); a++){
			if (enchantmentList.getCompoundTagAt(a).getShort("id") == ench.effectId)return true;
		}
		return false;
	}
	
	private short getEnchantmentCost(Enchantment ench, int level){
		return (short)Math.floor(Math.max(1F,1F+(1.7F*level*((float)level/ench.getMaxLevel()))+(10-ench.getWeight())*0.2F));
	}
	
	/**
	 * @param operation 0 = reset, 1 = increment
	 * @return current value
	 */
	private short updateItemCounter(ItemStack is, String counterName, int operation){
		if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
		NBTTagCompound nbt = is.stackTagCompound;
		
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
