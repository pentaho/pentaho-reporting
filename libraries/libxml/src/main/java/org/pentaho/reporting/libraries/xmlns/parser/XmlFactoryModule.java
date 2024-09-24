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

/**
 * The XmlFactoryModule is the base of a plugin-structure to allow parser-multiplexing. In that case, the actual
 * implementation of the parser will be selected according to the DTD, Namespace or root-tag of the document.
 *
 * @author Thomas Morgner
 */
public interface XmlFactoryModule {
  /**
   * A constant declaring that the content has been recognized by the declared namespace.
   */
  public static final int RECOGNIZED_BY_NAMESPACE = 4000;
  /**
   * A constant declaring that the content has been recognized by the declared Document Type Declaration (DTD).
   */
  public static final int RECOGNIZED_BY_DTD = 2000;
  /**
   * A constant declaring that the content has been recognized by the tagname of the root-element of the XML-document.
   */
  public static final int RECOGNIZED_BY_TAGNAME = 1000;
  /**
   * A constant declaring that the content has NOT been recognized by any mean.
   */
  public static final int NOT_RECOGNIZED = -1;

  /**
   * Checks the given document data to compute the propability of whether this factory module would be able to handle
   * the given data.
   *
   * @param documentInfo the document information collection.
   * @return an integer value indicating how good the document matches the factories requirements.
   */
  public int getDocumentSupport( XmlDocumentInfo documentInfo );

  /**
   * Creates an XmlReadHandler for the root-tag based on the given document information.
   *
   * @param documentInfo the document information that has been extracted from the parser.
   * @return the root handler or null.
   */
  public XmlReadHandler createReadHandler( XmlDocumentInfo documentInfo );

  /**
   * Returns the default namespace for a document with the characteristics given in the XmlDocumentInfo.
   *
   * @param documentInfo the document information.
   * @return the default namespace uri for the document.
   */
  public String getDefaultNamespace( XmlDocumentInfo documentInfo );
}
