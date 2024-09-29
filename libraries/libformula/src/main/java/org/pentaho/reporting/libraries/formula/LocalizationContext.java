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

import org.pentaho.reporting.libraries.formula.typing.Type;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

public interface LocalizationContext {
  public Locale getLocale();

  public ResourceBundle getBundle( String id );

  public TimeZone getTimeZone();

  public List<DateFormat> getDateFormats( Type type );

  public List<NumberFormat> getNumberFormats();
}
