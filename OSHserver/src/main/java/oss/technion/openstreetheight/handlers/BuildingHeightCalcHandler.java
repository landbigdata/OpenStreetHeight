package oss.technion.openstreetheight.handlers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import oss.technion.osh.server.algorithms.BuildingHeightCalc;
import ratpack.handling.Context;
import ratpack.jackson.Jackson;

import java.util.Map;

import static oss.technion.openstreetheight.handlers.utils.Doubles.unbox1d;
import static oss.technion.openstreetheight.handlers.utils.Doubles.unbox2d;
import static ratpack.jackson.Jackson.fromJson;


public class BuildingHeightCalcHandler {

    private static class HeightCalcInput {
        public final double focalLength;
        public final double sensorSize;
        public final double[] imageSize;
        public final double[][] points; // { {x1, x2, x3 ...}, {y1, y2, y3 ...} }
        public final double footprintLeft;
        public final double footprintRight;


        @JsonCreator
        public HeightCalcInput(Map<String, Object> props) {
            focalLength = (double) props.get("focalLength");
            sensorSize = (double) props.get("sensorSize");
            imageSize = unbox1d(props.get("imageSize"));
            points = unbox2d(props.get("points"));
            footprintLeft = (double) props.get("footprintLeft");
            footprintRight = (double) props.get("footprintRight");
        }

    }

    private static class HeightCalcOutput {
        public final double buildingHeight;

        private HeightCalcOutput(double buildingHeight) {
            this.buildingHeight = buildingHeight;
        }
    }


    // Force Jackson parse JSON arrays to Java arrays and not to lists
    private static ObjectMapper MY_OBJECT_MAPPER = new ObjectMapper()
                        .enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY);

    public static void handle(Context ctx) {
        ctx
                .parse(fromJson(HeightCalcInput.class, MY_OBJECT_MAPPER))
                .map(BuildingHeightCalcHandler::process)
                .map(Jackson::json)
                .then(ctx::render);
    }

    /**
     * @param args Outputs height of building
     */
    private static HeightCalcOutput process(HeightCalcInput args) throws MWException {
        BuildingHeightCalc heightFunc = new BuildingHeightCalc();


        Object[] output = heightFunc.culcH_main(1,
                args.footprintLeft,
                args.footprintRight,
                args.focalLength,
                args.sensorSize,
                new MWNumericArray(args.imageSize, MWClassID.DOUBLE),
                new MWNumericArray(args.points, MWClassID.DOUBLE)
        );

        heightFunc.dispose();

        double buildingHeight = ((MWNumericArray) output[0]).getDouble();

        return new HeightCalcOutput(buildingHeight);
    }
}
