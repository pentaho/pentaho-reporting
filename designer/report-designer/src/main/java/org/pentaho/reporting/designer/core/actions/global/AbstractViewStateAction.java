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

package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public abstract class AbstractViewStateAction extends AbstractReportContextAction {
  private class ViewStateChangeHandler implements PropertyChangeListener {
    private ViewStateChangeHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      setEnabled( recomputeEnabled() );
    }
  }

  private ViewStateChangeHandler changeHandler;

  protected AbstractViewStateAction() {
    changeHandler = new ViewStateChangeHandler();
  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    if ( oldContext != null ) {
      oldContext.getView().removePropertyChangeListener( ReportDesignerView.REPORT_DESIGNER_VIEW_STATE_PROPERTY,
        changeHandler );
    }
    super.updateDesignerContext( oldContext, newContext );
    if ( newContext != null ) {
      newContext.getView().addPropertyChangeListener( ReportDesignerView.REPORT_DESIGNER_VIEW_STATE_PROPERTY,
        changeHandler );
    }
  }

  protected void updateActiveContext( final ReportRenderContext oldContext, final ReportRenderContext newContext ) {
    super.updateActiveContext( oldContext, newContext );
    if ( getReportDesignerContext() == null ) {
      setEnabled( false );
      return;
    }

    setEnabled( recomputeEnabled() );
  }

  protected abstract boolean recomputeEnabled();
}
