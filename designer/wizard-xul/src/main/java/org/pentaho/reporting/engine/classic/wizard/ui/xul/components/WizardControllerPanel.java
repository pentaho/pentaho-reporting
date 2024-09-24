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
