package com.ngohung.form.el;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ngohung.form.R;
import com.ngohung.form.constant.HConstants;
import com.ngohung.form.el.store.HDataStore;
import com.ngohung.form.el.validator.ValidationStatus;
import com.ngohung.form.model.NamesWithID;
import com.ngohung.form.util.HStringUtil;

import java.util.ArrayList;
/*
 * This element will display a single choice dialog popup
 * The displaying options refer to the choices in the popup
 * The 'options' should contain unique String array
 * The 'value' is refering to the selection after user has clicked on an item in the popup
 *
 */
public class HPickerElement extends HElement implements OnClickListener {

    // the separator is so that we can pass the options String in the constructor
    // Example: Option 1|Option 2|Option 3 will generate the options array with 3 entry
    public final static String SEPARATOR_REGEX = "\\|";
    public final static String SEPARATOR = "|";
    public final static String DEFAULT_REQUIRED_MSG = "Please select";
    public static final ArrayList<HElement> pickerElements = new ArrayList<>();
    private String key2;
    protected int elType = HElementType.PICKER_EL;
    protected ArrayList<HElement> elements = new ArrayList<>();
    protected ArrayList<Integer> values = new ArrayList<>();
    protected String[] options;                                    // list of option for display		// list of option for display
    protected int selectedIndex = HConstants.NOT_SPECIFIED;        // which option user has selected
    //    private Map<String, Integer> mData = Collections.emptyMap();
    private ArrayList<NamesWithID> mData = new ArrayList<>();
    private ArrayList<HSection> posSection = new ArrayList<>();
    private ArrayList<HSection> negSection = new ArrayList<>();
    private boolean editable = true;

    // constructors
    // optionsStr is of format: Option 1|Option 2|Option 3
    public HPickerElement(String key, String label, String hint, boolean required, String optionsStr) {
        this.key = key;
        this.label = label;
        this.value = HConstants.BLANK;
        this.hint = hint;
        this.required = required;
        this.requireMsg = DEFAULT_REQUIRED_MSG;

        this.options = optionsStr.split(SEPARATOR_REGEX);
        selectedIndex = HConstants.NOT_SPECIFIED;

    }

    // constructors
    // optionsStr is of format: Option 1|Option 2|Option 3
    public HPickerElement(String key, String label, String hint, boolean required, String optionsStr, HDataStore store) {
        this.key = key;
        this.label = label;
        this.value = HConstants.BLANK;
        this.hint = hint;
        this.required = required;
        this.requireMsg = DEFAULT_REQUIRED_MSG;

        this.options = optionsStr.split(SEPARATOR_REGEX);
        selectedIndex = HConstants.NOT_SPECIFIED;

        this.setDataStore(store);
        if (store != null) {
            store.loadValueFromStore(this);
            selectedIndex = HStringUtil.getSelectedIndex(options, value);

        }
    }

    public HPickerElement(String key, String label, String hint, boolean required, String optionsStr, int selectedIndex) {
        this.key = key;
        this.label = label;
        this.value = HConstants.BLANK;
        this.hint = hint;
        this.required = required;
        this.requireMsg = DEFAULT_REQUIRED_MSG;

        this.options = optionsStr.split(SEPARATOR_REGEX);
        this.selectedIndex = selectedIndex;

    }

    public HPickerElement(String key, String label, String hint, boolean required, String optionsStr, int selectedIndex, HDataStore store) {
        this.key = key;
        this.label = label;
        this.value = HConstants.BLANK;
        this.hint = hint;
        this.required = required;
        this.requireMsg = DEFAULT_REQUIRED_MSG;

        this.options = optionsStr.split(SEPARATOR_REGEX);
        this.selectedIndex = selectedIndex;

        this.setDataStore(store);
        if (store != null) {
            store.loadValueFromStore(this);
            selectedIndex = HStringUtil.getSelectedIndex(options, value);
        }

    }

