package engine.forms.editor.material;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class GroupUvAnimations extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GroupUvAnimations(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group group = new Group(this, SWT.NONE);
		group.setText("UV animations");
		group.setLayout(new FormLayout());
		
		Label label = new Label(group, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(0, 5);
		fd_label.left = new FormAttachment(0, 7);
		label.setLayoutData(fd_label);
		label.setText("UV Animation");
		
		Combo combo = new Combo(group, SWT.READ_ONLY);
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(0, 24);
		fd_combo.left = new FormAttachment(0, 7);
		combo.setLayoutData(fd_combo);
		combo.setItems(new String[] {});
		
		Button button = new Button(group, SWT.NONE);
		FormData fd_button = new FormData();
		fd_button.top = new FormAttachment(0, 51);
		fd_button.left = new FormAttachment(0, 7);
		button.setLayoutData(fd_button);
		button.setText("Create");
		
		Button button_1 = new Button(group, SWT.NONE);
		fd_combo.right = new FormAttachment(button_1, 0, SWT.RIGHT);
		FormData fd_button_1 = new FormData();
		fd_button_1.top = new FormAttachment(0, 51);
		fd_button_1.left = new FormAttachment(0, 58);
		button_1.setLayoutData(fd_button_1);
		button_1.setText("Delete");
		
		Label label_1 = new Label(group, SWT.NONE);
		FormData fd_label_1 = new FormData();
		fd_label_1.top = new FormAttachment(label, 0, SWT.TOP);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("Anim type");
		
		Combo combo_1 = new Combo(group, SWT.READ_ONLY);
		combo_1.setEnabled(false);
		fd_label_1.left = new FormAttachment(combo_1, 0, SWT.LEFT);
		FormData fd_combo_1 = new FormData();
		fd_combo_1.top = new FormAttachment(combo, 0, SWT.TOP);
		fd_combo_1.left = new FormAttachment(combo, 6);
		combo_1.setLayoutData(fd_combo_1);
		
		Combo combo_2 = new Combo(group, SWT.READ_ONLY);
		combo_2.setEnabled(false);
		FormData fd_combo_2 = new FormData();
		fd_combo_2.left = new FormAttachment(combo_1, 6);
		fd_combo_2.top = new FormAttachment(combo, 0, SWT.TOP);
		combo_2.setLayoutData(fd_combo_2);
		
		Spinner spinner = new Spinner(group, SWT.BORDER);
		spinner.setEnabled(false);
		fd_combo_2.right = new FormAttachment(combo_1, 40, SWT.RIGHT);
		FormData fd_spinner = new FormData();
		fd_spinner.top = new FormAttachment(combo, 0, SWT.TOP);
		fd_spinner.left = new FormAttachment(combo_2, 6);
		spinner.setLayoutData(fd_spinner);
		
		Spinner spinner_1 = new Spinner(group, SWT.BORDER);
		spinner_1.setEnabled(false);
		FormData fd_spinner_1 = new FormData();
		fd_spinner_1.top = new FormAttachment(combo, 0, SWT.TOP);
		fd_spinner_1.left = new FormAttachment(spinner, 6);
		spinner_1.setLayoutData(fd_spinner_1);
		
		Label label_2 = new Label(group, SWT.NONE);
		FormData fd_label_2 = new FormData();
		fd_label_2.top = new FormAttachment(label, 0, SWT.TOP);
		fd_label_2.left = new FormAttachment(combo_2, 0, SWT.LEFT);
		label_2.setLayoutData(fd_label_2);
		label_2.setText("Axis");
		
		Label label_3 = new Label(group, SWT.NONE);
		FormData fd_label_3 = new FormData();
		fd_label_3.top = new FormAttachment(label, 0, SWT.TOP);
		fd_label_3.left = new FormAttachment(spinner, 0, SWT.LEFT);
		label_3.setLayoutData(fd_label_3);
		label_3.setText("Speed");
		
		Label label_4 = new Label(group, SWT.NONE);
		FormData fd_label_4 = new FormData();
		fd_label_4.top = new FormAttachment(label, 0, SWT.TOP);
		fd_label_4.left = new FormAttachment(spinner_1, 0, SWT.LEFT);
		label_4.setLayoutData(fd_label_4);
		label_4.setText("Amplitude");

	}

}
