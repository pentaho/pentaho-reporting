/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
