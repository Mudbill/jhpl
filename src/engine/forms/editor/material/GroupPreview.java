package engine.forms.editor.material;

import java.io.File;

import net.buttology.lwjgl.swt.GLComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import engine.StateMachine;
import engine.core.EPrimitiveType;
import engine.core.LowLevelTexture;
import engine.main.Application;
import engine.objects.Skybox;
import engine.util.Log;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.lwjgl.opengl.GL11;

public class GroupPreview extends Composite {
	
	private Text textCubeMap;
	private GLComposite glComposite;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GroupPreview(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group group = new Group(this, SWT.NONE);
		group.setText("Preview");
		group.setLayout(new FormLayout());
		
		glComposite = new GLComposite(group, SWT.NONE, Application.getEngine());

		textCubeMap = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		textCubeMap.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION_TEXT));
		
		glComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		FormData fd_preview = new FormData();
		fd_preview.right = new FormAttachment(100, -10);
		fd_preview.top = new FormAttachment(0, 10);
		fd_preview.left = new FormAttachment(0, 10);
		fd_preview.bottom = new FormAttachment(100, -140);
		glComposite.setLayoutData(fd_preview);
		
		Label labelModel = new Label(group, SWT.NONE);
		FormData fd_labelModel = new FormData();
		fd_labelModel.left = new FormAttachment(0, 10);
		fd_labelModel.top = new FormAttachment(glComposite, 6, SWT.BOTTOM);
		labelModel.setLayoutData(fd_labelModel);
		labelModel.setText("Model");
		
		Combo comboModel = new Combo(group, SWT.READ_ONLY);
		comboModel.setItems(new String[] {"Box", "Cylinder", "Sphere", "Plane", "Custom"});
		FormData fd_comboModel = new FormData();
		fd_comboModel.top = new FormAttachment(labelModel, 6);
		fd_comboModel.left = new FormAttachment(0, 10);
		comboModel.setLayoutData(fd_comboModel);
		comboModel.select(0);
		comboModel.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String primitive = comboModel.getText();
				EPrimitiveType type = null;
				if("Box".equals(primitive)) type = EPrimitiveType.CUBE;
				if("Cylinder".equals(primitive)) type = EPrimitiveType.CYLINDER;
				if("Sphere".equals(primitive)) type = EPrimitiveType.SPHERE;
				if("Plane".equals(primitive)) type = EPrimitiveType.PLANE;
				
				if(type != null)
				{
					Application.getForm().getGLComposite().setCurrent();
					Application.getEngine().setPrimitiveModel(type);
				}
				
				if("Custom".equals(primitive))
				{
					FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
					fd.setText("Open model");
					fd.setFilterExtensions(new String[] {"*.dae"});
					String path = fd.open();
					if(path != null)
					{
						Application.getForm().getGLComposite().setCurrent();
						Application.getEngine().updateModel(path);
					}
				}
			}
		});
		
		Label labelFixedColor = new Label(group, SWT.NONE);
		FormData fd_labelFixedColor = new FormData();
		fd_labelFixedColor.left = new FormAttachment(0, 10);
		labelFixedColor.setLayoutData(fd_labelFixedColor);
		labelFixedColor.setText("Fixed light color");
		
		Canvas canvasFixedColor = new Canvas(group, SWT.BORDER);
		canvasFixedColor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_colorFixed = new FormData();
		fd_colorFixed.top = new FormAttachment(labelFixedColor, -3, SWT.TOP);
		fd_colorFixed.bottom = new FormAttachment(labelFixedColor, 3, SWT.BOTTOM);
		fd_colorFixed.right = new FormAttachment(labelFixedColor, 6 + 24, SWT.RIGHT);
		fd_colorFixed.left = new FormAttachment(labelFixedColor, 6);
		canvasFixedColor.setLayoutData(fd_colorFixed);
		canvasFixedColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				ColorDialog cd = new ColorDialog(getShell(), SWT.DIALOG_TRIM);
				cd.setText("Pick a color");
				RGB color = cd.open();
				if(color != null)
				{
					canvasFixedColor.getBackground().dispose();
					canvasFixedColor.setBackground(new Color(Display.getCurrent(), color));
				}
			}
		});
		
		Label labelMovableColor = new Label(group, SWT.NONE);
		FormData fd_labelMovableColor = new FormData();
		fd_labelMovableColor.top = new FormAttachment(labelFixedColor, 0, SWT.TOP);
		fd_labelMovableColor.left = new FormAttachment(canvasFixedColor, 6);
		labelMovableColor.setLayoutData(fd_labelMovableColor);
		labelMovableColor.setText("Movable light color");
		
		Canvas canvasMovableColor = new Canvas(group, SWT.BORDER);
		canvasMovableColor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_colorMovable = new FormData();
		fd_colorMovable.top = new FormAttachment(labelFixedColor, -3, SWT.TOP);
		fd_colorMovable.bottom = new FormAttachment(labelFixedColor, 3, SWT.BOTTOM);
		fd_colorMovable.right = new FormAttachment(labelMovableColor, 6 + 24, SWT.RIGHT);
		fd_colorMovable.left = new FormAttachment(labelMovableColor, 6);
		canvasMovableColor.setLayoutData(fd_colorMovable);
		canvasMovableColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				ColorDialog cd = new ColorDialog(getShell(), SWT.DIALOG_TRIM);
				cd.setText("Pick a color");
				RGB color = cd.open();
				if(color != null)
				{
					canvasMovableColor.getBackground().dispose();
					canvasMovableColor.setBackground(new Color(Display.getCurrent(), color));
				}
			}
		});
		
		Combo comboBGtype = new Combo(group, SWT.READ_ONLY);
		comboBGtype.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				StateMachine.setBoolean("preview.skybox", "CubeMap".equals(comboBGtype.getText()));
			}
		});
		comboBGtype.setItems(new String[] {"CubeMap", "Flat"});
		FormData fd_comboBGtype = new FormData();
		comboBGtype.setLayoutData(fd_comboBGtype);
		comboBGtype.select(0);
		fd_labelFixedColor.top = new FormAttachment(comboBGtype, 10);
		
		fd_comboBGtype.right = new FormAttachment(textCubeMap, -6);
		textCubeMap.setText("editor_cubemap_default.dds");
		
		Label labelCubeMap = new Label(group, SWT.NONE);
		FormData fd_textCubeMap = new FormData();
		fd_textCubeMap.top = new FormAttachment(labelCubeMap, 7);
		fd_textCubeMap.left = new FormAttachment(labelMovableColor, 0, SWT.LEFT);
		fd_textCubeMap.right = new FormAttachment(labelMovableColor, 232);
		textCubeMap.setLayoutData(fd_textCubeMap);
		
		Button buttonDot = new Button(group, SWT.NONE);
		FormData fd_buttonDot = new FormData();
		fd_buttonDot.top = new FormAttachment(labelCubeMap, 6);
		fd_buttonDot.left = new FormAttachment(textCubeMap, 6, SWT.RIGHT);
		fd_buttonDot.right = new FormAttachment(textCubeMap, 32, SWT.RIGHT);
		buttonDot.setLayoutData(fd_buttonDot);
		buttonDot.setText("...");
		buttonDot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.SHEET | SWT.OPEN);
				fd.setText("Open Cubemap");
				fd.setFilterExtensions(new String[]{"*.dds"});
				fd.setFilterNames(new String[]{"DDS files"});
				String path = fd.open();
				if(path != null) {
					LowLevelTexture llt = Application.getEngine().getTextureLoader().loadCubemapTexture(path);
					if(llt == null) {
						MessageBox mb = new MessageBox(getShell(), SWT.SHEET | SWT.ICON_WARNING | SWT.OK);
						mb.setText("Texture error");
						mb.setMessage("This DDS file is not a cubemap, and cannot be set as a skybox.");
						mb.open();
					} else {
						Application.getForm().getGLComposite().setCurrent();
						Application.getEngine().getRenderer().setSkybox(new Skybox(path));
						textCubeMap.setText(new File(path).getName());
					}
				}
			}
		});
		
		Label labelBGcolor = new Label(group, SWT.NONE);
		FormData fd_labelBGcolor = new FormData();
		fd_labelBGcolor.top = new FormAttachment(labelFixedColor, 0, SWT.TOP);
		fd_labelBGcolor.left = new FormAttachment(canvasMovableColor, 6);
		labelBGcolor.setLayoutData(fd_labelBGcolor);
		labelBGcolor.setText("Background color");
		
		Label labelBG = new Label(group, SWT.NONE);
		FormData fd_labelBG = new FormData();
		fd_labelBG.left = new FormAttachment(0, 10);
		fd_labelBG.top = new FormAttachment(comboModel, 6);
		labelBG.setLayoutData(fd_labelBG);
		labelBG.setText("Background type");
		fd_comboBGtype.left = new FormAttachment(labelBG, 0, SWT.LEFT);
		fd_comboBGtype.top = new FormAttachment(labelBG, 6);
		
		FormData fd_labelCubeMap = new FormData();
		fd_labelCubeMap.top = new FormAttachment(comboModel, 6);
		fd_labelCubeMap.left = new FormAttachment(labelMovableColor, 0, SWT.LEFT);
		labelCubeMap.setLayoutData(fd_labelCubeMap);
		labelCubeMap.setText("CubeMap file");
		
		Button checkRotate = new Button(group, SWT.CHECK);
		checkRotate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StateMachine.setBoolean("preview.rotate", checkRotate.getSelection());
			}
		});
		FormData fd_checkRotate = new FormData();
		fd_checkRotate.top = new FormAttachment(comboModel, 4, SWT.TOP);
		fd_checkRotate.left = new FormAttachment(labelMovableColor, 0, SWT.LEFT);
		checkRotate.setLayoutData(fd_checkRotate);
		checkRotate.setText("Rotate model");
		fd_comboModel.right = new FormAttachment(checkRotate, -6, SWT.LEFT);
		
		Canvas canvasBgColor = new Canvas(group, SWT.BORDER);
		canvasBgColor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_canvas = new FormData();
		fd_canvas.top = new FormAttachment(labelBGcolor, -3, SWT.TOP);
		fd_canvas.bottom = new FormAttachment(labelBGcolor, 3, SWT.BOTTOM);
		fd_canvas.left = new FormAttachment(labelBGcolor, 6);
		fd_canvas.right = new FormAttachment(labelBGcolor, 6 + 24, SWT.RIGHT);
		canvasBgColor.setLayoutData(fd_canvas);
		canvasBgColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				ColorDialog cd = new ColorDialog(getShell(), SWT.DIALOG_TRIM);
				cd.setText("Pick a color");
				RGB color = cd.open();
				if(color != null)
				{
					canvasBgColor.getBackground().dispose();
					canvasBgColor.setBackground(new Color(Display.getCurrent(), color));
					Log.info("Setting color to: " + color);
					glComposite.setCurrent();
					GL11.glClearColor(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, 1.0f);
				}
			}
		});

//		preview.init();
	}

	public GLComposite getGLComposite() {
		return glComposite;
	}
}
