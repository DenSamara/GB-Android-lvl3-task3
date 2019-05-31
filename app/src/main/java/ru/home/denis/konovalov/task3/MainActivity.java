package ru.home.denis.konovalov.task3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.widget.ProgressBar;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MyDialogFragment.IDlgResult {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String OUTPUT_FILENAME = "result.png";
    public static final int IDD_SELECT_PHOTO = 1;

    private ProgressBar progressBar;
    private AppCompatButton btSelect;
    private MyDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress);
        btSelect = findViewById(R.id.bt_select_img);
        btSelect.setOnClickListener(v -> startSelectImageActivity());
    }

    private void startSelectImageActivity() {
        enableButton(false);

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IDD_SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IDD_SELECT_PHOTO:
                    try {
                        Uri path = data.getData();

                        showProgress(true);

                        dialogFragment = MyDialogFragment.newInstance(getString(R.string.dlg_caption), getString(R.string.dlg_text));
                        dialogFragment.setListener(this);
                        FragmentTransaction transaction = getSupportFragmentManager()
                                .beginTransaction()
                                .add(dialogFragment, MyDialogFragment.class.getName())
                                .addToBackStack(null);
                        transaction.commitAllowingStateLoss();

                        Disposable d = Completable.fromAction(() -> {
                            InputStream inputStream = getContentResolver().openInputStream(path);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            FileOutputStream out = new FileOutputStream(getOutputImagePath(OUTPUT_FILENAME));
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.close();
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableCompletableObserver() {
                                    @Override
                                    public void onComplete() {
                                        GlobalProc.toast(MainActivity.this, getString(R.string.success));

                                        changeView();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        GlobalProc.logE(TAG, e.toString());

                                        changeView();
                                    }
                                });
                    } catch (Exception e) {
                        GlobalProc.logE(TAG, e.toString());

                        changeView();
                    }
                    break;
            }
        }
    }

    private String getOutputImagePath(String filename) {
        return String.format(Locale.ENGLISH, "%s/%s", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), filename);
    }

    private void showProgress(boolean value) {
        if (progressBar != null) {
            progressBar.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void enableButton(boolean value) {
        if (btSelect != null)
            btSelect.setEnabled(value);
    }

    private void closeDialog(){
        if (dialogFragment != null)
            dialogFragment.dismiss();
    }

    private void changeView(){
        showProgress(false);
        enableButton(true);
        closeDialog();
    }

    @Override
    public void onDialogResult(@MyDialogFragment.DlgResult int result) {
        switch (result) {
            case MyDialogFragment.RESULT_YES:
                GlobalProc.logE(TAG, "DialogResult == YES");
                break;
            case MyDialogFragment.RESULT_NO:
                GlobalProc.logE(TAG, "DialogResult == NO");
                break;
            case MyDialogFragment.RESULT_CANCEL:
                closeDialog();
                break;
            default:
                GlobalProc.logE(TAG, "Unexpectable result: " + result);
                break;
        }

    }
}
