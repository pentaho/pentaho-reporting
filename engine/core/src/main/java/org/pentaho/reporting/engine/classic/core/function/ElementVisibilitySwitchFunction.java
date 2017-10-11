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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

/**
 * A function that alternates between true and false for each item within a group. The functions value affects a defined
 * elements visibility. If the function evaluates to true, the named element is visible, else the element is invisible.
 * <p/>
 * Prior to Version 0.8.9, Elements in the Classic Engine did not define their own background color attribute. To create
 * a background, one had to place a rectangle shape element behind the element. To modify the band's background
 * directly, use the {@link org.pentaho.reporting.engine.classic.core.function.RowBandingFunction}.
 * <p/>
 * The ElementVisibilitySwitchFunction is used to trigger the visibility of an named element. If the element is your
 * background, you will get the alternating effect.
 * <p/>
 * The ElementVisibilitySwitchFunction defines two parameters:
 * <ul>
 * <li>element
 * <p>
 * The name of the element(s) in the itemband that should be modified. The element(s) must be named using the "name"
 * attribute.
 * </p>
 * <li>initial-state
 * <p>
 * The initial state of the function. (true or false) defaults to false. This is the reverse of the element's visiblity
 * (set to false to start with an visible element, set to true to hide the element in the first itemrow).
 * </p>
 * </ul>
 *
 * @author Thomas Morgner
 * @author Michael D'Amour
 * @deprecated Use the RowBandingFunction to modify the band's background color directly.
 */
public class ElementVisibilitySwitchFunction extends AbstractFunction implements PageEventListener,
    LayoutProcessorFunction {
  /**
   * The computed visibility value.
   */
  private transient boolean trigger;
  /**
   * An internal counter that counts the number of rows processed since the last visibility switch.
   */
  private transient int count;
  /**
   * If not null, this boolean flag defines the function state that should be used after page breaks. If not defined,
   * the initial state is used instead.
   */
  private Boolean newPageState;
  /**
   * A field defining the number of rows that must be processed before the visibility can switch again.
   */
  private int numberOfElements;
  /**
   * The name of the element that should be formatted.
   */
  private String element;
  /**
   * The initial visibility that is used on the start of a new report, a new group or a new page.
   */
  private boolean initialState;

  /**
   * A flag indicating whether the visiblity should be reset to its initial state when a group starts.
   */
  private boolean resetOnGroupStart;
  /**
   * A flag indicating whether the visiblity should be reset to its initial state when a page starts.
   */
  private boolean resetOnPageStart;

  /**
   * Default constructor.
   */
  public ElementVisibilitySwitchFunction() {
    numberOfElements = 1;
    resetOnGroupStart = true;
    resetOnPageStart = true;
  }

  /**
   * Receives notification that a page has started.
   *
   * @param event
   *          the event.
   */
  public void pageStarted( final ReportEvent event ) {
    if ( resetOnPageStart ) {
      // pagebreak = false;
      if ( newPageState == null ) {
        trigger = getInitialState();
      } else {
        trigger = newPageState.booleanValue();
      }
      count = 0;
      triggerVisibleState( event );
    }
  }

  /**
   * Receives notification that a page is completed.
   *
   * @param event
   *          The event.
   */
  public void pageFinished( final ReportEvent event ) {
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
    // pagebreak = false;
    trigger = !getInitialState();
    count = 0;
  }

  /**
   * Receives notification that the items are being processed. Sets the function value to false.
   * <P>
   * Following this event, there will be a sequence of itemsAdvanced events until the itemsFinished event is raised.
   *
   * @param event
   *          Information about the event.
   */
  public void itemsStarted( final ReportEvent event ) {
    if ( resetOnGroupStart ) {
      // pagebreak = false;
      trigger = !getInitialState();
      count = 0;
    }
  }

  /**
   * Triggers the visibility of an element. If the named element was visible at the last itemsAdvanced call, it gets now
   * invisible and vice versa. This creates the effect, that an element is printed every other line.
   *
   * @param event
   *          the report event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    triggerVisibleState( event );
  }

  /**
   * Triggers the visible state of the specified itemband element. If the named element was visible at the last call, it
   * gets now invisible and vice versa. This creates the effect, that an element is printed every other line.
   *
   * @param event
   *          the current report event.
   */
  private void triggerVisibleState( final ReportEvent event ) {
    if ( ( count % numberOfElements ) == 0 ) {
      trigger = ( !trigger );
    }
    count += 1;
    if ( element == null ) {
      return;
    }

    final ReportElement[] childs = event.getReport().getChildElementsByType( ItemBandType.INSTANCE );
    for ( int i = 0; i < childs.length; i++ ) {
      final ReportElement child = childs[i];
      final ReportElement[] elements = child.getChildElementsByName( getElement() );
      for ( int x = 0; x < elements.length; x++ ) {
        elements[x].getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, trigger );
      }
    }
  }

  /**
   * Returns the number of rows that must be processed before the visibility can switch again.
   *
   * @return a row count.
   */
  public int getNumberOfElements() {
    return numberOfElements;
  }

  /**
   * Defines the number of rows that must be processed before the visibility can switch again.
   *
   * @param numberOfElements
   *          a row count.
   */
  public void setNumberOfElements( final int numberOfElements ) {
    this.numberOfElements = numberOfElements;
  }

  /**
   * Gets the initial value for the visible trigger, either "true" or "false".
   *
   * @return the initial value for the trigger.
   * @deprecated use getInitialState instead.
   */
  public boolean getInitialTriggerValue() {
    return initialState;
  }

  /**
   * Returns the initial visibility that is used on the start of a new report, a new group or a new page.
   *
   * @return the initial value for the trigger.
   */
  public boolean getInitialState() {
    return initialState;
  }

  /**
   * Defines the initial visibility that is used on the start of a new report, a new group or a new page.
   *
   * @param initialState
   *          the initial value for the trigger.
   */
  public void setInitialState( final boolean initialState ) {
    this.initialState = initialState;
  }

  /**
   * Sets the element name. The name denotes an element or band within the root-band or the root-band itself. It is
   * possible to define multiple elements with the same name to apply the modification to all of these elements.
   *
   * @param name
   *          The element name.
   * @see org.pentaho.reporting.engine.classic.core.function.FunctionUtilities#findAllElements(org.pentaho.reporting
   *      .engine.classic.core.Band, String)
   */
  public void setElement( final String name ) {
    this.element = name;
  }

  /**
   * Returns the element name.
   *
   * @return The element name.
   * @see #setElement(String)
   */
  public String getElement() {
    return element;
  }

  /**
   * Returns the visibility state that should be used on new pages. This is only used if resetOnPageStart is set to
   * true. If this value is not defined, the initialState is used.
   *
   * @return the state on new pages.
   */
  public Boolean getNewPageState() {
    return newPageState;
  }

  /**
   * Defines the visibility state that should be used on new pages. This is only used if resetOnPageStart is set to
   * true. If this value is not defined, the initialState is used.
   *
   * @param newPageState
   *          the state on new pages or null to use the initialState.
   */
  public void setNewPageState( final Boolean newPageState ) {
    this.newPageState = newPageState;
  }

  /**
   * Returns the defined visibility of the element. Returns either true or false as java.lang.Boolean.
   *
   * @return the visibility of the element, either Boolean.TRUE or Boolean.FALSE.
   */
  public Object getValue() {
    if ( trigger ) {
      return Boolean.TRUE;
    } else {
      return Boolean.FALSE;
    }
  }
}
