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

package org.pentaho.reporting.libraries.css.resolver.tokens.types;

import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;

/**
 * A content type, that has an textual representation. It may be no surprise, that string content is text; images with
 * an alt-description have text as well.
 *
 * @author Thomas Morgner
 */
public interface TextType extends ContentToken {
  public String getText();
}
