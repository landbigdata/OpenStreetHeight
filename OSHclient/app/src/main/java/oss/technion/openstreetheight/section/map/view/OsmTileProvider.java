package oss.technion.openstreetheight.section.map.view;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class OsmTileProvider extends UrlTileProvider {

    private static String baseUrl = "http://a.tile.openstreetmap.org/{z}/{x}/{y}.png";

    public OsmTileProvider(int width, int height) {
        super(width, height);
    }

    @Override
    public URL getTileUrl(int x, int y, int zoom) {
        try {
            return new URL(baseUrl.replace("{z}", "" + zoom).replace("{x}", "" + x).replace("{y}", "" + y));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}