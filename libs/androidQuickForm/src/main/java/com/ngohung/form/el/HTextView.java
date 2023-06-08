package com.ngohung.form.el;

import android.view.View;
import android.widget.TextView;

import com.ngohung.form.el.validator.ValidationStatus;

/**
 * Modified by Sarath
 */
public class HTextView extends HElement {

    public HTextView(String label){
        this.label = label;
    }

    @Override
    public void loadValueForUI(View v) {
        if(v instanceof TextView) // assumption.. TextEntryElement will be represented by TextView and EditText in UI
        {
            TextView textView = (TextView) v;
            if(value != null)
                textView.setText(value);
                textView.setHint(hint);
        }
    }

    @Override
    public void saveValueFromUI(View v) {
        if( !(v instanceof TextView) ) // assumption.. TextEntryElement will be represented by TextView and EditText in UI
            return;

        TextView textView = (TextView) v;
        this.setValue(textView.getText().toString().trim());
    }

    @Override
    public ValidationStatus doValidationForUI(View v) {
        TextView textView = (TextView) v;

        // do validation here
        ValidationStatus vStatus = doValidation();

        // display error validation
        if(vStatus!=null && (!vStatus.isValid())  ){

            textView.setError( vStatus.getMsg() );
        }
        else{
            if (required || vStatus != null) // only do this for required field , if optional field, only when condition is satisfied
            {
                textView.setError(null);
            }
        }

        return vStatus;
    }

    public int getElType() {
        return HElementType.TEXT_EL_NEW;
    }
}
