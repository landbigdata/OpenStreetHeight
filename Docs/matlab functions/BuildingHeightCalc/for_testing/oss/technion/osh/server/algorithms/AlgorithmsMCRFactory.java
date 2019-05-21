/*
 * MATLAB Compiler: 6.6 (R2018a)
 * Date: Thu Mar 29 17:53:25 2018
 * Arguments: 
 * "-B""macro_default""-W""java:oss.technion.osh.server.algorithms,BuildingHeightCalc""-T""link:lib""-d""E:\\Projects\\OpenStreetHeight\\Stage 
 * 2\\osh-server\\Docs\\alex_new\\BuildingHeightCalc\\for_testing""class{BuildingHeightCalc:E:\\Projects\\OpenStreetHeight\\Stage 
 * 2\\osh-server\\Docs\\alex_new\\culcH_main.m}"
 */

package oss.technion.osh.server.algorithms;

import com.mathworks.toolbox.javabuilder.*;
import com.mathworks.toolbox.javabuilder.internal.*;

/**
 * <i>INTERNAL USE ONLY</i>
 */
public class AlgorithmsMCRFactory
{
   
    
    /** Component's uuid */
    private static final String sComponentId = "algorithms_CB1B6083EB4E08605847DB6BE72C6ADD";
    
    /** Component name */
    private static final String sComponentName = "algorithms";
    
   
    /** Pointer to default component options */
    private static final MWComponentOptions sDefaultComponentOptions = 
        new MWComponentOptions(
            MWCtfExtractLocation.EXTRACT_TO_CACHE, 
            new MWCtfClassLoaderSource(AlgorithmsMCRFactory.class)
        );
    
    
    private AlgorithmsMCRFactory()
    {
        // Never called.
    }
    
    public static MWMCR newInstance(MWComponentOptions componentOptions) throws MWException
    {
        if (null == componentOptions.getCtfSource()) {
            componentOptions = new MWComponentOptions(componentOptions);
            componentOptions.setCtfSource(sDefaultComponentOptions.getCtfSource());
        }
        return MWMCR.newInstance(
            componentOptions, 
            AlgorithmsMCRFactory.class, 
            sComponentName, 
            sComponentId,
            new int[]{9,4,0}
        );
    }
    
    public static MWMCR newInstance() throws MWException
    {
        return newInstance(sDefaultComponentOptions);
    }
}
