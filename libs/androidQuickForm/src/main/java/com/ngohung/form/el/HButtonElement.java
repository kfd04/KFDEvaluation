package com.ngohung.form.el;

import android.view.View;
import android.widget.Button;

import com.ngohung.form.R;
import com.ngohung.form.constant.HConstants;
import com.ngohung.form.el.validator.ValidationStatus;

/**
 * Modified by Sarath
 */
public class HButtonElement extends HElement {
    protected int elType = HElementType.BUTTON;
    public View.OnClickListener onClick;

    public void setOnClick(View.OnClickListener onClick) {
        this.onClick = onClick;
    }


    public HButtonElement(String label)
    {
        this.label = label;
        this.value = HConstants.BLANK;
    }


    @Override
    public void loadValueForUI(View v) {
        if(label == null) // only load value if there is something
            return;

        if(v instanceof Button){
            Button button = (Button) v;
            if(label!=null)
                button.setText(label);
            button.setOnClickListener(onClick);
        }

    }

    public void setElType(int elType) {
        this.elType = elType;
    }

    @Override
    public void saveValueFromUI(View v) {

    }

    @Override
    public ValidationStatus doValidationForUI(View v) {
        return null;
    }

    public int getElType() {
        return elType;
    }


    public void setStyle(int styleType){
        if(styleType == HConstants.SUBMIT_BUTTON_STYLE){
            setStyle(R.style.SubmitButtonStyle);
        }else if(styleType == HConstants.NORMAL_BUTTON_STYLE){
            setStyle(R.style.FormRowStyle);
        }
    }

    public Button getButtonView(){
        return (Button) getView().findViewById(R.id.btn);
    }
}

