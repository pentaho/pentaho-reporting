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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BulkAttributeReadHandler extends StringReadHandler
{
  private String namespace;
  private String name;
  private ReportAttributeMap<String> attributes;

  public BulkAttributeReadHandler(final String namespace, final String name)
  {
    if (namespace == null)
    {
      throw new NullPointerException();
    }
    if (name == null)
    {
      throw new NullPointerException();
    }
    this.namespace = namespace;
    this.name = name;
    this.attributes = new ReportAttributeMap<String>();
  }

  protected void startParsing(final Attributes attrs) throws SAXException
  {
    super.startParsing(attrs);
    final int length = attrs.getLength();
    for (int i = 0; i < length; i++)
    {
      attributes.setAttribute(attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i));
    }
  }

  public String getNamespace()
  {
    return namespace;
  }

  public String getName()
  {
    return name;
  }

  public ReportAttributeMap<String> getAttributes()
  {
    return attributes;
  }
}
