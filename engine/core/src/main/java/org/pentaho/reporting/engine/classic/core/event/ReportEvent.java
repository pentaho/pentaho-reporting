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

package org.pentaho.reporting.engine.classic.core.event;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.states.ReportState;

import java.util.EventObject;

/**
 * Represents a report event.
 * <p/>
 * Includes information which {@link org.pentaho.reporting.engine.classic.core.states.ReportState} generated the event.
 *
 * @author Thomas Morgner
 */
public class ReportEvent extends EventObject {
  /**
   * The event type constant, that the report initialize event is invoked.
   */
  public static final int REPORT_INITIALIZED = 0x01;
  /**
   * The event type constant, that the page start event is invoked.
   */
  public static final int PAGE_STARTED = 0x02;
  /**
   * The event type constant, that the report start event is invoked.
   */
  public static final int REPORT_STARTED = 0x04;
  /**
   * The event type constant, that a group start event is invoked.
   */
  public static final int GROUP_STARTED = 0x08;
  /**
   * The event type constant, that the items started event is invoked.
   */
  public static final int ITEMS_STARTED = 0x10;
  /**
   * The event type constant, that the items advanced event is invoked.
   */
  public static final int ITEMS_ADVANCED = 0x20;
  /**
   * The event type constant, that the items finished event is invoked.
   */
  public static final int ITEMS_FINISHED = 0x40;
  /**
   * The event type constant, that a group finished event is invoked.
   */
  public static final int GROUP_FINISHED = 0x80;
  /**
   * The event type constant, that the report finished event is invoked.
   */
  public static final int REPORT_FINISHED = 0x100;
  /**
   * The event type constant, that the report done event is invoked.
   */
  public static final int REPORT_DONE = 0x200;
  /**
   * The event type constant, that the page finished event is invoked.
   */
  public static final int PAGE_FINISHED = 0x400;

  /**
   * This event is fired when a summary row is going to be printed. This is a crosstab only event, and happens after the
   * group-finished event. Crosstab-aware functions must now select the result for the given group.
   */
  public static final int SUMMARY_ROW = 0x800;
  public static final int SUMMARY_ROW_START = 0x1800;
  public static final int SUMMARY_ROW_END = 0x2800;

  /**
   * This is a layout-helper event. It is only passed down to layouter functions. This event is fired before a
   * group-finished event is fired and helps the layouter to close the group-body so that keep-together and widows can
   * compute their state properly.
   */
  public static final int GROUP_BODY_FINISHED = 0x8000;

  private static final int RESERVED_BLOCK_1 = 0xC000;

  /**
   * Crosstab marker flag. This marks events that are part of a crosstab processing.
   */
  public static final int CROSSTABBING = 0x10000;
  public static final int CROSSTABBING_TABLE = 0x110000;
  public static final int CROSSTABBING_OTHER = 0x210000;
  public static final int CROSSTABBING_ROW = 0x410000;
  public static final int CROSSTABBING_COL = 0x810000;

  /**
   * A flag that marks the given event as a deep-traversing event. This flag is an indicator, that the event did not
   * originate in this report, so it propably came from a parent or child report.
   */
  public static final int DEEP_TRAVERSING_EVENT = 0x4000000;
  public static final int NO_PARENT_PASSING_EVENT = 0x8000000;
  public static final int ARTIFICIAL_EVENT_CODE = 0x80000000;

  /**
   * The event type for this event.
   */
  private int type;

  /**
   * The state that generated the event in the first place. For master reports this is the same as the event source, for
   * master-reports receiving events from a sub-report, this is the subreport's report state.
   */
  private ReportState originatingState;

  /**
   * Creates a new <code>ReportEvent</code>.
   *
   * @param state
   *          the current state of the processed report (<code>null</code> not permmitted).
   * @param type
   *          the event type for this event object.
   */
  public ReportEvent( final ReportState state, final int type ) {
    super( state );
    if ( state == null ) {
      throw new NullPointerException( "ReportEvent(ReportState) : null not permitted." );
    }
    if ( type <= 0 ) {
      throw new IllegalArgumentException( "This is not a valid EventType: " + type );
    }
    this.type = type;
    this.originatingState = state;
  }

  /**
   * Creates a new <code>ReportEvent</code>.
   *
   * @param state
   *          the current state of the processed report (<code>null</code> not permmitted).
   * @param originatingState
   *          the original state that generated the event.
   * @param type
   *          the event type for this event object.
   */
  public ReportEvent( final ReportState state, final ReportState originatingState, final int type ) {
    this( state, type );
    if ( originatingState == null ) {
      throw new NullPointerException( "Originating state can never be null." );
    }
    this.originatingState = originatingState;
  }

  /**
   * Returns the event type. The type is made up of a combination of several flags.
   *
   * @return the event type.
   */
  public int getType() {
    return type;
  }

  /**
   * Returns the <code>ReportState</code>, which is the source of the event.
   *
   * @return the state (never <code>null</code>).
   */
  public ReportState getState() {
    return (ReportState) getSource();
  }

