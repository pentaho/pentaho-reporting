/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
