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

package org.pentaho.reporting.libraries.css.resolver.tokens.computed;

import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;

/**
 * This is a simple placeholder to mark the location where the DOM content should be inserted.
 * <p/>
 * On 'string(..)' functions, this is the place holder where the PCDATA of that element is copied into the string.
 * <p/>
 * Todo: Maybe we should allow to copy the whole contents, as we would for the move-to function.
 */
public class ContentsToken extends ComputedToken {
  public static final ContentToken CONTENTS = new ContentsToken();

  private ContentsToken() {
  }
}
