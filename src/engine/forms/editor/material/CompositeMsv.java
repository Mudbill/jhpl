package engine.forms.editor.material;

import org.eclipse.swt.widgets.Composite;

import engine.materials.EMaterialType;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;

public class CompositeMsv extends Composite {

	private EMaterialType type;
	private Composite compositeDecal, compositeSoliddiffuse, compositeTranslucent, compositeWater;
	private Composite compositeHidden;
	private Composite composite;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeMsv(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		compositeHidden = new Composite(this, SWT.NONE);
		compositeHidden.setVisible(false);
		
		composite = new Composite(this, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0);
		fd_composite.left = new FormAttachment(0);
		fd_composite.right = new FormAttachment(100);
		fd_composite.bottom = new FormAttachment(100);
		composite.setLayoutData(fd_composite);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		compositeDecal = new Composite(composite, SWT.NONE);

		compositeSoliddiffuse = new Composite(composite, SWT.NONE);
		compositeSoliddiffuse.setLayout(new FormLayout());
		
		Label labelHMS = new Label(compositeSoliddiffuse, SWT.NONE);
		FormData fd_labelHMS = new FormData();
		fd_labelHMS.top = new FormAttachment(0, 10);
		fd_labelHMS.left = new FormAttachment(0, 10);
		labelHMS.setLayoutData(fd_labelHMS);
		labelHMS.setText("HeightMapScale");
		
		Spinner spinnerHMS = new Spinner(compositeSoliddiffuse, SWT.BORDER);
		FormData fd_spinnerHMS = new FormData();
		fd_spinnerHMS.top = new FormAttachment(labelHMS, 6);
		fd_spinnerHMS.left = new FormAttachment(labelHMS, 0, SWT.LEFT);
		spinnerHMS.setLayoutData(fd_spinnerHMS);
		
		Label labelHMB = new Label(compositeSoliddiffuse, SWT.NONE);
		FormData fd_labelHMB = new FormData();
		fd_labelHMB.top = new FormAttachment(labelHMS, 0, SWT.TOP);
		fd_labelHMB.left = new FormAttachment(labelHMS, 6);
		labelHMB.setLayoutData(fd_labelHMB);
		labelHMB.setText("HeightMapBias");
		
		Spinner spinnerHMB = new Spinner(compositeSoliddiffuse, SWT.BORDER);
		FormData fd_spinnerHMB = new FormData();
		fd_spinnerHMB.top = new FormAttachment(spinnerHMS, 0, SWT.TOP);
		fd_spinnerHMB.left = new FormAttachment(labelHMB, 0, SWT.LEFT);
		spinnerHMB.setLayoutData(fd_spinnerHMB);
		
		Label labelFB = new Label(compositeSoliddiffuse, SWT.NONE);
		FormData fd_labelFB = new FormData();
		fd_labelFB.top = new FormAttachment(labelHMS, 0, SWT.TOP);
		fd_labelFB.left = new FormAttachment(labelHMB, 6);
		labelFB.setLayoutData(fd_labelFB);
		labelFB.setText("FrenselBias");
		
		Spinner spinnerFB = new Spinner(compositeSoliddiffuse, SWT.BORDER);
		FormData fd_spinnerFB = new FormData();
		fd_spinnerFB.top = new FormAttachment(spinnerHMS, 0, SWT.TOP);
		fd_spinnerFB.left = new FormAttachment(labelFB, 0, SWT.LEFT);
		spinnerFB.setLayoutData(fd_spinnerFB);
		
		Label labelFP = new Label(compositeSoliddiffuse, SWT.NONE);
		FormData fd_labelFP = new FormData();
		fd_labelFP.top = new FormAttachment(labelHMS, 0, SWT.TOP);
		fd_labelFP.left = new FormAttachment(labelFB, 6);
		labelFP.setLayoutData(fd_labelFP);
		labelFP.setText("FrenselPow");
		
		Spinner spinnerFP = new Spinner(compositeSoliddiffuse, SWT.BORDER);
		FormData fd_spinnerFP = new FormData();
		fd_spinnerFP.top = new FormAttachment(spinnerHMS, 0, SWT.TOP);
		fd_spinnerFP.left = new FormAttachment(labelFP, 0, SWT.LEFT);
		spinnerFP.setLayoutData(fd_spinnerFP);
		
		Button checkADF = new Button(compositeSoliddiffuse, SWT.CHECK);
		FormData fd_checkADF = new FormData();
		fd_checkADF.top = new FormAttachment(labelHMS, -1, SWT.TOP);
		fd_checkADF.left = new FormAttachment(labelFP, 6);
		checkADF.setLayoutData(fd_checkADF);
		checkADF.setText("AlphaDissolveFilter");
		
		compositeTranslucent = new Composite(composite, SWT.NONE);
		compositeTranslucent.setLayout(new FormLayout());
		
		Button checkR = new Button(compositeTranslucent, SWT.CHECK);
		FormData fd_checkR = new FormData();
		fd_checkR.top = new FormAttachment(0, 10);
		fd_checkR.left = new FormAttachment(0, 10);
		checkR.setLayoutData(fd_checkR);
		checkR.setText("Refraction");
		
		Button checkREC = new Button(compositeTranslucent, SWT.CHECK);
		FormData fd_checkREC = new FormData();
		fd_checkREC.top = new FormAttachment(checkR, 0, SWT.TOP);
		fd_checkREC.left = new FormAttachment(checkR, 6);
		checkREC.setLayoutData(fd_checkREC);
		checkREC.setText("RefractionEdgeCheck");
		
		Button checkRN = new Button(compositeTranslucent, SWT.CHECK);
		FormData fd_checkRN = new FormData();
		fd_checkRN.top = new FormAttachment(checkR, 0, SWT.TOP);
		fd_checkRN.left = new FormAttachment(checkREC, 6);
		checkRN.setLayoutData(fd_checkRN);
		checkRN.setText("RefractionNormal");
		
		Button checkABLL = new Button(compositeTranslucent, SWT.CHECK);
		FormData fd_checkABLL = new FormData();
		fd_checkABLL.top = new FormAttachment(checkR, 6);
		fd_checkABLL.left = new FormAttachment(checkR, 0, SWT.LEFT);
		checkABLL.setLayoutData(fd_checkABLL);
		checkABLL.setText("AffectedByLightLevel");
		
		Label labelRS = new Label(compositeTranslucent, SWT.NONE);
		FormData fd_labelRS = new FormData();
		fd_labelRS.top = new FormAttachment(checkR, 0, SWT.TOP);
		fd_labelRS.left = new FormAttachment(checkRN, 6);
		labelRS.setLayoutData(fd_labelRS);
		labelRS.setText("RefractionScale");
		
		Spinner spinnerRS = new Spinner(compositeTranslucent, SWT.BORDER);
		FormData fd_spinnerRS = new FormData();
		fd_spinnerRS.top = new FormAttachment(checkABLL, 0, SWT.TOP);
		fd_spinnerRS.left = new FormAttachment(labelRS, 0, SWT.LEFT);
		spinnerRS.setLayoutData(fd_spinnerRS);
		
		Label labelFB2 = new Label(compositeTranslucent, SWT.NONE);
		FormData fd_labelFB2 = new FormData();
		fd_labelFB2.top = new FormAttachment(checkR, 0, SWT.TOP);
		fd_labelFB2.left = new FormAttachment(labelRS, 6);
		labelFB2.setLayoutData(fd_labelFB2);
		labelFB2.setText("FrenselBias");
		
		Label labelFP2 = new Label(compositeTranslucent, SWT.NONE);
		FormData fd_labelFP2 = new FormData();
		fd_labelFP2.top = new FormAttachment(checkR, 0, SWT.TOP);
		fd_labelFP2.left = new FormAttachment(labelFB2, 6);
		labelFP2.setLayoutData(fd_labelFP2);
		labelFP2.setText("FrenselPow");
		
		Label labelRLM = new Label(compositeTranslucent, SWT.NONE);
		FormData fd_labelRLM = new FormData();
		fd_labelRLM.top = new FormAttachment(checkR, 0, SWT.TOP);
		fd_labelRLM.left = new FormAttachment(labelFP2, 6);
		labelRLM.setLayoutData(fd_labelRLM);
		labelRLM.setText("RimLightMul");
		
		Label labelRLP = new Label(compositeTranslucent, SWT.NONE);
		FormData fd_labelRLP = new FormData();
		fd_labelRLP.top = new FormAttachment(checkR, 0, SWT.TOP);
		fd_labelRLP.left = new FormAttachment(labelRLM, 6);
		labelRLP.setLayoutData(fd_labelRLP);
		labelRLP.setText("RimLightPow");
		
		Spinner spinnerFB2 = new Spinner(compositeTranslucent, SWT.BORDER);
		FormData fd_spinnerFB2 = new FormData();
		fd_spinnerFB2.top = new FormAttachment(checkABLL, 0, SWT.TOP);
		fd_spinnerFB2.left = new FormAttachment(labelFB2, 0, SWT.LEFT);
		spinnerFB2.setLayoutData(fd_spinnerFB2);
		
		Spinner spinnerFP2 = new Spinner(compositeTranslucent, SWT.BORDER);
		FormData fd_spinnerFP2 = new FormData();
		fd_spinnerFP2.top = new FormAttachment(checkABLL, 0, SWT.TOP);
		fd_spinnerFP2.left = new FormAttachment(labelFP2, 0, SWT.LEFT);
		spinnerFP2.setLayoutData(fd_spinnerFP2);
		
		Spinner spinnerRLM = new Spinner(compositeTranslucent, SWT.BORDER);
		FormData fd_spinnerRLM = new FormData();
		fd_spinnerRLM.top = new FormAttachment(checkABLL, 0, SWT.TOP);
		fd_spinnerRLM.left = new FormAttachment(labelRLM, 0, SWT.LEFT);
		spinnerRLM.setLayoutData(fd_spinnerRLM);
		
		Spinner spinnerRLP = new Spinner(compositeTranslucent, SWT.BORDER);
		FormData fd_spinnerRLP = new FormData();
		fd_spinnerRLP.top = new FormAttachment(checkABLL, 0, SWT.TOP);
		fd_spinnerRLP.left = new FormAttachment(labelRLP, 0, SWT.LEFT);
		spinnerRLP.setLayoutData(fd_spinnerRLP);
		
		compositeWater = new Composite(composite, SWT.NONE);
		compositeWater.setLayout(new FormLayout());
		
		Label labelRS3 = new Label(compositeWater, SWT.NONE);
		FormData fd_labelRS3 = new FormData();
		fd_labelRS3.top = new FormAttachment(0, 10);
		fd_labelRS3.left = new FormAttachment(0, 10);
		labelRS3.setLayoutData(fd_labelRS3);
		labelRS3.setText("RefractionScale");
		
		Label labelFB3 = new Label(compositeWater, SWT.NONE);
		FormData fd_labelFB3 = new FormData();
		fd_labelFB3.top = new FormAttachment(labelRS3, 0, SWT.TOP);
		fd_labelFB3.left = new FormAttachment(labelRS3, 6);
		labelFB3.setLayoutData(fd_labelFB3);
		labelFB3.setText("FrenselBias");
		
		Label labelFP3 = new Label(compositeWater, SWT.NONE);
		FormData fd_labelFP3 = new FormData();
		fd_labelFP3.top = new FormAttachment(labelRS3, 0, SWT.TOP);
		fd_labelFP3.left = new FormAttachment(labelFB3, 6);
		labelFP3.setLayoutData(fd_labelFP3);
		labelFP3.setText("FrenselPow");
		
		Label labelWS = new Label(compositeWater, SWT.NONE);
		FormData fd_labelWS = new FormData();
		fd_labelWS.top = new FormAttachment(labelRS3, 0, SWT.TOP);
		fd_labelWS.left = new FormAttachment(labelFP3, 6);
		labelWS.setLayoutData(fd_labelWS);
		labelWS.setText("WaveSpeed");
		
		Label labelWA = new Label(compositeWater, SWT.NONE);
		FormData fd_labelWA = new FormData();
		fd_labelWA.top = new FormAttachment(labelRS3, 0, SWT.TOP);
		fd_labelWA.left = new FormAttachment(labelWS, 6);
		labelWA.setLayoutData(fd_labelWA);
		labelWA.setText("WaveAmplitude");
		
		Label labelWF = new Label(compositeWater, SWT.NONE);
		FormData fd_labelWF = new FormData();
		fd_labelWF.top = new FormAttachment(labelRS3, 0, SWT.TOP);
		fd_labelWF.left = new FormAttachment(labelWA, 6);
		labelWF.setLayoutData(fd_labelWF);
		labelWF.setText("WaveFreq");
		
		Label labelRFS = new Label(compositeWater, SWT.NONE);
		FormData fd_labelRFS = new FormData();
		fd_labelRFS.top = new FormAttachment(labelRS3, 0, SWT.TOP);
		fd_labelRFS.left = new FormAttachment(labelWF, 6);
		labelRFS.setLayoutData(fd_labelRFS);
		labelRFS.setText("ReflectionFadeStart");
		
		Label labelRFE = new Label(compositeWater, SWT.NONE);
		FormData fd_labelRFE = new FormData();
		fd_labelRFE.top = new FormAttachment(labelRS3, 0, SWT.TOP);
		fd_labelRFE.left = new FormAttachment(labelRFS, 6);
		labelRFE.setLayoutData(fd_labelRFE);
		labelRFE.setText("ReflectionFadeEnd");
		
		Button checkHR = new Button(compositeWater, SWT.CHECK);
		FormData fd_checkHR = new FormData();
		fd_checkHR.bottom = new FormAttachment(labelRS3, 0, SWT.BOTTOM);
		fd_checkHR.left = new FormAttachment(labelRFE, 6);
		checkHR.setLayoutData(fd_checkHR);
		checkHR.setText("HasReflection");
		
		Spinner spinnerRS3 = new Spinner(compositeWater, SWT.BORDER);
		FormData fd_spinnerRS3 = new FormData();
		fd_spinnerRS3.top = new FormAttachment(labelRS3, 6);
		fd_spinnerRS3.left = new FormAttachment(labelRS3, 0, SWT.LEFT);
		spinnerRS3.setLayoutData(fd_spinnerRS3);
		
		Spinner spinnerFB3 = new Spinner(compositeWater, SWT.BORDER);
		FormData fd_spinnerFB3 = new FormData();
		fd_spinnerFB3.top = new FormAttachment(labelFB3, 6);
		fd_spinnerFB3.left = new FormAttachment(labelFB3, 0, SWT.LEFT);
		spinnerFB3.setLayoutData(fd_spinnerFB3);
		
		Spinner spinnerFP3 = new Spinner(compositeWater, SWT.BORDER);
		FormData fd_spinnerFP3 = new FormData();
		fd_spinnerFP3.top = new FormAttachment(labelFP3, 6);
		fd_spinnerFP3.left = new FormAttachment(labelFP3, 0, SWT.LEFT);
		spinnerFP3.setLayoutData(fd_spinnerFP3);
		
		Spinner spinnerWS = new Spinner(compositeWater, SWT.BORDER);
		FormData fd_spinnerWS = new FormData();
		fd_spinnerWS.top = new FormAttachment(labelWS, 6);
		fd_spinnerWS.left = new FormAttachment(labelWS, 0, SWT.LEFT);
		spinnerWS.setLayoutData(fd_spinnerWS);
		
		Spinner spinnerWA = new Spinner(compositeWater, SWT.BORDER);
		FormData fd_spinnerWA = new FormData();
		fd_spinnerWA.top = new FormAttachment(labelWS, 6);
		fd_spinnerWA.left = new FormAttachment(labelWA, 0, SWT.LEFT);
		spinnerWA.setLayoutData(fd_spinnerWA);
		
		Spinner spinnerWF = new Spinner(compositeWater, SWT.BORDER);
		FormData fd_spinnerWF = new FormData();
		fd_spinnerWF.top = new FormAttachment(labelWF, 6);
		fd_spinnerWF.left = new FormAttachment(labelWF, 0, SWT.LEFT);
		spinnerWF.setLayoutData(fd_spinnerWF);
		
		Spinner spinnerRFS = new Spinner(compositeWater, SWT.BORDER);
		FormData fd_spinnerRFS = new FormData();
		fd_spinnerRFS.top = new FormAttachment(labelRFS, 6);
		fd_spinnerRFS.left = new FormAttachment(labelRFS, 0, SWT.LEFT);
		spinnerRFS.setLayoutData(fd_spinnerRFS);
		
		Spinner spinnerRFE = new Spinner(compositeWater, SWT.BORDER);
		FormData fd_spinnerRFE = new FormData();
		fd_spinnerRFE.top = new FormAttachment(labelRFE, 6);
		fd_spinnerRFE.left = new FormAttachment(labelRFE, 0, SWT.LEFT);
		spinnerRFE.setLayoutData(fd_spinnerRFE);
		
		Button checkOCWR = new Button(compositeWater, SWT.CHECK);
		FormData fd_checkOCWR = new FormData();
		fd_checkOCWR.top = new FormAttachment(spinnerRS3, 6);
		fd_checkOCWR.left = new FormAttachment(labelRS3, 0, SWT.LEFT);
		checkOCWR.setLayoutData(fd_checkOCWR);
		checkOCWR.setText("OcclusionCullWorldReflection");
		
		Button checkLS = new Button(compositeWater, SWT.CHECK);
		FormData fd_checkLS = new FormData();
		fd_checkLS.top = new FormAttachment(spinnerFP3, 6);
		fd_checkLS.left = new FormAttachment(checkOCWR, 6);
		checkLS.setLayoutData(fd_checkLS);
		checkLS.setText("LargeSurface");
	}
	
	private void switchContent()
	{
		compositeDecal.setParent(type == EMaterialType.DECAL ? composite : compositeHidden);
		compositeSoliddiffuse.setParent(type == EMaterialType.SOLIDDIFFUSE ? composite : compositeHidden);
		compositeTranslucent.setParent(type == EMaterialType.TRANSLUCENT ? composite : compositeHidden);
		compositeWater.setParent(type == EMaterialType.WATER ? composite : compositeHidden);
		composite.layout();
	}
	
	public void setActiveType(EMaterialType type) {
		this.type = type;
		switchContent();
	}

	public EMaterialType getType() {
		return type;
	}
}
