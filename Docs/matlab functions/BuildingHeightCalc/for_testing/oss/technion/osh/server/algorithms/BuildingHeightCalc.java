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
import java.util.*;

/**
 * The <code>BuildingHeightCalc</code> class provides a Java interface to MATLAB functions. 
 * The interface is compiled from the following files:
 * <pre>
 *  E:\\Projects\\OpenStreetHeight\\Stage 2\\osh-server\\Docs\\alex_new\\culcH_main.m
 * </pre>
 * The {@link #dispose} method <b>must</b> be called on a <code>BuildingHeightCalc</code> 
 * instance when it is no longer needed to ensure that native resources allocated by this 
 * class are properly freed.
 * @version 0.0
 */
public class BuildingHeightCalc extends MWComponentInstance<BuildingHeightCalc>
{
    /**
     * Tracks all instances of this class to ensure their dispose method is
     * called on shutdown.
     */
    private static final Set<Disposable> sInstances = new HashSet<Disposable>();

    /**
     * Maintains information used in calling the <code>culcH_main</code> MATLAB function.
     */
    private static final MWFunctionSignature sCulcH_mainSignature =
        new MWFunctionSignature(/* max outputs = */ 1,
                                /* has varargout = */ false,
                                /* function name = */ "culcH_main",
                                /* max inputs = */ 6,
                                /* has varargin = */ false);

    /**
     * Shared initialization implementation - private
     * @throws MWException An error has occurred during the function call.
     */
    private BuildingHeightCalc (final MWMCR mcr) throws MWException
    {
        super(mcr);
        // add this to sInstances
        synchronized(BuildingHeightCalc.class) {
            sInstances.add(this);
        }
    }

    /**
     * Constructs a new instance of the <code>BuildingHeightCalc</code> class.
     * @throws MWException An error has occurred during the function call.
     */
    public BuildingHeightCalc() throws MWException
    {
        this(AlgorithmsMCRFactory.newInstance());
    }
    
    private static MWComponentOptions getPathToComponentOptions(String path)
    {
        MWComponentOptions options = new MWComponentOptions(new MWCtfExtractLocation(path),
                                                            new MWCtfDirectorySource(path));
        return options;
    }
    
    /**
     * @deprecated Please use the constructor {@link #BuildingHeightCalc(MWComponentOptions componentOptions)}.
     * The <code>com.mathworks.toolbox.javabuilder.MWComponentOptions</code> class provides an API to set the
     * path to the component.
     * @param pathToComponent Path to component directory.
     * @throws MWException An error has occurred during the function call.
     */
    public BuildingHeightCalc(String pathToComponent) throws MWException
    {
        this(AlgorithmsMCRFactory.newInstance(getPathToComponentOptions(pathToComponent)));
    }
    
    /**
     * Constructs a new instance of the <code>BuildingHeightCalc</code> class. Use this 
     * constructor to specify the options required to instantiate this component.  The 
     * options will be specific to the instance of this component being created.
     * @param componentOptions Options specific to the component.
     * @throws MWException An error has occurred during the function call.
     */
    public BuildingHeightCalc(MWComponentOptions componentOptions) throws MWException
    {
        this(AlgorithmsMCRFactory.newInstance(componentOptions));
    }
    
    /** Frees native resources associated with this object */
    public void dispose()
    {
        try {
            super.dispose();
        } finally {
            synchronized(BuildingHeightCalc.class) {
                sInstances.remove(this);
            }
        }
    }
  
