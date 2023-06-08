package com.ngohung.form.el;

import android.view.View;
import android.widget.TextView;

import com.ngohung.form.R;
import com.ngohung.form.el.validator.ValidationStatus;

public class HTextElement extends HElement {

	protected int elType = HElementType.TEXT_EL;

	public HTextElement(String label) {
		this.label = label;
	}



	@Override
	public void setLabel(String label) {
		super.setLabel(label);
		if(getView()!=null){
			TextView textView = getView().findViewById(R.id.labelTextView);
			textView.setText(label);
		}
	}

	@Override
	public void loadValueForUI(View v) {
		if(label == null) // only load value if there is something
			return;

		if(v instanceof TextView){
			TextView tv = (TextView) v;
			if(label!=null)
				tv.setText(label);
			
		}
		
	}

	@Override
	public void saveValueFromUI(View v) {
		// no need to do anything as this is a label
		
	}

	@Override
	public ValidationStatus doValidationForUI(View v) {
		// no need to do anything as this is just a label
		return null;
	}

	public int getElType() {
		return HElementType.TEXT_EL;
	}
	
}
