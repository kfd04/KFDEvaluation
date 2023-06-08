package com.ngohung.form.el;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ngohung.form.el.store.HDataStore;

import java.util.Arrays;
import java.util.List;


public class HMultiPickerElement extends HPickerElement implements OnClickListener{

	private  MultiListener listener;
	boolean [] itemsChecked ;
	boolean editable = true;

    public HMultiPickerElement(String key, String label, String hint, boolean required, String optionsStr, HDataStore store) {
        super(key, label, hint, required, optionsStr, store);
        itemsChecked = new boolean[options.length];
        if (value != null && value.length() > 0) {
            parseItemsChecked();
        }
    }

    @Override
    public void onClick(final View view) {
        if (view instanceof Button) {
            final Button pickerBtn = (Button) view;

            String title = this.getHint();

            AlertDialog.Builder builder = new AlertDialog.Builder(pickerBtn.getContext());
            builder.setTitle(title);
            builder.setCancelable(true);

            builder.setMultiChoiceItems(options, itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (editable) {
                        pickerBtn.setText(options[which]);
                        itemsChecked[which] = isChecked;
                        setValue(options[which]); // store value
                        selectedIndex = which;
                        setVisibility(which, isChecked);
                        pickerBtn.setTextColor(Color.YELLOW);
                    } else {
                        itemsChecked[which] = !isChecked;
                        ((AlertDialog) dialog).getListView().setItemChecked(which, itemsChecked[which]);
                    }

                /*    String temp="";
                    for (int i = 0; i < options.length; i++) {
                        if (itemsChecked[i]){}
                        { if (temp.isEmpty())
                            temp=options[i];
                        else
                            temp=temp+"|"+options[i];}
                    }
                    setValue(temp);*/

                    if (listener != null)
                        listener.valueChanged(which, options, itemsChecked);
                }
            });

            builder.setNeutralButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });


            if (editable) {
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        String selectedItems = new String("");
                        for (int i = 0; i < options.length; i++) {
                            if (itemsChecked[i]) {
                                selectedItems = selectedItems.concat(options[i] + "|");
                            }
                        }
                        if (selectedItems.length() > 0) {
                            selectedItems = selectedItems.substring(0, selectedItems.length() - 1);
                        }
                        setValue(selectedItems);
                    }
                });
            }
            AlertDialog alert = builder.create();
            alert.show();
        }


    }

    public void setListener(MultiListener listener) {
        this.listener = listener;
        listener.valueChanged(-1, options, itemsChecked);
    }
    private void parseItemsChecked() {
        String[] selectedItems = value.split(SEPARATOR_REGEX);
        List<String> optionsArray = Arrays.asList(options);
        for (String selected : selectedItems) {
            if (optionsArray.indexOf(selected) >= 0) {
                itemsChecked[optionsArray.indexOf(selected)] = true;
            }
        }
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setVisibility(int which, boolean isChecked) {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == which) {
                elements.get(i).setHidden(!isChecked);
            }
        }
    }

    public interface MultiListener {
        void valueChanged(int which, String[] options, boolean[] values);

    }
}
