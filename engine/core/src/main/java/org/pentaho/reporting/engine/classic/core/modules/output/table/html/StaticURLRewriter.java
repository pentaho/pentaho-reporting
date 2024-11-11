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


package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.pentaho.reporting.libraries.formatting.FastMessageFormat;
import org.pentaho.reporting.libraries.repository.ContentEntity;

import java.util.Locale;

public class StaticURLRewriter implements URLRewriter {
  private final FastMessageFormat messageFormat;

  public StaticURLRewriter( final String pattern ) {
    this( pattern, Locale.US );
  }

  public StaticURLRewriter( final String pattern, final Locale locale ) {
    this.messageFormat = new FastMessageFormat( pattern, locale );
  }

  public String rewrite( final ContentEntity sourceDocument, final ContentEntity dataEntity )
    throws URLRewriteException {
    return messageFormat.format( new Object[] { dataEntity.getName() } );
  }
}
