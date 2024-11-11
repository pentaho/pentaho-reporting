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


package org.pentaho.reporting.libraries.css.resolver.tokens.types;

import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;

/**
 * This is raw data. Whether or not the raw-data is interpreted is up to the output target. However, it is suggested,
 * that at least Images, Drawables and Strings should be implemented (if the output format supports that).
 *
 * @author Thomas Morgner
 */
public interface GenericType extends ContentToken {
  public Object getRaw();
}
