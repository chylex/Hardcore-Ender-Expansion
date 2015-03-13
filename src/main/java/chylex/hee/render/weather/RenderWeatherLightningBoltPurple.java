package chylex.hee.render.weather;
import java.util.Random;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLightningBolt;
import net.minecraft.entity.effect.EntityLightningBolt;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWeatherLightningBoltPurple extends RenderLightningBolt{
	@Override
	public void doRender(EntityLightningBolt bolt, double x, double y, double z, float yaw, float partialTickTime){
		Tessellator tessellator = Tessellator.instance;
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);
		
		double[] xOffsets = new double[8];
		double[] zOffsets = new double[8];
		double d3 = 0D;
		double d4 = 0D;
		Random rand = new Random(bolt.boltVertex);

		for(int i = 7; i >= 0; --i){
			xOffsets[i] = d3;
			zOffsets[i] = d4;
			d3 += (rand.nextInt(11)-5);
			d4 += (rand.nextInt(11)-5);
		}

		for(int j = 0; j < 4; ++j){
			rand = new Random(bolt.boltVertex);

			for(int k = 0; k < 3; ++k){
				int l = 7;
				int i1 = 0;

				if (k > 0){
					l = 7-k;
				}

				if (k > 0){
					i1 = l-2;
				}

				double d5 = xOffsets[l]-d3;
				double d6 = zOffsets[l]-d4;

				for(int j1 = l; j1 >= i1; --j1){
					double d7 = d5;
					double d8 = d6;

					if (k == 0){
						d5 += (rand.nextInt(11)-5);
						d6 += (rand.nextInt(11)-5);
					}
					else{
						d5 += (rand.nextInt(31)-15);
						d6 += (rand.nextInt(31)-15);
					}

					tessellator.startDrawing(5);
					tessellator.setColorRGBA_F(0.33595F,0.08395F,0.43165F,0.4F);
					double d9 = 0.1D+j*0.2D;

					if (k == 0){
						d9 *= j1*0.1D+1D;
					}

					double d10 = 0.1D+j*0.2D;

					if (k == 0){
						d10 *= (j1-1)*0.1D+1D;
					}

					for(int k1 = 0; k1 < 5; ++k1){
						double d11 = x+0.5D-d9;
						double d12 = z+0.5D-d9;

						if (k1 == 1 || k1 == 2){
							d11 += d9*2D;
						}

						if (k1 == 2 || k1 == 3){
							d12 += d9*2D;
						}

						double d13 = x+0.5D-d10;
						double d14 = z+0.5D-d10;

						if (k1 == 1 || k1 == 2){
							d13 += d10*2D;
						}

						if (k1 == 2 || k1 == 3){
							d14 += d10*2D;
						}

						tessellator.addVertex(d13+d5,y+(j1*16),d14+d6);
						tessellator.addVertex(d11+d7,y+((j1+1)*16),d12+d8);
					}

					tessellator.draw();
				}
			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
