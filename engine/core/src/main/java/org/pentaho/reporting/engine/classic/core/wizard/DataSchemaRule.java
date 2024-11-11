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


package org.pentaho.reporting.engine.classic.core.wizard;

import java.io.Serializable;

public interface DataSchemaRule extends Serializable {
  public DataAttributes getStaticAttributes();

  public DataAttributeReferences getMappedAttributes();

  public boolean isMatch( DataAttributes dataAttributes, final DataAttributeContext context );
}
