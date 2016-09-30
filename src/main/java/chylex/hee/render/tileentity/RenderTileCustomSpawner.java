package chylex.hee.render.tileentity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.system.abstractions.GL;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import chylex.hee.tileentity.spawner.CustomSpawnerLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileCustomSpawner extends TileEntitySpecialRenderer{
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime){
		GL.pushMatrix();
		GL.translate((float)x+0.5F, (float)y, (float)z+0.5F);

		CustomSpawnerLogic logic = ((TileEntityCustomSpawner)tile).getSpawnerLogic();
		Entity entity = logic.func_98281_h(); // OBFUSCATED spawn entity

		if (entity != null){
			entity.setWorld(logic.getSpawnerWorld());
			GL.translate(0F, 0.4F, 0F);
			GL.rotate((float)(logic.renderRotationPrev+(logic.renderRotation-logic.renderRotationPrev)*partialTickTime)*10F, 0F, 1F, 0F);
			GL.rotate(-30F, 1F, 0F, 0F);
			GL.translate(0F, -0.4F, 0F);
			GL.scale(0.4375F, 0.4375F, 0.4375F);
			entity.setLocationAndAngles(x, y, z, 0F, 0F);
			RenderManager.instance.renderEntityWithPosYaw(entity, 0D, 0D, 0D, 0F, partialTickTime);
		}

		GL.popMatrix();
	}
}
