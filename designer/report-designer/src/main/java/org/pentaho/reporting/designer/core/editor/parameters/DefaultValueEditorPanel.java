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

package org.pentaho.reporting.designer.core.editor.parameters;

import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.FastPropertyEditorManager;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTableModel;
import org.pentaho.reporting.designer.core.util.table.TableStyle;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.GenericCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.settings.LocaleSettings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Todo: Document me!
 * <p/>
 * Date: 10.05.2010 Time: 16:24:51
 *
 * @author Thomas Morgner.
 */
public class DefaultValueEditorPanel extends JPanel {
  private static class InjectDatePatternLocaleSettings implements LocaleSettings {
    private Class type;
    private String pattern;
    private TimeZone timeZone;

    private InjectDatePatternLocaleSettings( final Class type, final String pattern, final TimeZone timeZone ) {
      this.type = type;
      this.pattern = pattern;
      this.timeZone = timeZone;
    }

    public String getDateFormatPattern() {
      if ( StringUtils.isEmpty( pattern ) == false && java.sql.Date.class.equals( type ) ) {
        return pattern;
      }
      return WorkspaceSettings.getInstance().getDateFormatPattern();
    }

    public String getTimeFormatPattern() {
      if ( StringUtils.isEmpty( pattern ) == false && Time.class.equals( type ) ) {
        return pattern;
      }
      return WorkspaceSettings.getInstance().getTimeFormatPattern();
    }

    public String getDatetimeFormatPattern() {
      if ( StringUtils.isEmpty( pattern ) == false && Date.class.equals( type ) ) {
        return pattern;
      }
      if ( StringUtils.isEmpty( pattern ) == false && Timestamp.class.equals( type ) ) {
        return pattern;
      }
      return WorkspaceSettings.getInstance().getDatetimeFormatPattern();
    }

    public Locale getLocale() {
      return WorkspaceSettings.getInstance().getLocale();
    }

    public TimeZone getTimeZone() {
      return timeZone;
    }
  }

  private static class SingleValueMetaTableModel extends AbstractTableModel implements ElementMetaDataTableModel {
    private Object value;
    private Class valueType;
    private static final String[] EMPTY_EXTRA_FIELDS = new String[ 0 ];

    private SingleValueMetaTableModel() {
      valueType = Object.class;
    }

    public Object getValue() {
      return value;
    }

    public void setValue( final Object value, final Class valueType ) {
      if ( valueType == null ) {
        throw new NullPointerException();
      }
      this.value = value;
      this.valueType = valueType;
      fireTableStructureChanged();
    }

    public Class getValueType() {
      return valueType;
    }

    /**
     * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
      return 1;
    }

    /**
     * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns
     * it should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
      return 1;
    }

    /**
     * Returns false.  This is the default implementation for all cells.
     *
     * @param rowIndex    the row being queried
     * @param columnIndex the column being queried
     * @return false
     */
    public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
      return true;
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    public Class getColumnClass( final int columnIndex ) {
      return valueType;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt( final int rowIndex, final int columnIndex ) {
      return value;
    }

    public String[] getExtraFields( final int row, final int column ) {
      return EMPTY_EXTRA_FIELDS;
    }

    /**
     * This empty implementation is provided so users don't have to implement this method if their data model is not
     * editable.
     *
     * @param aValue      value to assign to cell
     * @param rowIndex    row of cell
     * @param columnIndex column of cell
     */
    public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
      value = aValue;
      fireTableCellUpdated( rowIndex, columnIndex );
    }

    public Class getClassForCell( final int row, final int column ) {
      return valueType;
    }

    public PropertyEditor getEditorForCell( final int row, final int column ) {
      if ( String.class.equals( valueType ) ) {
        return null;
      }

      return FastPropertyEditorManager.findEditor( valueType );
    }

    public String getValueRole( final int row, final int column ) {
      return "Value"; // NON-NLS
    }

    public void setTableStyle( final TableStyle tableStyle ) {

    }

    public TableStyle getTableStyle() {
      return TableStyle.ASCENDING;
    }
  }

