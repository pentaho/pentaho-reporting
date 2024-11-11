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


package org.pentaho.reporting.engine.classic.wizard.ui.xul.components;

import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.impl.XulEventHandler;

/**
 * This panel contains all the buttons.
 *
 * @author Thomas Morgner
 */
public class WizardControllerPanel {
  private static final String WIZARD_CONTROLLER_OVERLAY =
    "org/pentaho/reporting/engine/classic/wizard/ui/xul/res/wizard_controller_panel_Overlay.xul"; //$NON-NLS-1$
  private WizardController controller;

  public WizardControllerPanel( final WizardController controller ) {
    if ( controller == null ) {
      throw new NullPointerException();
    }
    this.controller = controller;
  }

  public WizardController getController() {
    return controller;
  }

  /**
   * @param mainWizardContainer
   */
  public void addContent( final XulDomContainer mainWizardContainer ) throws XulException {
    mainWizardContainer.loadOverlay( WIZARD_CONTROLLER_OVERLAY );
    mainWizardContainer.addEventHandler( (XulEventHandler) controller );
  }
}
