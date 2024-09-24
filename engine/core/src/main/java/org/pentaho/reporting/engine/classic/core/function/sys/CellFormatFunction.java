/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.function.sys;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.filter.FormatSpecification;
import org.pentaho.reporting.engine.classic.core.filter.RawDataSource;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

/**
 * The cell-format function is an internal structure function that copies the format-strings of any text-field into the
 * stylesheet of the element. This function does nothing if the current export type is not Table/Excel.
 *
 * @author Thomas Morgner
 */
public class CellFormatFunction extends AbstractElementFormatFunction implements StructureFunction {
  /**
   * A reusable format-specification object.
   */
  private FormatSpecification formatSpecification;

  /**
   * Default Constructor.
   */
  public CellFormatFunction() {
  }

  public int getProcessingPriority() {
    return 30000;
  }

  protected boolean isExecutable() {
    return getRuntime().getExportDescriptor().startsWith( "table/excel" );
  }

  protected boolean evaluateElement( final ReportElement e ) {
    final DataSource source = e.getElementType();
    if ( source instanceof RawDataSource ) {
      final ElementStyleSheet style = e.getStyle();
      final String oldFormat = (String) style.getStyleProperty( ElementStyleKeys.EXCEL_DATA_FORMAT_STRING );
      if ( oldFormat != null && oldFormat.length() > 0 ) {
        final Object attribute =
            e.getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.EXCEL_CELL_FORMAT_AUTOCOMPUTE );
        if ( Boolean.TRUE.equals( attribute ) == false ) {
          return false;
        }
      }
      final RawDataSource rds = (RawDataSource) source;
      if ( formatSpecification != null ) {
        formatSpecification.redefine( FormatSpecification.TYPE_UNDEFINED, null );
      }
      formatSpecification = rds.getFormatString( getRuntime(), e, formatSpecification );
      if ( formatSpecification != null ) {
        if ( formatSpecification.getType() == FormatSpecification.TYPE_DATE_FORMAT
            || formatSpecification.getType() == FormatSpecification.TYPE_DECIMAL_FORMAT ) {
          style.setStyleProperty( ElementStyleKeys.EXCEL_DATA_FORMAT_STRING, formatSpecification.getFormatString() );
          e.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.EXCEL_CELL_FORMAT_AUTOCOMPUTE,
              Boolean.TRUE );
          return true;
        }
      }
    }
    return false;
  }

  public CellFormatFunction getInstance() {
    final CellFormatFunction instance = (CellFormatFunction) super.getInstance();
    instance.formatSpecification = null;
    return instance;
  }
}
