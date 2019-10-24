package oss.technion.openstreetheight.section.osmauth.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.evernote.android.state.StateSaver;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.section.SnackbarDuration;
import oss.technion.openstreetheight.section.osmauth.presenter.OsmAuthPresenter;

public class OsmAuthFragment extends Fragment implements OsmAuthView {
    private OsmAuthPresenter presenter = new OsmAuthPresenter(this);

    private Snackbar curSnackbar;
    private ActionBar actionBar;
    private Menu menu;

    @BindView(R.id.webview) WebView webview;
    @BindView(R.id.text_status) TextView authStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_osm_auth, container, false);
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

        webview.getSettings().setSaveFormData(false);
        webview.getSettings().setAppCacheEnabled(false);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        // Attach listeners
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){

                return presenter.onRedirectIntercept(url);
            }
        });



    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StateSaver.restoreInstanceState(presenter, savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_osm_auth, menu);

        this.menu = menu;

        presenter.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_done:
                presenter.onNavContinueClick();
                return true;

            case R.id.nav_skip:
                presenter.onNavSkipClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        StateSaver.saveInstanceState(presenter, outState);
    }

    @Override
    public void showBackButton(boolean isShowBackButton) {
        actionBar.setDisplayShowHomeEnabled(isShowBackButton);
        actionBar.setDisplayHomeAsUpEnabled(isShowBackButton);
    }

    @Override
    public void loadUrl(String url) {
        webview.loadUrl(url);
    }

    @Override
    public void showSuccessText() {
        webview.setVisibility(View.GONE);
    }

    @Override
    public void clearWebviewCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
    }

    @Override
    public void setApproveButtonEnabled(boolean enable) {
        menu.findItem(R.id.nav_done).setEnabled(enable);
        menu.findItem(R.id.nav_done).getIcon().setAlpha(enable ? 255 : 64);
    }

    @Override
    public void setStatusTextAuth(String displayName) {
        authStatus.setText(getString(R.string.osmauth_status_authorized, displayName));
    }

    @Override
    public void setStatusTextNonAuth() {
        authStatus.setText(R.string.osmauth_status_not_authorized);
    }

    @Override
    public void setSkipButtonVisibility(boolean visible) {
        menu.findItem(R.id.nav_skip).setVisible(visible);
    }

    @Override
    public void authStatusShortBlink() {
        Animation hyperspaceJump = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
        authStatus.startAnimation(hyperspaceJump);
    }

    @Override
    public void showSnackbarText(@StringRes int text, SnackbarDuration type) {
        int snackbarType = -1;

        switch (type) {

            case LONG:
                snackbarType = Snackbar.LENGTH_LONG;
                break;

            case SHORT:
                snackbarType = Snackbar.LENGTH_SHORT;
                break;

            case INDEFINITE:
                snackbarType = Snackbar.LENGTH_INDEFINITE;
                break;
        }

        curSnackbar =  Snackbar
                .make(getView(), text, snackbarType);

        curSnackbar.show();
    }
}
