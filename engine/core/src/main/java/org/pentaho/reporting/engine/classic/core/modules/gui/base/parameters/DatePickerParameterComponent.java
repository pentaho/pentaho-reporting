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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.designtime.swing.date.DateChooserPanel;
import org.pentaho.reporting.libraries.designtime.swing.date.DateChooserPopupMenu;

public class DatePickerParameterComponent extends JPanel implements ParameterComponent {
  private class DateUpdateHandler implements ChangeListener {
    private String parameterName;

    private DateUpdateHandler( final String parameterName ) {
      this.parameterName = parameterName;
    }

    public void stateChanged( final ChangeEvent e ) {
      final Object value = updateContext.getParameterValue( parameterName );
      if ( value != null ) {
        try {
          adjustingToExternalInput = true;
          setDate( value );
        } finally {
          adjustingToExternalInput = false;
        }
      }
    }
  }

  private class PickDateListener extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private PickDateListener() {
      final URL iconRes =
          getClass().getResource( "/org/pentaho/reporting/engine/classic/core/modules/gui/base/date/datepicker.png" ); // NON-NLS
      if ( iconRes != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( iconRes ) );
      } else {
        putValue( Action.NAME, ".." );
      }
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
      dateWindow.show( DatePickerParameterComponent.this, 0, pickDateButton.getHeight() );
    }
  }

  private class InternalDateUpdateHandler implements PropertyChangeListener {
    public void propertyChange( final PropertyChangeEvent changeEvent ) {
      if ( adjustingToExternalInput ) {
        return;
      }

      if ( !DateChooserPanel.PROPERTY_DATE.equals( changeEvent.getPropertyName() ) ) {
        return;
      }

      final Date date = (Date) changeEvent.getNewValue();
      if ( date == null ) {
        dateField.setText( null );
      } else {
        dateField.setText( sdf.format( date ) );
      }
      if ( dateChooserPanel.isDateSelected() ) {
        dateWindow.setVisible( false );
      }
      updateContext.setParameterValue( parameterName, date );
    }
  }

  private static final Log logger = LogFactory.getLog( DatePickerParameterComponent.class );
  private DateChooserPanel dateChooserPanel;
  private JTextField dateField;
  private DateFormat sdf;
  private ParameterUpdateContext updateContext;
  private JPopupMenu dateWindow;
  private JButton pickDateButton;
  private JSpinner hourSpinner;
  private JSpinner minuteSpinner;
  private JSpinner secondSpinner;
  private JComboBox<String> amPmComboBox;
  private boolean displayTimeSelector;
  private Class dateType;
  private String parameterName;
  private boolean adjustingToExternalInput;

  /**
   * Constructs a new <code>DatePickerParameterComponent</code>.
   *
   * @param entry
   *          the parameter-definition for which we create an input component.
   * @param updateContext
   *          the update context, which resyncs parameters on changes.
   */
  public DatePickerParameterComponent( final ParameterDefinitionEntry entry, final ParameterContext parameterContext,
      final ParameterUpdateContext updateContext, boolean displayTimeSelector ) {
    this.parameterName = entry.getName();
    this.updateContext = updateContext;
    this.displayTimeSelector = displayTimeSelector;
    this.dateType = entry.getValueType();
    if ( this.dateType.isArray() ) {
      this.dateType = this.dateType.getComponentType();
    }
    String formatString = entry.getTranslatedParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );

    final String timeZoneSpec =
        entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TIMEZONE,
            parameterContext );
    final Locale locale = parameterContext.getResourceBundleFactory().getLocale();
    final TimeZone timeZone =
        TextComponentEditHandler.createTimeZone( timeZoneSpec, parameterContext.getResourceBundleFactory()
            .getTimeZone() );
    sdf = createDateFormat( formatString, locale, timeZone );

    final Calendar calendar = Calendar.getInstance();
    calendar.setTimeZone( timeZone );

    dateChooserPanel = new DateChooserPanel( calendar, true );
    dateChooserPanel.addPropertyChangeListener( DateChooserPanel.PROPERTY_DATE, new InternalDateUpdateHandler() );

    dateField = new JTextField();
    dateField.setColumns( 20 );

    final TextComponentEditHandler listener =
        new TextComponentEditHandler( entry.getValueType(), entry.getName(), dateField, updateContext, sdf );
    dateField.getDocument().addDocumentListener( listener );
    dateField.addActionListener( listener );

    setLayout( new BorderLayout() );
    dateField.setEditable( true );

    pickDateButton = new JButton( new PickDateListener() );

    final JPanel datePanel = new JPanel( new FlowLayout() );
    datePanel.add( dateField );
    datePanel.add( pickDateButton );

    // Add time selector components if `displayTimeSelector` is true
    if (displayTimeSelector) {
      final JPanel dateTimePanel = datePanel;
      hourSpinner = new JSpinner(new SpinnerNumberModel(12, 1, 12, 1));
      minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
      secondSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
      // Set double-digit format for the spinners
      hourSpinner.setEditor(new JSpinner.NumberEditor(hourSpinner, "00"));
      minuteSpinner.setEditor(new JSpinner.NumberEditor(minuteSpinner, "00"));
      secondSpinner.setEditor(new JSpinner.NumberEditor(secondSpinner, "00"));
      amPmComboBox = new JComboBox<>(new String[]{"AM", "PM"});
      // Retrieve the parameter value from the previous context
      Object value = updateContext.getParameterValue(parameterName);
      if (value instanceof Date) {
        calendar.setTime((Date) value);
        hourSpinner.setValue(calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR));
        minuteSpinner.setValue(calendar.get(Calendar.MINUTE));
        secondSpinner.setValue(calendar.get(Calendar.SECOND));
        amPmComboBox.setSelectedIndex(calendar.get(Calendar.AM_PM));
      }
      dateTimePanel.add(new JLabel("HH:"));
      dateTimePanel.add(hourSpinner);
      dateTimePanel.add(new JLabel("MM:"));
      dateTimePanel.add(minuteSpinner);
      dateTimePanel.add(new JLabel("SS:"));
      dateTimePanel.add(secondSpinner);
      dateTimePanel.add(amPmComboBox);

      // Add listeners to dynamically update the time
      hourSpinner.addChangeListener(e -> updateTime());
      minuteSpinner.addChangeListener(e -> updateTime());
      secondSpinner.addChangeListener(e -> updateTime());
      amPmComboBox.addActionListener(e -> updateTime());

      dateTimePanel.setVisible(true);
      add( dateTimePanel, BorderLayout.WEST );
    } else {
      add( datePanel, BorderLayout.WEST );
    }

    this.updateContext.addChangeListener( new DateUpdateHandler( parameterName ) );
  }
  private void updateTime() {
    Date selectedDate = dateChooserPanel.getDate();
    if (selectedDate != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeZone(sdf.getTimeZone());
      calendar.setTime(selectedDate);
      if (displayTimeSelector) {
        calendar.set(Calendar.HOUR, (Integer) hourSpinner.getValue() == 12 ? 0 : (Integer) hourSpinner.getValue());
        calendar.set(Calendar.MINUTE, (Integer) minuteSpinner.getValue());
        calendar.set(Calendar.SECOND, (Integer) secondSpinner.getValue());
        calendar.set(Calendar.AM_PM, amPmComboBox.getSelectedIndex());
        selectedDate = calendar.getTime();
      }
      dateField.setText(sdf.format(selectedDate));
      updateContext.setParameterValue(parameterName, selectedDate);
    }
  }

  DateFormat createDateFormat( final String parameterFormatString, final Locale locale, final TimeZone timeZone ) {
    if ( parameterFormatString != null ) {
      try {
        final SimpleDateFormat dateFormat = new SimpleDateFormat( parameterFormatString, locale );
        dateFormat.setTimeZone( timeZone );
        dateFormat.setLenient( false );
        return dateFormat;
      } catch ( Exception e ) {
        // boo! Not a valid pattern ...
        // its not a show-stopper either, as the pattern is a mere hint, not a mandatory thing
        logger.warn( "Parameter format-string for date-parameter was not a valid date-format-string", e ); // NON-NLS
      }
    }

    DateFormat dateTimeInstance = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG, locale );
    dateTimeInstance.setTimeZone( timeZone );
    dateTimeInstance.setLenient( false );

    return dateTimeInstance;
  }

  private void setDate( final Object value ) {
    if ( dateWindow != null && dateWindow.isVisible() ) {
      dateWindow.setVisible( false );
    }
    if ( value == null || "".equals( value ) ) {
      return;
    }
    if ( value instanceof String ) {
      // if its a string, then it must be in the normalized parameter format.
      try {
        final Date date = (Date) ConverterRegistry.toPropertyValue( (String) value, dateType );
        dateChooserPanel.setDateSelected( false );
        dateChooserPanel.setDate( date );
        dateField.setText( sdf.format( date ) );

        if (displayTimeSelector) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(sdf.getTimeZone());
        calendar.setTime(date);
        hourSpinner.setValue(calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR));
        minuteSpinner.setValue(calendar.get(Calendar.MINUTE));
        secondSpinner.setValue(calendar.get(Calendar.SECOND));
        amPmComboBox.setSelectedIndex(calendar.get(Calendar.AM_PM));
        }

        if ( adjustingToExternalInput == false ) {
          updateContext.setParameterValue( parameterName, dateChooserPanel.getDate() );
        }
      } catch ( Exception e ) {
        logger.debug( "Unparsable date-string", e ); // NON-NLS
      }
    } else if ( value instanceof Date ) {
      final Date date = (Date) value;
      dateChooserPanel.setDateSelected( false );
      dateChooserPanel.setDate( date );
      dateField.setText( sdf.format( date ) );

      if ( adjustingToExternalInput == false ) {
        updateContext.setParameterValue( parameterName, dateChooserPanel.getDate() );
      }
    } else {
      logger.debug( "Date-parameter must be set either as normalized date-string or as date-object: " + // NON-NLS
          value + " [" + value.getClass() + "]" );
    }

  }

  public JComponent getUIComponent() {
    return this;
  }

  public void initialize() throws ReportDataFactoryException {
    final Object value = updateContext.getParameterValue( parameterName );
    if ( value != null ) {
      try {
        adjustingToExternalInput = true;
        setDate( value );
      } finally {
        adjustingToExternalInput = false;

      }
    }

  }
}
