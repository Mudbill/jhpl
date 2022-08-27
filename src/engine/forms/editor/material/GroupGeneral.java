package engine.forms.editor.material;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

import engine.materials.EMaterialType;

public class GroupGeneral extends Composite {
	
	private Text textValue;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GroupGeneral(Composite parent, int style, ShellMaterialEditor editor) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group group = new Group(this, SWT.NONE);
		group.setText("General");
		group.setLayout(new FormLayout());
		
		Label labelType = new Label(group, SWT.NONE);
		FormData fd_labelType = new FormData();
		fd_labelType.top = new FormAttachment(0, 5);
		fd_labelType.left = new FormAttachment(0, 7);
		labelType.setLayoutData(fd_labelType);
		labelType.setText("Type:");
		
		Combo comboType = new Combo(group, SWT.READ_ONLY);
		comboType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				EMaterialType type = EMaterialType.getTypeFromString(comboType.getText());
				editor.updateMaterialType(type);
			}
		});
		FormData fd_comboType = new FormData();
		fd_comboType.top = new FormAttachment(0, 24);
		fd_comboType.left = new FormAttachment(0, 7);
		comboType.setLayoutData(fd_comboType);
		comboType.setItems(new String[] {"decal", "soliddiffuse", "translucent", "water"});
		comboType.select(0);
		
		Button buttonDepthTest = new Button(group, SWT.CHECK);
		FormData fd_buttonDepthTest = new FormData();
		fd_buttonDepthTest.top = new FormAttachment(comboType, 2, SWT.TOP);
		buttonDepthTest.setLayoutData(fd_buttonDepthTest);
		buttonDepthTest.setText("Depth Test");
		buttonDepthTest.setSelection(true);
		
		Label labelBlendMode = new Label(group, SWT.NONE);
		FormData fd_labelBlendMode = new FormData();
		fd_labelBlendMode.top = new FormAttachment(labelType, 0, SWT.TOP);
		labelBlendMode.setLayoutData(fd_labelBlendMode);
		labelBlendMode.setText("Blend Mode");
		
		Combo comboBlendMode = new Combo(group, SWT.READ_ONLY);
		fd_labelBlendMode.left = new FormAttachment(comboBlendMode, 0, SWT.LEFT);
		FormData fd_comboBlendMode = new FormData();
		fd_comboBlendMode.top = new FormAttachment(comboType, 0, SWT.TOP);
		fd_comboBlendMode.left = new FormAttachment(buttonDepthTest, 6);
		comboBlendMode.setLayoutData(fd_comboBlendMode);
		comboBlendMode.setItems(new String[] {"Add", "Mul", "Mulx2", "Alpha", "PremulAlpha"});
		comboBlendMode.select(0);
		
		Label labelPhysicsMaterial = new Label(group, SWT.NONE);
		FormData fd_labelPhysicsMaterial = new FormData();
		fd_labelPhysicsMaterial.top = new FormAttachment(0, 51);
		fd_labelPhysicsMaterial.left = new FormAttachment(0, 7);
		labelPhysicsMaterial.setLayoutData(fd_labelPhysicsMaterial);
		labelPhysicsMaterial.setText("Physics Material");
		
		Combo comboPhysicsMaterial = new Combo(group, SWT.READ_ONLY);
		fd_comboType.right = new FormAttachment(comboPhysicsMaterial, 0, SWT.RIGHT);
		FormData fd_comboPhysicsMaterial = new FormData();
		fd_comboPhysicsMaterial.top = new FormAttachment(0, 70);
		fd_comboPhysicsMaterial.left = new FormAttachment(0, 7);
		comboPhysicsMaterial.setLayoutData(fd_comboPhysicsMaterial);
		comboPhysicsMaterial.setItems(new String[] {"Default", "Dirt", "Dust", "Dyn_Book", "Dyn_Paper", "Generic_Hard", "Generic_Soft", "Glass", "Metal", "Metal_Chain", "Metal_Roll", "Organic", "Rock", "Rock_Reverb", "Rock_Roll", "Rock_Water", "Silent", "Water", "Wood", "Wood_Heavy", "Wood_Roll", "Wood_Squeaky"});
		comboPhysicsMaterial.select(0);
		
		Label labelValue = new Label(group, SWT.NONE);
		fd_buttonDepthTest.left = new FormAttachment(labelValue, 0, SWT.LEFT);
		FormData fd_labelValue = new FormData();
		fd_labelValue.top = new FormAttachment(buttonDepthTest, 9);
		labelValue.setLayoutData(fd_labelValue);
		labelValue.setText("Value");
		
		textValue = new Text(group, SWT.BORDER);
		fd_labelValue.left = new FormAttachment(textValue, 0, SWT.LEFT);
		FormData fd_textValue = new FormData();
		fd_textValue.left = new FormAttachment(comboPhysicsMaterial, 11);
		fd_textValue.top = new FormAttachment(comboPhysicsMaterial, 0, SWT.TOP);
		textValue.setLayoutData(fd_textValue);

	}
}
