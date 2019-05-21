package oss.technion.openstreetheight.hub.messages;

import oss.technion.openstreetheight.hub.MessageHub.Message;


public class PhotoCornerPointsMsg implements Message {
    public final double[][] points; // { {x, y}, {x, y}, {x, y} ... }

    public PhotoCornerPointsMsg(double[][] points) {
        this.points = points;
    }
}
