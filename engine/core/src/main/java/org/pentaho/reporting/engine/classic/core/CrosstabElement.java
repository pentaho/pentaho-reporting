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


package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.CrosstabElementType;

/**
 * Creates a new crosstab subreport instance.
 *
 * @author Sulaiman Karmali
 */
public class CrosstabElement extends SubReport {
  public CrosstabElement() {
    setElementType( CrosstabElementType.INSTANCE );
    setRootGroup( new CrosstabGroup() );
  }
}
