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


package org.pentaho.reporting.libraries.formula;

import java.io.Serializable;
import java.util.Locale;

public interface ErrorValue extends Serializable {
  public String getNamespace();

  public int getErrorCode();

  public String getErrorMessage( Locale locale );
}
