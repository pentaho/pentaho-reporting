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
