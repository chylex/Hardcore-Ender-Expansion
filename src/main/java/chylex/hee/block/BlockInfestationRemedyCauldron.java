package chylex.hee.block;
/*import java.util.Random;
import net.minecraft.block.BlockCauldron;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.item.ItemList;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockInfestationRemedyCauldron extends BlockCauldron{
	@SideOnly(Side.CLIENT)
	public IIcon waterGray;
	
	public BlockInfestationRemedyCauldron(){
		super();
		setTickRandomly(true);
	}
	
	@Override
	public int getRenderType(){
		return ModCommonProxy.renderIdInfestationRemedyCauldron;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		if (rand.nextInt(3) == 0 || world.getBlock(x,y-1,z) != Blocks.fire)return;
		
		int meta = world.getBlockMetadata(x,y,z);
		if (CauldronState.getByMeta(meta).isCooking)world.setBlockMetadataWithNotify(x,y,z,meta+1,3);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if (world.isRemote)return true;
		
		ItemStack is = player.inventory.getCurrentItem();
		if (is == null)return true;
		
		int meta = world.getBlockMetadata(x,y,z);
		CauldronState state = CauldronState.getByMeta(meta);
		
		if (state.canBeBottled && is.getItem() == Items.glass_bottle){
			ItemStack remedy = new ItemStack(ItemList.infestation_remedy);
			
			if ((--is.stackSize) <= 0)player.inventory.setInventorySlotContents(player.inventory.currentItem,remedy);
			else if (!player.inventory.addItemStackToInventory(remedy))world.spawnEntityInWorld(new EntityItem(world,x+0.5D,y+1.5D,z+0.5D,remedy));
			
			if (player instanceof EntityPlayerMP)((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
			
			if (CauldronState.getByMeta(meta+1) == CauldronState.EMPTY)world.setBlock(x,y,z,Blocks.cauldron,0,3);
			else world.setBlockMetadataWithNotify(x,y,z,meta+1,3);
			
			return true;
		}
		else if (is.getItem() instanceof ItemArmor){
			ItemArmor armor = (ItemArmor)is.getItem();
			if (armor.getArmorMaterial() != ArmorMaterial.CLOTH)return true;
			
			int red = (int)Math.floor(state.color[0]*255F),green = (int)Math.floor(state.color[1]*255F),blue = (int)Math.floor(state.color[2]*255F);
			armor.func_82813_b(is,red<<16|green<<8|blue); // OBFUSCATED set armor dye
		}

		return true;
	}

	@Override
	public void fillWithRain(World world, int x, int y, int z){}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		world.markBlockRangeForRenderUpdate(x,y,z,x,y,z);
		
		if (CauldronState.getByMeta(world.getBlockMetadata(x,y,z)).isCooking && world.getBlock(x,y-1,z) == Blocks.fire && rand.nextInt(3) == 0){
			HardcoreEnderExpansion.fx.bubble(world,x+0.5D+(rand.nextFloat()-0.5D)*0.5D,y+0.5F+rand.nextFloat()*0.4F,z+0.5D+(rand.nextFloat()-0.5D)*0.5D,0D,0.03D,0D);
			if (rand.nextInt(7) == 0)world.playSound(x+0.5D,y+0.8D,z+0.5D,"hardcoreenderexpansion:environment.random.bubble",1F,rand.nextFloat()*0.1F+0.95F,false);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		super.registerBlockIcons(iconRegister);
		waterGray = iconRegister.registerIcon("hardcoreenderexpansion:cauldron_water_gray");
	}
	
	public static enum CauldronState{
		HAS_SILVERFISH_BLOOD(0,true,false,false,false,false,new float[]{ 0.4063F,0.1484F,0.5039F }), // 104,38,129
		HAS_BAT_WING(1,false,true,false,false,false,new float[]{ 0.2929F,0.2656F,0.3828F }), // 75,68,98
		HAS_DRY_SPLINTER(2,false,false,true,false,false,new float[]{ 0.1523F,0.1328F,0.2852F }), // 39,34,73
		HAS_SILVERFISH_BLOOD_AND_BAT_WING(3,true,true,false,false,false,new float[]{ 0.3711F,0.2109F,0.3516F }), // 95,54,90
		HAS_SILVERFISH_BLOOD_AND_DRY_SPLINTER(4,true,false,true,false,false,new float[]{ 0.2109F,0.1094F,0.1758F }), // 54,28,45
		HAS_BAT_WING_AND_DRY_SPLINTER(5,false,true,true,false,false,new float[]{ 0.1914F,0.1406F,0.0977F }), // 49,36,25
		HAS_ALL_ITEMS(6,true,true,true,true,false,new float[]{ 0.2305F,0.1172F,0.0821F }), // 59,30,21
		COOKING_1(7,true,true,true,true,false,new float[]{ 0.2891F,0.1367F,0.1094F }), // 74,35,28
		COOKING_2(8,true,true,true,true,false,new float[]{ 0.3477F,0.1563F,0.1328F }), // 89,40,34
		COOKING_3(9,true,true,true,true,false,new float[]{ 0.4063F,0.1758F,0.1602F }), // 104,45,41
		COOKING_4(10,true,true,true,true,false,new float[]{ 0.4609F,0.1953F,0.1836F }), // 118,50,47
		COOKING_5(11,true,true,true,true,false,new float[]{ 0.5195F,0.2148F,0.2109F }), // 133,55,54
		FULL(12,true,true,true,false,true,new float[]{ 0.5781F,0.2344F,0.2344F }), // 148,60,60
		ALMOST_FULL(13,true,true,true,false,true,new float[]{ 0.5781F,0.2344F,0.2344F }),
		NEARLY_EMPTY(14,true,true,true,false,true,new float[]{ 0.5781F,0.2344F,0.2344F }),
		EMPTY(15,false,false,false,false,false,new float[]{ 0.5781F,0.2344F,0.2344F });
		
		public static CauldronState getByMeta(int meta){
			return values()[Math.min(meta,values().length-1)];
		}
		
		public final byte metadata;
		public final boolean hasSilverfishBlood;
		public final boolean hasBatWing;
		public final boolean hasDrySplinter;
		public final boolean isCooking;
		public final boolean canBeBottled;
		public final float[] color;

		CauldronState(int metadata, boolean hasSilverfishBlood, boolean hasBatWing, boolean hasDrySplinter, boolean isCooking, boolean canBeBottled, float[] color){
			this.metadata = (byte)metadata;
			this.hasSilverfishBlood = hasSilverfishBlood;
			this.hasBatWing = hasBatWing;
			this.hasDrySplinter = hasDrySplinter;
			this.isCooking = isCooking;
			this.canBeBottled = canBeBottled;
			this.color = color;
		}
		
		public int getLiquidAmount(){
			return this == NEARLY_EMPTY?1:this == ALMOST_FULL?2:3;
		}
		
		public CauldronState tryAddItem(Item item){
			if (hasSilverfishBlood && hasBatWing && hasDrySplinter)return null;
			
			if (item == ItemList.silverfish_blood){
				return hasSilverfishBlood?null:
					   hasBatWing && hasDrySplinter?HAS_ALL_ITEMS:
					   hasBatWing?HAS_SILVERFISH_BLOOD_AND_BAT_WING:
					   hasDrySplinter?HAS_SILVERFISH_BLOOD_AND_DRY_SPLINTER:
					   null;
			}
			else if (item == ItemList.infested_bat_wing){
				return hasBatWing?null:
					   hasSilverfishBlood && hasDrySplinter?HAS_ALL_ITEMS:
					   hasSilverfishBlood?HAS_SILVERFISH_BLOOD_AND_BAT_WING:
					   hasDrySplinter?HAS_BAT_WING_AND_DRY_SPLINTER:
	 				   null;
			}
			else if (item == ItemList.dry_splinter){
				return hasDrySplinter?null:
					   hasSilverfishBlood && hasBatWing?HAS_ALL_ITEMS:
					   hasSilverfishBlood?HAS_SILVERFISH_BLOOD_AND_DRY_SPLINTER:
					   hasBatWing?HAS_BAT_WING_AND_DRY_SPLINTER:
	 				   null;
			}
			
			return null;
		}
	}
}
*/