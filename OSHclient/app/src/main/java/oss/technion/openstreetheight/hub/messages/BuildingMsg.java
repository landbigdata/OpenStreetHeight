package oss.technion.openstreetheight.hub.messages;

import oss.technion.openstreetheight.hub.MessageHub.Message;

public class BuildingMsg implements Message {
    public final long osm_id;
    public final double leftSide, rightSide;

    public BuildingMsg(long osm_id, double leftSide, double rightSide) {
        this.osm_id = osm_id;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }
}
