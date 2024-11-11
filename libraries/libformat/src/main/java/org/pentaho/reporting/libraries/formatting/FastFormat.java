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


package org.pentaho.reporting.libraries.formatting;

import java.io.Serializable;
import java.util.Locale;

/**
 * A fast-format is a wrapper around the existing java.text.Formatter objects. The fast-format reduces the possible
 * interactions with the wrapped formatter and therefore allows to treat the formatter as some sort of immutable object.
 * This later simplifies cloning, as now the formatter and all of its internal objects no longer need to be cloned.
 *
 * @author Thomas Morgner
 */
public interface FastFormat extends Serializable, Cloneable {
  /**
   * Returns the current locale of the formatter.
   *
   * @return the current locale, never null.
   */
  public Locale getLocale();

  /**
   * Formats the given object in a formatter-specific way.
   *
   * @param parameters the parameters for the formatting.
   * @return the formatted string.
   */
  public String format( Object parameters );

  /**
   * Clones the formatter.
   *
   * @return the clone.
   * @throws CloneNotSupportedException if cloning failed.
   */
  public Object clone() throws CloneNotSupportedException;
}
