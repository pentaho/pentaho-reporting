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

package org.pentaho.reporting.libraries.css.resolver.values.computed.text;

import org.pentaho.reporting.libraries.css.keys.text.Direction;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 21.12.2005, 15:07:34
 *
 * @author Thomas Morgner
 */
public class DirectionResolveHandler extends ConstantsResolveHandler {
  public DirectionResolveHandler() {
    addNormalizeValue( Direction.LTR );
    addNormalizeValue( Direction.RTL );
    setFallback( Direction.LTR );
  }

}
