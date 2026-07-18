/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.css.resolver.tokens.types;

import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;
import org.pentaho.reporting.libraries.resourceloader.Resource;

/**
 * Content, that has been loaded from a resource-key. This is always something binary.
 *
 * @author Thomas Morgner
 */
public interface ResourceType extends ContentToken {
  public Resource getContent();

}