    /**
     * Invokes the first MATLAB function specified to MCC, with any arguments given on
     * the command line, and prints the result.
     *
     * @param args arguments to the function
     */
    public static void main (String[] args)
    {
        try {
            MWMCR mcr = AlgorithmsMCRFactory.newInstance();
            mcr.runMain( sCulcH_mainSignature, args);
            mcr.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    /**
     * Calls dispose method for each outstanding instance of this class.
     */
    public static void disposeAllInstances()
    {
        synchronized(BuildingHeightCalc.class) {
            for (Disposable i : sInstances) i.dispose();
            sInstances.clear();
        }
    }

    /**
     * Provides the interface for calling the <code>culcH_main</code> MATLAB function 
     * where the first argument, an instance of List, receives the output of the MATLAB function and
     * the second argument, also an instance of List, provides the input to the MATLAB function.
     * <p>
     * Description as provided by the author of the MATLAB function:
     * </p>
     * <pre>
     * % [ H_min ] =culcH_main(14.48,10.43,0.0042,1.4e-6,[3024,4032],image_points)
     * %%input :
     * %A B METER
     * %% focal length in meter
     * %% pixel size in meter
     * %% image points 2x6 mat 
     * %%imaga size [W,H]
     * </pre>
     * @param lhs List in which to return outputs. Number of outputs (nargout) is
     * determined by allocated size of this List. Outputs are returned as
     * sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>.
     * Each output array should be freed by calling its <code>dispose()</code>
     * method.
     *
     * @param rhs List containing inputs. Number of inputs (nargin) is determined
     * by the allocated size of this List. Input arguments may be passed as
     * sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or
     * as arrays of any supported Java type. Arguments passed as Java types are
     * converted to MATLAB arrays according to default conversion rules.
     * @throws MWException An error has occurred during the function call.
     */
    public void culcH_main(List lhs, List rhs) throws MWException
    {
        fMCR.invoke(lhs, rhs, sCulcH_mainSignature);
    }

    /**
     * Provides the interface for calling the <code>culcH_main</code> MATLAB function 
     * where the first argument, an Object array, receives the output of the MATLAB function and
     * the second argument, also an Object array, provides the input to the MATLAB function.
     * <p>
     * Description as provided by the author of the MATLAB function:
     * </p>
     * <pre>
     * % [ H_min ] =culcH_main(14.48,10.43,0.0042,1.4e-6,[3024,4032],image_points)
     * %%input :
     * %A B METER
     * %% focal length in meter
     * %% pixel size in meter
     * %% image points 2x6 mat 
     * %%imaga size [W,H]
     * </pre>
     * @param lhs array in which to return outputs. Number of outputs (nargout)
     * is determined by allocated size of this array. Outputs are returned as
     * sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>.
     * Each output array should be freed by calling its <code>dispose()</code>
     * method.
     *
     * @param rhs array containing inputs. Number of inputs (nargin) is
     * determined by the allocated size of this array. Input arguments may be
     * passed as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of
     * any supported Java type. Arguments passed as Java types are converted to
     * MATLAB arrays according to default conversion rules.
     * @throws MWException An error has occurred during the function call.
     */
    public void culcH_main(Object[] lhs, Object[] rhs) throws MWException
    {
        fMCR.invoke(Arrays.asList(lhs), Arrays.asList(rhs), sCulcH_mainSignature);
    }

    /**
     * Provides the standard interface for calling the <code>culcH_main</code> MATLAB function with 
     * 6 comma-separated input arguments.
     * Input arguments may be passed as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of
     * any supported Java type. Arguments passed as Java types are converted to
     * MATLAB arrays according to default conversion rules.
     *
     * <p>
     * Description as provided by the author of the MATLAB function:
     * </p>
     * <pre>
     * % [ H_min ] =culcH_main(14.48,10.43,0.0042,1.4e-6,[3024,4032],image_points)
     * %%input :
     * %A B METER
     * %% focal length in meter
     * %% pixel size in meter
     * %% image points 2x6 mat 
     * %%imaga size [W,H]
     * </pre>
     * @param nargout Number of outputs to return.
     * @param rhs The inputs to the MATLAB function.
     * @return Array of length nargout containing the function outputs. Outputs
     * are returned as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>. Each output array
     * should be freed by calling its <code>dispose()</code> method.
     * @throws MWException An error has occurred during the function call.
     */
    public Object[] culcH_main(int nargout, Object... rhs) throws MWException
    {
        Object[] lhs = new Object[nargout];
        fMCR.invoke(Arrays.asList(lhs), 
                    MWMCR.getRhsCompat(rhs, sCulcH_mainSignature), 
                    sCulcH_mainSignature);
        return lhs;
    }
}
