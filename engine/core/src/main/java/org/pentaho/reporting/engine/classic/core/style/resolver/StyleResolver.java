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

package org.pentaho.reporting.engine.classic.core.style.resolver;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;

import java.io.Serializable;

public interface StyleResolver extends Serializable, Cloneable {
  public void resolve( final ReportElement element, final ResolverStyleSheet resolverTarget );

  public StyleResolver clone();
}
