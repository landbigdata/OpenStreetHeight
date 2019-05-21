package oss.technion.openstreetheight.model.data;

import java.io.Serializable;

public class LatLng implements Serializable {
        public final double lat;
        public final double lon;

        public LatLng(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        // Uses haversine formula
        // Returns result in meters
        public double distanceTo(LatLng dest)
        {
            double earthRadius = 3958.75;
            double latDiff = Math.toRadians(dest.lat - this.lat);
            double lngDiff = Math.toRadians(dest.lon - this.lon);
            double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                    Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(dest.lat)) *
                            Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = earthRadius * c;

            int meterConversion = 1609;

            return distance * meterConversion;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LatLng) {
                LatLng to = (LatLng) obj;

                return lat == to.lat && lon == to.lon;
            } else {
                return false;
            }
        }

    }