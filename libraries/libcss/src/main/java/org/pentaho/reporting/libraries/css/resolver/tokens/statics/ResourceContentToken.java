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

import org.pentaho.reporting.libraries.css.resolver.tokens.types.GenericType;
import org.pentaho.reporting.libraries.css.resolver.tokens.types.ResourceType;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;


public class ResourceContentToken extends StaticToken
  implements ResourceType, GenericType {
  private Resource content;

  public ResourceContentToken( final Resource content ) {
    if ( content == null ) {
      throw new NullPointerException();
    }
    this.content = content;
  }

  public Object getRaw() {
    try {
      return content.getResource();
    } catch ( ResourceException e ) {
      return null;
    }
  }

  public Resource getContent() {
    return content;
  }
}
