package ru.home.denis.konovalov.task3;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.IntDef;
import androidx.fragment.app.DialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class MyDialogFragment extends DialogFragment {
    private static final String EXTRA_CAPTION = "MyDialogFragment.caption";
    private static final String EXTRA_TEXT = "MyDialogFragment.text";

    @IntDef({RESULT_YES, RESULT_NO, RESULT_CANCEL})
    public @interface DlgResult{}
    public final static int RESULT_YES = 1;
    public final static int RESULT_NO = 2;
    public final static int RESULT_CANCEL = 3;

    private String caption;
    private String text;
    private IDlgResult listener;

    public interface IDlgResult {
        void onDialogResult(@DlgResult int result);
    }

    public static MyDialogFragment newInstance(String caption, String text) {
        Bundle args = new Bundle();
        args.putString(EXTRA_CAPTION, caption);
        args.putString(EXTRA_TEXT, text);

        MyDialogFragment fragment = new MyDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(IDlgResult listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        caption = getArguments().getString(EXTRA_CAPTION);
        text = getArguments().getString(EXTRA_TEXT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(caption)
                .setNeutralButton(R.string.cancel, (dialogInterface, i) -> {
                    if (listener != null){
                        listener.onDialogResult(RESULT_CANCEL);
                    }
                })
                .setMessage(text);
        return adb.create();
    }
}
