package oss.technion.openstreetheight.section.options.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.franmontiel.attributionpresenter.AttributionPresenter;

import butterknife.ButterKnife;
import butterknife.OnClick;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.section.options.presenter.OptionsPresenter;

public class OptionsFragment extends Fragment implements OptionsView {

    private final OptionsPresenter presenter = new OptionsPresenter(this);


    // Listeners
    @OnClick(R.id.button_about) public void onAboutClick() { presenter.onAboutClick(); }

    @OnClick(R.id.button_how_it_works) public void onHowItWorksClick() { presenter.onHowItWorksClick();}

    @OnClick(R.id.button_licenses) public void onLicensesClick() { presenter.onLicensesClick();}

    @OnClick(R.id.button_cam_params) public void onCamParamsClick() { presenter.onCamParamsClick(); }

    @OnClick(R.id.button_osm_auth) public void onOsmAuthClick() { presenter.onOsmAuthClick();}

    @OnClick(R.id.button_measure) public void onMeasureClick() { presenter.onMeasureClick();}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_options, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        presenter.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.onStop();
    }

    @Override
    public void showLicenses() {

        ListAdapter adapter = new AttributionPresenter.Builder(getContext())
                .addAttributions(LicenseProvider.getAttributions(getContext(), R.raw.licenses))
                .build()
                .getAdapter();

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.options_licenses)
                .setPositiveButton(android.R.string.ok, (__, ___) -> {})
                .setAdapter(adapter, null)
                .show();
    }

    @Override
    public void setOsmAuthButtonEnabled(boolean enabled) {
        getView().findViewById(R.id.button_osm_auth).setEnabled(enabled);
    }


}
