package oss.technion.openstreetheight.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.hub.MessageHub;
import oss.technion.openstreetheight.model.PhoneParams;
import oss.technion.openstreetheight.model.compass.Compass;
import oss.technion.openstreetheight.model.camera.params.CameraParams;
import oss.technion.openstreetheight.model.FusedLocation;
import oss.technion.openstreetheight.model.InternetAccess;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.mvp.SectionSwitcher;
import oss.technion.openstreetheight.section.corners.view.CornerPointsOnPhotoFragment;
import oss.technion.openstreetheight.section.finish.view.FinishFragment;
import oss.technion.openstreetheight.section.map.intro.view.IntroMapFragment;
import oss.technion.openstreetheight.section.map.view.BuildingSidesMapFragment;
import oss.technion.openstreetheight.section.corners.intro.view.PhotoFragment;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Responsibilities:
 * <p>
 * 1. Section switching
 * <p>
 * 2. Model/MessageHub/Router state saving
 * <p>
 * 3. Calling onActivityResult on current fragment
 * (as fragments do not survive config change)
 * <p>
 * 4. Check prerequisites for app functionality
 * In case of this app:
 * - Internet connection
 * - Whether camera of this smartphone is supported
 */
public class MainActivity extends AppCompatActivity implements SectionSwitcher {
    private static final String CURRENT_FRAGMENT_TAG = "current_fragment";
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Router.setSwitcher(this);
        initializeModels();

        checkPrerequisites();

        setContentView(R.layout.activity_main);


        if (savedInstanceState != null) {
            MessageHub.restoreState(savedInstanceState);
            Router.restoreState(savedInstanceState);
            CameraParams.restoreFromPreferences();
            Compass.restoreState(savedInstanceState);
        }

        if (savedInstanceState == null) {
            Router.onInitialSwitch();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        InternetAccess.deInitialize();
        disposables.clear();
    }

    private void initializeModels() {
        AndroidNetworking.initialize(getApplicationContext());
        InternetAccess.initialize(getApplicationContext());
        Compass.initialize(getApplicationContext());
        FusedLocation.initialize(getApplicationContext());
        PhoneParams.initialize(getApplicationContext());
        CameraParams.initialize(getApplicationContext());
    }


    private void checkPrerequisites() {
        // Google Play Services
        isGooglePlayServicesAvailable();

    }

    @Override
    public void onBackPressed() {
        Router.onBackClick();
    }

    @Override
    public void doTransaction(Section dest) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;

        switch (dest) {
            case INTRO_MAP:
                fragment = new IntroMapFragment();
                break;

            case MAP:
                fragment = new BuildingSidesMapFragment();
                break;

            case PHOTO:
                fragment = new PhotoFragment();
                break;

            case DRAW:
                fragment = new CornerPointsOnPhotoFragment();
                break;

            case FINISH:
                fragment = new FinishFragment();
                break;
        }

        ft
                .replace(R.id.container, fragment, CURRENT_FRAGMENT_TAG)
                .commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Router.onBackClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        MessageHub.saveState(outState);
        Router.saveState(outState);
        CameraParams.saveToPreferences();
        Compass.saveState(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onStateNotSaved();

        Fragment curFragment = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT_TAG);
        curFragment.onActivityResult(requestCode, resultCode, data);
    }

    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************* THIS SECTION CHECKS PRESENCE OF GOOGLE SERVICES **********************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************

    private static final int REQUEST_GOOGLE_PLAY_RESOLUTION = 1;

    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.showErrorDialogFragment(this, status,
                        REQUEST_GOOGLE_PLAY_RESOLUTION, __ -> finish());
            }
            return false;
        }
        return true;
    }
}