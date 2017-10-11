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

package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.LinkedHashMap;
import java.util.Map;

public class InlineSubreportProcessor {
  private static InstanceID DUMMY_SUBREPORT_MARKER = new InstanceID();
  private static InlineSubreportMarker[] EMPTY_MARKERS = new InlineSubreportMarker[0];

  private InlineSubreportProcessor() {
  }

  private static InlineSubreportMarker[] collectMarkers( final ProcessState state, final RootLevelBand rootLevelBand )
    throws ReportProcessingException {
    final Map<InstanceID, InlineSubreportMarker> markers = collectSubReportMarkers( state, rootLevelBand );
    if ( markers == null || markers.size() == 0 ) {
      return EMPTY_MARKERS;
    }

    return markers.values().toArray( new InlineSubreportMarker[markers.size()] );
  }

  public static ProcessState processInline( ProcessState state, final RootLevelBand rootLevelBand )
    throws ReportProcessingException {
    final InlineSubreportMarker[] markers = collectMarkers( state, rootLevelBand );
    if ( markers.length == 0 ) {
      return state;
    }

    state.getLayoutProcess().getOutputFunction().clearInlineSubreports( SubReportProcessType.INLINE );
    final int index = findNextIndex( markers, SubReportProcessType.INLINE, 0 );
    if ( index == -1 ) {
      return state;
    }

    try {
      // this recreates the process key.
      state = state.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }

    // we need to execute the state until it returns ..
    final ReportStateKey processKey = state.getProcessKey();
    ProcessState process = new ProcessState();
    process.initializeForSubreport( markers, index, state );
    while ( processKey.equals( process.getProcessKey() ) == false ) {
      process = process.advance();
      if ( processKey.equals( process.getProcessKey() ) ) {
        throw new IllegalStateException( "You cannot switch contexts when not being commited!" );
      }
      process = process.commit();
    }
    return process;
  }

  public static boolean hasSubReports( final ProcessState state, final RootLevelBand rootLevelBand )
    throws ReportProcessingException {
    final Map<InstanceID, InlineSubreportMarker> markers = collectSubReportMarkers( state, rootLevelBand );
    if ( markers == null || markers.size() == 0 ) {
      return false;
    }
    return true;
  }

  public static ProcessState processBandedSubReports( final ProcessState state, final RootLevelBand rootLevelBand )
    throws ReportProcessingException {
    final InlineSubreportMarker[] markers = collectMarkers( state, rootLevelBand );
    if ( markers.length == 0 ) {
      return state;
    }

    state.getLayoutProcess().getOutputFunction().clearInlineSubreports( SubReportProcessType.BANDED );
    final int index = findNextIndex( markers, SubReportProcessType.BANDED, 0 );
    if ( index == -1 ) {
      return state;
    }

    final ProcessState pstate = new ProcessState();
    pstate.initializeForSubreport( markers, index, state );
    return pstate;
  }

  private static Map<InstanceID, InlineSubreportMarker> collectSubReportMarkers( final ProcessState state,
      final RootLevelBand rootLevelBand ) throws ReportProcessingException {
    final Map<InstanceID, InlineSubreportMarker> list = collectSubReportMarkers( (Section) rootLevelBand, null );
    if ( list == null ) {

      final InlineSubreportMarker[] subreports = state.getLayoutProcess().getOutputFunction().getInlineSubreports();
      if ( subreports.length == 0 ) {
        return null;
      }
      final Map<InstanceID, InlineSubreportMarker> map = new LinkedHashMap<InstanceID, InlineSubreportMarker>();
      for ( int i = 0; i < subreports.length; i++ ) {
        final InlineSubreportMarker subreport = subreports[i];
        map.put( subreport.getSubreport().getObjectID(), subreport );
      }
      return map;
    }

    final InlineSubreportMarker[] markers = state.getLayoutProcess().getOutputFunction().getInlineSubreports();
    for ( int i = 0; i < markers.length; i++ ) {
      final InlineSubreportMarker marker = markers[i];
      list.put( marker.getSubreport().getObjectID(), marker );
    }
    return list;
  }

  private static Map collectBandedSubReportMarkers( final RootLevelBand rootLevelBand,
      Map<InstanceID, InlineSubreportMarker> list ) throws ReportProcessingException {
    final int count = rootLevelBand.getSubReportCount();
    for ( int i = 0; i < count; i++ ) {
      final SubReport element = rootLevelBand.getSubReport( i );
      if ( list == null ) {
        list = new LinkedHashMap<InstanceID, InlineSubreportMarker>();
      }
      list.put( element.getObjectID(), new InlineSubreportMarker( element, null, SubReportProcessType.BANDED ) );
    }
    return list;
  }

  private static Map<InstanceID, InlineSubreportMarker> collectSubReportMarkers( final Section rootLevelBand,
      Map<InstanceID, InlineSubreportMarker> list ) throws ReportProcessingException {
    if ( rootLevelBand instanceof RootLevelBand ) {
      list = collectBandedSubReportMarkers( (RootLevelBand) rootLevelBand, list );
    }

    final int count = rootLevelBand.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = rootLevelBand.getElement( i );
      if ( element instanceof SubReport ) {
        if ( list == null ) {
          list = new LinkedHashMap<InstanceID, InlineSubreportMarker>();
        }
        list.put( element.getObjectID(), new InlineSubreportMarker( (SubReport) element, DUMMY_SUBREPORT_MARKER,
            SubReportProcessType.INLINE ) );
      } else if ( element instanceof Section ) {
        list = collectSubReportMarkers( (Section) element, list );
      }
    }
    return list;
  }

  public static int findNextIndex( final InlineSubreportMarker[] markers, final SubReportProcessType type,
      final int startIndex ) {
    for ( int i = startIndex; i < markers.length; i++ ) {
      final InlineSubreportMarker marker = markers[i];
      if ( marker.getProcessType() == type ) {
        return i;
      }
    }
    return -1;
  }
}
