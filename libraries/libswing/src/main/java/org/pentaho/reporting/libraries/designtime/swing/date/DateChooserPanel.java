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

package org.pentaho.reporting.libraries.designtime.swing.date;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

/**
 * A panel that allows the user to select a date.
 *
 * @author David Gilbert
 * @noinspection UnnecessaryBoxing
 */
public class DateChooserPanel extends JPanel {

  private class MonthSelectionAction implements ActionListener {
    private MonthSelectionAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final JComboBox c = (JComboBox) e.getSource();

      if ( !refreshing ) {
        // In most cases, changing the month will not change the selected
        // day.  But if the selected day is 29, 30 or 31 and the newly
        // selected month doesn't have that many days, we revert to the
        // last day of the newly selected month...
        final int dayOfMonth = dateView.get( Calendar.DAY_OF_MONTH );
        dateView.set( Calendar.DAY_OF_MONTH, 1 );
        dateView.set( Calendar.MONTH, c.getSelectedIndex() );
        final int maxDayOfMonth = dateView.getActualMaximum( Calendar.DAY_OF_MONTH );
        dateView.set( Calendar.DAY_OF_MONTH, Math.min( dayOfMonth, maxDayOfMonth ) );
        setDateSelected( false );
        if ( yearOrMonthChangeSelectsDate ) {
          setDate( dateView.getTime() );
        } else {
          selectedDate = dateView.getTime();
          refreshButtons();
        }
      }
    }
  }

  private class YearSelectionAction implements ActionListener {
    private YearSelectionAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( !refreshing ) {
        final JComboBox c = (JComboBox) e.getSource();
        final String yT = (String) c.getSelectedItem();
        if ( yT == null ) {
          return;
        }

        try {
          final int y = Integer.parseInt( yT );
          // in most cases, changing the year will not change the
          // selected day.  But if the selected day is Feb 29, and the
          // newly selected year is not a leap year, we revert to
          // Feb 28...
          final int dayOfMonth = dateView.get( Calendar.DAY_OF_MONTH );
          dateView.set( Calendar.DAY_OF_MONTH, 1 );
          dateView.set( Calendar.YEAR, y );
          final int maxDayOfMonth = dateView.getActualMaximum( Calendar.DAY_OF_MONTH );
          dateView.set( Calendar.DAY_OF_MONTH, Math.min( dayOfMonth, maxDayOfMonth ) );
          setDateSelected( false );
          if ( yearOrMonthChangeSelectsDate ) {
            setDate( dateView.getTime() );
            refreshYearSelector();
          } else {
            selectedDate = dateView.getTime();
            refreshYearSelector();
            refreshButtons();
          }
        } catch ( NumberFormatException nf ) {
          // ignore user input ..
        }
      }
    }
  }

  private class SelectTodayAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private SelectTodayAction() {
      putValue( Action.NAME, Messages.getInstance().getString( "DateChooserPanel.Today" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      setDateSelected( true );
      setDate( new Date() );
    }
  }

  private class SelectDayAction extends AbstractAction {
    private Date number;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private SelectDayAction( final Date selectedDate, final String number ) {
      this.number = selectedDate;
      putValue( Action.NAME, number );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      setDateSelected( true );
      setDate( number );
    }
  }

  public final static String PROPERTY_DATE = "date";

  /**
   * The date used to display the current selection in the panel. This date cannot be null, while the selected date can
   * be null.
   */
  private Calendar dateView;

  private Date selectedDate;

  /**
   * The color for the selected date.
   */
  private Color chosenDateButtonColor;

  /**
   * The color for dates in the current month.
   */
  private Color chosenMonthButtonColor;

  /**
   * The color for dates that are visible, but not in the current month.
   */
  private Color chosenOtherButtonColor;

  /**
   * The first day-of-the-week.
   */
  private int firstDayOfWeek;

  /**
   * The range used for selecting years.
   */
  private int yearSelectionRange = 20;

  /**
   * The font used to display the date.
   */
  private Font dateFont = new Font( "SansSerif", Font.PLAIN, 10 ); //$NON-NLS-1$

  /**
   * A combo for selecting the month.
   */
  private JComboBox monthSelector;

  /**
   * A combo for selecting the year.
   */
  private JComboBox yearSelector;

  /**
   * An array of buttons used to display the days-of-the-month.
   */
  private JButton[] buttons;

  /**
   * A flag that indicates whether or not we are currently refreshing the buttons.
   */
  private boolean refreshing = false;

  /**
   * The ordered set of all seven days of a week, beginning with the 'firstDayOfWeek'.
   */
  private int[] WEEK_DAYS;

  private boolean dateSelected;
  private boolean yearOrMonthChangeSelectsDate;

  /**
   * Constructs a new date chooser panel, using today's date as the initial selection.
   */
  public DateChooserPanel() {
    this( Calendar.getInstance(), false );
  }

  /**
   * Constructs a new date chooser panel.
   *
   * @param calendar     the calendar controlling the date.
   * @param controlPanel a flag that indicates whether or not the 'today' button should appear on the panel.
   */
  public DateChooserPanel( final Calendar calendar,
                           final boolean controlPanel ) {

    super( new BorderLayout() );

    this.chosenDateButtonColor = SystemColor.textHighlight; //$NON-NLS-1$
    this.chosenMonthButtonColor = SystemColor.control; //$NON-NLS-1$
    this.chosenOtherButtonColor = SystemColor.controlShadow; //$NON-NLS-1$

    // the default date is today...
    this.dateView = calendar;
    this.firstDayOfWeek = calendar.getFirstDayOfWeek();
    this.WEEK_DAYS = new int[ 7 ];
    for ( int i = 0; i < 7; i++ ) {
      this.WEEK_DAYS[ i ] = ( ( this.firstDayOfWeek + i - 1 ) % 7 ) + 1;
    }

    add( constructSelectionPanel(), BorderLayout.NORTH );
    add( getCalendarPanel(), BorderLayout.CENTER );
    if ( controlPanel ) {
      add( constructControlPanel(), BorderLayout.SOUTH );
    }
    setDateSelected( false );
    setDate( calendar.getTime() );
  }

  public boolean isDateSelected() {
    return dateSelected;
  }

  public void setDateSelected( final boolean dateSeleccted ) {
    this.dateSelected = dateSeleccted;
  }

  /**
   * Sets the date chosen in the panel.
   *
   * @param theDate the new date.
   */
  public void setDate( final Date theDate, boolean firePC ) {
    final Date oldDate = this.selectedDate;
    this.selectedDate = theDate;
    if ( theDate != null ) {
      this.dateView.setTime( theDate );
      refreshing = true;
      this.monthSelector.setSelectedIndex( this.dateView.get( Calendar.MONTH ) );
      refreshing = false;
      refreshYearSelector();
      refreshButtons();
    }
    if ( firePC ) {
      if ( ObjectUtilities.equal( oldDate, theDate ) ) {
        firePropertyChange( PROPERTY_DATE, null, theDate );
      } else {
        firePropertyChange( PROPERTY_DATE, oldDate, theDate );
      }
    }
  }

  public void setDate( final Date theDate ) {
    setDate( theDate, true );
  }

  /**
   * Returns the date selected in the panel.
   *
   * @return the selected date.
   */
  public Date getDate() {
    return selectedDate;
  }

  /**
   * Returns a panel of buttons, each button representing a day in the month. This is a sub-component of the DatePanel.
   *
   * @return the panel.
   */
  private JPanel getCalendarPanel() {

    final JPanel p = new JPanel( new GridLayout( 7, 7 ) );
    final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
    final String[] weekDays = dateFormatSymbols.getShortWeekdays();

    for ( int i = 0; i < this.WEEK_DAYS.length; i++ ) {
      p.add( new JLabel( weekDays[ this.WEEK_DAYS[ i ] ], SwingConstants.CENTER ) );
    }

    this.buttons = new JButton[ 42 ];
    for ( int i = 0; i < 42; i++ ) {
      final JButton b = new JButton( "" );
      b.setMargin( new Insets( 1, 1, 1, 1 ) );
      b.setName( Integer.toString( i ) );
      b.setFont( this.dateFont );
      b.setFocusPainted( false );
      b.putClientProperty( "JButton.buttonType", "square" ); //$NON-NLS-1$ $NON-NLS-2$
      this.buttons[ i ] = b;
      p.add( b );
    }
    return p;

  }

  /**
   * Returns the button color according to the specified date.
   *
   * @param theDate the date.
   * @return the color.
   */
  private Color getButtonBackgroundColor( final Calendar theDate ) {
    if ( equalDates( theDate, this.dateView ) ) {
      return this.chosenDateButtonColor;
    } else if ( theDate.get( Calendar.MONTH ) == this.dateView.get( Calendar.MONTH ) ) {
      return this.chosenMonthButtonColor;
    } else {
      return this.chosenOtherButtonColor;
    }
  }

  /**
   * Returns true if the two dates are equal (time of day is ignored).
   *
   * @param c1 the first date.
   * @param c2 the second date.
   * @return boolean.
   */
  private boolean equalDates( final Calendar c1, final Calendar c2 ) {
    if ( ( c1.get( Calendar.DATE ) == c2.get( Calendar.DATE ) )
      && ( c1.get( Calendar.MONTH ) == c2.get( Calendar.MONTH ) )
      && ( c1.get( Calendar.YEAR ) == c2.get( Calendar.YEAR ) ) ) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns the first date that is visible in the grid.  This should always be in the month preceding the month of the
   * selected date.
   *
   * @return the date.
   */
  private Calendar getFirstVisibleDate() {
    final Calendar c = Calendar.getInstance();
    c.set( this.dateView.get( Calendar.YEAR ), this.dateView.get(
      Calendar.MONTH ), 1 );
    c.add( Calendar.DATE, -1 );
    while ( c.get( Calendar.DAY_OF_WEEK ) != getFirstDayOfWeek() ) {
      c.add( Calendar.DATE, -1 );
    }
    c.set( Calendar.HOUR_OF_DAY, 0 );
    c.set( Calendar.MINUTE, 0 );
    c.set( Calendar.SECOND, 0 );
    c.set( Calendar.MILLISECOND, 0 );
    c.set( Calendar.ZONE_OFFSET, 3 );
    return c;
  }

  /**
   * Returns the first day of the week (controls the labels in the date panel).
   *
   * @return the first day of the week.
   */
  private int getFirstDayOfWeek() {
    return this.firstDayOfWeek;
  }

  /**
   * Update the button labels and colors to reflect date selection.
   */
  private void refreshButtons() {
    final Calendar c = getFirstVisibleDate();
    for ( int i = 0; i < 42; i++ ) {
      final JButton b = this.buttons[ i ];
      b.setAction( new SelectDayAction( c.getTime(), String.valueOf( c.get( Calendar.DATE ) ) ) );
      b.setBackground( getButtonBackgroundColor( c ) );
      b.setFont( getButtonFontStyle( c ) );
      c.add( Calendar.DATE, 1 );
    }
  }

  private Font getButtonFontStyle( final Calendar theDate ) {
    if ( equalDates( theDate, this.dateView ) ) {
      return this.dateFont.deriveFont( Font.BOLD );
    } else if ( theDate.get( Calendar.MONTH ) == this.dateView.get( Calendar.MONTH ) ) {
      return this.dateFont;
    } else {
      return this.dateFont.deriveFont( Font.ITALIC );
    }
  }

  /**
   * Changes the contents of the year selection JComboBox to reflect the chosen date and the year range.
   */
  private void refreshYearSelector() {
    if ( !this.refreshing ) {
      this.refreshing = true;
      this.yearSelector.removeAllItems();
      final String[] years = getYears( this.dateView.get( Calendar.YEAR ) );
      for ( int i = 0; i < years.length; i++ ) {
        this.yearSelector.addItem( years[ i ] );
      }
      this.yearSelector.setSelectedItem( String.valueOf( this.dateView.get( Calendar.YEAR ) ) );
      this.refreshing = false;
    }
  }

  /**
   * Returns a vector of years preceding and following the specified year. The number of years preceding and following
   * is determined by the yearSelectionRange attribute.
   *
   * @param chosenYear the selected year.
   * @return a vector of years.
   */
  private String[] getYears( final int chosenYear ) {
    final int size = this.yearSelectionRange * 2 + 1;
    final int start = chosenYear - this.yearSelectionRange;

    final String[] years = new String[ size ];
    for ( int i = 0; i < size; i++ ) {
      years[ i ] = String.valueOf( i + start );
    }
    return years;
  }

  /**
   * Constructs a panel containing two JComboBoxes (for the month and year) and a button (to reset the date to TODAY).
   *
   * @return the panel.
   */
  private JPanel constructSelectionPanel() {
    final JPanel p = new JPanel();

    final int minMonth = this.dateView.getMinimum( Calendar.MONTH );
    final int maxMonth = this.dateView.getMaximum( Calendar.MONTH );
    final String[] months = new String[ maxMonth - minMonth + 1 ];
    System.arraycopy( new DateFormatSymbols().getMonths(), minMonth, months, 0, months.length );

    this.monthSelector = new JComboBox( months );
    this.monthSelector.addActionListener( new MonthSelectionAction() );
    p.add( this.monthSelector );

    this.yearSelector = new JComboBox( getYears( 0 ) );
    this.yearSelector.setEditable( true );
    this.yearSelector.addActionListener( new YearSelectionAction() );
    p.add( this.yearSelector );

    return p;
  }

  /**
   * Returns a panel that appears at the bottom of the calendar panel - contains a button for selecting today's date.
   *
   * @return the panel.
   */
  private JPanel constructControlPanel() {

    final JPanel p = new JPanel();
    p.setBorder( BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) );
    p.add( new JButton( new SelectTodayAction() ) );
    return p;

  }

  /**
   * Returns the color for the currently selected date.
   *
   * @return a color.
   */
  public Color getChosenDateButtonColor() {
    return this.chosenDateButtonColor;
  }

  /**
   * Redefines the color for the currently selected date.
   *
   * @param chosenDateButtonColor the new color
   */
  public void setChosenDateButtonColor( final Color chosenDateButtonColor ) {
    if ( chosenDateButtonColor == null ) {
      throw new NullPointerException( "UIColor must not be null." );
    }
    final Color oldValue = this.chosenDateButtonColor;
    this.chosenDateButtonColor = chosenDateButtonColor;
    refreshButtons();
    firePropertyChange( "chosenDateButtonColor", oldValue, chosenDateButtonColor ); //$NON-NLS-1$
  }

  /**
   * Returns the color for the buttons representing the current month.
   *
   * @return the color for the current month.
   */
  public Color getChosenMonthButtonColor() {
    return this.chosenMonthButtonColor;
  }

  /**
   * Defines the color for the buttons representing the current month.
   *
   * @param chosenMonthButtonColor the color for the current month.
   */
  public void setChosenMonthButtonColor( final Color chosenMonthButtonColor ) {
    if ( chosenMonthButtonColor == null ) {
      throw new NullPointerException( "UIColor must not be null." );
    }
    final Color oldValue = this.chosenMonthButtonColor;
    this.chosenMonthButtonColor = chosenMonthButtonColor;
    refreshButtons();
    firePropertyChange( "chosenMonthButtonColor", oldValue, chosenMonthButtonColor ); //$NON-NLS-1$
  }

  /**
   * Returns the color for the buttons representing the other months.
   *
   * @return a color.
   */
  public Color getChosenOtherButtonColor() {
    return this.chosenOtherButtonColor;
  }

  /**
   * Redefines the color for the buttons representing the other months.
   *
   * @param chosenOtherButtonColor a color.
   */
  public void setChosenOtherButtonColor( final Color chosenOtherButtonColor ) {
    if ( chosenOtherButtonColor == null ) {
      throw new NullPointerException( "UIColor must not be null." );
    }
    final Color oldValue = this.chosenOtherButtonColor;
    this.chosenOtherButtonColor = chosenOtherButtonColor;
    refreshButtons();
    firePropertyChange( "chosenOtherButtonColor", oldValue, chosenOtherButtonColor ); //$NON-NLS-1$
  }

  /**
   * Returns the range of years available for selection (defaults to 20).
   *
   * @return The range.
   */
  public int getYearSelectionRange() {
    return this.yearSelectionRange;
  }

  /**
   * Sets the range of years available for selection.
   *
   * @param yearSelectionRange the range.
   */
  public void setYearSelectionRange( final int yearSelectionRange ) {
    final int oldYearSelectionRange = this.yearSelectionRange;
    this.yearSelectionRange = yearSelectionRange;
    refreshYearSelector();
    firePropertyChange( "yearSelectionRange", oldYearSelectionRange, yearSelectionRange ); //$NON-NLS-1$
  }
}
