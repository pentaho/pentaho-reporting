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


package org.pentaho.reporting.libraries.css.resolver.tokens.statics;

import org.pentaho.reporting.libraries.css.resolver.tokens.types.FormattedTextType;

import java.text.Format;

/**
 * Creation-Date: 04.07.2006, 20:16:16
 *
 * @author Thomas Morgner
 */
public class FormattedContentToken extends StaticToken
  implements FormattedTextType {
  private Object original;
  private Format format;
  private String text;

  public FormattedContentToken( final Object original,
                                final Format format,
                                final String text ) {
    this.format = format;
    this.original = original;
    this.text = text;
  }

  public Object getOriginal() {
    return original;
  }

  public Format getFormat() {
    return format;
  }

  public String getText() {
    return text;
  }
}
