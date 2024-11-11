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


package org.pentaho.reporting.libraries.css.resolver.tokens.statics;

import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;

/**
 * A static content token always defines a fixed textual content. Static tokens hold no reference to any parent, as they
 * have no parent at all.
 *
 * @author Thomas Morgner
 */
public abstract class StaticToken implements ContentToken {
  protected StaticToken() {
  }
}
