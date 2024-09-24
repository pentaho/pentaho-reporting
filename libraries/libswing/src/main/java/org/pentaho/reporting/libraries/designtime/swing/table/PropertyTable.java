/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.designtime.swing.table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.FormattingTableCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.GenericCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.GenericCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.PaintCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.date.DateCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.date.TimeCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.PropertyCellEditorWithEllipsis;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.PropertyEditorCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.PropertyEditorCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.settings.LocaleSettings;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PropertyTable extends JTable {
  public static final String DATETIME_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss.SSSS";
  public static final String TIME_FORMAT_DEFAULT = "HH:mm:ss.SSSS";
  public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd";

  private static Log logger = LogFactory.getLog( PropertyTable.class );

  private PropertyEditorCellRenderer propertyEditorCellRenderer;
  private PropertyCellEditorWithEllipsis propertyEditorCellEditor;
  private PropertyEditorCellEditor taggedPropertyEditorCellEditor;
  private ArrayCellRenderer arrayCellRenderer;
  private ArrayCellEditor arrayCellEditor;

  public PropertyTable() {
    putClientProperty( "terminateEditOnFocusLost", Boolean.TRUE );
    this.setShowHorizontalLines( true );
    this.setShowVerticalLines( true );
    this.setGridColor( SystemColor.controlShadow );

    taggedPropertyEditorCellEditor = new PropertyEditorCellEditor();
    propertyEditorCellEditor = new PropertyCellEditorWithEllipsis();
    propertyEditorCellRenderer = new PropertyEditorCellRenderer();
    arrayCellRenderer = new ArrayCellRenderer();
    arrayCellEditor = new ArrayCellEditor();

    setDefaultEditor( Object.class, null );

    setDefaultEditor( Number.class, new GenericCellEditor( BigDecimal.class ) );
    setDefaultEditor( Integer.class, new GenericCellEditor( Integer.class ) );
    setDefaultEditor( Float.class, new GenericCellEditor( Float.class ) );
    setDefaultEditor( Double.class, new GenericCellEditor( Double.class ) );
    setDefaultEditor( Short.class, new GenericCellEditor( Short.class ) );
    setDefaultEditor( Byte.class, new GenericCellEditor( Byte.class ) );
    setDefaultEditor( Long.class, new GenericCellEditor( Long.class ) );
    setDefaultEditor( BigInteger.class, new GenericCellEditor( BigInteger.class ) );
    setDefaultEditor( BigDecimal.class, new GenericCellEditor( BigDecimal.class ) );
    setDefaultEditor( String.class, new GenericCellEditor( String.class ) );
    setDefaultEditor( Date.class, new DateCellEditor( Date.class ) );
    setDefaultEditor( java.sql.Date.class, new DateCellEditor( Date.class ) );
    setDefaultEditor( Time.class, new TimeCellEditor( Time.class ) );
    setDefaultEditor( Timestamp.class, new DateCellEditor( Timestamp.class ) );

    setDefaultRenderer( Paint.class, new PaintCellRenderer() );
    setDefaultRenderer( Object.class, new GenericCellRenderer() );
    setDefaultRenderer( String.class, new GenericCellRenderer() );

    final SimpleDateFormat isoDateFormat =
      new SimpleDateFormat( DATETIME_FORMAT_DEFAULT, Locale.ENGLISH );
    setDefaultRenderer( Date.class, new FormattingTableCellRenderer( isoDateFormat ) );
    setDefaultRenderer( java.sql.Date.class, new FormattingTableCellRenderer
      ( new SimpleDateFormat( DATE_FORMAT_DEFAULT, Locale.ENGLISH ) ) );
    setDefaultRenderer( Time.class, new FormattingTableCellRenderer
      ( new SimpleDateFormat( TIME_FORMAT_DEFAULT, Locale.ENGLISH ) ) );
    setDefaultRenderer( Timestamp.class, new FormattingTableCellRenderer( isoDateFormat ) );
  }

  public PropertyTable( final TableModel dm ) {
    this();
    setModel( dm );
  }

  private static SimpleDateFormat createSafely( final String pattern, final String defaultPattern,
                                                final Locale locale ) {
    try {
      if ( StringUtils.isEmpty( pattern ) == false ) {
        return new SimpleDateFormat( pattern, locale );
      }
    } catch ( Exception e ) {
      logger.warn( "Invalid format string found in locale settings", e ); // NON-NLS
    }
    return new SimpleDateFormat( defaultPattern, locale );
  }

  public void applyLocaleSettings( final LocaleSettings localeSettings ) {
    final SimpleDateFormat isoDateFormat = createSafely( localeSettings.getDatetimeFormatPattern(),
      DATETIME_FORMAT_DEFAULT, localeSettings.getLocale() );
    final TimeZone timeZone = localeSettings.getTimeZone();
    isoDateFormat.setTimeZone( timeZone );
    setDefaultRenderer( Date.class, new FormattingTableCellRenderer( isoDateFormat ) );
    setDefaultRenderer( Timestamp.class, new FormattingTableCellRenderer( isoDateFormat ) );

    final DateCellEditor dateCellEditor = new DateCellEditor( Date.class );
    dateCellEditor.setDateFormat( isoDateFormat );
    setDefaultEditor( Date.class, dateCellEditor );

    final DateCellEditor timestampEditor = new DateCellEditor( Timestamp.class );
    timestampEditor.setDateFormat( isoDateFormat );
    setDefaultEditor( Timestamp.class, timestampEditor );

    final SimpleDateFormat dateFormat = createSafely( localeSettings.getDateFormatPattern(),
      DATE_FORMAT_DEFAULT, localeSettings.getLocale() );
    dateFormat.setTimeZone( timeZone );
    setDefaultRenderer( java.sql.Date.class, new FormattingTableCellRenderer( dateFormat ) );

    final DateCellEditor sqlDateCellEditor = new DateCellEditor( java.sql.Date.class );
    sqlDateCellEditor.setDateFormat( dateFormat );
    setDefaultEditor( java.sql.Date.class, sqlDateCellEditor );

    final SimpleDateFormat timeFormat = createSafely( localeSettings.getTimeFormatPattern(),
      TIME_FORMAT_DEFAULT, localeSettings.getLocale() );
    timeFormat.setTimeZone( timeZone );
    setDefaultRenderer( Time.class, new FormattingTableCellRenderer( timeFormat ) );

    final TimeCellEditor timeCellEditor = new TimeCellEditor( Time.class );
    timeCellEditor.setDateFormat( timeFormat );
    setDefaultEditor( Time.class, timeCellEditor );
  }

  public boolean isCellEditable( final int row, final int column ) {
    final int columnIndex = convertColumnIndexToModel( column );
    if ( getModel().isCellEditable( row, columnIndex ) ) {
      if ( getCellEditor( row, columnIndex ) == null ) {
        // no editor, so not editable ...
        return false;
      }
      return true;
    }
    return false;
  }

  public TableCellRenderer getCellRenderer( final int row, final int viewColumn ) {
    final TableModel tableModel = getModel();
    if ( tableModel instanceof PropertyTableModel ) {
      final PropertyTableModel model = (PropertyTableModel) getModel();
      final int column = convertColumnIndexToModel( viewColumn );

      final Class columnClass = model.getClassForCell( row, column );
      if ( columnClass.isArray() ) {
        return arrayCellRenderer;
      }

      final PropertyEditor propertyEditor = model.getEditorForCell( row, column );
      if ( propertyEditor != null ) {
        propertyEditorCellRenderer.setPropertyEditor( propertyEditor );
        return propertyEditorCellRenderer;
      }

      final TableColumn tableColumn = getColumnModel().getColumn( column );
      final TableCellRenderer renderer = tableColumn.getCellRenderer();
      if ( renderer != null ) {
        return renderer;
      }

      final TableCellRenderer defaultRenderer = getDefaultRenderer( columnClass );
      if ( defaultRenderer != null ) {
        return defaultRenderer;
      }

      if ( logger.isTraceEnabled() ) {
        logger.trace( "No renderer for column class " + columnClass ); // NON-NLS
      }
      return getDefaultRenderer( Object.class );
    }

    return super.getCellRenderer( row, viewColumn );
  }

  public TableCellEditor getCellEditor( final int row, final int viewColumn ) {
    final TableModel tableModel = getModel();
    if ( tableModel instanceof PropertyTableModel ) {
      final PropertyTableModel model = (PropertyTableModel) getModel();
      final int column = convertColumnIndexToModel( viewColumn );

      final PropertyEditor propertyEditor = model.getEditorForCell( row, column );
      final Class columnClass = model.getClassForCell( row, column );

      if ( propertyEditor != null ) {
        final String[] tags = propertyEditor.getTags();

        if ( columnClass.isArray() ) {
          arrayCellEditor.setPropertyEditorType( propertyEditor.getClass() );
        } else if ( tags == null || tags.length == 0 ) {
          propertyEditorCellEditor.setPropertyEditor( propertyEditor );
          return propertyEditorCellEditor;
        } else {
          taggedPropertyEditorCellEditor.setPropertyEditor( propertyEditor );
          return taggedPropertyEditorCellEditor;
        }
      }

      final TableColumn tableColumn = getColumnModel().getColumn( column );
      final TableCellEditor renderer = tableColumn.getCellEditor();
      if ( renderer != null ) {
        return renderer;
      }

      if ( columnClass.isArray() ) {
        return arrayCellEditor;
      }

      final TableCellEditor editor = getDefaultEditor( columnClass );
      if ( editor != null && logger.isTraceEnabled() ) {
        logger.trace( "Using preconfigured default editor for column class " + columnClass + ": " + editor ); // NON-NLS
      }
      return editor;
    }
    return super.getCellEditor( row, viewColumn );
  }

  public void stopEditing() {
    final TableCellEditor cellEditor = getCellEditor();
    if ( cellEditor != null ) {
      cellEditor.stopCellEditing();
    }
  }
}
