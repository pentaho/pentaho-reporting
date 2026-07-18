/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.formula;

import java.util.Locale;
import java.util.TimeZone;

public interface FormulaContextFactory {
  public FormulaContext create( Locale locale, TimeZone timeZone );
}
