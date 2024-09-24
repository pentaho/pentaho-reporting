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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model;

import org.pentaho.reporting.engine.classic.core.Band;

/**
 * Creation-Date: 20.08.2007, 19:32:27
 *
 * @author Thomas Morgner
 */
public class SourceChunk {
  private Band rootBand;

  public SourceChunk( final Band band ) {
    this.rootBand = band;
  }

  public Band getRootBand() {
    return rootBand;
  }
}
