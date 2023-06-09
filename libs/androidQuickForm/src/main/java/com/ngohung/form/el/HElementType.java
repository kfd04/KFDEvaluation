package com.ngohung.form.el;

// base element types
public class HElementType {

	public final static int TEXT_EL = 0;   				//  label ( TextView only)
	public final static int TEXT_ENTRY_EL = 1;  		//  label and textfield
	public final static int NUMERIC_ENTRY_EL = 2;		//  label and textfield (numeric keyboard)
	public final static int DATE_ENTRY_EL = 3;			//  button and date picker
	public final static int PICKER_EL = 4; 				//  single choice popup with dialog popup
	public final static int LIST_EL = 5;  				//  allow user to select which option
	public final static int MULTI_LINES_TEXT_ENTRY_EL = 6; // same as text entry element but allow multi lines
	public final static int GPS_BUTTON = 7; // gps button with lat, long, alt views
	public final static int BUTTON = 8; // Button
	public final static int SUBMIT_BUTTON = 9; // Button
	public final static int TEXT_EL_NEW = 10; // Button
	public final static int CUSTOM_ENTRY=11;


}
