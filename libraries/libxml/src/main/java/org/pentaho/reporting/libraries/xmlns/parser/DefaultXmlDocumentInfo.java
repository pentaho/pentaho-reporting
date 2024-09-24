/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.parser;

import org.xml.sax.Attributes;

/**
 * A data class that holds all relevant information about a XML document to make a decision on what parser to use to
 * interpret the XML content.
 *
 * @author Thomas Morgner
 */
public class DefaultXmlDocumentInfo implements XmlDocumentInfo {
  private String rootElement;
  private String rootElementNameSpace;
  private String publicDTDId;
  private String systemDTDId;
  private String defaultNameSpace;
  private Attributes rootElementAttributes;

  /**
   * Default-Constructor.
   */
  public DefaultXmlDocumentInfo() {
  }

  /**
   * Returns the tag name of the root-level element.
   *
   * @return the root-tag-name.
   */
  public String getRootElement() {
    return rootElement;
  }

  /**
   * Defines the tag name of the root-level element.
   *
   * @param rootElement the root-tag-name.
   */
  public void setRootElement( final String rootElement ) {
    this.rootElement = rootElement;
  }

  /**
   * Returns the namespace URI for the root-element of the document.
   *
   * @return the namespace of the root-element.
   */
  public String getRootElementNameSpace() {
    return rootElementNameSpace;
  }

  /**
   * Defines the namespace URI for the root-element of the document.
   *
   * @param rootElementNameSpace the namespace of the root-element.
   */
  public void setRootElementNameSpace( final String rootElementNameSpace ) {
    this.rootElementNameSpace = rootElementNameSpace;
  }

  public Attributes getRootElementAttributes() {
    return rootElementAttributes;
  }

  public void setRootElementAttributes( final Attributes rootElementAttributes ) {
    this.rootElementAttributes = rootElementAttributes;
  }

  /**
   * Returns the Public-ID of the Document's DTD (if there's any).
   *
   * @return the public id.
   */
  public String getPublicDTDId() {
    return publicDTDId;
  }

  /**
   * Defines the Public-ID of the Document's DTD (if there's any).
   *
   * @param publicDTDId the public id.
   */
  public void setPublicDTDId( final String publicDTDId ) {
    this.publicDTDId = publicDTDId;
  }

  /**
   * Returns the System-ID of the document's DTD.
   *
   * @return the system-id.
   */
  public String getSystemDTDId() {
    return systemDTDId;
  }

  /**
   * Defines the System-ID of the document's DTD.
   *
   * @param systemDTDId the system-id.
   */
  public void setSystemDTDId( final String systemDTDId ) {
    this.systemDTDId = systemDTDId;
  }

  /**
   * Returns a string representation of the document info. This is for debugging purposes only.
   *
   * @return the string version of the document info.
   */
  public String toString() {
    final StringBuffer buffer = new StringBuffer( 120 );
    buffer.append( "XmlDocumentInfo={rootElementTag=" );
    buffer.append( rootElement );
    buffer.append( ", rootElementNS=" );
    buffer.append( rootElementNameSpace );
    buffer.append( ", SystemDTD-ID=" );
    buffer.append( systemDTDId );
    buffer.append( ", PublicDTD-ID=" );
    buffer.append( publicDTDId );
    buffer.append( ", defaultnamespace=" );
    buffer.append( defaultNameSpace );
    buffer.append( '}' );
    return buffer.toString();
  }

  /**
   * Returns the default-namespace declared on the root-element. It is not guaranteed that this information is filled
   * until a XmlFactoryModule has been selected.
   *
   * @return the default-namespace.
   */
  public String getDefaultNameSpace() {
    return defaultNameSpace;
  }

  /**
   * Defines the default-namespace declared on the root-element. It is not guaranteed that this information is filled
   * until a XmlFactoryModule has been selected.
   *
   * @param defaultNameSpace the default-namespace.
   */
  public void setDefaultNameSpace( final String defaultNameSpace ) {
    this.defaultNameSpace = defaultNameSpace;
  }
}
