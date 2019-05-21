package oss.technion.openstreetheight.hub.messages;

import oss.technion.openstreetheight.hub.MessageHub.Message;


public class PhotoMsg implements Message {
    public final String path;

    public PhotoMsg(String path) {
        this.path = path;
    }
}