  private ElementMetaDataTable editor;
  private SingleValueMetaTableModel singleValueMetaTableModel;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public DefaultValueEditorPanel() {
    singleValueMetaTableModel = new SingleValueMetaTableModel();

    editor = new InstantEditingTable();
    editor.setDefaultRenderer( Boolean.class, new GenericCellRenderer() );
    editor.setDefaultEditor( Boolean.class,
      new DefaultCellEditor( new JComboBox( new Object[] { null, Boolean.TRUE, Boolean.FALSE } ) ) );
    editor.setModel( singleValueMetaTableModel );

    setLayout( new BorderLayout() );
    // Yes, without a scroll-pane, so that the table looks more like a text-field.
    add( editor, BorderLayout.CENTER );
    installDefaults();
  }

  protected void installDefaults() {
    // stealing the look-and-feel of a plain text-field.

    final Font f = editor.getFont();
    if ( ( f == null ) || ( f instanceof UIResource ) ) {
      editor.setFont( UIManager.getFont( "TextField.font" ) ); // NON-NLS
    }

    final Color bg = editor.getBackground();
    if ( ( bg == null ) || ( bg instanceof UIResource ) ) {
      editor.setBackground( UIManager.getColor( "TextField.background" ) ); // NON-NLS
      editor.setSelectionBackground( UIManager.getColor( "TextField.background" ) ); // NON-NLS
    }

    final Color fg = editor.getForeground();
    if ( ( fg == null ) || ( fg instanceof UIResource ) ) {
      editor.setForeground( UIManager.getColor( "TextField.foreground" ) ); // NON-NLS
      editor.setSelectionForeground( UIManager.getColor( "TextField.foreground" ) ); // NON-NLS
    }

    final Border b = editor.getBorder();
    if ( ( b == null ) || ( b instanceof UIResource ) ) {
      final Insets insets = UIManager.getInsets( "TextField.margin" ); // NON-NLS
      if ( insets == null ) {
        editor.setBorder( UIManager.getBorder( "TextField.border" ) ); // NON-NLS
      } else {
        editor.setBorder( BorderFactory.createCompoundBorder
          ( new EmptyBorder( insets ), UIManager.getBorder( "TextField.border" ) ) );//NON-NLS
      } // NON-NLS
      editor.setRowMargin( 2 );
      editor.setRowHeight( 20 );
    }

    editor.setShowGrid( false );
    editor.setShowHorizontalLines( false );
    editor.setShowVerticalLines( false );
  }

  public Object getValue() {
    final TableCellEditor editor1 = editor.getCellEditor();
    if ( editor1 != null ) {
      editor1.stopCellEditing();
    }
    return singleValueMetaTableModel.getValue();
  }

  public void setValue( final Object value, final Class valueType ) {
    singleValueMetaTableModel.setValue( value, valueType );
  }

  public void setValueType( final Class valueType, final String pattern, final TimeZone timeZone ) {
    final Object value = singleValueMetaTableModel.getValue();

    if ( valueType.isArray() == singleValueMetaTableModel.getValueType().isArray() ) {
      try {
        final String oldValueAsString = ConverterRegistry.toAttributeValue( value );
        final Object oldValueConverted = ConverterRegistry.toPropertyValue( oldValueAsString, valueType );
        singleValueMetaTableModel.setValue( oldValueConverted, valueType );
      } catch ( BeanException e ) {
        singleValueMetaTableModel.setValue( null, valueType );
      }
    } else if ( valueType.isArray() ) {
      try {
        final Object array = Array.newInstance( singleValueMetaTableModel.getValueType(), 1 );
        Array.set( array, 0, value );
        final String oldValueAsString = ConverterRegistry.toAttributeValue( array );
        final Object oldValueConverted = ConverterRegistry.toPropertyValue( oldValueAsString, valueType );
        singleValueMetaTableModel.setValue( oldValueConverted, valueType );
      } catch ( BeanException e ) {
        singleValueMetaTableModel.setValue( null, valueType );
      }
    } else // last case: metadata-table contains array
    {
      try {
        final String oldValueAsString = ConverterRegistry.toAttributeValue( Array.get( value, 0 ) );
        final Object oldValueConverted = ConverterRegistry.toPropertyValue( oldValueAsString, valueType );
        singleValueMetaTableModel.setValue( oldValueConverted, valueType );
      } catch ( Exception e ) {
        singleValueMetaTableModel.setValue( null, valueType );
      }
    }

    editor.applyLocaleSettings( new InjectDatePatternLocaleSettings( valueType, pattern, timeZone ) );
  }
}
