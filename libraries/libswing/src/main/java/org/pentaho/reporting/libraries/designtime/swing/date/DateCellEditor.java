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

package org.pentaho.reporting.libraries.designtime.swing.date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.EllipsisButton;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DateFormatter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import java.util.Locale;
import java.util.TimeZone;

public class DateCellEditor extends JPanel implements TableCellEditor {
  private class PickDateListener extends AbstractAction {

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private PickDateListener() {
      putValue( Action.NAME, ".." );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( dateWindow != null && dateWindow.isVisible() ) {
        return;
      }


      if ( dateWindow == null ) {
        dateChooserPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        dateWindow = new DateChooserPopupMenu( dateChooserPanel );
        dateWindow.setLayout( new BorderLayout() );
        dateWindow.add( dateChooserPanel, BorderLayout.CENTER );
        dateWindow.pack();
      }

      dateChooserPanel.setDateSelected( false );
      dateWindow.show( DateCellEditor.this, 0, pickDateButton.getHeight() );

    }
  }

  private class InternalDateUpdateHandler implements PropertyChangeListener {
    public void propertyChange( final PropertyChangeEvent changeEvent ) {
      if ( !DateChooserPanel.PROPERTY_DATE.equals( changeEvent.getPropertyName() ) ) {
        return;
      }

      final Date date = (Date) changeEvent.getNewValue();
      dateField.setValue( date );
      if ( dateChooserPanel.isDateSelected() ) {
        stopCellEditing();
      }
    }
  }

  private static final Log logger = LogFactory.getLog( DateCellEditor.class );
  private DateChooserPanel dateChooserPanel;
  private JFormattedTextField dateField;
  private DateChooserPopupMenu dateWindow;
  private JButton pickDateButton;
  private Class dateType;
  private EventListenerList listeners;
  private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

  /**
   * Constructs a new <code>DatePickerParameterComponent</code>.
   */
  public DateCellEditor( final Class dateType ) {
    this.listeners = new EventListenerList();
    this.dateType = dateType;
    if ( this.dateType.isArray() ) {
      this.dateType = this.dateType.getComponentType();
    }

    setLayout( new BorderLayout() );
    dateField = new JFormattedTextField();
    dateField.setColumns( 20 );
    dateField.setEditable( true );

    pickDateButton = new EllipsisButton( new PickDateListener() );

    add( dateField, BorderLayout.CENTER );
    add( pickDateButton, BorderLayout.EAST );
  }

  private void init() {
    if ( dateChooserPanel == null ) {
      dateChooserPanel = new DateChooserPanel( Calendar.getInstance(), true );
      dateChooserPanel.addPropertyChangeListener( DateChooserPanel.PROPERTY_DATE, new InternalDateUpdateHandler() );

      dateField.addPropertyChangeListener( "value", new PropertyChangeListener() {
        public void propertyChange( final PropertyChangeEvent evt ) {
          Date newValue = (Date) evt.getNewValue();
          newValue = newValue == null ? null : DateConverter.convertToDateType( newValue, dateType );
          dateChooserPanel.setDate( newValue, false );
          dateChooserPanel.setDateSelected( true );
        }
      } );
      dateField.addFocusListener( new FocusAdapter() {
        public void focusGained( final FocusEvent e ) {
          dateChooserPanel.setDateSelected( false );
        }
      } );
      if ( dateField.getFormatterFactory() == null ) {
        setDateFormat( createDateFormat( DEFAULT_FORMAT, Locale.getDefault(), TimeZone.getDefault() ) );
      }
    }
  }

  private static DateFormat createDateFormat( final String parameterFormatString,
                                              final Locale locale,
                                              final TimeZone timeZone ) {
    if ( parameterFormatString != null ) {
      try {
        final SimpleDateFormat dateFormat =
          new SimpleDateFormat( parameterFormatString, new DateFormatSymbols( locale ) );
        dateFormat.setTimeZone( timeZone );
        dateFormat.setLenient( true );
        return dateFormat;
      } catch ( Exception e ) {
        // boo! Not a valid pattern ...
        // its not a show-stopper either, as the pattern is a mere hint, not a mandatory thing
        logger.warn( "Parameter format-string for date-parameter was not a valid date-format-string", e );
      }
    }

    final SimpleDateFormat dateFormat = new SimpleDateFormat( DEFAULT_FORMAT, new DateFormatSymbols( locale ) );
    dateFormat.setTimeZone( timeZone );
    dateFormat.setLenient( true );
    return dateFormat;
  }

  private void setDate( final Object value ) {
    init();

    if ( dateWindow != null && dateWindow.isVisible() ) {
      dateWindow.setVisible( false );
    }
    if ( value == null ) {
      dateChooserPanel.setDate( null );
      return;
    }
    if ( value instanceof String ) {
      // if its a string, then it must be in the normalized parameter format.
      try {
        final String text = (String) value;
        if ( StringUtils.isEmpty( text ) ) {
          dateField.setText( null );
        } else {
          dateField.setValue( text );
        }
      } catch ( Exception e ) {
        logger.debug( "Unparsable date-string", e );
      }
    } else if ( value instanceof Date ) {
      final Date date = (Date) value;
      dateField.setValue( DateConverter.convertToDateType( date, dateType ) );
    } else {
      logger.debug( "Date-parameter must be set either as normalized date-string or as date-object: "
        + value + " [" + value.getClass() + "]" );
    }
  }


