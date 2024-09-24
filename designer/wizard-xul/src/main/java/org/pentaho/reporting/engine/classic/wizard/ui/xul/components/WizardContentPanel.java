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
import org.pentaho.ui.xul.binding.BindingFactory;

public class WizardContentPanel {
  private WizardController controller;

  private BindingFactory bf;

  public WizardContentPanel( final WizardController controller ) {
    this.controller = controller;
    this.bf = controller.getBindingFactory();
  }

  /**
   * @param mainWizardContainer
   * @throws XulException
   */
  public void addContent( final XulDomContainer mainWizardContainer ) throws XulException {
    for ( int i = 0; i < this.controller.getStepCount(); i++ ) {
      controller.getStep( i ).setBindingFactory( bf );  // This must be done before creation of presentation component
      controller.getStep( i ).setDocument( mainWizardContainer.getDocumentRoot() );
      controller.getStep( i ).createPresentationComponent( mainWizardContainer );
    }
    new WizardControllerPanel( controller ).addContent( mainWizardContainer );
  }

}
