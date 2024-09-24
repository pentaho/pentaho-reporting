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

package org.pentaho.reporting.designer.core.util.table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.DesignerContextComponent;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.table.expressions.ExpressionCellHandler;
import org.pentaho.reporting.designer.core.util.table.expressions.ReportPreProcessorCellEditor;
import org.pentaho.reporting.designer.core.util.table.expressions.ReportPreProcessorCellRenderer;
import org.pentaho.reporting.designer.core.util.table.expressions.StructureFunctionCellEditor;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.FormattingTableCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.GenericCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.GenericCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.PaintCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.date.DateCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.date.TimeCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.PropertyEditorCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.PropertyEditorCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.settings.LocaleSettings;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.SystemColor;
import java.awt.Paint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A table implementation that selects the cell-renderer and editor based on some extended rules (and not just based on
 * the current column).
 *
 * @author Thomas Morgner
 */
public class ElementMetaDataTable extends JTable implements DesignerContextComponent {
  private class LocaleSettingsListener implements SettingsListener {
    private LocaleSettingsListener() {
    }

    public void settingsChanged() {
      applyLocaleSettings( WorkspaceSettings.getInstance() );
    }
  }

  private class ActiveContextChangeHandler implements PropertyChangeListener {
    private ActiveContextChangeHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange( final PropertyChangeEvent evt ) {
      final ReportRenderContext oldContext = (ReportRenderContext) evt.getOldValue();
      final ReportRenderContext activeContext = (ReportRenderContext) evt.getNewValue();
      updateActiveContext( oldContext, activeContext );
    }
  }

  private static final Log logger = LogFactory.getLog( ElementMetaDataTable.class );

  private GroupingHeaderCellRenderer groupingCellRenderer;
  private PropertyEditorCellRenderer propertyEditorCellRenderer;
  private DesignerPropertyCellEditorWithEllipsis propertyEditorCellEditor;
  private PropertyEditorCellEditor taggedPropertyEditorCellEditor;
  private ArrayCellRenderer arrayCellRenderer;
  private ArrayCellEditor arrayCellEditor;
  private ExpressionCellHandler expressionsCellEditor;
  private ExpressionCellHandler expressionsCellRenderer;
  private StringValueCellEditor stringValueCellEditor;

  private StructureFunctionCellEditor structureFunctionCellEditor;
  private ReportPreProcessorCellEditor reportPreProcessorCellEditor;
  private ReportPreProcessorCellRenderer reportPreProcessorCellRenderer;
  private ActiveContextChangeHandler changeHandler;

