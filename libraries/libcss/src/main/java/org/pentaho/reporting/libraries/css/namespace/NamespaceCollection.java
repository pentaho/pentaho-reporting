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



package org.pentaho.reporting.libraries.css.namespace;

/**
 * A collection of all namespace information for a document.
 *
 * @author Thomas Morgner
 */
public interface NamespaceCollection {
  public String[] getNamespaces();

  public NamespaceDefinition getDefinition( String namespace );
}
