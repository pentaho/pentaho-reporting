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


package org.pentaho.reporting.libraries.css.resolver.tokens.resolved;

import org.pentaho.reporting.libraries.css.resolver.tokens.computed.ComputedToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.types.TextType;

/**
 * Creation-Date: 12.06.2006, 14:38:29
 *
 * @author Thomas Morgner
 */
public class ResolvedStringToken implements TextType {
  private ComputedToken parent;
  private String text;

  public ResolvedStringToken( final ComputedToken parent, final String text ) {
    if ( parent == null ) {
      throw new NullPointerException();
    }
    this.parent = parent;
    this.text = text;
  }

  public ComputedToken getParent() {
    return parent;
  }

  public String getText() {
    return text;
  }
}
