package chylex.hee.render.tileentity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileCustomSpawner extends TileEntitySpecialRenderer{
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime){
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x+0.5F,(float)y,(float)z+0.5F);

		MobSpawnerBaseLogic logic = ((TileEntityCustomSpawner)tile).getSpawnerLogic();
		Entity entity = logic.func_98281_h(); // OBFUSCATED spawn entity

		if (entity != null){
			entity.setWorld(logic.getSpawnerWorld());
			GL11.glTranslatef(0F,0.4F,0F);
			GL11.glRotatef((float)(logic.field_98284_d+(logic.field_98287_c-logic.field_98284_d)*partialTickTime)*10F,0F,1F,0F);
			GL11.glRotatef(-30F,1F,0F,0F);
			GL11.glTranslatef(0F,-0.4F,0F);
			GL11.glScalef(0.4375F,0.4375F,0.4375F);
			entity.setLocationAndAngles(x,y,z,0F,0F);
			RenderManager.instance.renderEntityWithPosYaw(entity,0D,0D,0D,0F,partialTickTime);
		}

		GL11.glPopMatrix();
	}
}
