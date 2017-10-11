/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
