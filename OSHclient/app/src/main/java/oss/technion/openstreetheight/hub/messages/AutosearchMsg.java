package oss.technion.openstreetheight.hub.messages;

import oss.technion.openstreetheight.hub.MessageHub;

public class AutosearchMsg implements MessageHub.Message {
    public final float pixelSize; // in metres
    public final float focalLength; // in metres


    public AutosearchMsg(float focalLength, float pixelSize) {
        this.pixelSize = pixelSize;
        this.focalLength = focalLength;
    }
}
