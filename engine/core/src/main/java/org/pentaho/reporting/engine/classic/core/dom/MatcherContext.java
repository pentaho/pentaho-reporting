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


package org.pentaho.reporting.engine.classic.core.dom;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class MatcherContext {
  private boolean matchSubReportChilds;
  private boolean singleSelectionHint;
  private InstanceID rootParent;

  public MatcherContext() {
  }

  public MatcherContext( final boolean matchSubReports, final InstanceID rootParent ) {
    this.matchSubReportChilds = matchSubReports;
    this.rootParent = rootParent;
  }

  public boolean isMatchSubReportChilds() {
    return matchSubReportChilds;
  }

  public void setMatchSubReportChilds( final boolean matchSubReportChilds ) {
    this.matchSubReportChilds = matchSubReportChilds;
  }

  public InstanceID getRootParent() {
    return rootParent;
  }

  public void setRootParent( final InstanceID rootParent ) {
    this.rootParent = rootParent;
  }

  public Section getParent( final ReportElement e ) {
    if ( e.getObjectID() == rootParent ) {
      return null;
    }
    return e.getParentSection();
  }

  public void setSingleSelectionHint( final boolean singleSelectionHint ) {
    this.singleSelectionHint = singleSelectionHint;
  }

  public boolean isSingleSelectionHint() {
    return singleSelectionHint;
  }
}
