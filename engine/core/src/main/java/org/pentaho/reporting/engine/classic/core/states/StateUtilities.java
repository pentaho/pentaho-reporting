/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorRegistry;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;

/**
 * Creation-Date: Dec 14, 2006, 7:59:39 PM
 *
 * @author Thomas Morgner
 */
public class StateUtilities {
  /**
   * A comparator for levels in descending order.
   */
  public static final class DescendingComparator<T extends Comparable> implements Comparator<T>, Serializable {
    /**
     * Default constructor.
     */
    public DescendingComparator() {
    }

    /**
     * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.
     * <p>
     *
     * @param c1
     *          the first object to be compared.
     * @param c2
     *          the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
     *         than the second.
     * @throws ClassCastException
     *           if the arguments' types prevent them from being compared by this Comparator.
     */
    public int compare( final T c1, final T c2 ) {
      // noinspection unchecked
      return -1 * c1.compareTo( c2 );
    }
  }

  private StateUtilities() {
  }

  public static boolean computeLevels( final DefaultFlowController report, final LayoutProcess lp,
      final HashSet<Integer> levels ) {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( lp == null ) {
      throw new NullPointerException();
    }
    if ( levels == null ) {
      throw new NullPointerException();
    }
    boolean retval = false;
    final StructureFunction[] collectionFunctions = lp.getCollectionFunctions();
    for ( int i = 0; i < collectionFunctions.length; i++ ) {
      final StructureFunction function = collectionFunctions[i];
      if ( function.getDependencyLevel() == LayoutProcess.LEVEL_STRUCTURAL_PREPROCESSING ) {
        // this indicates a structural-preprocessor function, like the CrosstabNormalizer. They do not
        // take part in the ordinary processing.
        continue;
      }
      final Integer level = IntegerCache.getInteger( function.getDependencyLevel() );
      levels.add( level );

      if ( level != LayoutProcess.LEVEL_PAGINATE ) {
        retval = true;
      }
    }
    levels.add( IntegerCache.getInteger( LayoutProcess.LEVEL_PAGINATE ) );

    final Expression[] expressions = report.getMasterRow().getExpressionDataRow().getExpressions();
    for ( int i = 0; i < expressions.length; i++ ) {
      final Expression expression = expressions[i];
      final Integer level = IntegerCache.getInteger( expression.getDependencyLevel() );
      levels.add( level );
      if ( level != LayoutProcess.LEVEL_PAGINATE ) {
        retval = true;
      }
    }

    return retval;
  }

  public static ValidationResult validate( final MasterReport report, final ValidationResult result )
    throws ReportProcessingException {

    final ReportParameterDefinition parameters = report.getParameterDefinition();
    final DefaultParameterContext parameterContext = new DefaultParameterContext( report );

    try {
      final ReportParameterValidator reportParameterValidator = parameters.getValidator();
      return reportParameterValidator.validate( result, parameters, parameterContext );
    } finally {
      parameterContext.close();
    }
  }

  /**
   * Computes the parameter value set for a given report. Note that this method ignores the validation result, so if the
   * specified parameter values are wrong you may end up with a bunch of default values.
   *
   * @param report
   * @return
   * @throws ReportProcessingException
   */
  public static ReportParameterValues computeParameterValueSet( final MasterReport report )
    throws ReportProcessingException {

    final ReportParameterDefinition parameters = report.getParameterDefinition();
    final DefaultParameterContext parameterContext = new DefaultParameterContext( report );

    final ReportParameterValues parameterValues;
    try {
      final ReportParameterValidator reportParameterValidator = parameters.getValidator();
      final ValidationResult validationResult =
          reportParameterValidator.validate( new ValidationResult(), parameters, parameterContext );
      parameterValues = validationResult.getParameterValues();
      return computeParameterValueSet( report, parameterValues );
    } finally {
      parameterContext.close();
    }
  }

  public static ReportParameterValues computeParameterValueSet( final MasterReport report,
      final ReportParameterValues parameterValues ) throws ReportProcessingException {
    final ReportParameterValues retval = new ReportParameterValues();
    retval.putAll( parameterValues );

    final Configuration config = report.getConfiguration();
    if ( "true".equals( config
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.legacy.ReportNameAsProperty" ) ) ) {
      retval.put( "report.name", report.getName() );
    }

    final ReportEnvironment reportEnvironment = report.getReportEnvironment();
    final Object property = reportEnvironment.getEnvironmentProperty( "::internal::report.date" );
    if ( property instanceof Date == false ) {
      retval.put( MasterReport.REPORT_DATE_PROPERTY, new Date() );
    } else {
      retval.put( MasterReport.REPORT_DATE_PROPERTY, property );
    }
    return retval;
  }

  public static ReportParameterValues computeParameterValueSet( final SubReport report ) {
    // todo: Grab parent reports and compute the dataschema for them, so that the parameters here
    // get a meaning.

    final ReportParameterValues retval = new ReportParameterValues();
    // for the sake of backward compatiblity ..
    retval.put( MasterReport.REPORT_DATE_PROPERTY, new Date() );

    final ParameterMapping[] reportParameterValues = report.getInputMappings();
    for ( int i = 0; i < reportParameterValues.length; i++ ) {
      final ParameterMapping mapping = reportParameterValues[i];
      if ( "*".equals( mapping.getName() ) ) {
        continue;
      }
      retval.put( mapping.getName(), null );
    }
    return retval;
  }

  public static ReportPreProcessor[] getAllPreProcessors( final AbstractReportDefinition reportDefinition,
      final boolean designTime ) {
    final ReportPreProcessorRegistry registry = ReportPreProcessorRegistry.getInstance();
    final ReportPreProcessor[] processors = reportDefinition.getPreProcessors();
    final ArrayList<ReportPreProcessor> preProcessors = new ArrayList<ReportPreProcessor>();
    for ( int i = 0; i < processors.length; i++ ) {
      final ReportPreProcessor o = processors[i];
      if ( o == null ) {
        continue;
      }

      final String identifier = o.getClass().getName();
      if ( registry.isReportPreProcessorRegistered( identifier ) ) {
        final ReportPreProcessorMetaData metaData = registry.getReportPreProcessorMetaData( identifier );
        if ( designTime && metaData.isExecuteInDesignMode() == false ) {
          continue;
        }
      }
      preProcessors.add( o );
    }

    final ReportPreProcessorMetaData[] allProcessors = registry.getAllReportPreProcessorMetaDatas();
    Arrays.sort( allProcessors, new PreProcessorComparator() );
    for ( int i = 0; i < allProcessors.length; i++ ) {
      final ReportPreProcessorMetaData processor = allProcessors[i];
      if ( designTime && processor.isExecuteInDesignMode() == false ) {
        continue;
      }

      if ( processor.isAutoProcessor() ) {
        try {
          preProcessors.add( processor.create() );
        } catch ( InstantiationException e ) {
          throw new InvalidReportStateException( "Failed to instantiate automatic-report-pre-processor", e );
        }
      }
    }
    return preProcessors.toArray( new ReportPreProcessor[preProcessors.size()] );
  }

  private static class PreProcessorComparator implements Comparator<ReportPreProcessorMetaData> {
    public int compare( final ReportPreProcessorMetaData o1, final ReportPreProcessorMetaData o2 ) {
      return Integer.valueOf( o1.getExecutionPriority() ).compareTo( o2.getExecutionPriority() );
    }
  }

}
