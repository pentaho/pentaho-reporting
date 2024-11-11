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


package org.pentaho.reporting.libraries.fonts.text.font;

/**
 * Creation-Date: 11.06.2006, 18:30:42
 *
 * @author Thomas Morgner
 */
public class NoKerningProducer implements KerningProducer {
  public NoKerningProducer() {
  }

  public long getKerning( final int codePoint ) {
    return 0;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
