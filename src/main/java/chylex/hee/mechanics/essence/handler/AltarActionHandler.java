package chylex.hee.mechanics.essence.handler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AltarActionHandler{
	protected final TileEntityEssenceAltar altar;
	
	public AltarActionHandler(TileEntityEssenceAltar altar){
		this.altar = altar;
	}
	
	public void onUpdate(){}
	public boolean onRightClick(EntityPlayer player, ItemStack is){ return false; }
	@SideOnly(Side.CLIENT)
	public void onClientUpdate(){}
	@SideOnly(Side.CLIENT)
	public void onRender(){}
	public void onTileWriteToNBT(NBTTagCompound nbt){}
	public void onTileReadFromNBT(NBTTagCompound nbt){}
}
