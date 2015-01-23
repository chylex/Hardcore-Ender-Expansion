package chylex.hee.tileentity;
import gnu.trove.map.hash.TObjectFloatHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.block.BlockCorruptedEnergy;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.ItemDamagePair;
import chylex.hee.system.util.MathUtil;

public class TileEntityExtractionTable extends TileEntityAbstractTable{
	private static final int[] slotsTop = new int[]{ 0 }, slotsSides = new int[]{ 1, 2 };
	private static final TObjectFloatHashMap<ItemDamagePair> energyValues = new TObjectFloatHashMap<>();
	private static final float maxStoredEnergy = EnergyChunkData.energyDrainUnit*10F;
	
	private static void setItemEnergy(Block block, float energyMp){
		energyValues.put(new ItemDamagePair(Item.getItemFromBlock(block),-1),energyMp*EnergyChunkData.energyDrainUnit);
	}
	
	private static void setItemEnergy(Item item, float energyMp){
		energyValues.put(new ItemDamagePair(item,-1),energyMp*EnergyChunkData.energyDrainUnit);
	}
	
	private static float getItemEnergy(ItemStack is){
		for(ItemDamagePair idp:energyValues.keySet()){
			if (idp.check(is))return energyValues.get(idp);
		}
		
		return 0;
	}
	
	static{
		setItemEnergy(Blocks.end_stone, 0.40F);
		setItemEnergy(BlockList.end_terrain, 0.50F);
		setItemEnergy(ItemList.adventurers_diary, 0.50F);
		setItemEnergy(BlockList.persegrit, 0.55F);
		setItemEnergy(ItemList.silverfish_blood, 0.65F);
		setItemEnergy(ItemList.stardust, 0.75F);
		setItemEnergy(ItemList.end_powder, 0.80F);
		setItemEnergy(ItemList.igneous_rock, 0.90F);
		setItemEnergy(ItemList.endium_ingot, 1.10F);
		setItemEnergy(ItemList.knowledge_note, 1.20F);
		setItemEnergy(Items.ender_pearl, 1.50F);
		setItemEnergy(ItemList.enhanced_ender_pearl, 1.65F);
		setItemEnergy(BlockList.enhanced_brewing_stand, 1.65F);
		setItemEnergy(BlockList.enhanced_tnt, 1.65F);
		setItemEnergy(Items.ender_eye, 1.95F);
		setItemEnergy(ItemList.instability_orb, 2.60F);
		setItemEnergy(BlockList.spooky_log, 3.10F);
		setItemEnergy(BlockList.spooky_leaves, 3.10F);
		setItemEnergy(ItemList.rune, 3.25F);
		setItemEnergy(ItemList.auricion, 3.40F);
		setItemEnergy(Blocks.ender_chest, 3.75F);
		setItemEnergy(ItemList.temple_caller, 4.00F);
		setItemEnergy(ItemList.ectoplasm, 4.20F);
		setItemEnergy(ItemList.spectral_tear, 4.20F);
		setItemEnergy(BlockList.void_chest, 4.30F);
		setItemEnergy(ItemList.charm, 4.80F);
		setItemEnergy(ItemList.spatial_dash_gem, 5.50F);
		setItemEnergy(ItemList.energy_wand_core, 6.20F);
		setItemEnergy(ItemList.energy_wand, 6.40F);
		setItemEnergy(ItemList.transference_gem, 7.80F);
		setItemEnergy(BlockList.endium_block, 10.10F);
		setItemEnergy(ItemList.living_matter, 10.50F);
	}

	private byte leakTimer = 100;
	
	@Override
	protected float getDrainAmount(){
		return time < totalTime ? EnergyChunkData.energyDrainUnit*0.25F : 0F;
	}
	
