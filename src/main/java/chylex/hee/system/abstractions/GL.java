package chylex.hee.system.abstractions;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GL{
	public static final int SRC_ALPHA = GL11.GL_SRC_ALPHA;
	public static final int ONE_MINUS_SRC_ALPHA = GL11.GL_ONE_MINUS_SRC_ALPHA;
	public static final int ONE = GL11.GL_ONE;
	
	public static final int EQUAL = GL11.GL_EQUAL;
	public static final int GEQUAL = GL11.GL_GEQUAL;
	public static final int LEQUAL = GL11.GL_LEQUAL;
	public static final int GREATER = GL11.GL_GREATER;
	
	public static final int TEXTURE = GL11.GL_TEXTURE;
	public static final int MODELVIEW = GL11.GL_MODELVIEW;
	
	public static final int FLAT = GL11.GL_FLAT;
	public static final int SMOOTH = GL11.GL_SMOOTH;
	
	public static void pushMatrix(){
		GL11.glPushMatrix();
	}
	
	public static void popMatrix(){
		GL11.glPopMatrix();
	}
	
	public static void translate(float x, float y, float z){
		GL11.glTranslatef(x, y, z);
	}
	
	public static void translate(double x, double y, double z){
		GL11.glTranslated(x, y, z);
	}
	
	public static void scale(float x, float y, float z){
		GL11.glScalef(x, y, z);
	}
	
	public static void scale(double x, double y, double z){
		GL11.glScaled(x, y, z);
	}
	
	public static void rotate(float angle, float x, float y, float z){
		GL11.glRotatef(angle, x, y, z);
	}
	
	public static void rotate(double angle, float x, float y, float z){
		GL11.glRotated(angle, x, y, z);
	}
	
	public static void color(float red, float green, float blue){
		GL11.glColor4f(red, green, blue, 1F);
	}
	
	public static void color(float red, float green, float blue, float alpha){
		GL11.glColor4f(red, green, blue, alpha);
	}
	
	public static void enableTexture2D(){
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public static void disableTexture2D(){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public static void enableRescaleNormal(){
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}
	
	public static void disableRescaleNormal(){
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}
	
	public static void enableLighting(){
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	public static void disableLighting(){
		GL11.glDisable(GL11.GL_LIGHTING);
	}
	
	public static void enableCullFace(){
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	public static void disableCullFace(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public static void enableAlphaTest(){
		GL11.glEnable(GL11.GL_ALPHA_TEST);
	}
	
	public static void enableAlphaTest(int func, float ref){
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(func, ref);
	}
	
	public static void setAlphaFunc(int func, float ref){
		GL11.glAlphaFunc(func, ref);
	}
	
	public static void disableAlphaTest(){
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}
	
	public static void enableDepthTest(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public static void disableDepthTest(){
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	public static void enableDepthMask(){
		GL11.glDepthMask(true);
	}
	
	public static void disableDepthMask(){
		GL11.glDepthMask(false);
	}
	
	public static void setDepthFunc(int func){
		GL11.glDepthFunc(func);
	}
	
	public static void enableBlend(){
		GL11.glEnable(GL11.GL_BLEND);
	}
	
	public static void enableBlend(int src, int dst){
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(src, dst);
	}
	
	public static void enableBlend(int src, int dist, int srcAlpha, int dstAlpha){
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(src, dist, srcAlpha, dstAlpha);
	}
	
	public static void enableBlendAlpha(){
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void disableBlend(){
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void setBlendFunc(int src, int dst){
		GL11.glBlendFunc(src, dst);
	}
	
	public static void setBlendFunc(int src, int dist, int srcAlpha, int dstAlpha){
		OpenGlHelper.glBlendFunc(src, dist, srcAlpha, dstAlpha);
	}
	
	public static void disableFog(){
		GL11.glDisable(GL11.GL_FOG);
	}
	
	public static void setMatrixMode(int mode){
		GL11.glMatrixMode(mode);
	}
	
	public static void setShadeModel(int mode){
		GL11.glShadeModel(mode);
	}
	
	public static void loadIdentity(){
		GL11.glLoadIdentity();
	}
}
