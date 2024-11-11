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


package org.pentaho.reporting.libraries.xmlns.writer;

/**
 * A tag-description provides information about xml tags. At the moment, we simply care whether an element can contain
 * CDATA. In such cases, we do not indent the inner elements.
 *
 * @author Thomas Morgner
 */
public interface TagDescription {
  /**
   * Checks, whether the element specified by the tagname and namespace can contain CDATA.
   *
   * @param namespace the namespace (as URI)
   * @param tagname   the tagname
   * @return true, if the element can contain CDATA, false otherwise
   */
  boolean hasCData( String namespace, String tagname );
}