  /**
   * Returns the originating state. The originating state is the state, that generated the event in the first place. For
   * master reports this is the same as the event source, for master-reports receiving events from a sub-report, this is
   * the subreport's report state.
   *
   * @return the originating state.
   */
  public ReportState getOriginatingState() {
    return originatingState;
  }

  /**
   * Returns the report that generated the event.
   * <P>
   * This is a convenience method that extracts the report from the report state.
   *
   * @return the report.
   */
  public ReportDefinition getReport() {
    return getState().getReport();
  }

  /**
   * Returns the currently assigned dataRow for this event.
   * <p/>
   * The {@link DataRow} is used to access the fields of the
   * {@link org.pentaho.reporting.engine.classic.core.filter .DataSource} and other functions and expressions within the
   * current row of the report.
   *
   * @return the data row.
   */
  public DataRow getDataRow() {
    return getState().getDataRow();
  }

  /**
   * Returns the current function level.
   *
   * @return the function level.
   */
  public int getLevel() {
    return getState().getLevel();
  }

  /**
   * Checks whether the deep-traversing flag is set. An event is deep-traversing, if it did not originate in the current
   * report.
   *
   * @return true, if this is a deep-traversing element, false otherwise.
   */
  public boolean isDeepTraversing() {
    return ( type & ReportEvent.DEEP_TRAVERSING_EVENT ) == ReportEvent.DEEP_TRAVERSING_EVENT;
  }

  /**
   * @noinspection HardCodedStringLiteral
   */
  public static String translateStateCode( final int code ) {
    final StringBuffer b = new StringBuffer();
    if ( ( code & REPORT_INITIALIZED ) == REPORT_INITIALIZED ) {
      b.append( "Report-Init" );
    } else if ( ( code & PAGE_STARTED ) == PAGE_STARTED ) {
      b.append( "Page-Start" );
      final int i = code & ~PAGE_STARTED;
      if ( i != 0 ) {
        b.append( "[" );
        b.append( translateStateCode( i ) );
        b.append( "]" );
      }
    }
    if ( ( code & REPORT_STARTED ) == REPORT_STARTED ) {
      b.append( "Report-Start" );
    }
    if ( ( code & GROUP_STARTED ) == GROUP_STARTED ) {
      b.append( "Group-Start" );
    }
    if ( ( code & ITEMS_STARTED ) == ITEMS_STARTED ) {
      b.append( "Items-Start" );
    }
    if ( ( code & ITEMS_ADVANCED ) == ITEMS_ADVANCED ) {
      b.append( "Items-Advanced" );
    }
    if ( ( code & ITEMS_FINISHED ) == ITEMS_FINISHED ) {
      b.append( "Items-Finished" );
    }
    if ( ( code & GROUP_BODY_FINISHED ) == GROUP_BODY_FINISHED ) {
      b.append( "Group-Body-Finished" );
    }
    if ( ( code & GROUP_FINISHED ) == GROUP_FINISHED ) {
      b.append( "Group-Finished" );
    }
    if ( ( code & REPORT_FINISHED ) == REPORT_FINISHED ) {
      b.append( "Report-Finished" );
    }
    if ( ( code & REPORT_DONE ) == REPORT_DONE ) {
      b.append( "Report-Done" );
    }
    if ( ( code & PAGE_FINISHED ) == PAGE_FINISHED ) {
      b.append( "Page-Finished" );
    }
    if ( ( code & SUMMARY_ROW ) == SUMMARY_ROW ) {
      b.append( "Summary Row" );
      if ( ( code & SUMMARY_ROW_START ) == SUMMARY_ROW_START ) {
        b.append( " [Start]" );
      }
      if ( ( code & SUMMARY_ROW_END ) == SUMMARY_ROW_END ) {
        b.append( " [End]" );
      }
    }
    if ( ( code & CROSSTABBING ) == CROSSTABBING ) {
      b.append( " Crosstab" );
      if ( ( code & CROSSTABBING_TABLE ) == CROSSTABBING_TABLE ) {
        b.append( ":Table" );
      }
      if ( ( code & CROSSTABBING_OTHER ) == CROSSTABBING_OTHER ) {
        b.append( ":Other" );
      }
      if ( ( code & CROSSTABBING_ROW ) == CROSSTABBING_ROW ) {
        b.append( ":Row" );
      }
      if ( ( code & CROSSTABBING_COL ) == CROSSTABBING_COL ) {
        b.append( ":Col" );
      }
    }
    if ( ( code & ( DEEP_TRAVERSING_EVENT | NO_PARENT_PASSING_EVENT | ARTIFICIAL_EVENT_CODE ) ) != 0 ) {
      b.append( " (" );
      if ( ( code & DEEP_TRAVERSING_EVENT ) == DEEP_TRAVERSING_EVENT ) {
        b.append( " DeepTraverse" );
      }
      if ( ( code & NO_PARENT_PASSING_EVENT ) == NO_PARENT_PASSING_EVENT ) {
        b.append( " NoParent" );
      }
      if ( ( code & ARTIFICIAL_EVENT_CODE ) == ARTIFICIAL_EVENT_CODE ) {
        b.append( " Artificial" );
      }
      b.append( " )" );
    }
    return b.toString();
  }
}
