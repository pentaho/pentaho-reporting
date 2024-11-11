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


package org.pentaho.reporting.engine.classic.core.dom;

import org.pentaho.reporting.engine.classic.core.ReportElement;

public class AnyNodeMatcher extends ElementMatcher {
  public AnyNodeMatcher() {
    super( "*" );
  }

  public boolean matches( final MatcherContext context, final ReportElement node ) {
    return matchAttributes( context, node );
  }
}
