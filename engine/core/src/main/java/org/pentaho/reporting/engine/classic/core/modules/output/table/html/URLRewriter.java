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

import org.pentaho.reporting.libraries.repository.ContentEntity;

/**
 * This service takes a generated content location and rewrites it into an absolute or relative URL.
 *
 * @author Thomas Morgner
 */
public interface URLRewriter {
  public String rewrite( ContentEntity sourceDocument, ContentEntity dataEntity ) throws URLRewriteException;
}
