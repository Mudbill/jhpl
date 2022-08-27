package engine.forms.editor.material;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import engine.materials.EMaterialType;
import engine.materials.ETextureUnitType;
import engine.util.Log;

public class CompositeTextureUnits extends Composite {

	private EMaterialType type;
	private Composite compositeListArea;
	private Composite compositeUnitArea;
	private Composite compositeHiddenUnitArea;
	private RowLayout rl_compositeUnitArea;
	private ScrolledComposite scrolledComposite;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeTextureUnits(Composite parent, int style, EMaterialType type) {
		super(parent, style);
		this.type = type;
		setLayout(new FormLayout());
		
		compositeListArea = new Composite(this, SWT.NONE);
		compositeListArea.setLayout(new GridLayout(1, false));
		FormData fd_compositeListArea = new FormData();
		fd_compositeListArea.right = new FormAttachment(0, 120);
		fd_compositeListArea.bottom = new FormAttachment(100, -10);
		fd_compositeListArea.top = new FormAttachment(0, 10);
		fd_compositeListArea.left = new FormAttachment(0, 10);
		compositeListArea.setLayoutData(fd_compositeListArea);
		
		Label separator = new Label(this, SWT.SEPARATOR | SWT.SHADOW_IN);
		FormData fd_separator = new FormData();
		fd_separator.left = new FormAttachment(compositeListArea, 2, SWT.RIGHT);
		fd_separator.top = new FormAttachment(compositeListArea, 0, SWT.TOP);
		fd_separator.bottom = new FormAttachment(compositeListArea, 0, SWT.BOTTOM);
		separator.setLayoutData(fd_separator);
		
		scrolledComposite = new ScrolledComposite(this, SWT.NONE | SWT.V_SCROLL);
		scrolledComposite.setAlwaysShowScrollBars(true);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.left = new FormAttachment(compositeListArea, 6);
		fd_scrolledComposite.bottom = new FormAttachment(compositeListArea, 0, SWT.BOTTOM);
		fd_scrolledComposite.top = new FormAttachment(0, 10);
		fd_scrolledComposite.right = new FormAttachment(100, -10);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		compositeUnitArea = new Composite(scrolledComposite, SWT.NONE);
		rl_compositeUnitArea = new RowLayout(SWT.VERTICAL);
		rl_compositeUnitArea.spacing = 20;
		rl_compositeUnitArea.wrap = false;
		compositeUnitArea.setLayout(rl_compositeUnitArea);
		scrolledComposite.setContent(compositeUnitArea);
		scrolledComposite.setMinSize(SWT.DEFAULT, SWT.DEFAULT);
		scrolledComposite.setMinHeight(computePreferredHeight());
		scrolledComposite.addListener(SWT.RESIZE, event -> {
			scrolledComposite.setMinHeight(computePreferredHeight());
		});
		
		compositeHiddenUnitArea = new Composite(this, SWT.NONE);
		compositeHiddenUnitArea.setLayoutData(new FormData());
		compositeHiddenUnitArea.setVisible(false);
		compositeHiddenUnitArea.moveBelow(parent);
		
		setupTypes();
	}
	
	public EMaterialType getType() {
		return type;
	}
	
	private void setupTypes() {
		ETextureUnitType[] types;
		
		switch(type)
		{
		case DECAL:
			types = new ETextureUnitType[] {
					ETextureUnitType.DIFFUSE
			};
			break;
		case SOLIDDIFFUSE:
			types = new ETextureUnitType[] {
					ETextureUnitType.DIFFUSE,
					ETextureUnitType.NORMAL,
					ETextureUnitType.ALPHA,
					ETextureUnitType.SPECULAR,
					ETextureUnitType.HEIGHT,
					ETextureUnitType.ILLUMINATION,
					ETextureUnitType.DISSOLVE_ALPHA,
					ETextureUnitType.CUBEMAP,
					ETextureUnitType.CUBEMAP_ALPHA
			};
			break;
		case TRANSLUCENT:
			types = new ETextureUnitType[] {
					ETextureUnitType.DIFFUSE,
					ETextureUnitType.NORMAL,
					ETextureUnitType.CUBEMAP,
					ETextureUnitType.CUBEMAP_ALPHA
			};
			break;
		case WATER:
			types = new ETextureUnitType[] {
					ETextureUnitType.DIFFUSE,
					ETextureUnitType.NORMAL,
					ETextureUnitType.CUBEMAP
			};
			break;
		default:
			Log.error("Incorrect material type.");
			return;
		}
		
		int i = 0;
		for(ETextureUnitType type : types)
		{
			CompositeUnit unit = createUnitPanel(type);
			unit.setIndex(i++);
		}
	}
	
	private CompositeUnit createUnitPanel(ETextureUnitType type)
	{
		CompositeUnit unit = new CompositeUnit(compositeHiddenUnitArea, SWT.NONE, type);
		Button check = new Button(compositeListArea, SWT.CHECK);
		check.setText(type.getString());
		check.addListener(SWT.Selection, event -> {
			if(check.getSelection())
			{
				unit.setParent(compositeUnitArea);
				int lowestControlIndex = compositeUnitArea.getChildren().length - 1;
				int index = unit.getIndex();
				
				for(int i = 0; i < lowestControlIndex; i++)
				{
					CompositeUnit unit2 = (CompositeUnit) compositeUnitArea.getChildren()[i];
					if(index < unit2.getIndex())
					{
						index = i;
						break;
					}
				}
				
				if(index < lowestControlIndex)
				{
					unit.moveAbove(compositeUnitArea.getChildren()[index]);
				}
			}
			else
			{
				unit.setParent(compositeHiddenUnitArea);
			}
			
			//TODO: statemachine
			compositeUnitArea.layout(true, true);
			scrolledComposite.setMinHeight(computePreferredHeight());
		});
		return unit;
	}
	
	private int computePreferredHeight()
	{
		int height = 0;
		for(Control child : compositeUnitArea.getChildren())
		{
			height += child.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			height += rl_compositeUnitArea.spacing;
		}
		return height;
	}
}
