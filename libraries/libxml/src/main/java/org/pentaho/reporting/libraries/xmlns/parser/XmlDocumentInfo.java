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