	@Override
	public void update(){
		super.update();
		
		if (!worldObj.isRemote && leakTimer < 0 || (leakTimer -= (items[2] == null || items[2].getItem() != ItemList.instability_orb ? 16 : Math.max(0,16-items[2].stackSize))) < 0){
			leakTimer = 100;
			
			if (storedEnergy >= EnergyChunkData.minSignificantEnergy){
				float release = EnergyChunkData.energyDrainUnit*0.08F+(float)Math.sqrt(storedEnergy)*0.005F;
				storedEnergy = Math.max(storedEnergy-release,0F);
				
				List<TileEntityEnergyCluster> clusters = new ArrayList<>();
				int x = getPos().getX(), y = getPos().getY(), z = getPos().getZ(), chunkX = x>>4, chunkZ = z>>4, cx, cz;
				
				for(int a = 0; a < 9; a++){
					Map<BlockPos,TileEntity> tiles = worldObj.getChunkFromChunkCoords(chunkX+chunkOffX[a],chunkZ+chunkOffZ[a]).getTileEntityMap();
					cx = chunkX*16+chunkOffX[a]*16;
					cz = chunkZ*16+chunkOffZ[a]*16;
					
					for(Entry<BlockPos,TileEntity> entry:tiles.entrySet()){
						BlockPos pos = entry.getKey();
						
						if (entry.getValue().getClass() == TileEntityEnergyCluster.class && MathUtil.distance(cx+pos.getX()-x,pos.getY()-y,cz+pos.getZ()-z) <= 16D){
							clusters.add((TileEntityEnergyCluster)entry.getValue());
						}
					}
				}
				
				if (!clusters.isEmpty()){
					Collections.shuffle(clusters);
					
					for(Iterator<TileEntityEnergyCluster> iter = clusters.iterator(); iter.hasNext();){
						if ((release = iter.next().addEnergy(release,this)) < EnergyChunkData.minSignificantEnergy)break;
					}
				}
				
				if (release >= EnergyChunkData.minSignificantEnergy && worldObj.provider.getDimensionId() == 1){
					release = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords(worldObj,x,z,true).addEnergy(release);
				}
				
				if (release >= EnergyChunkData.minSignificantEnergy){
					BlockPosM testPos = new BlockPosM();
					
					for(int attempt = 0, placed = 0; attempt < 8 && placed < 4; attempt++){
						testPos.moveTo(getPos()).moveBy(worldObj.rand.nextInt(7)-3,worldObj.rand.nextInt(7)-3,worldObj.rand.nextInt(7)-3);
						
						if (testPos.isAir(worldObj)){
							testPos.setBlock(worldObj,BlockCorruptedEnergy.createState(3+MathUtil.floor(release*4.5F)));
							++placed;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void invalidateInventory(){
		if (worldObj != null && worldObj.isRemote)return;
		if (requiredStardust == 0 || items[0] == null)resetTable();
		
		if (items[0] != null){
			float energy = getItemEnergy(items[0]);
			
			if (energy > 0F){
				requiredStardust = (byte)(1+1.5F*Math.sqrt(energy*4F/EnergyChunkData.energyDrainUnit));
				timeStep = (short)Math.max(1,20-(requiredStardust>>1));
				updateComparatorStatus();
			}
		}
	}

	@Override
	protected boolean onWorkFinished(){
		float energy = getItemEnergy(items[0]);
		if (storedEnergy+energy > maxStoredEnergy)return false;
		
		storedEnergy += energy;
		if ((items[0].stackSize -= 1) <= 0)items[0] = null;
		if ((items[1].stackSize -= requiredStardust) <= 0)items[1] = null;
		
		return true;
	}
	
	@Override
	public float getMaxStoredEnergy(){
		return maxStoredEnergy;
	}
	
	@Override
	public int getHoldingStardust(){
		return items[1] == null ? 0 : items[1].stackSize;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setByte("leakTimer",leakTimer);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		leakTimer = nbt.getByte("leakTimer");
		invalidateInventory();
	}

	@Override
	public int getSizeInventory(){
		return 3;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack is){
		super.setInventorySlotContents(slot,is);
		if (slot == 0)invalidateInventory();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return slot == 1 ? is.getItem() == ItemList.stardust : slot == 2 ? is.getItem() == ItemList.instability_orb : true;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return side == EnumFacing.DOWN ? ArrayUtils.EMPTY_INT_ARRAY : side == EnumFacing.UP ? slotsTop : slotsSides;
	}

	@Override
	protected String getContainerDefaultName(){
		return "container.extractionTable";
	}
}
