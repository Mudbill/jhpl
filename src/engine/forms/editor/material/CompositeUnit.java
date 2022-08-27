package engine.forms.editor.material;

import java.io.File;

import net.buttology.lwjgl.swt.GLComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import engine.EngineTextureUnit;
import engine.main.Application;
import engine.materials.ETextureUnitType;

public class CompositeUnit extends Composite {
	
	private static GLComposite mainComposite = null;
	
	//=========================================================================
	
	private EngineTextureUnit engine;
	private ETextureUnitType type;
	private GLComposite compositePreview;
	private int index = 0;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeUnit(Composite parent, int style, ETextureUnitType type) {
		super(parent, style);
		setLayout(new FormLayout());
		this.type = type;
		this.engine = Application.getTextureUnitEngine();//new EngineTextureUnit();
		
		Label labelTitle = new Label(this, SWT.NONE);
		FormData fd_labelTitle = new FormData();
		fd_labelTitle.top = new FormAttachment(0);
		fd_labelTitle.left = new FormAttachment(0);
		labelTitle.setLayoutData(fd_labelTitle);
		labelTitle.setText(type.getString());
		
		Label labelFile = new Label(this, SWT.NONE);
		FormData fd_labelFile = new FormData();
		fd_labelFile.top = new FormAttachment(labelTitle, 6);
		fd_labelFile.left = new FormAttachment(labelTitle, 10, SWT.LEFT);
		labelFile.setLayoutData(fd_labelFile);
		labelFile.setText("File");
		
		Text textFile = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		textFile.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION_TEXT));
		FormData fd_textFile = new FormData();
		fd_textFile.top = new FormAttachment(labelFile, 6);
		fd_textFile.left = new FormAttachment(0, 10);
		textFile.setLayoutData(fd_textFile);
		
		Button buttonOpenFile = new Button(this, SWT.NONE);
		fd_textFile.right = new FormAttachment(buttonOpenFile, -6);
		FormData fd_buttonOpenFile = new FormData();
		fd_buttonOpenFile.top = new FormAttachment(0, 42);
		buttonOpenFile.setLayoutData(fd_buttonOpenFile);
		buttonOpenFile.setText("...");
		buttonOpenFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.SHEET | SWT.OPEN);
				fd.setText("Load texture file");
				fd.setFilterExtensions(new String[] {"*.*", "*.dds", "*.png", "*.jpg", "*.tga"});
				
				String path = fd.open();
				if(path != null)
				{
					File file = new File(path);
					textFile.setText(file.getName());
					
//					compositePreview.setCurrent();
//					GL11.glClearColor(1, 0, 0, 1);
					engine.setTexture(compositePreview, file);

//					ShellMaterialEditor.getInstance().getGLComposite().setCurrent();
//					Application.getEngine().setTexture(type, file);
				}
			}
		});
		
		Label labelWrapMode = new Label(this, SWT.NONE);
		FormData fd_labelWrapMode = new FormData();
		fd_labelWrapMode.top = new FormAttachment(textFile, 6);
		fd_labelWrapMode.left = new FormAttachment(labelFile, 0, SWT.LEFT);
		labelWrapMode.setLayoutData(fd_labelWrapMode);
		labelWrapMode.setText("Wrap mode");
		
		Combo comboWrapMode = new Combo(this, SWT.READ_ONLY);
		fd_buttonOpenFile.bottom = new FormAttachment(comboWrapMode, -27);
		fd_buttonOpenFile.right = new FormAttachment(comboWrapMode, 0, SWT.RIGHT);
		comboWrapMode.setItems(new String[] {"Repeat", "Clamp", "ClampToEdge", "ClampToBorder"});
		FormData fd_comboWrapMode = new FormData();
		fd_comboWrapMode.top = new FormAttachment(labelWrapMode, 6);
		fd_comboWrapMode.left = new FormAttachment(labelFile, 0, SWT.LEFT);
		fd_comboWrapMode.right = new FormAttachment(labelFile, 130);
		comboWrapMode.setLayoutData(fd_comboWrapMode);
		comboWrapMode.select(0);
		comboWrapMode.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				int index = comboWrapMode.getSelectionIndex();
				EWrapMode mode = EWrapMode.values()[index];
				if(mode == EWrapMode.CLAMP_TO_BORDER)
				{
					comboWrapMode.select(0);
					return;
				}
				compositePreview.setCurrent();
				engine.setWrapMode(mode);
				
