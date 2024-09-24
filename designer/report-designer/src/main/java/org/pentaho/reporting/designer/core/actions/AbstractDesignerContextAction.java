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

package org.pentaho.reporting.designer.core.actions;

import org.pentaho.reporting.designer.core.ReportDesignerContext;

import javax.swing.*;

public abstract class AbstractDesignerContextAction extends AbstractAction implements DesignerContextAction {
  private ReportDesignerContext reportDesignerContext;

  /**
   * Defines an <code>Action</code> object with a default description string and default icon.
   */
  protected AbstractDesignerContextAction() {
    setEnabled( false );
  }

  public void setReportDesignerContext( final ReportDesignerContext context ) {
    final ReportDesignerContext old = this.reportDesignerContext;
    this.reportDesignerContext = context;
    updateDesignerContext( old, reportDesignerContext );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  public void setVisible( final boolean visible ) {
    putValue( "visible", visible );
  }

  public boolean isVisible() {
    final Object visibleRaw = getValue( "visible" );
    return visibleRaw == null || Boolean.TRUE.equals( visibleRaw );
  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    setEnabled( newContext != null );
  }

}
