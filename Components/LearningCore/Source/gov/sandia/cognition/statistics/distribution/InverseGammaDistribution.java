/*
 * File:                InverseGammaDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 10, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothUnivariateDistribution;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import java.util.ArrayList;
import java.util.Random;

/**
 * Defines an inverse-gamma distribution.  That is, if X is drawn from IG(a,b),
 * then 1/X follows a Gamma(a,1.0/b) distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Inverse-gamma distribution",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Inverse-gamma_distribution"
)
public class InverseGammaDistribution 
    extends AbstractClosedFormSmoothUnivariateDistribution
{

    /**
     * Default shape, {@value}.
     */
    public static final double DEFAULT_SHAPE = 3.0;

    /**
     * Default scale, {@value}.
     */
    public static final double DEFAULT_SCALE = 1.0;

    /**
     * Shape parameter, must be greater than zero.
     */
    protected double shape;

    /**
     * Scale parameter, must be greater than zero.
     */
    protected double scale;

    /** 
     * Creates a new instance of InverseGammaDistribution 
     */
    public InverseGammaDistribution()
    {
        this( DEFAULT_SHAPE, DEFAULT_SCALE );
    }

    /**
     * Creates a new instance of InverseGammaDistribution
     * @param shape
     * Shape parameter, must be greater than zero.
     * @param scale
     * Scale parameter, must be greater than zero.
     */
    public InverseGammaDistribution(
        final double shape,
        final double scale)
    {
        this.shape = shape;
        this.scale = scale;
    }

    /**
     * Copy constructor
     * @param other
     * InverseGammaDistribution to copy
     */
    public InverseGammaDistribution(
        final InverseGammaDistribution other )
    {
        this( other.getShape(), other.getScale() );
    }

    @Override
    public InverseGammaDistribution clone()
    {
        return (InverseGammaDistribution) super.clone();
    }

    /**
     * Getter for shape
     * @return 
     * Shape parameter, must be greater than zero.
     */
    public double getShape()
    {
        return this.shape;
    }

    /**
     * Setter for shape
     * @param shape
     * Shape parameter, must be greater than zero.
     */
    public void setShape(
        final double shape)
    {
        if( shape <= 0.0 )
        {
            throw new IllegalArgumentException(
                "Shape must be > 0.0" );
        }
        this.shape = shape;
    }

    /**
     * Getter for scale
     * @return
     * Scale parameter, must be greater than zero.
     */
    public double getScale()
    {
        return this.scale;
    }

    /**
     * Setter for scale
     * @param scale the scale to set
     * Scale parameter, must be greater than zero.
     */
    public void setScale(
        final double scale)
    {
        if( scale <= 0.0 )
        {
            throw new IllegalArgumentException(
                "Scale must be > 0.0" );
        }
        this.scale = scale;
    }

    @Override
    public double sampleAsDouble(
        final Random random)
    {
        final double g = GammaDistribution.sampleAsDouble(
            this.shape, 1.0 / this.scale, random);
        return 1.0 / g;
    }
    
    @Override
    public void sampleInto(
        final Random random,
        final double[] output,
        final int start,
        final int length)
    {
        // Sample the requested number of gammas.
        GammaDistribution.sampleInto(this.shape, 1.0 / this.scale, 
            random, output, start, length);
        
        // Now invert the gammas.
        final int end = start + length;
        for (int i = start; i < end; i++)
        {
            output[i] = 1.0 / output[i];
        }
    }
    
    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.shape, this.scale );
    }

    @Override
    public void convertFromVector(
        Vector parameters)
    {
        parameters.assertDimensionalityEquals(2);
        this.setShape( parameters.getElement(0) );
        this.setScale( parameters.getElement(1) );
    }

    @Override
    public double getMeanAsDouble()
    {
        if( this.shape > 1.0 )
        {
            return this.scale / (this.shape - 1.0);
        }
        else
        {
            throw new IllegalArgumentException(
                "Shape must be > 1.0" );
        }
    }

    @Override
    public double getVariance()
    {
        if( this.shape > 2.0 )
        {
            final double am1 = this.shape - 1.0;
            final double am2 = this.shape - 2.0;
            return this.scale*this.scale / (am1*am1*am2);
        }
        else
        {
            throw new IllegalArgumentException(
                "Shape must be > 2.0" );
        }
    }

    @Override
    public InverseGammaDistribution.CDF getCDF()
    {
        return new InverseGammaDistribution.CDF( this );
    }

    @Override
    public InverseGammaDistribution.PDF getProbabilityFunction()
    {
        return new InverseGammaDistribution.PDF( this );
    }

    @Override
    public String toString()
    {
        return "Shape = " + this.getShape() + ", Scale = " + this.getScale();
    }

    @Override
    public Double getMinSupport()
    {
        return 0.0;
    }

    @Override
    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * CDF of the inverseRootFinder-gamma distribution.
     */
    public static class CDF
        extends InverseGammaDistribution
        implements SmoothCumulativeDistributionFunction
    {

        /**
         * Default constructor
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of InverseGammaDistribution
         * @param shape
         * Shape parameter, must be greater than zero.
         * @param scale
         * Scale parameter, must be greater than zero.
         */
        public CDF(
            final double shape,
            final double scale)
        {
            super( shape, scale );
        }

        /**
         * Copy constructor
         * @param other
         * InverseGammaDistribution to copy
         */
        public CDF(
            final InverseGammaDistribution other )
        {
            super( other );
        }

        @Override
        public Double evaluate(
            final Double input)
        {
            return this.evaluate(input.doubleValue());
        }
        
        @Override
        public double evaluateAsDouble(
            final Double input)
        {
            return this.evaluate(input.doubleValue());
        }

        @Override
        public double evaluate(
            final double input)
        {
            if( input > 0.0 )
            {
                return 1.0-GammaDistribution.CDF.evaluate(1.0/input, shape, 1.0/scale);
            }
            else
            {
                return 0.0;
            }
        }

        @Override
        public InverseGammaDistribution.CDF getCDF()
        {
            return this;
        }

        @Override
        public InverseGammaDistribution.PDF getDerivative()
        {
            return this.getProbabilityFunction();
        }

        @Override
        public Double differentiate(
            final Double input)
        {
            return this.getDerivative().evaluate(input);
        }

    }

    /**
     * PDF of the inverseRootFinder-Gamma distribution.
     */
    public static class PDF
        extends InverseGammaDistribution
        implements UnivariateProbabilityDensityFunction
    {

        /**
         * Default constructor
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of InverseGammaDistribution
         * @param shape
         * Shape parameter, must be greater than zero.
         * @param scale
         * Scale parameter, must be greater than zero.
         */
        public PDF(
            final double shape,
            final double scale)
        {
            super( shape, scale );
        }

        /**
         * Copy constructor
         * @param other
         * InverseGammaDistribution to copy
         */
        public PDF(
            final InverseGammaDistribution other )
        {
            super( other );
        }

        @Override
        public InverseGammaDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

        @Override
        public double logEvaluate(
            final Double input)
        {
            return this.logEvaluate((double) input);
        }

        @Override
        public double logEvaluate(
            final double input)
        {
            if( input > 0.0 )
            {
                double logSum = 0.0;
                logSum += this.shape * Math.log(this.scale);
                logSum -= MathUtil.logGammaFunction( this.shape );
                logSum -= (this.shape+1.0) * Math.log(input);
                logSum -= this.scale / input;
                return logSum;
            }
            else
            {
                return Math.log(0.0);
            }
        }

        @Override
        public Double evaluate(
            final Double input)
        {
            return this.evaluate( input.doubleValue() );
        }

        @Override
        public double evaluateAsDouble(
            final Double input)
        {
            return this.evaluate(input.doubleValue());
        }

        @Override
        public double evaluate(
            final double input)
        {
            return Math.exp(this.logEvaluate(input));
        }

    }

}
