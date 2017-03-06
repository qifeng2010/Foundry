/*
 * File:                DirichletDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 14, 2009, Sandia Corporation.
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
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * The Dirichlet distribution is the multivariate generalization of the beta
 * distribution.  It describes the belief that the probabilities of K
 * mutually exclusive events "x_i" have been observed "a_i -1" times.  The
 * Dirichlet distribution is the conjugate prior of the multinomial
 * distribution.
 * 
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Dirichlet distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Dirichlet_distribution"
)
public class DirichletDistribution
    extends AbstractDistribution<Vector>
    implements ClosedFormComputableDistribution<Vector>
{

    /**
     * Parameters of the Dirichlet distribution, must be at least 2-dimensional
     * and each element must be positive.
     */
    protected Vector parameters;

    /** 
     * Creates a new instance of DirichletDistribution 
     */
    public DirichletDistribution()
    {
        this( 2 );
    }

    /**
     * Creates a new instance of DirichletDistribution
     * @param dimensionality
     * Dimensionality of the distribution
     */
    public DirichletDistribution(
        final int dimensionality )
    {
        this( VectorFactory.getDefault().createVector(dimensionality,1.0) );
    }

    /**
     * Creates a new instance of DirichletDistribution
     * @param parameters
     * Parameters of the Dirichlet distribution, must be at least 2-dimensional
     * and each element must be positive.
     *
     */
    public DirichletDistribution(
        final Vector parameters )
    {
        this.setParameters(parameters);
    }

    /**
     * Copy Constructor.
     * @param other
     * DirichletDistribution to copy.
     */
    public DirichletDistribution(
        final DirichletDistribution other )
    {
        this( ObjectUtil.cloneSafe( other.getParameters() ) );
    }

    @Override
    public DirichletDistribution clone()
    {
        DirichletDistribution clone = (DirichletDistribution) super.clone();
        clone.setParameters( ObjectUtil.cloneSafe(this.getParameters()) );
        return clone;
    }

    @Override
    public Vector getMean()
    {
        return this.parameters.scale( 1.0/this.parameters.norm1() );
    }

    @Override
    public Vector sample(
        final Random random)
    {
        // We create one Gamma and update it to reuse across the function.
        final GammaDistribution.CDF gammaRV = new GammaDistribution.CDF(1.0, 1.0);
        
        // Create the result vector.
        final int K = this.getParameters().getDimensionality();
        final Vector y = VectorFactory.getDenseDefault().createVector(K);
        double sum = 0.0;
        for (int i = 0; i < K; i++)
        {
            gammaRV.setShape(this.parameters.get(i));
            final double yi = gammaRV.sampleAsDouble(random);
            y.set(i, yi);
            sum += yi;
        }
        
        if (sum != 0.0)
        {
            y.scaleEquals(1.0 / sum);
        }
        
        return y;
    }
    
    @Override
    public void sampleInto(
        final Random random,
        final int numSamples,
        final Collection<? super Vector> output)
    {
        GammaDistribution.CDF gammaRV = new GammaDistribution.CDF(1.0, 1.0);

        int K = this.getParameters().getDimensionality();
        double[][] gammaData = new double[K][];
        for (int i = 0; i < K; i++)
        {
            double ai = this.parameters.get(i);
            gammaRV.setShape(ai);
            gammaData[i] = gammaRV.sampleAsDoubles(random, numSamples);
        }

        for (int n = 0; n < numSamples; n++)
        {
            Vector y = VectorFactory.getDenseDefault().createVector(K);
            double sum = 0.0;
            for (int i = 0; i < K; i++)
            {
                double yin = gammaData[i][n];
                y.set(i, yin);
                sum += yin;
            }
            if (sum != 0.0)
            {
                y.scaleEquals(1.0 / sum);
            }
            output.add(y);
        }
    }

    @Override
    public Vector convertToVector()
    {
        return ObjectUtil.cloneSafe(this.getParameters());
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertSameDimensionality( this.getParameters() );
        this.setParameters( ObjectUtil.cloneSafe(parameters) );
    }

    /**
     * Getter for parameters
     * @return
     * Parameters of the Dirichlet distribution, must be at least 2-dimensional
     * and each element must be positive.
     */
    public Vector getParameters()
    {
        return this.parameters;
    }

    /**
     * Setter for parameters
     * @param parameters
     * Parameters of the Dirichlet distribution, must be at least 2-dimensional
     * and each element must be positive.
     */
    public void setParameters(
        final Vector parameters)
    {

        final int N = parameters.getDimensionality();

        if( N < 2 )
        {
            throw new IllegalArgumentException( "Dimensionality must be >= 2" );
        }
        
        for( int i = 0; i < N; i++ )
        {
            if( parameters.getElement(i) <= 0.0 )
            {
                throw new IllegalArgumentException(
                    "All parameter elements must be > 0.0" );
            }
        }

        this.parameters = parameters;
    }

    @Override
    public DirichletDistribution.PDF getProbabilityFunction()
    {
        return new DirichletDistribution.PDF( this );
    }

    /**
     * PDF of the Dirichlet distribution.
     */
    public static class PDF
        extends DirichletDistribution
        implements ProbabilityDensityFunction<Vector>,
        VectorInputEvaluator<Vector,Double>
    {

        /**
         * Default constructor.
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of PDF
         * @param parameters
         * Parameters of the Dirichlet distribution, must be at least 2-dimensional
         * and each element must be positive.
         */
        public PDF(
            final Vector parameters )
        {
            super( parameters );
        }

        /**
         * Copy Constructor.
         * @param other
         * DirichletDistribution to copy.
         */
        public PDF(
            final DirichletDistribution other )
        {
            super( other );
        }

        /**
         * Evaluates the Dirichlet PDF about the given input.  Note that we
         * normalize the given input by its L1 norm to ensure that its entries
         * sum to 1.
         * @param input
         * Input to consider, automatically normalized by its L1 norm without
         * side-effect.
         * @return
         * Dirichlet PDF evaluated about the given (unnormalized) input.
         */
        @Override
        public Double evaluate(
            final Vector input)
        {
            Vector xn = input.scale( 1.0 / input.norm1() );

            Vector a = this.getParameters();
            input.assertSameDimensionality( a );

            double logsum = 0.0;
            final int K = a.getDimensionality();
            for( int i = 0; i < K; i++ )
            {
                double xi = xn.getElement(i);
                if( (xi <= 0.0) || (1.0 <= xi) )
                {
                    throw new IllegalArgumentException(
                        "Expected all inputs to be (0.0,infinity): " + input );
                }
                double ai = a.getElement(i);
                logsum += (ai-1.0) * Math.log( xi );
            }
            logsum -= MathUtil.logMultinomialBetaFunction( a );
            
            return Math.exp(logsum);
        }

        @Override
        public double logEvaluate(
            final Vector input)
        {
            Vector xn = input.scale( 1.0 / input.norm1() );

            Vector a = this.getParameters();
            input.assertSameDimensionality( a );

            double logsum = 0.0;
            final int K = a.getDimensionality();
            for( int i = 0; i < K; i++ )
            {
                double xi = xn.getElement(i);
                if( (xi <= 0.0) || (1.0 <= xi) )
                {
                    throw new IllegalArgumentException(
                        "Expected all inputs to be (0.0,infinity): " + input );
                }
                double ai = a.getElement(i);
                logsum += (ai-1.0) * Math.log( xi );
            }

            logsum -= MathUtil.logMultinomialBetaFunction( a );
            return logsum;
        }

        @Override
        public int getInputDimensionality()
        {
            return (this.parameters != null) ? this.parameters.getDimensionality() : 0;
        }

        @Override
        public DirichletDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

    }

}
