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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

public class RootLevelContentReadHandler extends BandReadHandler {
  private AbstractRootLevelBand rootLevelBand;

  public RootLevelContentReadHandler( final ElementType elementType, final AbstractRootLevelBand rootLevelBand )
    throws ParseException {
    super( elementType, false );
    this.rootLevelBand = rootLevelBand;
    initialize( elementType );
  }

  protected Element createElement() throws ParseException {
    return rootLevelBand;
  }
}
