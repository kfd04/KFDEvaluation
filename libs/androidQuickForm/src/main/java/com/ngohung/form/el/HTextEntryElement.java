package com.ngohung.form.el;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.ngohung.form.R;
import com.ngohung.form.constant.HConstants;
import com.ngohung.form.el.store.HDataStore;
import com.ngohung.form.el.validator.ValidationStatus;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class HTextEntryElement extends HElement implements OnFocusChangeListener{ // when focus change we will do validation

	protected int elType = HElementType.TEXT_ENTRY_EL;
	@Override
	public void setValue(String newValue) {
		super.setValue(newValue);
		if(getEditText()!=null){
			getEditText().setText(newValue);

			getEditText().addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {

					// you can call or do what you want with your EditText here

					// yourEditText...
					Log.e("hjkjhj",""+s.toString());
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				public void onTextChanged(CharSequence s, int start, int before, int count) {}
			});
		/*	if (elType != HElementType.NUMERIC_ENTRY_EL)

            else{
				NumberFormat myformatter = new DecimalFormat("########");

				String result = myformatter.format(newValue);
				getEditText().setText(result);
			}*/
		}
	}

	private boolean editable = true;

	public HTextEntryElement(String key, String label, String hint, boolean required)
	{
		this.key = key;
		this.label = label;
		this.value = HConstants.BLANK;
		this.hint = hint;
		this.required = required;
	}

	public HTextEntryElement(String key, String label, String initVal, String hint, boolean required)
	{
		this.key = key;
		this.label = label;
		this.value = initVal;
		this.hint = hint;
		this.required = required;
	}

	public HTextEntryElement(String key, String label, String hint, boolean required, HDataStore store)
	{
		this.key = key;
		this.label = label;
		this.hint = hint;
		this.required = required;

		if(store!=null)
		{
			store.loadValueFromStore(this); // load value from store
			this.setDataStore(store);

		}
		else
			this.value = HConstants.BLANK;
	}

	@Override
	public void clearValue() {
		super.clearValue();
		if(getEditText()!=null&&!disableClear) {
			getEditText().setText(HConstants.BLANK);
		}
	}

	@Override
	public void loadValueForUI(View v) {

		if(v instanceof EditText) // assumption.. TextEntryElement will be represented by TextView and EditText in UI
		{
			EditText editText = (EditText) v;
			if(value != null){
				editText.setText(value);
			//	 editText.setTextColor(Color.YELLOW);
			}

			editText.setHint(hint);
		//	editText.setTextColor(Color.YELLOW);

		}

	}

	@Override
	public void saveValueFromUI(View v) {
		if( !(v instanceof EditText) ) // assumption.. TextEntryElement will be represented by TextView and EditText in UI
			return;

		EditText editText = (EditText) v;
		this.setValue(editText.getText().toString().trim() );
	}

	@Override
	public ValidationStatus doValidationForUI(View v) {
		if( !(v instanceof EditText) )
			return null;

		EditText editText = (EditText) v;

		// do validation here
		ValidationStatus vStatus = doValidation();

		// display error validation
		if(vStatus!=null && (!vStatus.isValid())  ){

			editText.setError( vStatus.getMsg() );
		}
		else{
			if(required || (vStatus!=null && vStatus.isValid() ) ) // only do this for required field , if optional field, only when condition is satisfied
			{
				editText.setError(null);
			}
		}

		return vStatus;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// do validation when EditText lose focus
		if(v instanceof EditText){

			if(!hasFocus){

				// save data first
				saveValueFromUI(v);

				// validation if necessary
				doValidationForUI(v);

			}

		}
	}

	public EditText getEditText(){
		if(getView()!=null) {
			return (EditText) getView().findViewById(R.id.valueTextView);
		}
		return null;
	}

	public int getElType() {
		return HElementType.TEXT_ENTRY_EL;
	}

	public void setNotEditable(){
		editable=false;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isEditable(){
		return editable;
	}

}







