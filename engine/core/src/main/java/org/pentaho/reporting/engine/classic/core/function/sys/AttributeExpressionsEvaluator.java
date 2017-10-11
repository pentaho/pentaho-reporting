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

package org.pentaho.reporting.engine.classic.core.function.sys;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.util.beans.ValueConverter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.formula.ErrorValue;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * Evaluates style-expressions and updates the stylesheet. This is an internal helper function. It is not meant to be
 * used by end-users and manually adding this function to a report will cause funny side-effects.
 *
 * @author Thomas Morgner
 */
public class AttributeExpressionsEvaluator extends AbstractElementFormatFunction implements StructureFunction {
  private static final Log logger = LogFactory.getLog( AttributeExpressionsEvaluator.class );

  private boolean failOnErrors;

  /**
   * Default Constructor.
   */
  public AttributeExpressionsEvaluator() {
  }

  /**
   * Receives notification that report generation initializes the current run.
   * <P>
   * The event carries a ReportState.Started state. Use this to initialize the report.
   *
   * @param event
   *          The event.
   */
  public void reportInitialized( final ReportEvent event ) {
    failOnErrors =
        "true".equals( getRuntime().getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.FailOnAttributeExpressionErrors" ) );

    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }

    super.reportInitialized( event );

    if ( event.getState().isSubReportEvent() == false ) {
      // only evaluate master-reports. Subreports are evaluated when their parent-band is evaluated.
      final ReportDefinition definition = event.getReport();
      evaluateElement( definition );
    }
  }

  /**
   * Evaluates all defined style-expressions of the given element.
   *
   * @param e
   *          the element that should be updated.
   * @return true, if the element had attribute-expressions, false otherwise.
   */
  protected boolean evaluateElement( final ReportElement e ) {
    if ( e == null ) {
      throw new NullPointerException();
    }
    final String[] namespaces = e.getAttributeExpressionNamespaces();
    if ( namespaces.length == 0 ) {
      return false;
    }

    final ConverterRegistry instance = ConverterRegistry.getInstance();
    final ElementMetaData metaData = e.getMetaData();
    boolean retval = false;

    for ( int namespaceIdx = 0; namespaceIdx < namespaces.length; namespaceIdx++ ) {
      final String namespace = namespaces[namespaceIdx];
      final String[] names = e.getAttributeExpressionNames( namespace );
      for ( int nameIdx = 0; nameIdx < names.length; nameIdx++ ) {
        final String name = names[nameIdx];
        final Expression ex = e.getAttributeExpression( namespace, name );
        if ( ex == null ) {
          continue;
        }

        final AttributeMetaData attribute = metaData.getAttributeDescription( namespace, name );
        if ( attribute != null && attribute.isDesignTimeValue() ) {
          continue;
        }

        retval = true;
        ex.setRuntime( getRuntime() );
        try {
          final Object value = evaluate( ex );
          if ( attribute == null ) {
            // Not a declared attribute, but maybe one of the output-handlers can work on this one.
            e.setAttribute( namespace, name, value );
          } else {
            final Class<?> type = attribute.getTargetType();
            if ( value == null || type.isAssignableFrom( value.getClass() ) ) {
              e.setAttribute( namespace, name, value );
            } else if ( value instanceof ErrorValue ) {
              if ( failOnErrors ) {
                throw new InvalidReportStateException( String.format(
                    "Failed to evaluate attribute-expression for attribute %s:%s on element [%s]", // NON-NLS
                    namespace, name, FunctionUtilities.computeElementLocation( e ) ) );
              }
              e.setAttribute( namespace, name, null );
            } else {

              final PropertyEditor propertyEditor = attribute.getEditor();
              if ( propertyEditor != null ) {
                propertyEditor.setAsText( String.valueOf( value ) );
                e.setAttribute( namespace, name, propertyEditor.getValue() );
              } else {
                final ValueConverter valueConverter = instance.getValueConverter( type );
                if ( type.isAssignableFrom( String.class ) ) {
                  // the attribute would allow raw-string values, so copy the element ..
                  e.setAttribute( namespace, name, value );
                } else if ( valueConverter != null ) {
                  final Object o = ConverterRegistry.toPropertyValue( String.valueOf( value ), type );
                  e.setAttribute( namespace, name, o );
                } else {
                  // undo any previous computation
                  e.setAttribute( namespace, name, null );
                }
              }
            }
          }
        } catch ( InvalidReportStateException exception ) {
          throw exception;
        } catch ( Exception exception ) {
          if ( logger.isDebugEnabled() ) {
            logger.debug( String.format( "Failed to evaluate attribute-expression for attribute %s:%s on element [%s]", // NON-NLS
                namespace, name, FunctionUtilities.computeElementLocation( e ) ), exception );
          }
          if ( failOnErrors ) {
            throw new InvalidReportStateException( String.format(
                "Failed to evaluate attribute-expression for attribute %s:%s on element [%s]", // NON-NLS
                namespace, name, FunctionUtilities.computeElementLocation( e ) ), exception );
          }
          e.setAttribute( namespace, name, null );
        } finally {
          ex.setRuntime( null );
        }
      }
    }
    return retval;
  }

  private Object evaluate( final Expression ex ) throws IOException, SQLException {
    final Object retval = ex.getValue();
    if ( retval instanceof Clob ) {
      return IOUtils.getInstance().readClob( (Clob) retval );
    }
    if ( retval instanceof Blob ) {
      return IOUtils.getInstance().readBlob( (Blob) retval );
    }
    return retval;
  }

  public int getProcessingPriority() {
    return 2000;
  }
}