    public HPickerElement(String key, String key2, String label, String hint, boolean required, int selectedIndex, ArrayList<NamesWithID> data, HDataStore store) {
        this.key = key;
        this.key2 = key2;
        this.label = label;
        this.value = HConstants.BLANK;
        this.hint = hint;
        this.required = required;
        this.requireMsg = DEFAULT_REQUIRED_MSG;
        ArrayList<String> species = new ArrayList<>();
        for (NamesWithID location : data) {
            species.add(location.getName());
        }
        this.options = species.toArray(new String[0]);//optionsStr.split(SEPARATOR_REGEX);
        this.selectedIndex = selectedIndex;
        this.mData = data;
        this.setDataStore(store);
        if (store != null) {
            store.loadValueFromStore(this);
            selectedIndex = HStringUtil.getSelectedIndex(options, value);
        }

    }

    public HPickerElement(String key, String label, String hint, boolean required, String options[]) {
        this.key = key;
        this.label = label;
        this.value = HConstants.BLANK;
        this.hint = hint;
        this.required = required;
        this.requireMsg = DEFAULT_REQUIRED_MSG;
        this.options = options;
        selectedIndex = HConstants.NOT_SPECIFIED;

    }

    public HPickerElement(String key, String label, String hint, boolean required, String options[], int selectedIndex) {
        this.key = key;
        this.label = label;
        this.value = HConstants.BLANK;
        this.hint = hint;
        this.required = required;
        this.requireMsg = DEFAULT_REQUIRED_MSG;
        this.options = options;
        this.selectedIndex = selectedIndex;

    }

    public HPickerElement(String key, String label, String hint, boolean required, String options[], int selectedIndex, HDataStore store) {
        this.key = key;
        this.label = label;
        this.value = HConstants.BLANK;
        this.hint = hint;
        this.required = required;
        this.requireMsg = DEFAULT_REQUIRED_MSG;

        this.options = options;
        this.selectedIndex = selectedIndex;

        this.setDataStore(store);
        if (store != null) {
            store.loadValueFromStore(this);
            selectedIndex = HStringUtil.getSelectedIndex(options, value);
        }

    }

    public ArrayList<HElement> getElements() {
        return elements;
    }

    /*public HPickerElement(String key, String label, String hint, boolean required, int selectedIndex, Map<String, Integer> data, HDataStore store) {
        this.key = key;
        this.label = label;
        this.value = HConstants.BLANK;
        this.hint = hint;
        this.required = required;
        this.requireMsg = DEFAULT_REQUIRED_MSG;

        this.options = data.keySet().toArray(new String[0]);//optionsStr.split(SEPARATOR_REGEX);
        this.selectedIndex = selectedIndex;
        this.mData = data;
        this.setDataStore(store);
        if (store != null) {
            store.loadValueFromStore(this);
            selectedIndex = HStringUtil.getSelectedIndex(options, value);
        }

    }*/

