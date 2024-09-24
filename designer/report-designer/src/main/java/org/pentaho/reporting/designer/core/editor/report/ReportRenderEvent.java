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

package org.pentaho.reporting.designer.core.editor.report;

import java.util.EventObject;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ReportRenderEvent extends EventObject {
  public ReportRenderEvent( final RootBandRenderingModel source ) {
    super( source );
  }

  public RootBandRenderingModel getModel() {
    return (RootBandRenderingModel) getSource();
  }
}