  /**
   * Sets an initial <code>value</code> for the editor.  This will cause the editor to <code>stopEditing</code> and lose
   * any partially edited value if the editor is editing when this method is called. <p>
   * <p/>
   * Returns the component that should be added to the client's <code>Component</code> hierarchy.  Once installed in the
   * client's hierarchy this component will then be able to draw and receive user input.
   *
   * @param table      the <code>JTable</code> that is asking the editor to edit; can be <code>null</code>
   * @param value      the value of the cell to be edited; it is up to the specific editor to interpret and draw the
   *                   value.  For example, if value is the string "true", it could be rendered as a string or it could
   *                   be rendered as a check box that is checked.  <code>null</code> is a valid value
   * @param isSelected true if the cell is to be rendered with highlighting
   * @param row        the row of the cell being edited
   * @param column     the column of the cell being edited
   * @return the component for editing
   */
  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    init();
    setDate( value );
    return this;
  }

  /**
   * Returns the value contained in the editor.
   *
   * @return the value contained in the editor
   */
  public Object getCellEditorValue() {
    init();
    return dateChooserPanel.getDate();
  }

  /**
   * Asks the editor if it can start editing using <code>anEvent</code>. <code>anEvent</code> is in the invoking
   * component coordinate system. The editor can not assume the Component returned by
   * <code>getCellEditorComponent</code> is installed.  This method is intended for the use of client to avoid the cost
   * of setting up and installing the editor component if editing is not possible. If editing can be started this method
   * returns true.
   *
   * @param anEvent the event the editor should use to consider whether to begin editing or not
   * @return true if editing can be started
   * @see #shouldSelectCell
   */
  public boolean isCellEditable( final EventObject anEvent ) {
    if ( anEvent instanceof MouseEvent ) {
      final MouseEvent mouseEvent = (MouseEvent) anEvent;
      return mouseEvent.getClickCount() >= 2 && mouseEvent.getButton() == MouseEvent.BUTTON1;
    }
    return true;
  }

  /**
   * Returns true if the editing cell should be selected, false otherwise. Typically, the return value is true, because
   * is most cases the editing cell should be selected.  However, it is useful to return false to keep the selection
   * from changing for some types of edits. eg. A table that contains a column of check boxes, the user might want to be
   * able to change those checkboxes without altering the selection.  (See Netscape Communicator for just such an
   * example) Of course, it is up to the client of the editor to use the return value, but it doesn't need to if it
   * doesn't want to.
   *
   * @param anEvent the event the editor should use to start editing
   * @return true if the editor would like the editing cell to be selected; otherwise returns false
   * @see #isCellEditable
   */
  public boolean shouldSelectCell( final EventObject anEvent ) {
    return true;
  }

  /**
   * Tells the editor to stop editing and accept any partially edited value as the value of the editor.  The editor
   * returns false if editing was not stopped; this is useful for editors that validate and can not accept invalid
   * entries.
   *
   * @return true if editing was stopped; false otherwise
   */
  public boolean stopCellEditing() {
    if ( dateChooserPanel.isDateSelected() ) {
      fireEditingStopped();
    }
    if ( dateWindow != null ) {
      dateWindow.setVisible( false );
    }
    return true;
  }

  /**
   * Tells the editor to cancel editing and not accept any partially edited value.
   */
  public void cancelCellEditing() {
    if ( dateWindow != null ) {
      dateWindow.setVisible( false );
    }
    fireEditingCanceled();
  }


  protected void fireEditingCanceled() {
    final CellEditorListener[] listeners = this.listeners.getListeners( CellEditorListener.class );
    final ChangeEvent event = new ChangeEvent( this );
    for ( int i = 0; i < listeners.length; i++ ) {
      final CellEditorListener listener = listeners[ i ];
      listener.editingCanceled( event );
    }
  }


  protected void fireEditingStopped() {
    final CellEditorListener[] listeners = this.listeners.getListeners( CellEditorListener.class );
    final ChangeEvent event = new ChangeEvent( this );
    for ( int i = 0; i < listeners.length; i++ ) {
      final CellEditorListener listener = listeners[ i ];
      listener.editingStopped( event );
    }
  }

  /**
   * Adds a listener to the list that's notified when the editor stops, or cancels editing.
   *
   * @param l the CellEditorListener
   */
  public void addCellEditorListener( final CellEditorListener l ) {
    listeners.add( CellEditorListener.class, l );
  }

  /**
   * Removes a listener from the list that's notified
   *
   * @param l the CellEditorListener
   */
  public void removeCellEditorListener( final CellEditorListener l ) {
    listeners.remove( CellEditorListener.class, l );
  }

  public void setDateFormat( final DateFormat timeFormat ) {
    if ( timeFormat == null ) {
      throw new NullPointerException();
    }

    dateField.setFormatterFactory( new JFormattedTextField.AbstractFormatterFactory() {
      public JFormattedTextField.AbstractFormatter getFormatter( final JFormattedTextField tf ) {
        return new DateFormatter( timeFormat ) {
          // allow to clear the field
          public Object stringToValue( final String text ) throws ParseException {
            return "".equals( text ) ? null : super.stringToValue( text );
          }
        };
      }
    } );

    if ( timeFormat instanceof SimpleDateFormat ) {
      final SimpleDateFormat dateFormat = (SimpleDateFormat) timeFormat;
      setToolTipText( dateFormat.toLocalizedPattern() );
    } else {
      setToolTipText( null );
    }
    if ( dateChooserPanel != null ) {
      setDate( getCellEditorValue() );
    }
  }
}
