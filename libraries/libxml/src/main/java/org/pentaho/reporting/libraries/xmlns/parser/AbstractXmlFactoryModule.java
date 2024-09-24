/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.parser;

/**
 * A common base class for all XmlFactoryModules. Almost all XmlFactoryModules follow the same pattern and this class
 * provides a sensible default implementation for a module checking for a well-known namespace and root-element.
 *
 * @author Thomas Morgner.
 */
public abstract class AbstractXmlFactoryModule implements XmlFactoryModule {
  private String namespace;
  private String rootElementName;

  protected AbstractXmlFactoryModule( final String namespace,
                                      final String rootElementName ) {
    if ( namespace == null ) {
      throw new NullPointerException();
    }
    if ( rootElementName == null ) {
      throw new NullPointerException();
    }

    this.namespace = namespace;
    this.rootElementName = rootElementName;
  }

  /**
   * Checks the given document data to compute the propability of whether this factory module would be able to handle
   * the given data.
   *
   * @param documentInfo the document information collection.
   * @return an integer value indicating how good the document matches the factories requirements.
   */
  public int getDocumentSupport( final XmlDocumentInfo documentInfo ) {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if ( rootNamespace != null && rootNamespace.length() > 0 ) {
      if ( namespace.equals( rootNamespace ) == false ) {
        return XmlFactoryModule.NOT_RECOGNIZED;
      } else if ( rootElementName.equals( documentInfo.getRootElement() ) ) {
        return XmlFactoryModule.RECOGNIZED_BY_NAMESPACE;
      }
    } else if ( rootElementName.equals( documentInfo.getRootElement() ) ) {
      return XmlFactoryModule.RECOGNIZED_BY_TAGNAME;
    }

    return XmlFactoryModule.NOT_RECOGNIZED;
  }

  /**
   * Returns the default namespace for a document with the characteristics given in the XmlDocumentInfo.
   *
   * @param documentInfo the document information.
   * @return the default namespace uri for the document.
   */
  public String getDefaultNamespace( final XmlDocumentInfo documentInfo ) {
    return namespace;
  }
}
