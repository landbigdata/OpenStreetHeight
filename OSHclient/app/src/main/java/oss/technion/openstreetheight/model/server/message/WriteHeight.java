package oss.technion.openstreetheight.model.server.message;

public class WriteHeight {
    public static class Input {
        public final long wayOsmId;
        public final double height;

        public Input(long wayOsmId, double height) {
            this.wayOsmId = wayOsmId;
            this.height = height;
        }
    }
}