    public void addPosSection(HSection posSection) {
        ArrayList<HElement> pickerElements = new ArrayList<>();
        this.posSection.add(posSection);
        for (HElement element : posSection.getElements()) {
            if (element instanceof HPickerElement) {
                HPickerElement picker = (HPickerElement) element;
                pickerElements.addAll(picker.getElements());
            }
            if (!pickerElements.contains(element)) {
                addPosElement(element);
            }
        }
        if (getIndex() == 0 && !isHidden()) {
            posSection.setVisible(true);
        } else {
            posSection.setVisible(false);
        }
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void addNegSection(HSection negSection) {
        ArrayList<HElement> pickerElements = new ArrayList<>();
        this.negSection.add(negSection);
        for (HElement element : negSection.getElements()) {
            if (element instanceof HPickerElement) {
                HPickerElement picker = (HPickerElement) element;
                pickerElements.addAll(picker.getElements());
            }
            if (!pickerElements.contains(element)) {
                addNegElement(element);
            }
        }
        if (getIndex() == 1 && !isHidden()) {
            negSection.setVisible(true);
        } else {
            negSection.setVisible(false);
        }
    }

    public void setYears(String optionsStr) {
        this.options = optionsStr.split(SEPARATOR_REGEX);
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        setVisibility(getIndex());
    }

    public void addPosElement(HElement element) {
        elements.add(element);
        values.add(0);
        updateVisibility();
    }

    public void addNegElement(HElement element) {
        elements.add(element);
        values.add(1);
        updateVisibility();
    }

    public void addElementForValue(HElement element, int value) {
        elements.add(element);
        values.add(value);
        if (!disableClear)
            updateVisibility();
    }

    public void updateVisibility() {
        setVisibility(getIndex());
    }

    public int getIndex() {
        for (int j = 0; j < options.length; j++) {
            if (options[j].equalsIgnoreCase(getValue())) {
                return j;
            }
        }
        return -1;
    }

    // the view here is refering to the button that this element represent
    @Override
    public void loadValueForUI(View v) {
        if (!(v instanceof Button))
            return;
        Button btn = (Button) v;

        if (value == null || value.length() == 0 || (selectedIndex == HConstants.NOT_SPECIFIED) || value == HConstants.BLANK) {
            btn.setText(this.getHint());
            return;
        }
        btn.setTextColor(Color.YELLOW);
        btn.setText(value);

    }

    @Override
    public void saveValueFromUI(View v) {
        // nothing to do here as the view is a button which only display the value

    }

    @Override
    public ValidationStatus doValidationForUI(View v) {
        if (!(v instanceof Button))
            return null;

        Button btn = (Button) v;
        // do validation here
        ValidationStatus vStatus = doValidation();

        // display error validation
        if (vStatus != null && (!vStatus.isValid())) {
            // display error here
            btn.setError(vStatus.getMsg());

            // set text color to red
            btn.setText(vStatus.getMsg());
            btn.setTextColor(Color.RED);
        } else {
            if (required || (vStatus != null && vStatus.isValid())) // only do this for required field , if optional field, only when condition is satisfied
            {
                btn.setError(null);
                //btn.setTextColor(Color.BLACK);
                btn.setTextColor(Color.YELLOW);
            }
        }

        return vStatus;
    }

    private void showEdittext(boolean value) {

    }

    @Override
    public void clearValue() {
        super.clearValue();
        if (!disableClear)
            getButton().setText(this.getHint());
        selectedIndex = HConstants.NOT_SPECIFIED;
    }

    // trigger the dialog upon clicking of the picker btn
    @Override
    public void onClick(final View view) {
        if (view instanceof Button && editable) {
            final Button pickerBtn = (Button) view;

            String title = this.getHint();

            AlertDialog.Builder builder = new AlertDialog.Builder(pickerBtn.getContext());
            builder.setTitle(title);
            builder.setCancelable(true);

            builder.setSingleChoiceItems(options, selectedIndex, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    // update button text
                    pickerBtn.setText(options[which]);
                    pickerBtn.setTextColor(Color.YELLOW);

                    if (mData.isEmpty()) {
                        setValue(options[which]); // store value
                    } else {
                        for (NamesWithID locationWithID : mData) {
                            if (String.valueOf(mData.get(which)).equalsIgnoreCase(locationWithID.getName())){
                                setValue(String.valueOf(locationWithID.getName()));
                                getDataStore().saveData(key2, String.valueOf(locationWithID.getId()));
                                setValue(options[which]);
                                pickerBtn.setText(locationWithID.getName());
                            }
                        }

                    }
                    selectedIndex = which;
                    setVisibility(which);
                    //new category selected
                    dialog.dismiss();

                    // display error if fail validation
                    doValidationForUI(pickerBtn);
                }
            });


            AlertDialog alert = builder.create();
            alert.show();
        }


    }

    public Button getButton() {
        return (Button) getView().findViewById(R.id.btn_picker);
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }


    public int getElType() {
        return HElementType.PICKER_EL;
    }

    public void setVisibility(int value) {

        ArrayList<HElement> visibleElements = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            if (values.get(i) == value && !isHidden()) {
                elements.get(i).setHidden(false);
                visibleElements.add(elements.get(i));
            } else if (!visibleElements.contains(elements.get(i))) {
                elements.get(i).setHidden(true);
            }
        }
        if (value == 0) {
            for (HSection section : posSection) {
                if (section != null) {
                    section.setVisible(true);
                }
            }
            for (HSection section : negSection) {
                if (section != null) {
                    section.setVisible(false);
                }
            }
        } else {
            for (HSection section : posSection) {
                if (section != null) {
                    section.setVisible(false);
                }
            }
            for (HSection section : negSection) {
                if (section != null) {
                    section.setVisible(true);
                }
            }
        }
    }
}
