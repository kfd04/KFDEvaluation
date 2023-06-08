package com.ngohung.form.el;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/* 
 * This class represent a form section. Form elements can be grouped into sections which have their own headers
 */
public class HSection {

	private List<HElement> elements;
	private String title; // title for this section
	private View view;
	private boolean isVisible = true;
	
	public HSection(String title) {
		this.title = title;
		elements = new ArrayList<>();
	}
	
	
	public void addEl(HElement el){
		if(el == null)
			return;
		
		if(!elements.contains(el))
			elements.add(el);

	}

	/*public void addTwoEl(HElement el, HElement e2){
		if(el == null || e2 == null)
			return;

		if(!elements.contains(el) || !elements.contains(e2))
			elements.add(0,el);
		    elements.add(1,e2);


	}*/
	
	public void removeEl(HElement el){
		
		if(el == null)
			return;
		
		if(elements.contains(el))
			elements.remove(el);
	}
	
	public List<HElement> getElements() {
		return elements;
	}
	public void disableSubElementClear(){
		for (HElement  hElement: getElements())
			hElement.setDisableClear(true);
	}

	public void setElements(List<HElement> elements) {
		this.elements = elements;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setVisible(boolean value){
		isVisible = value;
		if(view != null){
			if(isVisible){
				view.setVisibility(View.VISIBLE);
			}else{
				view.setVisibility(View.GONE);
			}
		}
	}

	public void setNotEditable(){
		for(HElement element : elements){
			if(element instanceof HTextEntryElement){
				HTextEntryElement textEntryElement = (HTextEntryElement) element;
				textEntryElement.setNotEditable();
			} else if(element instanceof HTextAreaEntryElement){
				HTextAreaEntryElement textEntryElement = (HTextAreaEntryElement) element;
				textEntryElement.setNotEditable();
			} else if (element instanceof HPickerElement){
				HPickerElement pickerElement = (HPickerElement) element;
				pickerElement.setEditable(false);
			} else if (element instanceof HDatePickerElement){
				HDatePickerElement pickerElement = (HDatePickerElement) element;
				pickerElement.setEditable(false);
			}
		}
	}

	public void setView(View view) {
		this.view = view;
		if(!isVisible){
			view.setVisibility(View.GONE);
		}
	}
}
