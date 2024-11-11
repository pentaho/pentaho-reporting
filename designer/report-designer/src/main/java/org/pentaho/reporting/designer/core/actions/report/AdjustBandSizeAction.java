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


package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class AdjustBandSizeAction extends AbstractReportContextAction {

  public AdjustBandSizeAction() {
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getLayoutBandsIcon() );
    putValue( Action.NAME, ActionMessages.getString( "AdjustBandSizeAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "AdjustBandSizeAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "AdjustBandSizeAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "AdjustBandSizeAction.Accelerator" ) );
  }


  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final AbstractReportDefinition definition = activeContext.getReportDefinition();
    iterateSection( definition );
    getReportDesignerContext().getView().redrawAll();
  }

  private void iterateSection( final Section s ) {
    final int elementCount = s.getElementCount();
    for ( int i = 0; i < elementCount; i++ ) {
      final ReportElement e = s.getElement( i );
      if ( e instanceof SubReport ) {
        continue;
      }
      if ( e instanceof RootLevelBand ) {
        final CachedLayoutData layoutData = ModelUtility.getCachedLayoutData( (Element) e );
        final double height = StrictGeomUtility.toExternalValue( layoutData.getHeight() );
        e.setAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, "visual-height", new Double( height ) ); // NON-NLS
      }

      if ( e instanceof Section ) {
        iterateSection( (Section) e );
      }

    }
  }
}
