package oss.technion.openstreetheight.model;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.data.Way;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Osm {
    public static Maybe<String> getWayTagValue(String tag, long wayId) {
        OsmConnection osm = new OsmConnection(
                "https://api.openstreetmap.org/api/0.6/",
                "OpenStreetHeight Client", null);

        MapDataDao mapDao = new MapDataDao(osm);

        return Single
                .fromCallable(() -> mapDao.getWay(wayId))

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .map(Way::getTags)
                .filter(map -> map.containsKey(tag))
                .map(map -> map.get(tag));

    }
}
