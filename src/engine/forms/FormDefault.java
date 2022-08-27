package engine.forms;

import net.buttology.lwjgl.swt.GLComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import engine.StateMachine;
import engine.main.Application;
import engine.materials.ETextureUnitType;
import engine.util.Utils;

public class FormDefault extends Form {

	protected Shell shell;
	private GLComposite glComposite;
	private Label labelPos, labelYaw, labelPitch, labelBottomDisplay;
	private MenuItem itemOpenDae;
	private ProgressBar progressBar;
	private Display display;
	
	private int barMax = 100;
	private int barState = 0;
	private String loadText = "";
	private String title = Application.APP_NAME + " - " + Application.APP_VERSION;
	private String shellHeading = "";
	
	private boolean initalized = false;

	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		init();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void init() {
		if(initalized) return;
		display = Display.getDefault();
		createContents();
		glComposite.init();
		initalized = true;
	}
	
	public Display getDisplay() {
		return display;
	}
	
	public boolean openDialog() {
		FileDialog fd = new FileDialog(shell, SWT.SHEET);
		fd.setFilterExtensions(new String[]{"*.dae"});
		String path = fd.open();
		
		if(path != null) {
			Application.getEngine().updateModel(path);
			return true;
		}
		return false;
	}
	
	public void setOpenOptionActive(boolean b, boolean async) {
		if(itemOpenDae != null) if(async) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					itemOpenDae.setEnabled(b);
					if(b) itemOpenDae.setText("Open model (*.dae)");
				}
			});
		} else {
			itemOpenDae.setEnabled(b);
			if(b) itemOpenDae.setText("Open model (*.dae)");
		}
	}

	public void setProgressAmount(int amount, boolean async) {
		if(progressBar != null) if(async) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					progressBar.setSelection(amount);
				}
			});
		} else progressBar.setSelection(amount);
		barState = amount;
	}
	
	public void increaseProgressBarState(boolean async) {
		if(progressBar != null) if(async) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					progressBar.setSelection(progressBar.getSelection()+1);
				}
			});
		} else progressBar.setSelection(progressBar.getSelection()+1);
		barState++;
	}
	
	public void configureProgressBar(int max, boolean async) {
		if(progressBar != null) if(async) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					progressBar.setMaximum(max);
				}
			});
		} else progressBar.setMaximum(max);
		barMax = max;
	}
	
	public void setLoadingLabelText(String text, boolean async) {
		if(labelBottomDisplay != null) if(async) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					labelBottomDisplay.setText(text);
				}
			});
		} else labelBottomDisplay.setText(text);
		loadText = text;
	}
	
	public void setAdditionalTitle(String text, boolean async) {
		if(shell != null) if(async) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					shell.setText(title + " - " + text);
				}
			});
		} else shell.setText(title + " - " + text);
		shellHeading = " - " + text;
	}
	
	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
//		shell = new Shell(Display.getDefault(), SWT.CLOSE | SWT.MIN);
		shell = new Shell();
		shell.setSize(800, 620);
		shell.setText(title + shellHeading);
		
		Utils.center(shell);
		shell.setLayout(new FormLayout());
		
		Composite panel = new Composite(shell, SWT.BORDER);
		panel.setVisible(false);
		FormData fd_panel = new FormData();
		fd_panel.bottom = new FormAttachment(0, 80);
		fd_panel.right = new FormAttachment(0, 220);
		fd_panel.top = new FormAttachment(0, 4);
		fd_panel.left = new FormAttachment(0, 4);
		panel.setLayoutData(fd_panel);
		
		Label lblPos = new Label(panel, SWT.NONE);
		lblPos.setBounds(10, 10, 33, 13);
		lblPos.setText("Pos:");
		
		Label lblYaw = new Label(panel, SWT.NONE);
		lblYaw.setBounds(10, 29, 33, 13);
		lblYaw.setText("Yaw:");
		
		Label lblPitch = new Label(panel, SWT.NONE);
		lblPitch.setBounds(10, 48, 33, 13);
		lblPitch.setText("Pitch:");
		
		labelPos = new Label(panel, SWT.NONE);
		labelPos.setBounds(49, 10, 253, 13);
		labelPos.setText("getpos");
		
		labelYaw = new Label(panel, SWT.NONE);
		labelYaw.setBounds(49, 29, 153, 13);
		labelYaw.setText("getyaw");
		
		labelPitch = new Label(panel, SWT.NONE);
		labelPitch.setBounds(49, 48, 153, 13);
		labelPitch.setText("getpitch");
		
		glComposite = new GLComposite(shell, SWT.NONE, Application.getEngine());
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		itemOpenDae = new MenuItem(menu_1, SWT.NONE);
		itemOpenDae.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				openDialog();
			}
		});
		itemOpenDae.setText("Open model (*.dae) - Loading");
		itemOpenDae.setEnabled(false);
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
			}
		});
		mntmExit.setText("Exit");
		
		MenuItem mntmView = new MenuItem(menu, SWT.CASCADE);
		mntmView.setText("View");
		
		Menu menu_2 = new Menu(mntmView);
		mntmView.setMenu(menu_2);
		
		MenuItem mntmCameraPosition = new MenuItem(menu_2, SWT.CHECK);
		mntmCameraPosition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				panel.setVisible(mntmCameraPosition.getSelection());
			}
		});
		mntmCameraPosition.setText("Camera position");
		
		MenuItem mntmSkybox = new MenuItem(menu_2, SWT.CHECK);
		mntmSkybox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				StateMachine.setBoolean("preview.skybox", mntmSkybox.getSelection());
