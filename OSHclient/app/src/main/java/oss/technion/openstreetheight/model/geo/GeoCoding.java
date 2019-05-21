package oss.technion.openstreetheight.model.geo;


import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import oss.technion.openstreetheight.model.data.LatLng;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GeoCoding {
    public static class OsmBuilding {
        public final long osm_id;
        public final List<LatLng> points;

        public OsmBuilding(long osm_id, List<LatLng> points) {
            this.osm_id = osm_id;
            this.points = points;
        }
    }

    // see https://wiki.openstreetmap.org/wiki/Overpass_API
    public static Maybe<OsmBuilding> getBuilding(double lat, double lon) {
        return Rx2AndroidNetworking
                .post("https://overpass-api.de/api/interpreter")
                .addStringBody(String.format(
                        Locale.US,
                        "[timeout:10][out:json];way(around:200, %f, %f)[\"building\"];out geom;",
                        lat,
                        lon))

                .build()
                .getJSONObjectSingle()

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .map(obj -> obj.getJSONArray("elements"))

                .filter(elements -> elements.length() > 0)

                .flatMapObservable(elements -> {
                    List<JSONObject> buildings = new ArrayList<>();
                    for (int i = 0; i < elements.length(); i++) {
                        buildings.add(elements.getJSONObject(i));
                    }
                    return Observable.fromIterable(buildings);

                })

                .map(building -> {
                    // here we parse geojson attached to response
                    int osm_id = building.getInt("id");
                    List<LatLng> points = extractPolygon(building);

                    return new OsmBuilding(osm_id, points);
                })

                .filter(osmBuilding -> Observable
                        .fromIterable(osmBuilding.points)
                        .map(point -> CoordinateConversions.getXYZfromLatLonDegrees(point.lat, point.lon, 0))
                        .collectInto(new Polygon(), (polygon, xy) -> polygon.add(new Point(xy[0], xy[1])))
                        .map(polygon -> {
                            double[] xy = CoordinateConversions.getXYZfromLatLonDegrees(lat, lon, 0);
                            return polygon.contains(new Point(xy[0], xy[1]));
                        })
                        .blockingGet()
                )

                .singleElement();
    }

    private static List<LatLng> extractPolygon(JSONObject building) throws JSONException {
        List<LatLng> points = new ArrayList<>();

        JSONArray geometryArray = building
                .getJSONArray("geometry");

        for (int i = 0; i < geometryArray.length(); i++) {
            JSONObject latLon = geometryArray.getJSONObject(i);
            double pointLat = latLon.getDouble("lat");
            double pointLon = latLon.getDouble("lon");

            points.add(new LatLng(pointLat, pointLon));
        }

        return points;
    }

    /* Sample output
    {
  "version": 0.6,
  "generator": "Overpass API 0.7.54.13 ff15392f",
  "osm3s": {
    "timestamp_osm_base": "2018-04-19T11:49:02Z",
    "copyright": "The data included in this document is from www.openstreetmap.org. The data is made available under ODbL."
  },
  "elements": [

{
  "type": "way",
  "id": 184223018,
  "bounds": {
    "minlat": 32.7885467,
    "minlon": 35.0186482,
    "maxlat": 32.7887283,
    "maxlon": 35.0188787
  },
  "nodes": [
    1946743232,
    1946743195,
    1946743151,
    1946743198,
    1946743232
  ],
  "geometry": [
    { "lat": 32.7887283, "lon": 35.0188036 },
    { "lat": 32.7886364, "lon": 35.0186482 },
    { "lat": 32.7885467, "lon": 35.0187233 },
    { "lat": 32.7886386, "lon": 35.0188787 },
    { "lat": 32.7887283, "lon": 35.0188036 }
  ],
  "tags": {
    "building": "yes"
  }
}

  ]
}
     */
}
