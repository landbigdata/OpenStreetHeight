package oss.technion.openstreetheight.model.server.message;

public class HeightCalc {
    public static class Input {
        public final double focalLength;
        public final double sensorSize;
        public final double[] imageSize;
        public final double[][] points;
        public final double footprintLeft;
        public final double footprintRight;


        public Input(
                double focalLength,
                double sensorSize,
                double[] imageSize,
                double[][] points,
                double footprintLeft,
                double footprintRight) {

            this.focalLength = focalLength;
            this.sensorSize = sensorSize;
            this.imageSize = imageSize;
            this.points = points;
            this.footprintLeft = footprintLeft;
            this.footprintRight = footprintRight;
        }
    }

    public static class Output {
        public final double buildingHeight;

        private Output(double buildingHeight) {
            this.buildingHeight = buildingHeight;
        }
    }
}
