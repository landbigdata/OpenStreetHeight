package oss.technion.openstreetheight.section.options.view;

import android.content.Context;

import androidx.annotation.RawRes;
import androidx.annotation.XmlRes;

import com.franmontiel.attributionpresenter.entities.Attribution;
import com.franmontiel.attributionpresenter.entities.License;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import oss.technion.openstreetheight.R;

public class LicenseProvider {
    public static class Notice {
        public String name;
        public String copyright;
        public String url_lib;
        public String license;
        public String url_license;
    }

    public static Attribution[] getAttributions(Context context, @RawRes int license) {
        List<Notice> notices = new ArrayList<>();
        List<Attribution> attributions = new ArrayList<>();

        try {
            InputStream inputStream = context.getResources().openRawResource(license);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();

            String json = byteArrayOutputStream.toString();

            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<List<Notice>> jsonAdapter = moshi.adapter(Types.newParameterizedType(List.class, Notice.class));

            notices = jsonAdapter.fromJson(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Notice notice : notices) {
            attributions.add(
                    new Attribution.Builder(notice.name)
                            .addCopyrightNotice(notice.copyright)
                            .addLicense(notice.license, notice.url_license)
                            .setWebsite(notice.url_lib)
                            .build()
            );
        }

        return attributions.toArray(new Attribution[0]);

    }

}
