package chylex.hee.item.block;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import chylex.hee.init.BlockList;
import chylex.hee.proxy.ModClientProxy;
import chylex.hee.render.item.RenderItemEndermanHead;
import chylex.hee.system.abstractions.GL;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.tileentity.TileEntityEndermanHead;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockEndermanHead extends ItemBlock{
	public ItemBlockEndermanHead(Block block){
		super(block);
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
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		Pos pos = Pos.at(x,y,z);
		Facing6 sideFacing = Facing6.fromSide(side);
		if (sideFacing == Facing6.DOWN_NEGY || !pos.getMaterial(world).isSolid())return false;
		
		pos = pos.offset(sideFacing);
		if (!player.canPlayerEdit(pos.getX(),pos.getY(),pos.getZ(),side,is) || !BlockList.enderman_head.canPlaceBlockAt(world,pos.getX(),pos.getY(),pos.getZ()))return false;
		
		pos.setBlock(world,BlockList.enderman_head,side,2);
		TileEntityEndermanHead tile = (TileEntityEndermanHead)pos.getTileEntity(world);
		
		if (side == EnumFacing.UP.ordinal())tile.setRotation(MathHelper.floor_double((player.rotationYaw*16F/360F)+0.5D)&15);
		else tile.setMeta(side);

		--is.stackSize;
		return true;
	}
	
	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity){
		return armorType == 0;
	}

	private static final ResourceLocation tex = new ResourceLocation("hardcoreenderexpansion:textures/armor/enderman_head.png");
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onArmorModelSet(RenderPlayerEvent.SetArmorModel e){
		if (e.stack == null || e.stack.getItem() != this || e.slot != 3)return;

		Minecraft.getMinecraft().renderEngine.bindTexture(tex);
		e.renderer.setRenderPassModel(ModClientProxy.endermanHeadModelBiped);
		
		ModClientProxy.endermanHeadModelBiped.isSneak = e.entityPlayer.isSneaking();
		ModClientProxy.endermanHeadModelBiped.isRiding = e.entityPlayer.isRiding();
		
		GL.color(1F,1F,1F);
		e.result = e.stack.isItemEnchanted() ? 15 : 1;
		
		RenderItemEndermanHead.isRenderingArmor = true;
	}
}
