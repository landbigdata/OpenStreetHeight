import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import oss.technion.osh.server.algorithms.BuildingHeightCalc;

import static org.junit.Assert.assertThat;

public class MatlabTests {

    @Test
    // Tests whether culcH_main function returns proper result
    public void calcBuildingHeight() throws MWException {
        double result;

        BuildingHeightCalc heightCalc = new BuildingHeightCalc();
        double[] imageSize = {3024, 4032};
        double[][] points = { {429, 1513, 2401, 523, 1499, 2299}, {2772, 2840, 2794, 1245, 587, 1059} };

        Object[] output = heightCalc.culcH_main(1,
                14.48,                                           // footprint A
                10.43,                                           // footprint B
                0.0042,                                          // focal length
                0.0000014,                                       // sensor size
                new MWNumericArray(imageSize, MWClassID.DOUBLE), // image size
                new MWNumericArray(points, MWClassID.DOUBLE)     // image points
        );

        result = ((MWNumericArray) output[0]).getDouble();

        heightCalc.dispose();

        assertThat(result, is(15.75));
    }
}
