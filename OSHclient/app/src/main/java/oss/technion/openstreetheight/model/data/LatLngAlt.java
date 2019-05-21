package oss.technion.openstreetheight.model.data;

public class LatLngAlt extends LatLng {
    public double alt;

    public LatLngAlt(double lat, double lon, double alt) {
        super(lat, lon);
        this.alt = alt;
    }
}
