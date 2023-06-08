package com.ngohung.form.el.store;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ngohung.form.constant.HConstants;
import com.ngohung.form.el.HElement;

// store element values to SharedPreferences data store
public class HPrefDataStore implements HDataStore{

	private SharedPreferences pref;
	
	public HPrefDataStore(SharedPreferences pref)
	{
		this.pref = pref;
	}
	
	
	@Override
	public void saveValueToStore(HElement el) {
		if (pref != null && el != null && el.getKey() != null) {
			// store to sharepreferences
			Editor editor = pref.edit();
			editor.putString(el.getKey(), el.getValue());
			editor.apply();

		}

	}

	public void saveValueToStore(String key, String value) {
		Editor editor = pref.edit();
		editor.putString(key, value);
		editor.apply();
	}

	@Override
	public void loadValueFromStore(HElement el) {
		if (el != null && el.getKey() != null) {
			String key = el.getKey();
			String storeValue = pref.getString(key, HConstants.BLANK);
			el.setValueFromStore(storeValue);
		}
	}

	@Override
	public void saveData(String key, String value) {
		Editor editor = pref.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public SharedPreferences getPref() {
		return pref;
	}

	public void setPref(SharedPreferences pref) {
		this.pref = pref;
	}

	
}
