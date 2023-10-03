package com.dichthuatjun88binh.jun88.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dichthuatjun88binh.jun88.R;


public class DeleteHistoryDialog extends DialogFragment {
    OnClickListenerDeleteModeDialog onClickListenerDeleteModeDialog;
    private TextView mTextViewOk;
    private TextView mTextViewCancel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_history, null);

        mTextViewOk = view.findViewById(R.id.txtOkDeleteMode);
        mTextViewCancel = view.findViewById(R.id.txtCancelDeleteMode);
        mTextViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mTextViewOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListenerDeleteModeDialog.OnOk();
                dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }


    @Override
    public void onResume() {
        super.onResume();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
            window.setBackgroundDrawableResource(R.drawable.bg_white_radius);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            window.setLayout((int) (dm.widthPixels - (dm.widthPixels * 0.1)), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void setOnClickListenerDeleteModeDialog(OnClickListenerDeleteModeDialog onClickListenerDeleteModeDialog) {
        this.onClickListenerDeleteModeDialog = onClickListenerDeleteModeDialog;
    }

    public interface OnClickListenerDeleteModeDialog {
        void OnOk();
    }
}
