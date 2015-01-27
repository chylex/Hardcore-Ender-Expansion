package chylex.hee.item;
import net.minecraft.block.BlockSkull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEndermanHead;

public class ItemEndermanHead extends Item{
	@Override
	public String getUnlocalizedName(ItemStack is){
		return BlockList.enderman_head.getUnlocalizedName();
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		int armorSlot = 3;

		if (player.getCurrentArmor(armorSlot) == null){
			ItemStack copy = is.copy();
			copy.stackSize = 1;
			player.setCurrentItemOrArmor(armorSlot+1,copy);
			--is.stackSize;
		}

		return is;
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ){
		if (side == EnumFacing.UP || !world.getBlockState(pos).getBlock().getMaterial().isSolid())return false;
		pos = pos.offset(side);

		if (!player.canPlayerEdit(pos,side,is) || !BlockList.enderman_head.canPlaceBlockAt(world,pos))return false;
		
		world.setBlockState(pos,BlockList.enderman_head.getDefaultState().withProperty(BlockSkull.FACING,side));
		
		/*if (side == EnumFacing.UP && ApocalypseEvents.checkEndermanpocalypseStructure(world,pos)){
			int rotation = (int)((MathHelper.floor_double((player.rotationYaw*16F/360F)+0.5D)&15)*360F/16F);
			--is.stackSize;
			return true;
		}*/
		
		TileEntityEndermanHead tile = (TileEntityEndermanHead)world.getTileEntity(pos);
		if (tile != null)tile.setMeta(side != EnumFacing.UP ? 0 : (int)((MathUtil.floor((player.rotationYaw*16F/360F)+0.5D)&15)*360F/16F));

		--is.stackSize;
		return true;
	}
	
	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity){
		return armorType == 0;
	}

	private static final ResourceLocation tex = new ResourceLocation("hardcoreenderexpansion:textures/armor/enderman_head.png");
	
	/*@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onArmorModelSet(RenderPlayerEvent.SetArmorModel e){
		if (e.stack == null || e.stack.getItem() != this || e.slot != 3)return;
		
		e.renderer.getPlayerModel().bipedHead.isHidden = true;
		Minecraft.getMinecraft().renderEngine.bindTexture(tex);
		e.renderer.setRenderPassModel(ModClientProxy.endermanHeadModelBiped);
		
		ModClientProxy.endermanHeadModelBiped.isSneak = e.entityPlayer.isSneaking();
		ModClientProxy.endermanHeadModelBiped.isRiding = e.entityPlayer.isRiding();
		
		GL11.glColor3f(1F,1F,1F);
		e.result = e.stack.isItemEnchanted() ? 15 : 1;
	}*/
	// TODO
}
