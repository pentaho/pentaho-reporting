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



package org.pentaho.reporting.engine.classic.core.style.css.namespaces;

/**
 * A collection of all namespace information for a document.
 *
 * @author Thomas Morgner
 */
public interface NamespaceCollection {
  public String getDefaultNamespaceURI();

  public String[] getNamespaces();

  public NamespaceDefinition getDefinition( String namespaceURI );

  public String lookupNamespaceURI( String prefix );

}
