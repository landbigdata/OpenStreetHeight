/*
 * MATLAB Compiler: 6.6 (R2018a)
 * Date: Thu Mar 29 17:53:25 2018
 * Arguments: 
 * "-B""macro_default""-W""java:oss.technion.osh.server.algorithms,BuildingHeightCalc""-T""link:lib""-d""E:\\Projects\\OpenStreetHeight\\Stage 
 * 2\\osh-server\\Docs\\alex_new\\BuildingHeightCalc\\for_testing""class{BuildingHeightCalc:E:\\Projects\\OpenStreetHeight\\Stage 
 * 2\\osh-server\\Docs\\alex_new\\culcH_main.m}"
 */

package oss.technion.osh.server.algorithms;

import com.mathworks.toolbox.javabuilder.pooling.Poolable;
import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The <code>BuildingHeightCalcRemote</code> class provides a Java RMI-compliant 
 * interface to MATLAB functions. The interface is compiled from the following files:
 * <pre>
 *  E:\\Projects\\OpenStreetHeight\\Stage 2\\osh-server\\Docs\\alex_new\\culcH_main.m
 * </pre>
 * The {@link #dispose} method <b>must</b> be called on a 
 * <code>BuildingHeightCalcRemote</code> instance when it is no longer needed to ensure 
 * that native resources allocated by this class are properly freed, and the server-side 
 * proxy is unexported.  (Failure to call dispose may result in server-side threads not 
 * being properly shut down, which often appears as a hang.)  
 *
 * This interface is designed to be used together with 
 * <code>com.mathworks.toolbox.javabuilder.remoting.RemoteProxy</code> to automatically 
 * generate RMI server proxy objects for instances of 
 * oss.technion.osh.server.algorithms.BuildingHeightCalc.
 */
public interface BuildingHeightCalcRemote extends Poolable
{
    /**
     * Provides the standard interface for calling the <code>culcH_main</code> MATLAB 
     * function with 6 input arguments.  
     *
     * Input arguments to standard interface methods may be passed as sub-classes of 
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of any 
     * supported Java type (i.e. scalars and multidimensional arrays of any numeric, 
     * boolean, or character type, or String). Arguments passed as Java types are 
     * converted to MATLAB arrays according to default conversion rules.
     *
     * All inputs to this method must implement either Serializable (pass-by-value) or 
     * Remote (pass-by-reference) as per the RMI specification.
     *
     * Documentation as provided by the author of the MATLAB function:
     * <pre>
     * % [ H_min ] =culcH_main(14.48,10.43,0.0042,1.4e-6,[3024,4032],image_points)
     * %%input :
     * %A B METER
     * %% focal length in meter
     * %% pixel size in meter
     * %% image points 2x6 mat 
     * %%imaga size [W,H]
     * </pre>
     *
     * @param nargout Number of outputs to return.
     * @param rhs The inputs to the MATLAB function.
     *
     * @return Array of length nargout containing the function outputs. Outputs are 
     * returned as sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>. 
     * Each output array should be freed by calling its <code>dispose()</code> method.
     *
     * @throws java.rmi.RemoteException An error has occurred during the function call or 
     * in communication with the server.
     */
    public Object[] culcH_main(int nargout, Object... rhs) throws RemoteException;
  
    /** 
     * Frees native resources associated with the remote server object 
     * @throws java.rmi.RemoteException An error has occurred during the function call or in communication with the server.
     */
    void dispose() throws RemoteException;
}
