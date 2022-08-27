package engine.forms.editor.material;

import java.util.ArrayList;
import java.util.List;

import net.buttology.lwjgl.swt.GLComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import engine.forms.Form;
import engine.materials.EMaterialType;

public class ShellMaterialEditor extends Form {

	private static ShellMaterialEditor instance;
	public static ShellMaterialEditor getInstance() { return instance; }
	
	//================================================================
	
	protected Display display;
	protected Shell shell;
	private Composite compositeHidden;
	private Group groupTextureUnits;
	private List<CompositeTextureUnits> compositeTextureUnits = new ArrayList<CompositeTextureUnits>();
	private CompositeMsv compositeMsv;
	private GroupPreview groupPreview;

	public ShellMaterialEditor() { instance = this; }
	
//	/**
//	 * Launch the application.
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		try {
//			ShellMaterialEditor window = new ShellMaterialEditor();
//			window.open();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Open the window.
	 */
	public void open() {
		shell.open();
		shell.layout();
		while(!shell.isDisposed())
		{
			if(!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(1024, 768);
		shell.setMinimumSize(916, 600);
		shell.setText("Material Editor");
		shell.setLayout(new FormLayout());
		
		/* MENU */
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText("File");
		
		Menu menu_1 = new Menu(menuItem);
		menuItem.setMenu(menu_1);
		
		MenuItem menuItem_1 = new MenuItem(menu_1, SWT.NONE);
		menuItem_1.setText("New");
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		MenuItem menuItem_3 = new MenuItem(menu_1, SWT.NONE);
		menuItem_3.setText("Open");
		
		MenuItem menuItem_4 = new MenuItem(menu_1, SWT.NONE);
		menuItem_4.setText("Save");
		
		MenuItem menuItem_5 = new MenuItem(menu_1, SWT.NONE);
		menuItem_5.setText("Save as");
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		MenuItem menuItem_7 = new MenuItem(menu_1, SWT.NONE);
		menuItem_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		menuItem_7.setText("Exit");
		
		MenuItem menuItem_8 = new MenuItem(menu, SWT.CASCADE);
		menuItem_8.setText("Help");
		
		Menu menu_2 = new Menu(menuItem_8);
		menuItem_8.setMenu(menu_2);
		
		MenuItem menuItem_9 = new MenuItem(menu_2, SWT.NONE);
		menuItem_9.setText("About");
		
		/* END MENU */
		
//		SashComposite sashCompositeV = new SashComposite(shell, SWT.HORIZONTAL);
//		FormData fd_sashCompositeV = getMaximizedLayoutData();
//		sashCompositeV.setLayoutData(fd_sashCompositeV);
//		sashCompositeV.setSashPosition(70);
//		sashCompositeV.setLastMinimumPixels(100);
//		sashCompositeV.setLastMaximumPixels(170);
//		
//		SashComposite sashCompositeH = new SashComposite(sashCompositeV, SWT.NONE);
//		sashCompositeH.setLayoutData(new FormData());
//		sashCompositeV.setFirstControl(sashCompositeH);
//		sashCompositeH.setSashPosition(70);
//		sashCompositeH.setFirstMinimumPixels(350);
//		sashCompositeH.setFirstMaximumPixels(500);
		
		Composite panelLeft = new Composite(shell, SWT.NONE);
		Composite panelRight = new Composite(shell, SWT.NONE);
		Composite panelBottom = new Composite(shell, SWT.NONE);
		
		FormData fd_panelLeft = new FormData();
		fd_panelLeft.left = new FormAttachment(0);
		fd_panelLeft.right = new FormAttachment(0, 470);
		fd_panelLeft.top = new FormAttachment(0);
		fd_panelLeft.bottom = new FormAttachment(panelBottom, 0, SWT.TOP);
		panelLeft.setLayout(new FormLayout());
		panelLeft.setLayoutData(fd_panelLeft);
//		sashCompositeH.setFirstControl(panelLeft);
		
		GroupGeneral groupGeneral = new GroupGeneral(panelLeft, SWT.NONE, this);
		FormData fd_groupGeneral = new FormData();
		fd_groupGeneral.bottom = new FormAttachment(0, 130);
		fd_groupGeneral.top = new FormAttachment(0, 10);
		fd_groupGeneral.left = new FormAttachment(0, 10);
		fd_groupGeneral.right = new FormAttachment(100, -10);
		groupGeneral.setLayoutData(fd_groupGeneral);
		
		GroupUvAnimations groupUvAnimations = new GroupUvAnimations(panelLeft, SWT.NONE);
		FormData fd_groupUvAnimations = new FormData();
		fd_groupUvAnimations.top = new FormAttachment(groupGeneral, 6);
		fd_groupUvAnimations.left = new FormAttachment(groupGeneral, 0, SWT.LEFT);
		fd_groupUvAnimations.bottom = new FormAttachment(0, 240);
		fd_groupUvAnimations.right = new FormAttachment(groupGeneral, 0, SWT.RIGHT);
		groupUvAnimations.setLayoutData(fd_groupUvAnimations);
		
		groupTextureUnits = new Group(panelLeft, SWT.NONE);
		groupTextureUnits.setText("Texture units");
		groupTextureUnits.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_groupTextureUnits = new FormData();
		fd_groupTextureUnits.right = new FormAttachment(groupGeneral, 0, SWT.RIGHT);
		fd_groupTextureUnits.bottom = new FormAttachment(100);
		fd_groupTextureUnits.top = new FormAttachment(groupUvAnimations, 6);
		fd_groupTextureUnits.left = new FormAttachment(0, 10);
		groupTextureUnits.setLayoutData(fd_groupTextureUnits);
		
		FormData fd_panelRight = new FormData();
		fd_panelRight.left = new FormAttachment(panelLeft, 0, SWT.RIGHT);
		fd_panelRight.right = new FormAttachment(100);
		fd_panelRight.top = new FormAttachment(0);
		fd_panelRight.bottom = new FormAttachment(panelBottom, 0, SWT.TOP);
		panelRight.setLayout(new FormLayout());
		panelRight.setLayoutData(fd_panelRight);
		
		groupPreview = new GroupPreview(panelRight, SWT.NONE);
		FormData fd_groupPreview = new FormData();
		fd_groupPreview.left = new FormAttachment(0);
		fd_groupPreview.right = new FormAttachment(100, -10);
		fd_groupPreview.bottom = new FormAttachment(100);
		fd_groupPreview.top = new FormAttachment(0, 10);
		groupPreview.setLayoutData(fd_groupPreview);
//		sashCompositeH.setLastControl(panelRight);
		
		FormData fd_panelBottom = new FormData();
		fd_panelBottom.left = new FormAttachment(0);
		fd_panelBottom.right = new FormAttachment(100);
		fd_panelBottom.top = new FormAttachment(100, -120);
		fd_panelBottom.bottom = new FormAttachment(100);
		panelBottom.setLayout(new FormLayout());
		panelBottom.setLayoutData(fd_panelBottom);
		
		Group groupMSV = new Group(panelBottom, SWT.NONE);
		FormData fd_groupMSV = new FormData();
		fd_groupMSV.top = new FormAttachment(0, 6);
		fd_groupMSV.left = new FormAttachment(0, 10);
		fd_groupMSV.right = new FormAttachment(100, -10);
		fd_groupMSV.bottom = new FormAttachment(100, -10);
		groupMSV.setText("Material Specific Variables");
		groupMSV.setLayout(new FillLayout());
		groupMSV.setLayoutData(fd_groupMSV);
		
		compositeHidden = new Composite(shell, SWT.NONE);
		compositeHidden.setLayoutData(new FormData());
		compositeHidden.setVisible(false);
		
		compositeTextureUnits.add(new CompositeTextureUnits(groupTextureUnits, SWT.NONE, EMaterialType.DECAL));
		compositeTextureUnits.add(new CompositeTextureUnits(compositeHidden, SWT.NONE, EMaterialType.SOLIDDIFFUSE));
		compositeTextureUnits.add(new CompositeTextureUnits(compositeHidden, SWT.NONE, EMaterialType.TRANSLUCENT));
		compositeTextureUnits.add(new CompositeTextureUnits(compositeHidden, SWT.NONE, EMaterialType.WATER));
		
		compositeMsv = new CompositeMsv(groupMSV, SWT.NONE);
		compositeMsv.setActiveType(EMaterialType.DECAL);
	}

	public void updateMaterialType(EMaterialType type) {
		if(compositeMsv != null) compositeMsv.setActiveType(type);
		for(CompositeTextureUnits ctu : compositeTextureUnits)
		{
			if(type == ctu.getType()) ctu.setParent(groupTextureUnits);
			else ctu.setParent(compositeHidden);
			groupTextureUnits.layout();
		}
	}
	
	@Override
	public void init() {
		display = Display.getDefault();
		createContents();
		getGLComposite().init();
	}

	@Override
	public GLComposite getGLComposite() {
		return groupPreview.getGLComposite();
	}

	@Override
	public Shell getShell() {
		return shell;
	}

	@Override
	public void setPitch(float pitch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setYaw(float yaw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPos(float x, float y, float z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAdditionalTitle(String title, boolean async) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLoadingLabelText(String text, boolean async) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureProgressBar(int value, boolean async) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProgressAmount(int value, boolean async) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseProgressBarState(boolean async) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOpenOptionActive(boolean active, boolean async) {
		// TODO Auto-generated method stub
		
	}
}
