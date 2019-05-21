package oss.technion.openstreetheight.model.server;

import com.rx2androidnetworking.Rx2AndroidNetworking;

import oss.technion.openstreetheight.model.server.message.HeightCalc;
import oss.technion.openstreetheight.model.server.message.CameraParams;
import oss.technion.openstreetheight.model.server.message.WriteHeight;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MyServer {
    private static String ENDPOINT_URL = "http://199.247.19.154:5050";


    public static Single<Double> getHeight(
            double focalLength,
            double sensorSize,

            double photoWidth,
            double photoHeight,

            double[][] points, // { {x1, y1}, {x2, y2}, ...}

            double footprintLeft,
            double footprintRight
    ) {
        HeightCalc.Input msg = new HeightCalc.Input(
                focalLength,
                sensorSize,
                new double[]{photoWidth, photoHeight},
                points,
                footprintLeft,
                footprintRight
        );

        return Rx2AndroidNetworking
                .post(ENDPOINT_URL + "/calc_height")
                .addApplicationJsonBody(msg)

                .build()
                .getObjectSingle(HeightCalc.Output.class)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .map(output -> output.buildingHeight);
    }

    public static Completable putHeightToOsm(long way_id, double height) {
        WriteHeight.Input msg = new WriteHeight.Input(way_id, height);

        return Completable.fromAction(() ->
                Rx2AndroidNetworking
                        .post(ENDPOINT_URL + "/write_height")
                        .addApplicationJsonBody(msg)
                        .build()
                        .executeForOkHttpResponse()
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Single<CameraParams.Output> getCameraParams(String model) {
        CameraParams.Input input = new CameraParams.Input(model);

        return Rx2AndroidNetworking
                .post(ENDPOINT_URL + "/camera_parameters")
                .addApplicationJsonBody(input)

                .build()
                .getObjectSingle(CameraParams.Output.class)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
