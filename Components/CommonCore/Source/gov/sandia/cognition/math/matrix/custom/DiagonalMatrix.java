/*
 * File:                DiagonalMatrix.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2015, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.math.matrix.custom;

import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.Arrays;

/**
 * Diagonal matrices are a special case, but a rather common one with very quick
 * and simple solutions to multiplications, inverses, etc. This can't magically
 * change type, however. So if you will ever add an off-diagonal element, don't
 * use this!
 *
 * NOTE: This only supports square diagonal matrices.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
public class DiagonalMatrix
    extends BaseMatrix
{

    /**
     * The elements down the diagonal
     */
    private double[] diagonal;

    /**
     * Creates a square (n x n) diagonal matrix initialized to zero.
     *
     * @param n The dimensions (num rows and num columns) of the matrix
     */
    public DiagonalMatrix(
        final int n)
    {
        diagonal = new double[n];
        Arrays.fill(diagonal, 0);
    }

    /**
     * Copy constructor. Creates a deep copy of d.
     *
     * @param d The diagonal matrix to copy
     */
    public DiagonalMatrix(
        final DiagonalMatrix d)
    {
        diagonal = Arrays.copyOf(d.diagonal, d.diagonal.length);
    }

    /**
     * Creates a diagonal matrix as a copy of the given matrix. It must be
     * a diagonal one as well.
     * 
     * @param m The matrix to copy.
     */
    public DiagonalMatrix(
        final Matrix m)
    {
        if (m.getNumRows() != m.getNumColumns())
        {
            throw new IllegalArgumentException("Unable to copy a non-square "
                + "matrix into a diagonal matrix.");
        }

        diagonal = new double[m.getNumRows()];
        final int numRows = this.getNumRows();
        final int numColumns = this.getNumColumns();
        for (int i = 0; i < numRows; ++i)
        {
            for (int j = 0; j < numColumns; ++j)
            {
                if (i == j)
                {
                    diagonal[i] = m.get(i, i);
                }
                else if (m.get(i, j) != 0)
                {
                    throw new IllegalArgumentException("Unable to copy the "
                        + "input to a diagonal matrix as the element at " + i
                        + ", " + j + " is non-zero (" + m.get(i, j) + ")");
                }
            }
        }
    }

    /**
     * Package-private helper that saves a bit of time by not initializing the
     * values down the diagonal. It is assumed that any place that calls this
     * will immediately initialize the values along the diagonal to their
     * correct values.
     *
     * @param n The dimensions (num rows and num columns) of the matrix
     * @param unused Only here to differentiate the signature from the other
     * constructor
     */
    DiagonalMatrix(
        final int n,
        final boolean unused)
    {
        diagonal = new double[n];
        // Don't initialize
    }

    /**
     * Creates a square (diagonal.length x diagonal.length) diagonal matrix,
     * initialized to the input values (makes a deep copy).
     *
     * @param diagonal The initial values for the diagonal (implicitly defines
     * the size of the matrix, too!).
     */
    DiagonalMatrix(
        final double[] diagonal)
    {
        this.diagonal = Arrays.copyOf(diagonal, diagonal.length);
    }

    /**
     * This should never be called by anything or anyone other than Java's
     * serialization code.
     */
    protected DiagonalMatrix()
    {
        // NOTE: This doesn't initialize anything
    }

    @Override
    final public Matrix clone()
    {
        return new DiagonalMatrix(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    public void scaledPlusEquals(
        final SparseMatrix other,
        final double scaleFactor)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal elements are 0 (as well as subtracting along the diagonal)
        if (!other.isCompressed())
        {
            other.compress();
        }
        int rowNum = 0;
        final double[] otherValues = other.getValues();
        for (int i = 0; i < otherValues.length; ++i)
        {
            while (i >= other.getFirstInRows()[rowNum + 1])
            {
                ++rowNum;
            }
            if (otherValues[i] == 0)
            {
                continue;
            }
            if (other.getColumnIndices()[i] != rowNum)
            {
                throw new IllegalArgumentException("Unable to store the "
                    + " difference of a non-diagonal sparse matrix with a "
                    + "diagonal matrix in the diagonal matrix");
            }
            diagonal[rowNum] += otherValues[i] * scaleFactor;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    public void scaledPlusEquals(
        final DenseMatrix other,
        final double scaleFactor)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal are 0 (as well as summing along the diagonal)
        for (int i = 0; i < diagonal.length; ++i)
        {
            for (int j = 0; j < diagonal.length; ++j)
            {
                if (i == j)
                {
                    diagonal[i] += other.row(i).values[i] * scaleFactor;
                }
                else if (other.row(i).values[j] != 0)
                {
                    throw new IllegalArgumentException("Unable to store the "
                        + "sum of a non-diagonal dense matrix with a "
                        + "diagonal matrix in the diagonal matrix.");
                }
            }
        }
    }

    @Override
    public void scaledPlusEquals(
        final DiagonalMatrix other,
        final double scaleFactor)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diagonal.length; ++i)
        {
            diagonal[i] += other.diagonal[i] * scaleFactor;
        }
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    public final void plusEquals(
        final SparseMatrix other)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal elements are 0 (as well as subtracting along the diagonal)
        if (!other.isCompressed())
        {
            other.compress();
        }
        int rowNum = 0;
        final double[] otherValues = other.getValues();
        for (int i = 0; i < otherValues.length; ++i)
        {
            while (i >= other.getFirstInRows()[rowNum + 1])
            {
                ++rowNum;
            }
            if (otherValues[i] == 0)
            {
                continue;
            }
            if (other.getColumnIndices()[i] != rowNum)
            {
                throw new IllegalArgumentException("Unable to store the "
                    + " difference of a non-diagonal sparse matrix with a "
                    + "diagonal matrix in the diagonal matrix");
            }
            diagonal[rowNum] += otherValues[i];
        }
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    public final void plusEquals(
        final DenseMatrix other)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal are 0 (as well as summing along the diagonal)
        for (int i = 0; i < diagonal.length; ++i)
        {
            for (int j = 0; j < diagonal.length; ++j)
            {
                if (i == j)
                {
                    diagonal[i] += other.row(i).values[i];
                }
                else if (other.row(i).values[j] != 0)
                {
                    throw new IllegalArgumentException("Unable to store the "
                        + "sum of a non-diagonal dense matrix with a "
                        + "diagonal matrix in the diagonal matrix.");
                }
            }
        }
    }

    @Override
    public final void plusEquals(
        final DiagonalMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diagonal.length; ++i)
        {
            diagonal[i] += other.diagonal[i];
        }
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    public final void minusEquals(
        final SparseMatrix other)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal elements are 0 (as well as subtracting along the diagonal)
        if (!other.isCompressed())
        {
            other.compress();
        }
        int rowNum = 0;
        final double[] otherValues = other.getValues();
        for (int i = 0; i < otherValues.length; ++i)
        {
            while (i >= other.getFirstInRows()[rowNum + 1])
            {
                ++rowNum;
            }
            if (otherValues[i] == 0)
            {
                continue;
            }
            if (other.getColumnIndices()[i] != rowNum)
            {
                throw new IllegalArgumentException("Unable to store the "
                    + " difference of a non-diagonal sparse matrix with a "
                    + "diagonal matrix in the diagonal matrix");
            }
            diagonal[rowNum] -= otherValues[i];
        }
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    public final void minusEquals(
        final DenseMatrix other)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal are 0 (as well as subtracting along the diagonal)
        for (int i = 0; i < diagonal.length; ++i)
        {
            for (int j = 0; j < diagonal.length; ++j)
            {
                if (i == j)
                {
                    diagonal[i] -= other.row(i).values[i];
                }
                else if (other.row(i).values[j] != 0)
                {
                    throw new IllegalArgumentException("Unable to store the "
                        + "difference of a non-diagonal dense matrix with a "
                        + "diagonal matrix in the diagonal matrix.");
                }
            }
        }
    }
    
    @Override
    public final void minusEquals(
        final DiagonalMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diagonal.length; ++i)
        {
            diagonal[i] -= other.diagonal[i];
        }
    }

    @Override
    public final void dotTimesEquals(
        final SparseMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diagonal.length; ++i)
        {
            diagonal[i] *= other.get(i, i);
        }
    }

    @Override
    public final void dotTimesEquals(
        final DenseMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diagonal.length; ++i)
        {
            diagonal[i] *= other.row(i).values[i];
        }
    }

    @Override
    public final void dotTimesEquals(
        final DiagonalMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diagonal.length; ++i)
        {
            diagonal[i] *= other.diagonal[i];
        }
    }

    @Override
    public final Matrix times(
        final SparseMatrix other)
    {
        return other.preTimes(this);
    }

    @Override
    public final Matrix times(
        final DenseMatrix other)
    {
        this.assertMultiplicationDimensions(other);
        
        final DenseVector[] rows = new DenseVector[diagonal.length];
        for (int i = 0; i < diagonal.length; ++i)
        {
            DenseVector v = other.row(i);
            rows[i] = (DenseVector) v.scale(diagonal[i]);
        }
        return new DenseMatrix(rows);
    }

    @Override
    public final Matrix times(
        final DiagonalMatrix other)
    {
        this.assertMultiplicationDimensions(other);
        DiagonalMatrix result = new DiagonalMatrix(this);
        for (int i = 0; i < diagonal.length; ++i)
        {
            result.diagonal[i] *= other.diagonal[i];
        }

        return result;
    }

    @Override
    public final Vector times(
        final SparseVector vector)
    {
        vector.assertDimensionalityEquals(this.getNumColumns());
        SparseVector result = new SparseVector(diagonal.length);
        vector.compress();
        int[] locs = vector.getIndices();
        for (int i = 0; i < locs.length; ++i)
        {
            result.setElement(locs[i], vector.getValues()[i] * diagonal[locs[i]]);
        }

        return result;
    }
    
    @Override
    public final Vector times(
        final DenseVector vector)
    {
        vector.assertDimensionalityEquals(this.getNumColumns());
        DenseVector result = new DenseVector(diagonal.length);
        for (int i = 0; i < diagonal.length; ++i)
        {
            result.setElement(i, vector.get(i) * diagonal[i]);
        }

        return result;
    }

    @Override
    final public void scaleEquals(
        final double scaleFactor)
    {
        for (int i = 0; i < diagonal.length; ++i)
        {
            diagonal[i] *= scaleFactor;
        }
    }

    @Override
    final public int getNumRows()
    {
        return diagonal.length;
    }

    @Override
    final public int getNumColumns()
    {
        return diagonal.length;
    }

    /**
     * Helper that makes sure the input row index and column index are within
     * the bounds of matrix. This does not ensure the inputs are on the diagonal
     * as there are valid reasons to get elements off the main diagonal.
     *
     * @param rowIndex The row index
     * @param columnIndex The column index
     * @throws ArrayIndexOutOfBoundsException if the input values are outside
     * the matrix
     */
    private void checkBounds(
        final int rowIndex,
        final int columnIndex)
    {
        if (rowIndex < 0 || columnIndex < 0 || rowIndex >= diagonal.length
            || columnIndex
            >= diagonal.length)
        {
            throw new ArrayIndexOutOfBoundsException("Input index (" + rowIndex
                + ", " + columnIndex + ") is not within this " + diagonal.length
                + "x" + diagonal.length + " matrix");
        }
    }

    @Override
    public double get(
        final int rowIndex,
        final int columnIndex)
    {
        checkBounds(rowIndex, columnIndex);
        if (rowIndex == columnIndex)
        {
            return diagonal[rowIndex];
        }
        return 0.0;
    }

    /**
     * Returns the value stored at the input locations
     *
     * @param rowIndex The row index
     * @param columnIndex The column index
     * @return the value stored at the input locations
     * @throws ArrayIndexOutOfBoundsException if the input values are outside
     * the matrix
     */
    @Override
    final public double getElement(
        final int rowIndex,
        final int columnIndex)
    {
        checkBounds(rowIndex, columnIndex);
        if (rowIndex == columnIndex)
        {
            return diagonal[rowIndex];
        }
        return 0.0;
    }

    @Override
    public void set(
        final int rowIndex,
        final int columnIndex,
        final double value)
    {
        setElement(rowIndex, columnIndex, value);
    }

    /**
     * Set the value stored at the input locations to the input value
     *
     * @param rowIndex The row index
     * @param columnIndex The column index
     * @param value The new value for the input location
     * @throws ArrayIndexOutOfBoundsException if the input indices are outside
     * the matrix
     * @throws IllegalArgumentException if the input indices attempt to set a
     * non-zero value off the main axis
     */
    @Override
    final public void setElement(
        final int rowIndex,
        final int columnIndex,
        final double value)
    {
        checkBounds(rowIndex, columnIndex);
        if (rowIndex == columnIndex)
        {
            diagonal[rowIndex] = value;
        }
        else if (value != 0)
        {
            throw new IllegalArgumentException(
                "Unable to set an off-axis value in "
                + "a diagonal matrix");
        }
    }

    @Override
    final public Matrix getSubMatrix(
        final int minRow,
        final int maxRow,
        final int minColumn,
        final int maxColumn)
    {
        checkSubmatrixRange(minRow, maxRow, minColumn, maxColumn);
        SparseMatrix result = new SparseMatrix(maxRow - minRow + 1, maxColumn
            - minColumn + 1);
        // You only need worry about the diagonal, so one of the extents will do
        for (int i = minRow; i <= maxRow; ++i)
        {
            // Check to make sure that this element of the diagonal is also in
            // the other extents
            if (i >= minColumn && i <= maxColumn)
            {
                // If it is, add it at the right place in the output
                result.setElement(i - minRow, i - minColumn, get(i, i));
            }
        }
        return result;
    }

    @Override
    final public boolean isSymmetric(
        final double effectiveZero)
    {
        ArgumentChecker.assertIsNonNegative("effectiveZero", effectiveZero);
        return true;
    }

    @Override
    final public Matrix transpose()
    {
        return new DiagonalMatrix(this);
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException if this doesn't span the space, so
     * can't be inverted.
     * 
     * @return {@inheritDoc}
     */
    @Override
    final public Matrix inverse()
    {
        DiagonalMatrix result = new DiagonalMatrix(diagonal.length, true);
        for (int i = 0; i < diagonal.length; ++i)
        {
            if (diagonal[i] == 0)
            {
                throw new UnsupportedOperationException("Can't invert matrix "
                    + "because it does not span the columns");
            }
            result.diagonal[i] = 1.0 / diagonal[i];
        }

        return result;
    }

    @Override
    final public Matrix pseudoInverse(
        final double effectiveZero)
    {
        ArgumentChecker.assertIsNonNegative("effectiveZero", effectiveZero);
        DiagonalMatrix result = new DiagonalMatrix(diagonal.length, true);
        for (int i = 0; i < diagonal.length; ++i)
        {
            result.diagonal[i] = (Math.abs(diagonal[i]) > effectiveZero) ? 1.0 / diagonal[i]
                : 0;
        }

        return result;
    }

    @Override
    final public ComplexNumber logDeterminant()
    {
        // This can never fail in a diagonal matrix
        // if (!isSquare())
        // {
        // throw new IllegalArgumentException("Matrix must be square");
        // }

        // This is a diagonal matrix (which can be considered upper triangular).
        // NOTE: The determinant of a triangular matrix is the product of the
        // diagonal entries (see 
        // http://en.wikipedia.org/wiki/Triangular_matrix#Special_properties)
        // NOTE: The logarithm of the product of two numbers is the sum of the
        // logarightm of each of the two numbers (see
        // http://en.wikipedia.org/wiki/Logarithm)
        // 
        // The diagonal elements will be REAL, but they may be negative.
        // The logarithm of a negative number is the logarithm of the absolute
        // value of the number, with an imaginary part of PI.  The exponential
        // is all that matters, so the log-determinant is equivalent, MODULO
        // PI (3.14...), so we just toggle this sign bit.
        int sign = 1;
        double logsum = 0.0;
        for (int i = 0; i < diagonal.length; i++)
        {
            double eigenvalue = diagonal[i];
            if (eigenvalue < 0.0)
            {
                sign = -sign;
                logsum += Math.log(-eigenvalue);
            }
            else
            {
                logsum += Math.log(eigenvalue);
            }

        }

        return new ComplexNumber(logsum, (sign < 0) ? Math.PI : 0.0);
    }

    @Override
    final public int rank(
        final double effectiveZero)
    {
        ArgumentChecker.assertIsNonNegative("effectiveZero", effectiveZero);
        int rank = 0;
        for (int i = 0; i < diagonal.length; ++i)
        {
            if (Math.abs(diagonal[i]) > effectiveZero)
            {
                ++rank;
            }
        }

        return rank;
    }

    @Override
    public double normFrobeniusSquared()
    {
        double result = 0;
        for (int i = 0; i < diagonal.length; ++i)
        {
            result += diagonal[i] * diagonal[i];
        }
        return result;
    }

    @Override
    final public double normFrobenius()
    {
        return Math.sqrt(normFrobeniusSquared());
    }

    @Override
    final public boolean isSquare()
    {
        return true;
    }

    @Override
    final public Matrix solve(
        final Matrix B)
    {
        checkSolveDimensions(B);
        Matrix result = B.clone();
        final int numColumns = B.getNumColumns();
        for (int i = 0; i < diagonal.length; ++i)
        {
            for (int j = 0; j < numColumns; ++j)
            {
                if (diagonal[i] == 0)
                {
                    throw new UnsupportedOperationException("Can't invert "
                        + "matrix because it does not span the columns");
                }
                else
                {
                    result.setElement(i, j, result.get(i, j) / diagonal[i]);
                }
            }
        }

        return result;
    }

    @Override
    final public Vector solve(
        final Vector b)
    {
        checkSolveDimensions(b);
        Vector result = b.clone();
        for (int i = 0; i < diagonal.length; ++i)
        {
            if (diagonal[i] == 0)
            {
                if (result.get(i) != 0)
                {
                    throw new UnsupportedOperationException("Unable to solve "
                        + "Ax=b because b spans different space than A");
                }
            }
            else
            {
                result.setElement(i, result.get(i) / diagonal[i]);
            }
        }

        return result;
    }

    @Override
    final public void identity()
    {
        for (int i = 0; i < diagonal.length; ++i)
        {
            diagonal[i] = 1;
        }
    }

    @Override
    final public Vector getColumn(
        final int columnIndex)
    {
        if (columnIndex < 0 || columnIndex >= diagonal.length)
        {
            throw new ArrayIndexOutOfBoundsException("Input column index ("
                + columnIndex + ") is not within this " + diagonal.length + "x"
                + diagonal.length + " matrix");
        }
        SparseVector result = new SparseVector(diagonal.length);
        result.setElement(columnIndex, diagonal[columnIndex]);

        return result;
    }

    @Override
    final public Vector getRow(
        final int rowIndex)
    {
        if (rowIndex < 0 || rowIndex >= diagonal.length)
        {
            throw new ArrayIndexOutOfBoundsException("Input row index ("
                + rowIndex + ") is not within this " + diagonal.length + "x"
                + diagonal.length + " matrix");
        }
        SparseVector result = new SparseVector(diagonal.length);
        result.setElement(rowIndex, diagonal[rowIndex]);

        return result;
    }

    /**
     * {@inheritDoc}
     * @param parameters {@inheritDoc}
     * @throws IllegalArgumentException if input vector doesn't have enough
     * elements to cover all elements of this or if the input vector specifies
     * non-zero values in off-diagonal elements.
     */
    @Override
    final public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertDimensionalityEquals(this.getNumRows() * getNumColumns());

        final int numRows = this.getNumRows();
        final int numColumns = this.getNumColumns();
        for (int i = 0; i < numRows; ++i)
        {
            for (int j = 0; j < numColumns; ++j)
            {
                if (i == j)
                {
                    diagonal[i] = parameters.get(i * numColumns + j);
                }
                // this checks that all off-diagonal elements are zero
                else if (parameters.get(i * numColumns + j) != 0)
                {
                    throw new IllegalArgumentException("Cannot convert "
                        + "diagonal matrix from vector with non-zero element "
                        + "that maps to off-diagonal location at " + i
                        + ", " + j);
                }
            }
        }
    }

    @Override
    final public Vector convertToVector()
    {
        final int numColumns = this.getNumColumns();
        SparseVector result = new SparseVector(numColumns * numColumns);
        for (int i = 0; i < diagonal.length; ++i)
        {
            result.setElement(i * numColumns + i, diagonal[i]);
        }
        return result;
    }

    @Override
    public final Vector preTimes(
        final SparseVector vector)
    {
        // Only true for diagonal (and symmetric) matrices: pre-mult vector is the same as post-mult
        // vector
        return times(vector);
    }

    @Override
    public final Vector preTimes(
        final DenseVector vector)
    {
        // Only true for diagonal (and symmetric) matrices: pre-mult vector is the same as post-mult
        // vector
        return times(vector);
    }

    @Override
    public boolean isSparse()
    {
        return true;
    }

    @Override
    public MatrixFactory<?> getMatrixFactory()
    {
        return CustomDiagonalMatrixFactory.INSTANCE;
    }

    @Override
    public int getEntryCount()
    {
        return this.diagonal.length;
    }
    
}
