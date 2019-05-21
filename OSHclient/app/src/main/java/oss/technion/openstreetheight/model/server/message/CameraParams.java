package oss.technion.openstreetheight.model.server.message;

public class CameraParams {
    public static class Input {
        public final String phoneModel;


        public Input(String phoneModel) {
            this.phoneModel = phoneModel;
        }
    }

    // -1 for missing value
    public static class Output {
        public final double focalLenght;
        public final double sensorSize;


        public Output(double focalLenght, double sensorSize) {
            this.focalLenght = focalLenght;
            this.sensorSize = sensorSize;
        }
    }


}
