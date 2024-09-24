/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportState;

import java.io.Serializable;
import java.util.BitSet;

public class RepeatingFooterValidator implements Cloneable, Serializable {
  private BitSet lastPrintedRepeatFooterSignature;
  private BitSet computingSignature;

  public RepeatingFooterValidator() {
    this.lastPrintedRepeatFooterSignature = new BitSet();
    this.computingSignature = new BitSet();
  }

  public boolean isRepeatFooterValid( final ReportEvent event, final LayouterLevel[] levels ) {
    final BitSet bitSet = computeRepeatingFooterValidity( event, levels );
    if ( bitSet.equals( lastPrintedRepeatFooterSignature ) ) {
      return true;
    } else {
      this.lastPrintedRepeatFooterSignature.clear();
      this.lastPrintedRepeatFooterSignature.or( bitSet );
      return false;
    }
  }

  private BitSet computeRepeatingFooterValidity( final ReportEvent event, final LayouterLevel[] levels ) {
    final BitSet bits = computingSignature;
    bits.clear();

    int count = 0;

    final ReportDefinition report = event.getReport();
    final ReportState state = event.getState();
    final int groupsPrinted = state.getPresentationGroupIndex();
    final int levelCount = levels.length;

    for ( int i = 0; i < levelCount; i++ ) {
      final LayouterLevel level = levels[i];
      final ReportDefinition def = level.getReportDefinition();

      for ( int gidx = level.getGroupIndex(); gidx >= 0; gidx -= 1 ) {
        final Group g = def.getGroup( gidx );
        if ( g instanceof RelationalGroup ) {
          final RelationalGroup rg = (RelationalGroup) g;
          final GroupFooter footer = rg.getFooter();
          bits.set( count, DefaultOutputFunction.isGroupSectionPrintable( footer, true, true ) );
          count += 1;
        }
      }

      if ( level.isInItemGroup() ) {
        final DetailsFooter detailsFooter = def.getDetailsFooter();
        if ( detailsFooter != null ) {
          bits.set( count, DefaultOutputFunction.isGroupSectionPrintable( detailsFooter, true, true ) );
          count += 1;
        }
      }

    }

    /**
     * Repeating group header are only printed while ItemElements are processed.
     */
    for ( int gidx = groupsPrinted; gidx >= 0; gidx -= 1 ) {
      final Group g = report.getGroup( gidx );
      if ( g instanceof RelationalGroup ) {
        final RelationalGroup rg = (RelationalGroup) g;
        final GroupFooter footer = rg.getFooter();
        bits.set( count, DefaultOutputFunction.isGroupSectionPrintable( footer, false, true ) );
        count += 1;
      }
    }

    if ( state.isInItemGroup() ) {
      final DetailsFooter footer = report.getDetailsFooter();
      bits.set( count, DefaultOutputFunction.isGroupSectionPrintable( footer, false, true ) );
      count += 1;
    }

    return bits;
  }

  public RepeatingFooterValidator clone() {
    try {
      final RepeatingFooterValidator copy = (RepeatingFooterValidator) super.clone();
      copy.lastPrintedRepeatFooterSignature = (BitSet) lastPrintedRepeatFooterSignature.clone();
      copy.computingSignature = (BitSet) computingSignature.clone();
      return copy;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( cne );
    }
  }
}
