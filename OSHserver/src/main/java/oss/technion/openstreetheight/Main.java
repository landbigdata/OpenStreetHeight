package oss.technion.openstreetheight;

import oss.technion.openstreetheight.handlers.BuildingHeightCalcHandler;
import ratpack.server.RatpackServer;

public class Main {

    // All calls are POST and wrapped in JSON
    public static void main(String... args) throws Exception {
        RatpackServer.start(server -> server
                .handlers(chain -> chain
                        .get(ctx -> ctx.render("Hello World!"))
                        .post("calc_height", BuildingHeightCalcHandler::handle)

                )
        );
    }


}
