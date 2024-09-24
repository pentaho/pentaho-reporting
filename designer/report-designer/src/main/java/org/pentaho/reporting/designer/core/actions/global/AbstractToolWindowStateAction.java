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
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public abstract class AbstractToolWindowStateAction extends AbstractDesignerContextAction {
  private class ViewStateChangeHandler implements PropertyChangeListener {
    private ViewStateChangeHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      recomputeEnabled();
    }
  }

  private ViewStateChangeHandler changeHandler;

  protected AbstractToolWindowStateAction() {
    changeHandler = new ViewStateChangeHandler();
  }

  protected abstract String getPropertyName();

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    if ( oldContext != null ) {
      oldContext.getView().removePropertyChangeListener( getPropertyName(), changeHandler );
    }
    super.updateDesignerContext( oldContext, newContext );
    if ( newContext != null ) {
      newContext.getView().addPropertyChangeListener( getPropertyName(), changeHandler );
    }
    if ( newContext == null ) {
      setEnabled( false );
      return;
    }
    recomputeEnabled();
  }

  protected abstract boolean recomputeEnabled();
}
