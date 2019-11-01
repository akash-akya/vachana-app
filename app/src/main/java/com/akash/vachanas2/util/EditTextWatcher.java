/*
 * vachana. An application for Android users, it contains kannada vachanas
 * Copyright (c) 2017. akash
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.akash.vachanas2.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class EditTextWatcher implements TextWatcher {

    private final EditText mEditText;
    private String text;

    public EditTextWatcher(EditText editText){
        this.mEditText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditText.removeTextChangedListener(this);
        text = KannadaTransliteration.getUnicodeString(s.toString());
        mEditText.setText(text);
        mEditText.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable s) {
        mEditText.setSelection(text.length());
    }
}