//				StateMachine.renderSkybox = mntmSkybox.getSelection();
			}
		});
		mntmSkybox.setSelection(true);
		mntmSkybox.setText("Skybox");
		
		MenuItem mntmOpengl = new MenuItem(menu, SWT.CASCADE);
		mntmOpengl.setText("OpenGL");
		
		Menu menu_3 = new Menu(mntmOpengl);
		mntmOpengl.setMenu(menu_3);
		
		MenuItem menuItemDepthTest = new MenuItem(menu_3, SWT.CHECK);
		menuItemDepthTest.setSelection(true);
		menuItemDepthTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StateMachine.enableDepthTest = menuItemDepthTest.getSelection();
			}
		});
		menuItemDepthTest.setText("Depth test");
		
		MenuItem mntmRendermode = new MenuItem(menu_3, SWT.CASCADE);
		mntmRendermode.setText("Rendermode");
		
		Menu menu_7 = new Menu(mntmRendermode);
		mntmRendermode.setMenu(menu_7);
		
		MenuItem mntmdefault = new MenuItem(menu_7, SWT.RADIO);
		mntmdefault.setSelection(true);
		mntmdefault.setText("Default");
		mntmdefault.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StateMachine.renderMode = 0;
			}
		});
		
		MenuItem mntmtangentDebug = new MenuItem(menu_7, SWT.RADIO);
		mntmtangentDebug.setText("Tangent debug");
		mntmtangentDebug.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StateMachine.renderMode = 1;
			}
		});
		
		MenuItem mntmWireframe_1 = new MenuItem(menu_3, SWT.CASCADE);
		mntmWireframe_1.setText("Wireframe");
		
		Menu menu_5 = new Menu(mntmWireframe_1);
		mntmWireframe_1.setMenu(menu_5);
		
		MenuItem mntmOff = new MenuItem(menu_5, SWT.RADIO);
		mntmOff.setSelection(true);
		mntmOff.setText("Off");
		mntmOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				StateMachine.drawModelWireframe = false;
				StateMachine.drawModelFaces = true;
			}
		});
		
		MenuItem mntmOnly = new MenuItem(menu_5, SWT.RADIO);
		mntmOnly.setText("Only");
		mntmOnly.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				StateMachine.drawModelWireframe = true;
				StateMachine.drawModelFaces = false;
			}
		});
		
		MenuItem mntmBoth = new MenuItem(menu_5, SWT.RADIO);
		mntmBoth.setText("Both");
		mntmBoth.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				StateMachine.drawModelWireframe = true;
				StateMachine.drawModelFaces = true;
			}
		});
		
		MenuItem mntmCull = new MenuItem(menu_3, SWT.CHECK);
		mntmCull.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StateMachine.enableCulling = mntmCull.getSelection();
			}
		});
		mntmCull.setText("Cull");
		
		MenuItem mntmShowNormals = new MenuItem(menu_3, SWT.CHECK);
		mntmShowNormals.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				StateMachine.renderNormalsDebug = mntmShowNormals.getSelection();
			}
		});
		mntmShowNormals.setText("Show normals");
		
		MenuItem mntmTextureUnits = new MenuItem(menu, SWT.CASCADE);
		mntmTextureUnits.setText("Texture units");
		
		Menu menu_4 = new Menu(mntmTextureUnits);
		mntmTextureUnits.setMenu(menu_4);
		
		MenuItem mntmDiffuse = new MenuItem(menu_4, SWT.CHECK);
		mntmDiffuse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				StateMachine.setBoolean("Enable_Diffuse", mntmDiffuse.getSelection());
			}
		});
		mntmDiffuse.setSelection(true);
		mntmDiffuse.setText("Diffuse");
		
		MenuItem mntmSpecular = new MenuItem(menu_4, SWT.CHECK);
		mntmSpecular.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				StateMachine.setBoolean("Enable_Specular", mntmSpecular.getSelection());
			}
		});
		mntmSpecular.setSelection(true);
		mntmSpecular.setText("Specular");
		
		MenuItem mntmNmap = new MenuItem(menu_4, SWT.CHECK);
		mntmNmap.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				StateMachine.setBoolean("Enable_NMap", mntmNmap.getSelection());
			}
		});
		mntmNmap.setSelection(true);
		mntmNmap.setText("NMap");
		
		MenuItem mntmEditUnits = new MenuItem(menu, SWT.CASCADE);
		mntmEditUnits.setText("Edit units");
		
		Menu menu_6 = new Menu(mntmEditUnits);
		mntmEditUnits.setMenu(menu_6);
		
		MenuItem mntmDiffuse_1 = new MenuItem(menu_6, SWT.NONE);
		mntmDiffuse_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.SHEET);
				fd.setText("Load diffuse map");
				
				String filePath = fd.open();
				if(filePath != null)
					Application.setActiveModelTextureUnit(ETextureUnitType.DIFFUSE, filePath);
			}
		});
		mntmDiffuse_1.setText("Diffuse...");
		
		MenuItem mntmSpecular_1 = new MenuItem(menu_6, SWT.NONE);
		mntmSpecular_1.setText("Specular...");
		mntmSpecular_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.SHEET);
				fd.setText("Load specular map");
				
				String filePath = fd.open();
				if(filePath != null)
					Application.setActiveModelTextureUnit(ETextureUnitType.SPECULAR, filePath);
			}
		});
		
		MenuItem mntmNormal = new MenuItem(menu_6, SWT.NONE);
		mntmNormal.setText("Normal...");
		mntmNormal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.SHEET);
				fd.setText("Load normal map");
				
				String filePath = fd.open();
				if(filePath != null)
					Application.setActiveModelTextureUnit(ETextureUnitType.NORMAL, filePath);
			}
		});
		
		Composite panelBottomStatus = new Composite(shell, SWT.NONE);
		panelBottomStatus.setLayout(new FormLayout());
		FormData fd_composite_1 = new FormData();
		fd_composite_1.left = new FormAttachment(0);
		fd_composite_1.right = new FormAttachment(100);
		fd_composite_1.top = new FormAttachment(100, -20);
		fd_composite_1.bottom = new FormAttachment(100);
		panelBottomStatus.setLayoutData(fd_composite_1);
		
		progressBar = new ProgressBar(panelBottomStatus, SWT.BORDER);
		FormData fd_progressBar = new FormData();
		fd_progressBar.top = new FormAttachment(0);
		fd_progressBar.left = new FormAttachment(0);
		fd_progressBar.bottom = new FormAttachment(100);
		fd_progressBar.right = new FormAttachment(0, 200);
		progressBar.setLayoutData(fd_progressBar);
		progressBar.setMaximum(barMax);
		progressBar.setSelection(barState);
		
		labelBottomDisplay = new Label(panelBottomStatus, SWT.NONE);
		FormData fd_lblLoading = new FormData();
		fd_lblLoading.top = new FormAttachment(0, 2);
		fd_lblLoading.right = new FormAttachment(100, -10);
		fd_lblLoading.left = new FormAttachment(progressBar, 16);
		labelBottomDisplay.setLayoutData(fd_lblLoading);
		labelBottomDisplay.setText(loadText);
		
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(100, -20);
		fd_composite.right = new FormAttachment(100);
		fd_composite.top = new FormAttachment(0);
		fd_composite.left = new FormAttachment(0);
		glComposite.setLayoutData(fd_composite);
	}
	
	public void setYaw(float yaw) {
		labelYaw.setText(""+yaw);
	}

	public void setPitch(float pitch) {
		labelPitch.setText(""+pitch);
	}
	
	public void setPos(float x, float y, float z) {
		labelPos.setText(x+", "+y+", "+z);
	}
	
	public GLComposite getGLComposite() {
		return glComposite;
	}
	
	public Shell getShell() {
		return shell;
	}
}
