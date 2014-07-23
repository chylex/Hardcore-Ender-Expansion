package chylex.hee.item;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.misc.ApocalypseEvents;
import chylex.hee.proxy.ModClientProxy;
import chylex.hee.tileentity.TileEntityEndermanHead;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (side == 0 || !world.getBlock(x,y,z).getMaterial().isSolid())return false;
		
		switch(side){
			case 1: ++y; break;
			case 2: --z; break;
			case 3: ++z; break;
			case 4: --x; break;
			case 5: ++x; break;
			default:
		}

		if (!player.canPlayerEdit(x,y,z,side,is) || !BlockList.enderman_head.canPlaceBlockAt(world,x,y,z))return false;

		world.setBlock(x,y,z,BlockList.enderman_head,side,2);
		
		if (side == 1 && ApocalypseEvents.checkEndermanpocalypseStructure(world,x,y,z)){
			//int rotation = (int)((MathHelper.floor_double((player.rotationYaw*16F/360F)+0.5D)&15)*360F/16F);
			// TODO probably do something?
			--is.stackSize;
			return true;
		}
		
		TileEntityEndermanHead tile = (TileEntityEndermanHead)world.getTileEntity(x,y,z);
		if (tile != null){
			if (side == 1)tile.setRotation(MathHelper.floor_double((player.rotationYaw*16F/360F)+0.5D)&15);
			else tile.setMeta(side);
		}

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
		
		GL11.glColor3f(1F,1F,1F);
		e.result = e.stack.isItemEnchanted()?15:1;
	}
}
