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
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public final class ShowPreviewPaneAction extends AbstractDesignerContextAction implements ToggleStateAction {
  private class PreviewSelectionUpdateListener implements PropertyChangeListener {
    private PreviewSelectionUpdateListener() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      recomputeEnabled();
    }
  }

  private PreviewSelectionUpdateListener selectionUpdateListener;

  public ShowPreviewPaneAction() {
    selectionUpdateListener = new PreviewSelectionUpdateListener();
    putValue( Action.SELECTED_KEY, Boolean.FALSE );
    putValue( Action.NAME, ActionMessages.getString( "ShowPreviewPaneAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ShowPreviewPaneAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ShowPreviewPaneAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ShowPreviewPaneAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext reportDesignerContext = getReportDesignerContext();
    if ( reportDesignerContext == null ) {
      return;
    }
    reportDesignerContext.getView().setPreviewVisible( reportDesignerContext.getView().isPreviewVisible() == false );
  }

  protected String getPropertyName() {
    return ReportDesignerView.PREVIEW_VISIBLE_PROPERTY;
  }

  protected boolean recomputeEnabled() {
    final ReportDesignerContext reportDesignerContext = getReportDesignerContext();
    if ( reportDesignerContext == null ) {
      setSelected( false );
      return false;
    }
    setSelected( reportDesignerContext.getView().isPreviewVisible() );
    return true;
  }

  public void setSelected( final boolean selected ) {
    putValue( Action.SELECTED_KEY, selected );

    if ( selected == false ) {
      putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ShowPreviewPaneAction.Text" ) );
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getPreviewIcon() );
    } else {
      putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ShowEditorPaneAction.Text" ) );
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getEditIcon() );
    }

  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    if ( oldContext != null ) {
      oldContext.getView()
        .removePropertyChangeListener( ReportDesignerView.PREVIEW_VISIBLE_PROPERTY, selectionUpdateListener );
      oldContext.removePropertyChangeListener( ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, selectionUpdateListener );
    }
    super.updateDesignerContext( oldContext, newContext );
    if ( newContext != null ) {
      newContext.getView()
        .addPropertyChangeListener( ReportDesignerView.PREVIEW_VISIBLE_PROPERTY, selectionUpdateListener );
      newContext.addPropertyChangeListener( ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, selectionUpdateListener );
    }

    recomputeEnabled();
  }

  public boolean isSelected() {
    return Boolean.TRUE.equals( getValue( Action.SELECTED_KEY ) );
  }

}
