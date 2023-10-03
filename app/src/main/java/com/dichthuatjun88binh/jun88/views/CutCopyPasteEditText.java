package com.dichthuatjun88binh.jun88.views;

import android.content.Context;
import android.util.AttributeSet;

public class CutCopyPasteEditText extends androidx.appcompat.widget.AppCompatEditText {
    private OnCutCopyPasteListener mOnCutCopyPasteListener;

    public CutCopyPasteEditText(Context context) {
        super(context);
    }

    public CutCopyPasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CutCopyPasteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnCutCopyPasteListener(OnCutCopyPasteListener listener) {
        this.mOnCutCopyPasteListener = listener;
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        switch (id) {
            case android.R.id.cut:
                onCut();
                break;
            case android.R.id.copy:
                onCopy();
                break;
            case android.R.id.paste:
                onPaste();
        }
        return consumed;
    }

    /**
     * Text was cut from this EditText.
     */
    public void onCut() {
        if (mOnCutCopyPasteListener != null)
            mOnCutCopyPasteListener.onCut();
    }

    /**
     * Text was copied from this EditText.
     */
    public void onCopy() {
        if (mOnCutCopyPasteListener != null)
            mOnCutCopyPasteListener.onCopy();
    }

    /**
     * Text was pasted into the EditText.
     */
    public void onPaste() {
        if (mOnCutCopyPasteListener != null)
            mOnCutCopyPasteListener.onPaste();
    }

    public interface OnCutCopyPasteListener {
        void onCut();

        void onCopy();

        void onPaste();
    }
}
