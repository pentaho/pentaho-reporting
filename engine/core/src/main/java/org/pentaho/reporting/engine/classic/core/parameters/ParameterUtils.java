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
 * Copyright (c) 2001 - 2018 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
}
