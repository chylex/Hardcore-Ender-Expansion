package chylex.hee.block.override;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class BlockEndPortalFrameCustom extends BlockEndPortalFrame{
	public BlockEndPortalFrameCustom(){
		setStepSound(soundTypeGlass).setLightLevel(0.125F).setBlockName("endPortalFrame").setBlockUnbreakable().setResistance(6000000F).setCreativeTab(CreativeTabs.tabDecorations).setBlockTextureName("endframe");
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if (world.isRemote)player.addChatMessage(new ChatComponentText("This is not the Stronghold you're looking for."));
		return true;
	}
}
