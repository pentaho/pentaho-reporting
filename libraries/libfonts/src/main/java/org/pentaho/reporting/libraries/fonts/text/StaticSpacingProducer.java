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


package org.pentaho.reporting.libraries.fonts.text;

/**
 * Creation-Date: 11.06.2006, 18:37:39
 *
 * @author Thomas Morgner
 */
public class StaticSpacingProducer implements SpacingProducer {
  private Spacing spacing;

  public StaticSpacingProducer( final Spacing spacing ) {
    if ( spacing == null ) {
      this.spacing = Spacing.EMPTY_SPACING;
    } else {
      this.spacing = spacing;
    }
  }

  public Spacing createSpacing( final int codePoint ) {
    return spacing;
  }


  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
