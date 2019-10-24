package oss.technion.openstreetheight.section.camparams.manual.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.evernote.android.state.StateSaver;

import butterknife.BindView;
import butterknife.ButterKnife;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.func.Consumer;
import oss.technion.openstreetheight.section.camparams.manual.presenter.ManualParamsPresenter;

public class ManualParamsFragment extends Fragment implements ManualParamsView {
    // Fields
    private ManualParamsPresenter presenter = new ManualParamsPresenter(this);
    private boolean isFirstStart;

    private ActionBar actionBar;
    private Menu menu;
    @BindView(R.id.camera_focal_length) EditText cameraFocalLength;
    @BindView(R.id.camera_pixel_size) EditText cameraPixelSize;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cam_params, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Init ActionBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // Setup views
        setHasOptionsMenu(true);

        // Listeners for text change are set after presenter's onStart in onCreateOptionsMenu

        // Notify presenter views are set up
        isFirstStart = (savedInstanceState == null);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_cam_params, menu);
        this.menu = menu;
        // Notify presenter views are set up
        presenter.onStart(isFirstStart);

        // Listeners are put here as text change in presenter may invoke text change listener despite it is not needed
        setTextChangeListener(cameraFocalLength, presenter::onFocalLenghtTextChanged);
        setTextChangeListener(cameraPixelSize, presenter::onSensorSizeTextChanged);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_done:

                float focalLength = Float.valueOf(cameraFocalLength.getText().toString());
                float sensorWidth = Float.valueOf(cameraPixelSize.getText().toString());

                presenter.onApproveButtonClick(focalLength, sensorWidth);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }


    }

    @Override
    public void showBackButton(boolean isShowBackButton) {
        actionBar.setDisplayShowHomeEnabled(isShowBackButton);
        actionBar.setDisplayHomeAsUpEnabled(isShowBackButton);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StateSaver.restoreInstanceState(presenter, savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        StateSaver.saveInstanceState(presenter, outState);

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.onStop();
    }


    @Override
    public void setApproveButtonEnabled(boolean enabled) {
        menu.findItem(R.id.nav_done).setEnabled(enabled);
        menu.findItem(R.id.nav_done).getIcon().setAlpha(enabled ? 255 : 64);
    }

    @Override
    public void setFocalLengthText(String focalLengthMm) {
        cameraFocalLength.setText(focalLengthMm);
    }

    @Override
    public void setPixelSizeText(String pixelSizeMicroM) {
        cameraPixelSize.setText(pixelSizeMicroM);
    }

    private void setTextChangeListener(EditText edittext, Consumer<String> c) {
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.onFocalLenghtTextChanged(s.toString());
                c.accept(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }


}
