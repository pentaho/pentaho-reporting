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

public interface DataAttributeReferences extends Serializable {
  public String[] getMetaAttributeDomains();

  public String[] getMetaAttributeNames( String domainName );

  public DataAttributeReference getReference( String domain, String name );
}
