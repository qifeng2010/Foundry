/*
 * File:                AbstractIncrementalEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 2, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.learning.algorithm.AbstractBatchAndIncrementalLearner;

/**
 * Partial implementation of {@code IncrementalEstimator}.
 *
 * @param   <DataType>
 *      The type of data generated by the Distribution.
 * @param   <SufficientStatisticsType>
 *      The type of the sufficient statistics for the distribution.
 * @param   <DistributionType>
 *      The type of Distribution this is the sufficient statistics of.
 * @author  Kevin R. Dixon
 * @since   3.1.1
 */
public abstract class AbstractIncrementalEstimator<DataType, DistributionType extends Distribution<? extends DataType>, SufficientStatisticsType extends SufficientStatistic<DataType,DistributionType>>
    extends AbstractBatchAndIncrementalLearner<DataType, SufficientStatisticsType>
    implements IncrementalEstimator<DataType, DistributionType, SufficientStatisticsType>
{

    /** 
     * Creates a new instance of AbstractIncrementalEstimator
     */
    public AbstractIncrementalEstimator()
    {
        super();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractIncrementalEstimator<DataType, DistributionType, SufficientStatisticsType> clone()
    {
        return (AbstractIncrementalEstimator<DataType, DistributionType, SufficientStatisticsType>) super.clone();
    }

    @Override
    public void update(
        final SufficientStatisticsType target,
        final DataType data)
    {
        target.update(data);
    }

}
