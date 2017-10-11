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

import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Map;

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
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.util.beans.ValueConverter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.formula.ErrorValue;

/**
 * Evaluates style-expressions and updates the stylesheet. This is an internal helper function. It is not meant to be
 * used by end-users and manually adding this function to a report will cause funny side-effects.
 *
 * @author Thomas Morgner
 */
public class StyleExpressionsEvaluator extends AbstractElementFormatFunction implements StructureFunction {
  private static final Log logger = LogFactory.getLog( StyleExpressionsEvaluator.class );
  private boolean failOnErrors;

  /**
   * Default Constructor.
   */
  public StyleExpressionsEvaluator() {
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
            "org.pentaho.reporting.engine.classic.core.FailOnStyleExpressionErrors" ) );

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

  public int getProcessingPriority() {
    return 10000;
  }

  /**
   * Evaluates all defined style-expressions of the given element.
   *
   * @param e
   *          the element that should be updated.
   * @return true, if the element has style-expressions, or false otherwise.
   */
  protected boolean evaluateElement( final ReportElement e ) {
    final Map<StyleKey, Expression> styleExpressions = e.getStyleExpressions();
    if ( styleExpressions.isEmpty() ) {
      return false;
    }

    boolean retval = false;
    final ElementStyleSheet style = e.getStyle();
    for ( final Map.Entry<StyleKey, Expression> entry : styleExpressions.entrySet() ) {
      final StyleKey key = entry.getKey();
      final Expression ex = entry.getValue();
      if ( ex == null ) {
        continue;
      }
      retval = true;
      ex.setRuntime( getRuntime() );
      try {
        final Object value = evaluate( ex );
        if ( value == null ) {
          style.setStyleProperty( key, null );
        } else if ( key.getValueType().isInstance( value ) ) {
          style.setStyleProperty( key, value );
        } else if ( value instanceof ErrorValue ) {
          if ( failOnErrors ) {
            throw new InvalidReportStateException( String.format(
                "Failed to evaluate style-expression for key %s on element [%s]", // NON-NLS
                key.getName(), FunctionUtilities.computeElementLocation( e ) ) );
          }
          style.setStyleProperty( key, null );
        } else {
          final ValueConverter valueConverter = ConverterRegistry.getInstance().getValueConverter( key.getValueType() );
          if ( valueConverter != null ) {
            // try to convert it ..
            final Object o = ConverterRegistry.toPropertyValue( String.valueOf( value ), key.getValueType() );
            style.setStyleProperty( key, o );
          } else {
            style.setStyleProperty( key, null );
          }
        }
      } catch ( InvalidReportStateException exception ) {
        throw exception;
      } catch ( Exception exception ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( String.format( "Failed to evaluate style expression for element '%s', style-key %s", // NON-NLS
              e, key ), exception );
        }
        if ( failOnErrors ) {
          throw new InvalidReportStateException( String.format(
              "Failed to evaluate style-expression for key %s on element [%s]", // NON-NLS
              key.getName(), FunctionUtilities.computeElementLocation( e ) ), exception );
        }
        // ignored, but we clear the style as we have no valid value anymore.
        style.setStyleProperty( key, null );
      } finally {
        ex.setRuntime( null );
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
}
