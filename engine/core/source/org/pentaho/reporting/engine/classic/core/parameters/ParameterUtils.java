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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.parameters;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

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
  public static ReportParameterValues initializeDefaultValues( final MasterReport report,
                                                               final ParameterContext context )
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
      final ParameterDefinitionEntry entry = entries[ i ];
      final Object oldValue = parameters.get( entry.getName() );
      if ( oldValue == null ) {
        parameters.put( entry.getName(), entry.getDefaultValue( wrapper ) );
      }
    }
    return parameters;
  }

  public static String getTranslatedLabel( final ParameterDefinitionEntry entry, final ParameterContext context ) {
    String coreLabel =
      entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.LABEL,
        context );

    if ( coreLabel != null ) {
      String translateLabel = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TRANSLATE_LABEL,
        context );
      if ( Boolean.valueOf( translateLabel ) ) {
        coreLabel = getResourceKeyValue( coreLabel, entry, context );
      }
    }
    return coreLabel;
  }

  public static String getTranslatedDateFormat( final ParameterDefinitionEntry entry, final ParameterContext context ) {
    String formatString =
      entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.DATA_FORMAT,
        context );

    if ( formatString != null ) {
      String translateFormat = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TRANSLATE_DATA_FORMAT,
        context );
      if ( Boolean.valueOf( translateFormat ) ) {
        formatString = getResourceKeyValue( formatString, entry, context );
      }
    }
    return formatString;
  }

  public static String getTranslatedErrorMessage( final ParameterDefinitionEntry entry, final ParameterContext context ) {
    String errorMessage = entry.getParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.ERROR_MESSAGE,
      context );

    if ( errorMessage != null ) {
      final String translate = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TRANSLATE_ERROR_MESSAGE,
        context );
      if ( Boolean.valueOf( translate ) ) {
        errorMessage = getResourceKeyValue( errorMessage, entry, context );
      }
    }

    return errorMessage;
  }

  public static String getResourceKeyValue( final String key, final ParameterDefinitionEntry entry,
                                     final ParameterContext context ) {
    String resource = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID,
      context );
    return getResourceKeyValue( key, resource, context );
  }

  public static String getResourceKeyValue( final String key, final String resource,
                                     final ParameterContext context ) {
    if ( resource == null ) {
      return key;
    }
    String value;
    final ResourceBundleFactory resourceBundleFactory = context.getResourceBundleFactory();
    try {
      final ResourceBundle bundle = resourceBundleFactory.getResourceBundle( resource );
      value = bundle.getString( key );
    } catch ( NullPointerException | MissingResourceException | ClassCastException e ) {
      // there is no such property
      return key;
    }
    return value;
  }
}
