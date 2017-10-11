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

import java.awt.Color;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * A function that alternates the background-color for each item-band within a group. If the function evaluates to true,
 * then the background of the named element will be set to the visible-color, else it will be set to the
 * invisible-color.
 * <p/>
 * The ElementVisibilitySwitchFunction defines two parameters:
 * <ul>
 * <li>element
 * <p>
 * The name of the element in the itemband that should be modified. The element(s) must be named using the "name"
 * attribute, if no name is given here, the ItemBand's background color is defined instead.
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
 */
public class RowBandingFunction extends AbstractFunction implements PageEventListener, LayoutProcessorFunction {
  private static final Log logger = LogFactory.getLog( RowBandingFunction.class );

  /**
   * The computed visibility value.
   */
  private transient boolean trigger;
  /**
   * An internal counter that counts the number of rows processed since the last visibility switch.
   */
  private transient int count;
  /**
   * A internal flag indicating whether a warning has been printed.
   */
  private transient boolean warned;
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
   * The background color that is used if the row-banding background should be visible.
   */
  private Color visibleBackground;
  /**
   * The background color that is used if the row-banding background should be invisible.
   */
  private Color invisibleBackground;

  private boolean rowbandingOnGroup;

  private boolean ignoreCrosstabMode;

  private String group;

  /**
   * Default constructor.
   */
  public RowBandingFunction() {
    warned = false;
    numberOfElements = 1;
  }

