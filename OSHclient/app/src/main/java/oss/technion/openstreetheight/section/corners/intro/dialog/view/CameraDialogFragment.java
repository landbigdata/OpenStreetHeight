package oss.technion.openstreetheight.section.corners.intro.dialog.view;


import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.evernote.android.state.StateSaver;

import butterknife.BindView;
import butterknife.ButterKnife;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.section.corners.intro.dialog.presenter.CameraDialogPresenter;

public class CameraDialogFragment extends DialogFragment implements CameraDialogView {
    public static final String TAG = CameraDialogFragment.class.getSimpleName();

    private CameraDialogPresenter presenter = new CameraDialogPresenter(this);

    @BindView(R.id.camera_focal_length) EditText cameraFocalLength;
    @BindView(R.id.camera_pixel_size) EditText cameraSensorWidth;
    private AlertDialog dialog;

    private Bundle savedInstanceState;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.main_dialog_camera_title)
                        .setView(R.layout.dialog_camera)
                        .setPositiveButton(R.string.main_dialog_camera_approve, (__, ___) -> onApproveClick())
                        .setNegativeButton(R.string.main_dialog_camera_exit, (__, ___) -> onExitClick())
                        .create();

        return dialog;

    }



    @Override
    public void onStart() {
        super.onStart();
        ButterKnife.bind(this, getDialog());

        cameraFocalLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.onFocalLenghtTextChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cameraSensorWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.onSensorSizeTextChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        presenter.onStart(savedInstanceState == null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;

        StateSaver.restoreInstanceState(presenter, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        StateSaver.saveInstanceState(presenter, outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.onStop();
    }

    void onApproveClick() {
        double focalLength = Double.valueOf(cameraFocalLength.getText().toString());
        double sensorWidth = Double.valueOf(cameraSensorWidth.getText().toString());

        presenter.onApproveButtonClick(focalLength, sensorWidth);
    }

    void onExitClick() {
        presenter.onExitButtonClick();
    }


    @Override
    public void setPixelSizeText(double sensorSize) {
        cameraSensorWidth.setText(String.valueOf(sensorSize));
    }

    @Override
    public void setFocalLengthText(double focalLength) {
        cameraFocalLength.setText(String.valueOf(focalLength));
    }

    @Override
    public void setApproveButtonEnabled(boolean enabled) {
        Button approveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        approveButton.setEnabled(enabled);
    }
}
