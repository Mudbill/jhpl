package engine.forms;

import net.buttology.lwjgl.swt.GLComposite;

import org.eclipse.swt.widgets.Shell;

public abstract class Form {
	
	public abstract void init();
	public abstract void open();
	
	public abstract GLComposite getGLComposite();
	public abstract Shell getShell();
	
	public abstract void setPitch(float pitch);
	public abstract void setYaw(float yaw);
	public abstract void setPos(float x, float y, float z);
	public abstract void setAdditionalTitle(String title, boolean async);
	public abstract void setLoadingLabelText(String text, boolean async);
	public abstract void configureProgressBar(int value, boolean async);
	public abstract void setProgressAmount(int value, boolean async);
	public abstract void increaseProgressBarState(boolean async);
	public abstract void setOpenOptionActive(boolean active, boolean async);
	
}
