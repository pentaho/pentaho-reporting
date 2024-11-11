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


package org.pentaho.reporting.libraries.xmlns.parser;

import org.xml.sax.Attributes;

/**
 * The XmlDocumentInfo class collects basic information about the document that should be parsed.
 *
 * @author Thomas Morgner
 */
public interface XmlDocumentInfo {
  /**
   * Returns the tag name of the root-level element.
   *
   * @return the root-tag-name.
   */
  public String getRootElement();

  /**
   * Returns the namespace URI for the root-element of the document.
   *
   * @return the namespace of the root-element.
   */
  public String getRootElementNameSpace();

  public Attributes getRootElementAttributes();

  /**
   * Returns the Public-ID of the Document's DTD (if there's any).
   *
   * @return the public id.
   */
  public String getPublicDTDId();

  /**
   * Returns the System-ID of the document's DTD.
   *
   * @return the system-id.
   */
  public String getSystemDTDId();

  /**
   * Returns the default-namespace declared on the root-element. It is not guaranteed that this information is filled
   * until a XmlFactoryModule has been selected.
   *
   * @return the default-namespace.
   */
  public String getDefaultNameSpace();


}
