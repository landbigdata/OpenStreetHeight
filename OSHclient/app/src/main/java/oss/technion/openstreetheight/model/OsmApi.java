package oss.technion.openstreetheight.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.data.Way;
import de.westnordost.osmapi.user.Permission;
import de.westnordost.osmapi.user.PermissionsDao;
import de.westnordost.osmapi.user.UserDao;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import oauth.signpost.OAuthConsumer;

public class OsmApi {
    private static String API_URL = "https://api.openstreetmap.org/api/0.6/";
    private static String USER_AGENT = "OpenStreetHeight Client";

    public static Maybe<String> getWayTagValue(String tag, long wayId) {
        OsmConnection osm = new OsmConnection(API_URL, USER_AGENT, null);

        MapDataDao mapDao = new MapDataDao(osm);

        return Single
                .fromCallable(() -> mapDao.getWay(wayId))
                .map(Way::getTags)
                .filter(map -> map.containsKey(tag))
                .map(map -> map.get(tag))

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    // Refer to https://wiki.openstreetmap.org/wiki/Key:addr
    // Refer to https://wiki.openstreetmap.org/wiki/Addresses
    // Fallbacks to way osm id if no address was found
    public static Single<String> getBuildingAddress(long wayOsmId) {
        OsmConnection osm = new OsmConnection(API_URL, USER_AGENT, null);
        MapDataDao mapData = new MapDataDao(osm);

        return Single
                .fromCallable(() -> {
                    Way building = mapData.getWay(wayOsmId);

                    // Only these tags are mandatory according to the docs
                    // However, for specific countries different rules may apply (which are not covered here)
                    if (building.getTags().containsKey("addr:housenumber") && building.getTags().containsKey("addr:street")){
                        String addrStreet = building.getTags().get("addr:street");
                        String addrHouseNumber = building.getTags().get("addr:housenumber");

                        return String.format("%s %s", addrStreet, addrHouseNumber);
                    } else {
                        return String.format("OSM ID %d", wayOsmId);
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Single<String> getUsername(OAuthConsumer consumer) {
        OsmConnection osm = new OsmConnection(API_URL, USER_AGENT, consumer);
        UserDao user = new UserDao(osm);

        return Single
                .fromCallable(() -> user.getMine().displayName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Single<Boolean> hasPermissions(List<String> permissions, OAuthConsumer consumer) {
        OsmConnection osm = new OsmConnection(API_URL, USER_AGENT, consumer);
        PermissionsDao permissionsDao = new PermissionsDao(osm);

        return Single
                .fromCallable(() -> permissionsDao.get())
                .map(grantedPerms -> {
                    for (String reqPerm : permissions) {
                        if (!grantedPerms.contains(reqPerm)) return false;
                    }

                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Assumes user was already authenticated
    public static Completable changeBuildingHeightTag(long wayOsmId, double height) {
        OsmConnection osm = new OsmConnection(API_URL, USER_AGENT, OsmAuth.getSavedConsumer());
        MapDataDao mapData = new MapDataDao(osm);


        return Completable
                .fromAction(() -> {
                    Way building = mapData.getWay(wayOsmId);
                    building.getTags().put("height", String.valueOf(height));
                    mapData.updateMap(new HashMap<>(), Collections.singletonList(building), null);
                })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
