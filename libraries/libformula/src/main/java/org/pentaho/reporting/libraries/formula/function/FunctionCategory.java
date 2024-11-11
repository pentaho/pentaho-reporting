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


package org.pentaho.reporting.libraries.formula.function;

import java.util.Locale;

/**
 * Creation-Date: 05.11.2006, 14:24:20
 *
 * @author Thomas Morgner
 */
public interface FunctionCategory {
  public String getDisplayName( Locale locale );

  public String getDescription( Locale locale );
}
