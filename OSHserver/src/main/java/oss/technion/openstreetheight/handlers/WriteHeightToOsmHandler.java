package oss.technion.openstreetheight.handlers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.data.Way;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import ratpack.handling.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static ratpack.jackson.Jackson.fromJson;

public class WriteHeightToOsmHandler {
    private static String CONSUMER_KEY = "R8wMU2ezkp3ijpIcuAnKD4HSRCqfmf69NknPoSNA";
    private static String CONSUMER_SECRET = "40CZ42WcGZQ4PGBEqmi5lkUqrWGIh0xMZd6pZ1ap";

    private static String TOKEN = "7ajTLyWFhFsweFGOBbP7uMkRXs7wkAoNHpyBKRTF";
    private static String TOKEN_SECRET = "z0AzLoHa8OJYiLFfzrkRc8qva4qUKzHiio3p1pLY";

    private static String API_URL = "https://api.openstreetmap.org/api/0.6/";
    private static String USER_AGENT = "OpenStreetHeight Server";

    private static class WriteHeightInput {
        public final long wayOsmId;
        public final double height;



        @JsonCreator
        public WriteHeightInput(Map<String, Object> props) {
            wayOsmId = (long) props.get("wayOsmId");
            height = (double) props.get("height");
        }
    }

    // Force Jackson parse JSON arrays to Java arrays and not to lists
    private static ObjectMapper MY_OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)
            .enable(DeserializationFeature.USE_LONG_FOR_INTS);

    public static void handle(Context ctx) {
        ctx
                .parse(fromJson(WriteHeightInput.class, MY_OBJECT_MAPPER))
                .then(args -> {
                    process(args);
                    ctx.getResponse().send();
                });
    }

    private static void process(WriteHeightInput args) {
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

        // According to OSM docs, tokens never expire
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        OsmConnection osm = new OsmConnection(API_URL, USER_AGENT, consumer);

        MapDataDao mapData = new MapDataDao(osm);
        Way building = mapData.getWay(args.wayOsmId);

        building.getTags().put("height", String.valueOf(args.height));

        mapData.updateMap(new HashMap<>(), Collections.singletonList(building), null);
    }
}
