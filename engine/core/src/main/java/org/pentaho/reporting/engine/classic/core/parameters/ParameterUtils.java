/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.parameters;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import java.util.Locale;

public class ParameterUtils {

  private ParameterUtils() {
  }

  /**
   * Enriches the parameter values with computed default values. Warning: This is a dangerous thing as this may use
   * untrusted input for the default value computation.
   *
   * @param report
   * @param context
   * @return
   * @throws ReportDataFactoryException
   * @deprecated
   */
  public static ReportParameterValues
    initializeDefaultValues( final MasterReport report, final ParameterContext context )
      throws ReportDataFactoryException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }

    final ReportParameterDefinition definition = report.getParameterDefinition();
    final ReportParameterValues parameters = new ReportParameterValues( report.getParameterValues() );
    final ParameterContextWrapper wrapper = new ParameterContextWrapper( context, parameters );
    final ParameterDefinitionEntry[] entries = definition.getParameterDefinitions();
    for ( int i = 0; i < entries.length; i++ ) {
      final ParameterDefinitionEntry entry = entries[i];
      final Object oldValue = parameters.get( entry.getName() );
      if ( oldValue == null ) {
        parameters.put( entry.getName(), entry.getDefaultValue( wrapper ) );
      }
    }
    return parameters;
  }

  static Locale getLocale( final ReportEnvironment environment ) {
    final Locale locale = environment.getLocale();
    return locale != null ? locale : Locale.getDefault();
  }

   /**
    * Determines if the time selector is applicable based on the given value type.
    *
    * @param valueType the class type to check
    * @return true if the time selector is applicable, false otherwise
    */
 public static boolean isTimeSelectorApplicable(Class<?> valueType) {
     return valueType != null && java.util.Date.class.isAssignableFrom(valueType);
 }

}
