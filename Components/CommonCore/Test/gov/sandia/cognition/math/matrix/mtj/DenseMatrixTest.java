/*
 * File:                DenseMatrixTest.java
 * Authors:             Kevin Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 27, 2006, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.MatrixTestHarness;
import gov.sandia.cognition.math.matrix.Matrix;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit tests for class DenseMatrix
 * @author Kevin R. Dixon
 */
public class DenseMatrixTest extends MatrixTestHarness
{
    
    public DenseMatrixTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(DenseMatrixTest.class);
        
        return suite;
    }

    protected DenseMatrix createMatrix(int numRows, int numColumns)
    {
        return new DenseMatrix( numRows, numColumns );
    }

    protected Matrix createCopy(Matrix matrix)
    {
        return new DenseMatrix( matrix );
    } 
    
    public void testCreateMatrixWithOverflow()
    {        
        // A test that is unique to dense matrices
        System.out.println( 
            "creating a matrix with numRows * numColumns > Integer.MAX_VALUE");
        try
        {
            Matrix m1 = this.createMatrix(Integer.MAX_VALUE, 2);
            fail("Should have thrown an ArithmeticExcpetion due to overflow");
        }
        catch (Exception e)
        {
        }        
    }

    public void testIsSparse()
    {
        Matrix m1 = this.createRandom();
        assertFalse(m1.isSparse());
        assertFalse(m1.isSparse());
        assertFalse(this.createRandom().isSparse());
        assertFalse(this.createMatrix(m1.getNumRows(), m1.getNumColumns()).isSparse());
    }
}
