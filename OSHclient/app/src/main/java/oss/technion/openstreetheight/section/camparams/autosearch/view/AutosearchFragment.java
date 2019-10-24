package oss.technion.openstreetheight.section.camparams.autosearch.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.section.camparams.autosearch.presenter.AutosearchPresenter;

public class AutosearchFragment extends Fragment implements AutosearchView {

    private AutosearchPresenter presenter = new AutosearchPresenter(this);

    @BindView(R.id.autosearch_section_retry) View sectionRetry;
    @BindView(R.id.autosearch_section_wait) View sectionWait;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_autosearch, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Notify presenter can start working
        presenter.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.onStop();
    }

    @Override
    public void showSectionWait(boolean show) {
        if (show) {
            sectionWait.setVisibility(View.VISIBLE);
        } else {
            sectionWait.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSectionRetry(boolean show) {
        if (show) {
            sectionRetry.setVisibility(View.VISIBLE);
        } else {
            sectionRetry.setVisibility(View.GONE);
        }
    }
}
