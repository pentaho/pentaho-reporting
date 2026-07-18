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



package org.pentaho.reporting.libraries.css.resolver.tokens.computed;

import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;

/**
 * A computed token is a placeholder for content that *must* be resolved during the layouting phase. Usually, this deals
 * with compound counters (which need to be split into resolved single counters).
 *
 * @author Thomas Morgner
 */
public abstract class ComputedToken implements ContentToken {
  protected ComputedToken() {
  }
}
