package chylex.hee.mechanics.essence.handler;
/*import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.apache.commons.lang3.EnumUtils;
import org.lwjgl.opengl.GL11;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.minions.MinionData;
import chylex.hee.mechanics.minions.properties.IMinionInfusionHandler;
import chylex.hee.mechanics.minions.properties.MinionAbilities;
import chylex.hee.mechanics.minions.properties.MinionAttributes;
import chylex.hee.mechanics.minions.properties.MinionModifiers;
import chylex.hee.mechanics.minions.properties.MinionObsidianBase;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C17ParticleAltarOrb;
import chylex.hee.render.BlockRenderHelper;
import chylex.hee.render.tileentity.RenderTileEssenceAltar;
import chylex.hee.system.ReflectionPublicizer;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import chylex.hee.world.util.BlockLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SpectralEssenceHandler extends AltarActionHandler{
	private static final HashMap<BlockLocation,Long> lastUpdates = new HashMap<>();
	private static final byte[] pedestalOffsetX = new byte[]{ 0, 3, 0, -3 }, pedestalOffsetZ = new byte[]{ 3, 0, -3, 0 };
	private static final List<Enum<? extends IMinionInfusionHandler>> infusionHandlers = new ArrayList<>();
	
	static{
		infusionHandlers.addAll(EnumUtils.getEnumList(MinionModifiers.class));
		infusionHandlers.addAll(EnumUtils.getEnumList(MinionAttributes.class));
		infusionHandlers.addAll(EnumUtils.getEnumList(MinionAbilities.class));
	}
	
	public static final boolean handleMobDeath(LivingDropsEvent e){
		if (lastUpdates.isEmpty() || !(e.entity instanceof EntityLiving))return false;
		
		EntityLiving living = (EntityLiving)e.entity;
		long time = System.nanoTime();
		
		for(Iterator<Entry<BlockLocation,Long>> iter = lastUpdates.entrySet().iterator(); iter.hasNext();){
			Entry<BlockLocation,Long> entry = iter.next();
			
			if (TimeUnit.NANOSECONDS.toMillis(time-entry.getValue()) > 5000)iter.remove();
			else{
				BlockLocation loc = entry.getKey();
				TileEntityEssenceAltar altar = (TileEntityEssenceAltar)e.entity.worldObj.getTileEntity(loc.x,loc.y,loc.z);
				
				if (MathUtil.distance(loc.x+0.5D-e.entity.posX,loc.z+0.5D-e.entity.posZ) < 5D && Math.abs(e.entity.posY-loc.y) < 3D){
					int amt = e.drops.size()+(Integer)ReflectionPublicizer.get(ReflectionPublicizer.entityLivingExperienceValue,living)*2;
					for(EntityItem item:e.drops){
						ItemStack drop = item.getEntityItem();
						amt += drop.stackSize+EnchantmentHelper.getEnchantments(drop).size()*2;
					}
					
					if (amt <= 0)return true;
					
					PacketPipeline.sendToAllAround(altar,64D,new C17ParticleAltarOrb(living.posX,living.posY+0.2D,living.posZ,altar.xCoord+0.5D,altar.yCoord+0.5D,altar.zCoord+0.5D,altar.getEssenceType().id,Byte.valueOf((byte)(8+amt)),living.width,living.height));
										
					altar.drainEssence(-amt);
					ReflectionPublicizer.set(ReflectionPublicizer.entityLivingBaseRecentlyHit,living,0);
					e.drops.clear(); // just in case
					e.setCanceled(true);
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static float rotation = 0F;
	private static long prevWorldTime;
	
	private BlockLocation blockLocation;
	private byte blockLocationTimer = 0;

	private AxisAlignedBB itemBoundingBox;
	private MinionData currentMinionData;
	private Enum<? extends IMinionInfusionHandler> currentInfusionHandler;
	private byte infusionTimer = 0;
	private long lastItemHash;
	
	public SpectralEssenceHandler(TileEntityEssenceAltar altar){
		super(altar);
	}
	
	@Override
	public void onUpdate(){		
		if (blockLocation == null){
			blockLocation = new BlockLocation(altar.xCoord,altar.yCoord,altar.zCoord);
			lastUpdates.put(blockLocation,System.nanoTime());
		}
		else if (blockLocationTimer >= 20){
			lastUpdates.put(blockLocation,System.nanoTime());
			blockLocationTimer = 0;
		}
		else ++blockLocationTimer;
		
		if (currentMinionData != null){
			boolean missing = false;
			
			for(int a = 0; a < pedestalOffsetX.length; a++){
				if (!DragonEssenceHandler.hasCollisionBox(altar,altar.xCoord+pedestalOffsetX[a],altar.yCoord,altar.zCoord+pedestalOffsetZ[a])){
					missing = true;
					continue;
				}
				
				if (rand.nextInt(41) <= 1){
					PacketPipeline.sendToAllAround(altar,64D,new C17ParticleAltarOrb(altar,altar.xCoord+0.5D+pedestalOffsetX[a],altar.yCoord+0.5D,altar.zCoord+0.5D+pedestalOffsetZ[a]));
				}
			}
			
			if (itemBoundingBox == null){
				itemBoundingBox = AxisAlignedBB.getBoundingBox(altar.xCoord+0.5D-3.5D,altar.yCoord+0.9D,altar.zCoord+0.5D-3.5D,altar.xCoord+0.5+3.5D,altar.yCoord+1.6D,altar.zCoord+0.5D+3.5D);
			}

			EntityItem[] altarItems = new EntityItem[4];
			ItemStack[] altarItemStacks = new ItemStack[4];
			World world = altar.getWorldObj();
			List thrownItems = world.getEntitiesWithinAABB(EntityItem.class,itemBoundingBox);
			double targX,targY,targZ;
			long itemHash = 0;
			
			for(Object o:thrownItems){
				EntityItem item = (EntityItem)o;
				
				for(int a = 0; a < pedestalOffsetX.length; a++){
					targX = altar.xCoord+0.5D+pedestalOffsetX[a];
					targY = altar.yCoord+1.15D;
					targZ = altar.zCoord+0.5D+pedestalOffsetZ[a];
					
					if (Math.abs(item.posX-targX) > 0.001D || Math.abs(item.posY-targY) > 0.001D || Math.abs(item.posZ-targZ) > 0.001D){
						if (world.getEntitiesWithinAABB(EntityItemAltar.class,AxisAlignedBB.getBoundingBox(targX,targY,targZ,targX,targY,targZ)).size() == 0&&
							Math.sqrt(MathUtil.square(targX-item.posX)+MathUtil.square(targY-item.posY)+MathUtil.square(targZ-item.posZ)) < 0.275D){
							world.spawnEntityInWorld(new EntityItemAltar(world,targX,targY,targZ,item,EssenceType.SPECTRAL.id));
						}
					}
					else if (item instanceof EntityItemAltar){
						((EntityItemAltar)item).pedestalUpdate = 0;
						altarItems[a] = item;
						ItemStack is = altarItemStacks[a] = item.getEntityItem();
						itemHash = (((itemHash<<8)|is.toString().hashCode())<<8)|(is.hasTagCompound()?is.stackTagCompound.hashCode():0);
					}
				}
			}
			
			if (itemHash != lastItemHash){
				lastItemHash = itemHash;
				currentInfusionHandler = null;
				infusionTimer = 0;
			
				boolean hasAllItems = true;
				for(int a = 0; a < altarItems.length; a++){
					if (altarItems[a] == null){
						hasAllItems = false;
						break;
					}
				}
				
				if (hasAllItems){
					for(Enum<? extends IMinionInfusionHandler> infusionHandler:infusionHandlers){
						IMinionInfusionHandler handler = (IMinionInfusionHandler)infusionHandler;
						
						if (!handler.canApply(currentMinionData) || handler.getEssenceCost() > altar.getEssenceLevel())continue;
						
						ItemStack[] recipe = handler.getRecipe();
						if (recipe == null || recipe.length != 4)continue;
						
						for(int a = 0; a < 4; a++){
							for(int b = 0; b < 4; b++){
								if (ItemStack.areItemStacksEqual(altarItemStacks[(a+b) >= 4?a+b-4:a+b],recipe[b])){
									currentInfusionHandler = infusionHandler;
									break;
								}
							}
						}
					}
					
					if (currentInfusionHandler != null){
						infusionTimer = 100;
						world.markBlockForUpdate(altar.xCoord,altar.yCoord,altar.zCoord);
					}
				}
			}
			else if (infusionTimer > 0){
				if (--infusionTimer == 1){
					((IMinionInfusionHandler)currentInfusionHandler).apply(currentMinionData);
					
					currentInfusionHandler = null;
					infusionTimer = 0;
					for(EntityItem item:altarItems)item.setDead();
				}
			}
		}
	}
	
	@Override
	public boolean onRightClick(EntityPlayer player, ItemStack is){
		if (currentMinionData == null){
			for(MinionObsidianBase obsidianBase:MinionObsidianBase.values()){
				if (obsidianBase.blockSelector.isValid(is)){
					currentMinionData = new MinionData(obsidianBase);
					altar.getWorldObj().markBlockForUpdate(altar.xCoord,altar.yCoord,altar.zCoord);
					return true;
				}
			}
		}
		else if (is.getItem() == ItemList.spectral_wand){
			/*ItemStack minion = new ItemStack(ItemList.minion_container);
			NBTTagCompound data = new NBTTagCompound();
			currentMinionData.writeToNBT(data);
			(minion.stackTagCompound = new NBTTagCompound()).setTag("minion",data);
			
			EntityItem drop = new EntityItem(altar.getWorldObj(),altar.xCoord+0.5D,altar.yCoord+2.5D,altar.zCoord+0.5D,minion);
			drop.delayBeforeCanPickup = 10;
			altar.getWorldObj().spawnEntityInWorld(drop);
			
			currentMinionData = null;
			altar.getWorldObj().markBlockForUpdate(altar.xCoord,altar.yCoord,altar.zCoord);*/
			/*return true;
		}

		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClientUpdate(){
		if (infusionTimer > 0){
			if (itemBoundingBox == null){
				itemBoundingBox = AxisAlignedBB.getBoundingBox(altar.xCoord+0.5D-3.5D,altar.yCoord+0.9D,altar.zCoord+0.5D-3.5D,altar.xCoord+0.5+3.5D,altar.yCoord+1.6D,altar.zCoord+0.5D+3.5D);
			}
			
			List nearbyAltarItems = altar.getWorldObj().getEntitiesWithinAABB(EntityItemAltar.class,itemBoundingBox);
			double targX,targY,targZ;
			for(Object o:nearbyAltarItems){
				EntityItemAltar item = (EntityItemAltar)o;
				
				for(int a = 0; a < pedestalOffsetX.length; a++){
					targX = altar.xCoord+0.5D+pedestalOffsetX[a];
					targY = altar.yCoord+1.15D;
					targZ = altar.zCoord+0.5D+pedestalOffsetZ[a];
					
					if (Math.abs(item.posX-targX) <= 0.1D && Math.abs(item.posY-targY) <= 0.1D && Math.abs(item.posZ-targZ) <= 0.1D){
						ItemStack is = item.getEntityItem();
						
						for(int b = 0; b < rand.nextInt(Math.max(1,(101-infusionTimer)/18))+(infusionTimer == 1?10:1); b++){
							HardcoreEnderExpansion.fx.itemTarget(is,altar.getWorldObj(),targX+(rand.nextDouble()-rand.nextDouble())*0.3D,targY+0.1D+rand.nextDouble()*0.15D,targZ+(rand.nextDouble()-rand.nextDouble())*0.3D,altar.xCoord+0.5D,altar.yCoord+2.5D,altar.zCoord+0.5D,rand.nextFloat()*0.2F+0.2F);
						}
						
						if (infusionTimer > 25 && rand.nextInt(15) == 0)HardcoreEnderExpansion.fx.altarOrb(altar.getWorldObj(),targX,targY+0.2D,targZ,altar.xCoord+0.5D,altar.yCoord+2.5D,altar.zCoord+0.5D,EssenceType.SPECTRAL);
					}
				}
			}
			
			--infusionTimer;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onRender(){
		if (currentMinionData == null)return;
		
		MinionObsidianBase base = currentMinionData.getObsidianBase();
		if (base != null){
			ItemStack isToShow = base.blockSelector.getRepresentativeItem();
			
			if (altar.getWorldObj().getTotalWorldTime() != prevWorldTime){
				prevWorldTime = altar.getWorldObj().getTotalWorldTime();
				rotation += 1F;
			}

			GL11.glPushMatrix();
			GL11.glTranslatef(0F,2.4F,0F);
			GL11.glRotatef(rotation,0F,1F,0F);
			BlockRenderHelper.renderBlockAsItem(RenderTileEssenceAltar.blockRenderer,Block.getBlockFromItem(isToShow.getItem()),isToShow.getItemDamage());
			GL11.glPopMatrix();
		}
	}
	
	@Override
	public void onTileWriteToNBT(NBTTagCompound nbt){
		if (currentMinionData != null){
			NBTTagCompound data = new NBTTagCompound();
			currentMinionData.writeToNBT(data);
			nbt.setTag("S_obsidianData",data);
			nbt.setByte("clientInfusionTimer",infusionTimer);
		}
	}
	
	@Override
	public void onTileReadFromNBT(NBTTagCompound nbt){
		if (nbt.hasKey("S_obsidianData")){
			(currentMinionData = new MinionData((MinionObsidianBase)null)).readFromNBT(nbt.getCompoundTag("S_obsidianData"));
			if (nbt.hasKey("clientInfusionTimer") && (altar.hasWorldObj() && altar.getWorldObj().isRemote))infusionTimer = nbt.getByte("clientInfusionTimer");
		}
	}
}
*/