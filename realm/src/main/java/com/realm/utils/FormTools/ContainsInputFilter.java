package com.realm.utils.FormTools;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

public class ContainsInputFilter implements InputFilter {
    String inclusiveCharacters ;

    public ContainsInputFilter(String inclusiveCharacters) {
        this.inclusiveCharacters = inclusiveCharacters;
    }

//    @Override
    public CharSequence filter_(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        boolean keepOriginal = true;
        StringBuilder sb = new StringBuilder(end - start);
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (isCharAllowed(c)) {
                sb.append(c);
            }
            else {
                keepOriginal = false;
            }
        }
        if (keepOriginal)
            return null;
        else {
            if (source instanceof Spanned) {
                SpannableString sp = new SpannableString(sb);
                TextUtils.copySpansFrom((Spanned) source, start, end, null, sp, 0);
                return sp;
            } else {
                return sb;
            }
        }
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        boolean keepOriginal = true;
        StringBuilder sb = new StringBuilder(end - start);
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (isCharAllowed(c)) {
                sb.append(c);
            }
            else {
                keepOriginal = false;
            }
        }
        if (keepOriginal)
            return null;
        else {
            return source.toString().substring(0,source.length()-1);
//            if (source instanceof Spanned) {
//                SpannableString sp = new SpannableString(sb);
//                TextUtils.copySpansFrom((Spanned) source, start, end, null, sp, 0);
//                return sp;
//            } else {
//                return sb;
//            }
        }
    }
    private boolean isCharAllowed(char c) {
        return inclusiveCharacters.contains(c+"");
    }
}
