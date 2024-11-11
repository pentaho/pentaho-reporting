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


package org.pentaho.reporting.engine.classic.core.metadata;

/**
 * The attribute registry allows to update the available attributes for an element. The update should happen during the
 * boot-process or reports may behave inconsistently.
 */
public interface AttributeRegistry {
  /**
   * Adds a new or updates the metadata for an existing attribute of a report-element.
   *
   * @param metaData
   *          the new metadata object.
   */
  public void putAttributeDescription( AttributeMetaData metaData );

  /**
   * Retrieves the metadata for an attribute by namespace and name.
   *
   * @param namespace
   *          the namespace for the attribute.
   * @param name
   *          the attribute name.
   * @return the attribute definition or null, if the attribute is not defined.
   */
  public AttributeMetaData getAttributeDescription( String namespace, String name );
}