  public ElementMetaDataTable() {
    putClientProperty( "terminateEditOnFocusLost", Boolean.TRUE );

    // This is a hack as Mac chooses white on white for grids.
    this.setShowHorizontalLines( true );
    this.setShowVerticalLines( true );
    this.setGridColor( SystemColor.controlShadow );

    changeHandler = new ActiveContextChangeHandler();
    structureFunctionCellEditor = new StructureFunctionCellEditor();
    reportPreProcessorCellEditor = new ReportPreProcessorCellEditor();
    expressionsCellEditor = new ExpressionCellHandler();
    expressionsCellRenderer = new ExpressionCellHandler();
    reportPreProcessorCellRenderer = new ReportPreProcessorCellRenderer();
    groupingCellRenderer = new GroupingHeaderCellRenderer();
    taggedPropertyEditorCellEditor = new PropertyEditorCellEditor();
    propertyEditorCellEditor = new DesignerPropertyCellEditorWithEllipsis();
    propertyEditorCellRenderer = new PropertyEditorCellRenderer();
    arrayCellRenderer = new ArrayCellRenderer();
    arrayCellEditor = new ArrayCellEditor();
    stringValueCellEditor = new StringValueCellEditor();

    setDefaultEditor( Object.class, null );
    setDefaultEditor( GroupingHeader.class, new GroupingHeaderCellEditor() );
    setDefaultEditor( Expression.class, expressionsCellEditor );
    setDefaultEditor( StructureFunction.class, structureFunctionCellEditor );
    setDefaultEditor( ReportPreProcessor.class, reportPreProcessorCellEditor );

    setDefaultEditor( Number.class, new GenericCellEditor( BigDecimal.class ) );
    setDefaultEditor( Integer.class, new GenericCellEditor( Integer.class ) );
    setDefaultEditor( Float.class, new GenericCellEditor( Float.class ) );
    setDefaultEditor( Double.class, new GenericCellEditor( Double.class ) );
    setDefaultEditor( Short.class, new GenericCellEditor( Short.class ) );
    setDefaultEditor( Byte.class, new GenericCellEditor( Byte.class ) );
    setDefaultEditor( Long.class, new GenericCellEditor( Long.class ) );
    setDefaultEditor( BigInteger.class, new GenericCellEditor( BigInteger.class ) );
    setDefaultEditor( BigDecimal.class, new GenericCellEditor( BigDecimal.class ) );
    setDefaultEditor( String.class, stringValueCellEditor );
    setDefaultEditor( Date.class, new DateCellEditor( Date.class ) );
    setDefaultEditor( java.sql.Date.class, new DateCellEditor( java.sql.Date.class ) );
    setDefaultEditor( Time.class, new TimeCellEditor( Time.class ) );
    setDefaultEditor( Timestamp.class, new DateCellEditor( Timestamp.class ) );

    setDefaultRenderer( GroupingHeader.class, new GroupingHeaderCellRenderer() );
    setDefaultRenderer( GroupedName.class, new GroupedNameCellRenderer() );
    setDefaultRenderer( StructureFunction.class, expressionsCellRenderer );
    setDefaultRenderer( Expression.class, expressionsCellRenderer );
    setDefaultRenderer( Paint.class, new PaintCellRenderer() );
    setDefaultRenderer( Object.class, new GenericCellRenderer() );
    setDefaultRenderer( String.class, new GenericCellRenderer() );
    setDefaultRenderer( ReportPreProcessor.class, reportPreProcessorCellRenderer );

    final SimpleDateFormat isoDateFormat =
      new SimpleDateFormat( WorkspaceSettings.DATETIME_FORMAT_DEFAULT, Locale.ENGLISH );
    setDefaultRenderer( Date.class, new FormattingTableCellRenderer( isoDateFormat ) );
    setDefaultRenderer( java.sql.Date.class,
      new FormattingTableCellRenderer( new SimpleDateFormat( WorkspaceSettings.DATE_FORMAT_DEFAULT, Locale.ENGLISH ) ) );
    setDefaultRenderer( Time.class,
      new FormattingTableCellRenderer( new SimpleDateFormat( WorkspaceSettings.TIME_FORMAT_DEFAULT, Locale.ENGLISH ) ) );
    setDefaultRenderer( Timestamp.class, new FormattingTableCellRenderer( isoDateFormat ) );

    WorkspaceSettings.getInstance().addSettingsListener( new LocaleSettingsListener() );
    applyLocaleSettings( WorkspaceSettings.getInstance() );
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
      WorkspaceSettings.DATETIME_FORMAT_DEFAULT, localeSettings.getLocale() );
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
      WorkspaceSettings.DATE_FORMAT_DEFAULT, localeSettings.getLocale() );
    dateFormat.setTimeZone( timeZone );
    setDefaultRenderer( java.sql.Date.class, new FormattingTableCellRenderer( dateFormat ) );

    final DateCellEditor sqlDateCellEditor = new DateCellEditor( java.sql.Date.class );
    sqlDateCellEditor.setDateFormat( dateFormat );
    setDefaultEditor( java.sql.Date.class, sqlDateCellEditor );

    final SimpleDateFormat timeFormat = createSafely( localeSettings.getTimeFormatPattern(),
      WorkspaceSettings.TIME_FORMAT_DEFAULT, localeSettings.getLocale() );
    timeFormat.setTimeZone( timeZone );
    setDefaultRenderer( Time.class, new FormattingTableCellRenderer( timeFormat ) );

    final TimeCellEditor timeCellEditor = new TimeCellEditor( Time.class );
    timeCellEditor.setDateFormat( timeFormat );
    setDefaultEditor( Time.class, timeCellEditor );
  }

  /**
   * Returns true if the cell at <code>row</code> and <code>column</code> is editable.  Otherwise, invoking
   * <code>setValueAt</code> on the cell will have no effect.
   * <p/>
   * <b>Note</b>: The column is specified in the table view's display order, and not in the <code>TableModel</code>'s
   * column order.  This is an important distinction because as the user rearranges the columns in the table, the column
   * at a given index in the view will change. Meanwhile the user's actions never affect the model's column ordering.
   *
   * @param row    the row whose value is to be queried
   * @param column the column whose value is to be queried
   * @return true if the cell is editable
   * @see #setValueAt
   */
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
    final int column = convertColumnIndexToModel( viewColumn );
    final Object value = getModel().getValueAt( row, column );
    if ( value instanceof GroupingHeader ) {
      return groupingCellRenderer;
    }

    final ElementMetaDataTableModel model = (ElementMetaDataTableModel) getModel();
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

  public TableCellEditor getCellEditor( final int row, final int viewColumn ) {
    final int column = convertColumnIndexToModel( viewColumn );
    final Object value = getModel().getValueAt( row, column );
    if ( value instanceof GroupingHeader ) {
      return getDefaultEditor( GroupingHeader.class );
    }

    final ElementMetaDataTableModel model = (ElementMetaDataTableModel) getModel();
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

  public void setReportDesignerContext( final ReportDesignerContext newContext ) {
    final ReportDesignerContext oldContext = arrayCellEditor.getReportDesignerContext();
    if ( oldContext != null ) {
      oldContext.removePropertyChangeListener( this.changeHandler );
      final ReportDocumentContext oldActiveContext = getReportRenderContext();
      updateActiveContext( oldActiveContext, null );
    }

    arrayCellEditor.setReportDesignerContext( newContext );
    stringValueCellEditor.setReportDesignerContext( newContext );
    expressionsCellRenderer.setReportDesignerContext( newContext );
    expressionsCellEditor.setReportDesignerContext( newContext );
    structureFunctionCellEditor.setReportDesignerContext( newContext );

    if ( newContext != null ) {
      newContext.addPropertyChangeListener( ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, changeHandler );
      updateActiveContext( null, newContext.getActiveContext() );
    }
  }

  protected void updateActiveContext( final ReportDocumentContext oldContext,
                                      final ReportDocumentContext activeContext ) {
    structureFunctionCellEditor.setRenderContext( activeContext );
    reportPreProcessorCellEditor.setRenderContext( activeContext );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return arrayCellEditor.getReportDesignerContext();
  }

  public ReportDocumentContext getReportRenderContext() {
    final ReportDesignerContext reportDesignerContext = getReportDesignerContext();
    if ( reportDesignerContext == null ) {
      return null;
    }
    return reportDesignerContext.getActiveContext();
  }

  public boolean isFormulaFragment() {
    return stringValueCellEditor.isFormulaFragment();
  }

  public void setFormulaFragment( final boolean formulaFragment ) {
    stringValueCellEditor.setFormulaFragment( formulaFragment );
  }

  public void stopEditing() {
    final TableCellEditor cellEditor = getCellEditor();
    if ( cellEditor != null ) {
      cellEditor.stopCellEditing();
    }
  }
}
