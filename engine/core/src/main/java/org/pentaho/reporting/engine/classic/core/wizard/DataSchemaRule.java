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



package org.pentaho.reporting.engine.classic.core.wizard;

import java.io.Serializable;

public interface DataSchemaRule extends Serializable {
  public DataAttributes getStaticAttributes();

  public DataAttributeReferences getMappedAttributes();

  public boolean isMatch( DataAttributes dataAttributes, final DataAttributeContext context );
}
