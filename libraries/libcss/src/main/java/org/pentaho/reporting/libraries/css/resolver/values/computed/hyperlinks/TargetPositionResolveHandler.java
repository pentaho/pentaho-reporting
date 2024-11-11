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


package org.pentaho.reporting.libraries.css.resolver.values.computed.hyperlinks;

import org.pentaho.reporting.libraries.css.keys.hyperlinks.TargetPosition;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 21.12.2005, 11:36:34
 *
 * @author Thomas Morgner
 */
public class TargetPositionResolveHandler extends ConstantsResolveHandler {
  public TargetPositionResolveHandler() {
    addNormalizeValue( TargetPosition.ABOVE );
    addNormalizeValue( TargetPosition.BACK );
    addNormalizeValue( TargetPosition.BEHIND );
    addNormalizeValue( TargetPosition.FRONT );
    setFallback( TargetPosition.ABOVE );
  }


}