//				Application.getForm().getGLComposite().setCurrent();
//				Application.getEngine().setWrapMode(mode);
			}
		});
		
		Button checkMipMaps = new Button(this, SWT.CHECK);
		checkMipMaps.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				compositePreview.setCurrent();
				engine.setUseMipMaps(checkMipMaps.getSelection());
			}
		});
		checkMipMaps.setSelection(true);
		FormData fd_checkMipMaps = new FormData();
		fd_checkMipMaps.top = new FormAttachment(comboWrapMode, 6);
		fd_checkMipMaps.left = new FormAttachment(labelFile, 0, SWT.LEFT);
		checkMipMaps.setLayoutData(fd_checkMipMaps);
		checkMipMaps.setText("Use mip maps");
		
		Label labelAnimationMode = new Label(this, SWT.NONE);
		FormData fd_labelAnimationMode = new FormData();
		fd_labelAnimationMode.top = new FormAttachment(checkMipMaps, 6);
		fd_labelAnimationMode.left = new FormAttachment(labelFile, 0, SWT.LEFT);
		labelAnimationMode.setLayoutData(fd_labelAnimationMode);
		labelAnimationMode.setText("Animation mode");
		
		Combo comboAnimMode = new Combo(this, SWT.READ_ONLY);
		comboAnimMode.setItems(new String[] {"None", "Loop", "Oscillate"});
		FormData fd_comboAnimMode = new FormData();
		fd_comboAnimMode.right = new FormAttachment(comboWrapMode, 0, SWT.RIGHT);
		fd_comboAnimMode.top = new FormAttachment(labelAnimationMode, 6);
		fd_comboAnimMode.left = new FormAttachment(labelFile, 0, SWT.LEFT);
		comboAnimMode.setLayoutData(fd_comboAnimMode);
		comboAnimMode.select(0);
		
		Label labelFrameTime = new Label(this, SWT.NONE);
		FormData fd_labelFrameTime = new FormData();
		fd_labelFrameTime.top = new FormAttachment(comboAnimMode, 6);
		fd_labelFrameTime.left = new FormAttachment(labelFile, 0, SWT.LEFT);
		labelFrameTime.setLayoutData(fd_labelFrameTime);
		labelFrameTime.setText("Frame time");
		
		Spinner spinnerFrameTime = new Spinner(this, SWT.BORDER);
		FormData fd_spinnerFrameTime = new FormData();
		fd_spinnerFrameTime.top = new FormAttachment(labelFrameTime, 6);
		fd_spinnerFrameTime.left = new FormAttachment(labelFile, 0, SWT.LEFT);
		spinnerFrameTime.setLayoutData(fd_spinnerFrameTime);
		
		if(mainComposite == null)
		{
			compositePreview = new GLComposite(this, SWT.BORDER, engine);
			mainComposite = compositePreview;
		}
		else
		{
			compositePreview = new GLComposite(this, SWT.BORDER, engine, mainComposite);
		}
		compositePreview.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		FormData fd_compositePreview = new FormData();
		fd_compositePreview.left = new FormAttachment(buttonOpenFile, 10);
		fd_compositePreview.right = new FormAttachment(buttonOpenFile, 14 + 128, SWT.RIGHT);
		fd_compositePreview.top = new FormAttachment(labelFile, 0, SWT.TOP);
		fd_compositePreview.bottom = new FormAttachment(labelFile, 4 + 128, SWT.TOP);
		compositePreview.setLayoutData(fd_compositePreview);
		
		EngineTextureUnit.registerContext(compositePreview);
		compositePreview.init(true, false);
		engine.init(compositePreview);
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public ETextureUnitType getUnitType() {
		return type;
	}
	
	public GLComposite getContext() {
		return compositePreview;
	}
}
