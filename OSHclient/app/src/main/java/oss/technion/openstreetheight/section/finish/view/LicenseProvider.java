package oss.technion.openstreetheight.section.finish.view;

import com.franmontiel.attributionpresenter.entities.Attribution;
import com.franmontiel.attributionpresenter.entities.License;

import java.util.ArrayList;
import java.util.List;

public class LicenseProvider {
    public static Attribution[] getAttributions() {
        List<Attribution> attributions = new ArrayList<>();

        attributions.add(
                new Attribution.Builder("Android Support Library")
                        .addCopyrightNotice("The Android Open Source Project")
                        .addLicense(License.APACHE)
                        .setWebsite("https://developer.android.com/topic/libraries/support-library/index.html")
                        .build()
        );

        attributions.add(
                new Attribution.Builder("Google Play Services")
                        .addCopyrightNotice("Google Inc.")
                        .addLicense(License.APACHE)
                        .setWebsite("https://developers.google.com/android/guides/overview")
                        .build()
        );

        attributions.add(
                new Attribution.Builder("Android Maps Utils")
                        .addCopyrightNotice("Google Inc.")
                        .addLicense(License.APACHE)
                        .setWebsite("https://github.com/googlemaps/android-maps-utils")
                        .build()
        );

        attributions.add(
                new Attribution.Builder("Fast Android Networking")
                        .addCopyrightNotice("Amit Shekhar")
                        .addLicense(License.APACHE)
                        .setWebsite("https://github.com/amitshekhariitbhu/Fast-Android-Networking")
                        .build()
        );

        attributions.add(
                new Attribution.Builder("RxJava")
                        .addCopyrightNotice("RxJava Contributors")
                        .addLicense(License.APACHE)
                        .setWebsite("https://github.com/ReactiveX/RxJava")
                        .build()
        );

        attributions.add(
                new Attribution.Builder("AttributionPresenter")
                        .addCopyrightNotice("Francisco José Montiel Navarro")
                        .addLicense(License.APACHE)
                        .setWebsite("https://github.com/franmontiel/AttributionPresenter")
                        .build()
        );

        attributions.add(
                new Attribution.Builder("ButterKnife")
                        .addCopyrightNotice("Jake Wharton")
                        .addLicense(License.APACHE)
                        .setWebsite("https://github.com/JakeWharton/butterknife")
                        .build()
        );

        attributions.add(
                new Attribution.Builder("Android-State")
                        .addCopyrightNotice("Evernote Corporation")
                        .addLicense("Eclipse Public License v1.0", "http://www.eclipse.org/legal/epl-v10.html")
                        .setWebsite("https://github.com/evernote/android-state")
                        .build()
        );


        return attributions.toArray(new Attribution[attributions.size()]);
    }
}
