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


package org.pentaho.reporting.libraries.fonts.text;

/**
 * The base interface for all unicode statemachine producers.
 *
 * @author Thomas Morgner
 */
public interface ClassificationProducer extends Cloneable {
  public static final int START_OF_TEXT = Integer.MIN_VALUE;
  public static final int END_OF_TEXT = Integer.MAX_VALUE;

  public Object clone() throws CloneNotSupportedException;
}
