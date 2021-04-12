package com.example.dt_it;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class ErrorDialog extends DialogFragment implements OnClickListener {

    public static final String TITLE_KEY = "title";
    public static final String MESSAGE_KEY = "message";
    public static final String BUTTON_KEY = "button";

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getInt(TITLE_KEY))
                .setMessage(getArguments().getString(MESSAGE_KEY))
                .setNeutralButton(getArguments().getInt(BUTTON_KEY), this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        getActivity().finish();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().finish();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        getActivity().finish();
    }
}