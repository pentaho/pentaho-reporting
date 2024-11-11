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

import java.text.Format;

/**
 * Creation-Date: 04.07.2006, 20:17:37
 *
 * @author Thomas Morgner
 */
public interface FormattedTextType extends TextType {
  public Object getOriginal();

  public Format getFormat();
}
