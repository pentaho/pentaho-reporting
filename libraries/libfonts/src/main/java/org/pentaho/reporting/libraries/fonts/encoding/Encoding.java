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

package org.pentaho.reporting.libraries.fonts.encoding;

import java.util.Locale;

/**
 * A simple encoding. This encoding transforms characters into bytes in a uniform way. Each character results in exactly
 * the same number of bytes.
 *
 * @author Thomas Morgner
 */
public interface Encoding extends EncodingCore {
  public String getName();

  public String getName( Locale locale );
}
