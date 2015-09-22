package chylex.hee.tileentity;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import chylex.hee.entity.fx.FXType;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.mechanics.enhancements.types.EssenceAltarEnhancements;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.essence.ItemUseCache;
import chylex.hee.mechanics.essence.RuneItem;
import chylex.hee.mechanics.essence.handler.AltarActionHandler;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C00ClearInventorySlot;
import chylex.hee.packets.client.C17AltarRuneItemEffect;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.NBTUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityEssenceAltar extends TileEntityAbstractSynchronized implements IEnhanceableTile{
	public static final byte STAGE_BASIC = 0, STAGE_HASTYPE = 1, STAGE_WORKING = 2;
	
	private EssenceType essenceType = EssenceType.INVALID;
	private int essenceLevel;
	private byte currentStage;
	private RuneItem[] runeItems = new RuneItem[8]; // @STAGE_HASTYPE
	private byte runeItemIndex = -2; // @STAGE_HASTYPE
	
	private AltarActionHandler actionHandler;
	private final Map<String,ItemUseCache> playerItemCache = new HashMap<>();
	
	private List<Enum> enhancementList = new ArrayList<>();
	
	/*
	 * GETTERS, LOADING & SAVING
	 */
	
	public void loadFromDamage(int damage){
		if ((essenceType = EssenceType.getById(damage)) != EssenceType.INVALID){
			currentStage = STAGE_WORKING;
			createActionHandler();
			runeItemIndex = -1;
		}
	}
	
	public EssenceType getEssenceType(){
		return essenceType;
	}
	
	public int getEssenceLevel(){
		return essenceLevel;
	}
	
	public AltarActionHandler getActionHandler(){
		return actionHandler;
	}
	
	public byte getStage(){
		return currentStage;
	}
	
	public RuneItem[] getRuneItems(){
		return runeItems;
	}
	
	public ItemStack getShowedRuneItem(){
		return runeItemIndex < 0 || runeItemIndex >= runeItems.length ? null : runeItems[runeItemIndex] == null ? null : runeItems[runeItemIndex].getShowcaseItem();
	}
	
	public byte getRuneItemIndex(){
		return runeItemIndex;
	}
	
	public void drainEssence(int amount){
		essenceLevel = Math.max(essenceLevel-amount,0);
		worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
	}
	
	@Override
	public List<Enum> getEnhancements(){
		return enhancementList;
	}
	
	@Override
	public ItemStack createEnhancedItemStack(){
		return EnhancementHandler.addEnhancements(new ItemStack(BlockList.essence_altar,1,essenceType.id),enhancementList);
	}
	
	@Override
	public NBTTagCompound writeTileToNBT(NBTTagCompound nbt){
		nbt.setByte("stage",currentStage);
		nbt.setByte("essenceTypeId",essenceType.id);
		nbt.setInteger("essence",essenceLevel);
		
		NBTUtil.writeList(nbt,"runeItems",Arrays.stream(runeItems).map(item -> new NBTTagByte(item == null ? -1 : item.indexInArray)));
		nbt.setByte("runeIndex",runeItemIndex);
		
		nbt.setString("enhancements",EnhancementEnumHelper.serialize(enhancementList));
		
		if (actionHandler != null)actionHandler.onTileWriteToNBT(nbt);
		
		return nbt;
	}
	
	@Override
	public void readTileFromNBT(NBTTagCompound nbt){
		currentStage = nbt.getByte("stage");
		essenceType = EssenceType.getById(nbt.getByte("essenceTypeId"));
		essenceLevel = nbt.getInteger("essence");
		
		int[] readItems = NBTUtil.readNumericList(nbt,"runeItems").mapToInt(tag -> tag.func_150290_f()).toArray();
		
		for(int a = 0; a < Math.min(runeItems.length,readItems.length); a++){
			if (readItems[a] != -1)runeItems[a] = essenceType.itemsNeeded[readItems[a]];
		}
		
		runeItemIndex = nbt.getByte("runeIndex");
		
		enhancementList = EnhancementEnumHelper.deserialize(nbt.getString("enhancements"),EssenceAltarEnhancements.class);
		
		if (currentStage == STAGE_WORKING){
			createActionHandler();
			actionHandler.onTileReadFromNBT(nbt);
		}
	}
	
	/*
	 * UPDATING
	 */
	
	@Override
	public void updateEntity(){
		if (currentStage == STAGE_WORKING){
			if (!worldObj.isRemote)actionHandler.onUpdate();
			else actionHandler.onClientUpdate();
		}
	}
	
	/*
	 * ACTIONS
	 */
	
	private void createActionHandler(){
		try{
			actionHandler = essenceType.actionHandlerClass.getConstructor(TileEntityEssenceAltar.class).newInstance(this);
		}catch(NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e){
			Log.throwable(e,"Unable to create AltarActionHandler!");
		}
	}
	
	private void addOrRenewCache(EntityPlayer player, ItemStack is){
		ItemUseCache cache = playerItemCache.get(player.getCommandSenderName());
		if (cache == null || is.getItem() != cache.item || is.getItemDamage() != cache.damage)playerItemCache.put(player.getCommandSenderName(),new ItemUseCache(is));
		else cache.renewTime();
	}
	
	public void onRightClick(EntityPlayer player){
		ItemStack is = player.inventory.getCurrentItem();
		
		if (is == null){
			ItemUseCache cache = playerItemCache.get(player.getCommandSenderName());
			
			if (cache == null)return;
			else if (cache.getElapsedTime() > 1500000000){
				playerItemCache.remove(player.getCommandSenderName());
				return;
			}

			boolean found = false;
			
			for(byte a = 0; a < player.inventory.getSizeInventory(); a++){
				ItemStack invIs = player.inventory.getStackInSlot(a);
				if (invIs != null && invIs.getItem() == cache.item && invIs.getItemDamage() == cache.damage){
					player.inventory.mainInventory[player.inventory.currentItem] = is = invIs;
					player.inventory.setInventorySlotContents(a,null);
					
					PacketPipeline.sendToPlayer(player,new C00ClearInventorySlot(a));
					
					found = true;
					break;
				}
			}
			
			if (!found){
				playerItemCache.remove(player.getCommandSenderName());
				return;
			}
		}
		
		if (is == null)return;
		
		int giveAmount = player.capabilities.isCreativeMode ? (player.isSneaking() ? 1 : 32) : Math.min(is.stackSize,player.isSneaking() ? 1 : 32);
				
		if (is.getItem() == ItemList.essence){
			if (currentStage == STAGE_BASIC){
				essenceType = EssenceType.getById(is.getItemDamage()+1);
				currentStage = STAGE_HASTYPE;
				giveAmount = 1;
				
				runeItemIndex = 0;
				List<RuneItem> availableItems = new ArrayList<>(essenceType.itemsNeeded.length*2);
				for(RuneItem item:essenceType.itemsNeeded){
					availableItems.add(item);
					availableItems.add(item);
				}
				
				for(int a = 0; a < runeItems.length; a++){
					runeItems[a] = availableItems.remove(worldObj.rand.nextInt(availableItems.size()));
				}
				
				PacketPipeline.sendToAllAround(this,64D,new C20Effect(FXType.Basic.ESSENCE_ALTAR_SMOKE,this));
			}
			else if (currentStage == STAGE_WORKING && is.getItemDamage() == essenceType.id-1){
				essenceLevel += giveAmount;
			}
			else return;
		}
		else if (currentStage == STAGE_HASTYPE){
			if (runeItems[runeItemIndex] != null && runeItems[runeItemIndex].selector.isValid(is)){
				PacketPipeline.sendToAllAround(this,32D,new C17AltarRuneItemEffect(this,runeItems[runeItemIndex].indexInArray));
				
				giveAmount = 1;
				runeItems[runeItemIndex] = null;
				
				if (++runeItemIndex >= runeItems.length){
					currentStage = STAGE_WORKING;
					createActionHandler();
					runeItemIndex = -1;
					essenceLevel += 1;
					BlockPosM.tmp(xCoord,yCoord,zCoord).setMetadata(worldObj,blockMetadata = essenceType.id);
					
					if (essenceType == EssenceType.DRAGON)player.addStat(AchievementManager.DRAGON_ESSENCE,1);
				}
			}
			else return;
		}
		else if (currentStage == STAGE_WORKING){
			if (actionHandler.onRightClick(player,is) && !player.capabilities.isCreativeMode)--is.stackSize;
			return;
		}
		else return;
		
		addOrRenewCache(player,is);

		if (!player.capabilities.isCreativeMode)is.stackSize -= giveAmount;
		synchronize();
	}
	
	public void onBlockDestroy(){
		if (currentStage == STAGE_HASTYPE)worldObj.spawnEntityInWorld(createItem(this,new ItemStack(ItemList.essence,1,essenceType.getItemDamage())));
		
		int essence16 = MathUtil.floor(essenceLevel/16F);
		ItemStack is16 = new ItemStack(ItemList.essence,16,essenceType.getItemDamage());
		
		for(int a = 0; a < essence16; a++)worldObj.spawnEntityInWorld(createItem(this,is16.copy()));
		if (essenceLevel-(16*essence16) > 0)worldObj.spawnEntityInWorld(createItem(this,new ItemStack(ItemList.essence,essenceLevel-(16*essence16),essenceType.getItemDamage())));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(){
		return AxisAlignedBB.getBoundingBox(xCoord,yCoord,zCoord,xCoord+1,yCoord+3,zCoord+1);
	}
	
	private static EntityItem createItem(TileEntity tile, ItemStack is){
		EntityItem item = new EntityItem(tile.getWorldObj(),tile.xCoord+0.5D,tile.yCoord+1.175D,tile.zCoord+0.5D,is);
		item.delayBeforeCanPickup = 5;
		item.motionY = (item.motionX = item.motionZ = 0D)+0.02D;
		return item;
	}
}
