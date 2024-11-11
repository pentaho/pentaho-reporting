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


package org.pentaho.reporting.libraries.fonts.encoding;

/**
 * A complex encoding has a non-uniform relationship between the number of characters and the number of bytes generated.
 * UTF-8 is an example for such a non-uniform encoding.
 *
 * @author Thomas Morgner
 */
public interface ComplexEncoding extends Encoding {
  /**
   * Checks, whether this implementation supports encoding of character data.
   *
   * @return
   */
  public boolean isEncodingSupported();
}
