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

package org.pentaho.reporting.engine.classic.core.sorting;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortOrderReportPreProcessor extends AbstractReportPreProcessor {
  public SortOrderReportPreProcessor() {
  }

  public MasterReport performPreDataProcessing( final MasterReport definition,
      final DefaultFlowController flowController ) throws ReportProcessingException {
    return performInternalPreDataProcessing( definition );
  }

  public SubReport performPreDataProcessing( final SubReport definition, final DefaultFlowController flowController )
    throws ReportProcessingException {
    return performInternalPreDataProcessing( definition );
  }

  protected <T extends AbstractReportDefinition> T performInternalPreDataProcessing( final T report ) {
    Object attribute = report.getAutoSort();
    if ( !Boolean.TRUE.equals( attribute ) ) {
      return report;
    }

    final List<SortConstraint> sort = computeSortConstraints( report );
    report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMPUTED_SORT_CONSTRAINTS,
        Collections.unmodifiableList( sort ) );
    return report;
  }

  public List<SortConstraint> computeSortConstraints( final AbstractReportDefinition report ) {
    return collectSortData( report.getRootGroup(), new ArrayList<SortConstraint>() );
  }

  private List<SortConstraint> collectSortData( final Group rootGroup, final ArrayList<SortConstraint> sorts ) {
    sorts.addAll( rootGroup.getSortingConstraint() );
    GroupBody body = rootGroup.getBody();
    Group group = body.getGroup();
    if ( group == null ) {
      // todo: Allow additional sorting based on item/detail data?
      return sorts;
    }

    return collectSortData( group, sorts );
  }
}