  /**
   * Receives notification that a page has started.
   *
   * @param event
   *          the event.
   */
  public void pageStarted( final ReportEvent event ) {
    if ( newPageState != null ) {
      trigger = !newPageState.booleanValue();
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
    if ( ignoreCrosstabMode ) {
      // If the user forces us into relational-mode, then we obey ..
      rowbandingOnGroup = StringUtils.isEmpty( group ) == false;
    } else {
      // check whether there is a crosstab
      if ( FunctionUtilities.isCrosstabDefined( event ) ) {
        // when we have one, we always rowband on a group instead of an item-count
        rowbandingOnGroup = true;
      } else {
        // we only row-band on an item-count if the group is not empty.
        rowbandingOnGroup = StringUtils.isEmpty( group ) == false;
      }
    }

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
    if ( rowbandingOnGroup == false ) {
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
    if ( rowbandingOnGroup == false && StringUtils.isEmpty( group ) ) {
      triggerVisibleState( event );
    }
  }

  public void groupStarted( final ReportEvent event ) {
    if ( rowbandingOnGroup == false ) {
      return;
    }

    if ( StringUtils.isEmpty( group ) ) {
      final Group group = event.getReport().getGroup( event.getState().getCurrentGroupIndex() );
      if ( group instanceof CrosstabRowGroup ) {
        final GroupBody body = group.getBody();
        if ( body instanceof CrosstabColumnGroupBody ) {
          triggerVisibleStateCrosstab( event );
        }
      }
    } else {
      if ( FunctionUtilities.isDefinedGroup( group, event ) ) {
        triggerVisibleStateCrosstab( event );
      }
    }
  }

  /**
   * Triggers the visible state of the specified itemband element. If the named element was visible at the last call, it
   * gets now invisible and vice versa. This creates the effect, that an element is printed every other line.
   *
   * @param event
   *          the current report event.
   */
  private void triggerVisibleState( final ReportEvent event ) {
    // avoid divide by zero exception
    if ( numberOfElements == 0 ) {
      return;
    }

    if ( ( count % numberOfElements ) == 0 ) {
      trigger = ( !trigger );
    }
    count += 1;

    final ItemBand itemBand = event.getReport().getItemBand();
    if ( itemBand == null ) {
      return;
    }

    if ( element == null ) {
      if ( trigger ) {
        itemBand.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, visibleBackground );
      } else {
        itemBand.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, invisibleBackground );
      }
    } else {
      final Element[] e = FunctionUtilities.findAllElements( itemBand, getElement() );
      if ( e.length > 0 ) {
        for ( int i = 0; i < e.length; i++ ) {
          if ( trigger ) {
            e[i].getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, visibleBackground );
          } else {
            e[i].getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, invisibleBackground );
          }
        }
      } else {
        if ( warned == false ) {
          RowBandingFunction.logger.warn( "The Band does not contain an element named " + getElement() );
          warned = true;
        }
      }
    }
  }

  /**
   * Triggers the visible state of the specified itemband element. If the named element was visible at the last call, it
   * gets now invisible and vice versa. This creates the effect, that an element is printed every other line.
   *
   * @param event
   *          the current report event.
   */
  private void triggerVisibleStateCrosstab( final ReportEvent event ) {
    if ( ( count % numberOfElements ) == 0 ) {
      trigger = ( !trigger );
    }
    count += 1;

    final CrosstabCellBody cellBody = event.getReport().getCrosstabCellBody();
    if ( cellBody == null ) {
      return;
    }

    if ( element == null ) {
      final int elementCount = cellBody.getElementCount();
      for ( int i = 1; i < elementCount; i += 1 ) {
        final Element cell = cellBody.getElement( i );
        if ( trigger ) {
          cell.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, visibleBackground );
        } else {
          cell.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, invisibleBackground );
        }
      }
    } else {
      final Element[] e = FunctionUtilities.findAllElements( cellBody, getElement() );
      if ( e.length > 0 ) {
        for ( int i = 0; i < e.length; i++ ) {
          if ( trigger ) {
            e[i].getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, visibleBackground );
          } else {
            e[i].getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, invisibleBackground );
          }
        }
      } else {
        if ( warned == false ) {
          RowBandingFunction.logger.warn( "The cell-body does not contain an element named " + getElement() );
          warned = true;
        }
      }
    }
  }

  public void summaryRowSelection( final ReportEvent event ) {
    if ( rowbandingOnGroup == false ) {
      return;
    }

    if ( StringUtils.isEmpty( group ) ) {
      final Group group = event.getReport().getGroup( event.getState().getCurrentGroupIndex() );
      if ( group instanceof CrosstabRowGroup ) {
        final GroupBody body = group.getBody();
        if ( body instanceof CrosstabColumnGroupBody ) {
          if ( Boolean.TRUE.equals( group.getAttribute( AttributeNames.Crosstab.NAMESPACE,
              AttributeNames.Crosstab.PRINT_SUMMARY ) ) ) {
            triggerVisibleStateCrosstab( event );
          }
        }
      }
    } else {
      if ( FunctionUtilities.isDefinedGroup( group, event ) ) {
        final Group group = event.getReport().getGroup( event.getState().getCurrentGroupIndex() );
        if ( Boolean.TRUE.equals( group.getAttribute( AttributeNames.Crosstab.NAMESPACE,
            AttributeNames.Crosstab.PRINT_SUMMARY ) ) ) {
          triggerVisibleStateCrosstab( event );
        }
      }
    }
  }

  public boolean isIgnoreCrosstabMode() {
    return ignoreCrosstabMode;
  }

  public void setIgnoreCrosstabMode( final boolean ignoreCrosstabMode ) {
    this.ignoreCrosstabMode = ignoreCrosstabMode;
  }

  /**
   * Returns the background color that is used if the row-banding background should be invisible.
   *
   * @return a color.
   */
  public Color getInvisibleBackground() {
    return invisibleBackground;
  }

  /**
   * Defines the background color that is used if the row-banding background should be invisible.
   *
   * @param invisibleBackground
   *          a color.
   */
  public void setInvisibleBackground( final Color invisibleBackground ) {
    this.invisibleBackground = invisibleBackground;
  }

  /**
   * Returns the background color that is used if the row-banding background should be visible.
   *
   * @return a color.
   */
  public Color getVisibleBackground() {
    return visibleBackground;
  }

  /**
   * Defines the background color that is used if the row-banding background should be visible.
   *
   * @param visibleBackground
   *          a color.
   */
  public void setVisibleBackground( final Color visibleBackground ) {
    this.visibleBackground = visibleBackground;
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

  public String getGroup() {
    return group;
  }

  public void setGroup( final String group ) {
    this.group = group;
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
