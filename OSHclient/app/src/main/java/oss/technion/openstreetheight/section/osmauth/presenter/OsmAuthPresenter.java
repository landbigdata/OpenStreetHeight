package oss.technion.openstreetheight.section.osmauth.presenter;

import com.evernote.android.state.State;

import java.util.Arrays;

import de.westnordost.osmapi.user.Permission;
import io.reactivex.disposables.CompositeDisposable;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.model.FirstLaunch;
import oss.technion.openstreetheight.model.OsmApi;
import oss.technion.openstreetheight.model.OsmAuth;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.section.SnackbarDuration;
import oss.technion.openstreetheight.section.osmauth.view.OsmAuthView;

public class OsmAuthPresenter {
    private OsmAuthView view;
    private CompositeDisposable disposables = new CompositeDisposable();

    @State
    boolean isTokenObtained; // We do not care if user was generally authorized before


    public OsmAuthPresenter(OsmAuthView view) {
        this.view = view;
    }

    public void onStart() {
        view.showBackButton(Router.isShowBackButton);

        view.setSkipButtonVisibility(FirstLaunch.isInCamParamSetMode);


        // Set current status
        if (isTokenObtained) {
            disposables.add(
                    OsmApi
                            .getUsername(OsmAuth.getInProcessConsumer())
                            .subscribe(view::setStatusTextAuth)
            );
        }
        else if (OsmAuth.isAuthorized()) {
            disposables.add(
                    OsmApi
                            .getUsername(OsmAuth.getSavedConsumer())
                            .subscribe(view::setStatusTextAuth)
            );
        } else {
            view.setStatusTextNonAuth();
        }

        if (isTokenObtained) {
            view.setApproveButtonEnabled(true);

            view.showSuccessText();
        } else {

            view.setApproveButtonEnabled(false);

            // Get oAuth authorize url
            disposables.add(
                    OsmAuth.retrieveRequestToken()
                            .subscribe(authUrl -> view.loadUrl(authUrl))
            );
        }


    }

    public void onStop() {
        view.clearWebviewCookies();

        disposables.clear();
    }

    public void onNavSkipClick() {
        Router.onOsmAuthSkipClick();
    }

    public void onNavContinueClick() {
        OsmAuth.saveTokenToPrefs();

        isTokenObtained = true;





        Router.onOsmAuthDoneClick();
    }

    // We need to tell view whether we intercept page loading
    public boolean onRedirectIntercept(String callbackUrl) {
        if (OsmAuth.shouldInterceptCallback(callbackUrl)) {
            // Here we receive redirect of oAuth

            disposables.add(
                    OsmAuth
                            .retrieveAccessToken(callbackUrl)
                            .subscribe(this::onAuthFinish)
            );

            return true;
        }

        return false;

    }

    private void onAuthFinish() {

        OsmApi
                .hasPermissions(
                        Arrays.asList(Permission.MODIFY_MAP, Permission.READ_PREFERENCES_AND_USER_DETAILS),
                        OsmAuth.getInProcessConsumer()
                )
                .subscribe(isGranted -> {
                    if(isGranted) {
                        view.showSuccessText();
                        view.setApproveButtonEnabled(true);
                        view.authStatusShortBlink();

                        // Now we need to update status
                        disposables.add(
                                OsmApi
                                        .getUsername(OsmAuth.getInProcessConsumer())
                                        .subscribe(view::setStatusTextAuth)
                        );
                    } else {
                        view.showSnackbarText(R.string.osmauth_snack_grant_all_permissions, SnackbarDuration.LONG);

                        // Get oAuth authorize url
                        disposables.add(
                                OsmAuth.retrieveRequestToken()
                                        .subscribe(authUrl -> view.loadUrl(authUrl))
                        );
                    }
                });



    }
}
