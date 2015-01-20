package chylex.hee.render.tileentity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import chylex.hee.tileentity.spawner.CustomSpawnerLogic;

@SideOnly(Side.CLIENT)
public class RenderTileCustomSpawner extends TileEntitySpecialRenderer{
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime, int what){
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x+0.5F,(float)y,(float)z+0.5F);

		CustomSpawnerLogic logic = ((TileEntityCustomSpawner)tile).getSpawnerLogic();
		Entity entity = logic.func_180612_a(tile.getWorld()); // OBFUSCATED spawn entity

		if (entity != null){
			entity.setWorld(logic.getSpawnerWorld());
			GL11.glTranslatef(0F,0.4F,0F);
			GL11.glRotatef((float)(logic.renderRotationPrev+(logic.renderRotation-logic.renderRotationPrev)*partialTickTime)*10F,0F,1F,0F);
			GL11.glRotatef(-30F,1F,0F,0F);
			GL11.glTranslatef(0F,-0.4F,0F);
			GL11.glScalef(0.4375F,0.4375F,0.4375F);
			entity.setLocationAndAngles(x,y,z,0F,0F);
			Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(entity,0D,0D,0D,0F,partialTickTime);
		}

		GL11.glPopMatrix();
	}
}
