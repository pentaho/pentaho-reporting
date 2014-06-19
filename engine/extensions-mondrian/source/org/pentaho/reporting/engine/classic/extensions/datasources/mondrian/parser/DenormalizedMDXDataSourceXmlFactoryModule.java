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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianDataFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DenormalizedMDXDataSourceXmlFactoryModule implements XmlFactoryModule
{
  public DenormalizedMDXDataSourceXmlFactoryModule()
  {
  }

  public int getDocumentSupport(final XmlDocumentInfo documentInfo)
  {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if (rootNamespace != null && rootNamespace.length() > 0)
    {
      if (MondrianDataFactoryModule.NAMESPACE.equals(rootNamespace) == false)
      {
        return NOT_RECOGNIZED;
      }
      else if ("denormalized-mdx-datasource".equals(documentInfo.getRootElement()))
      {
        return RECOGNIZED_BY_NAMESPACE;
      }
    }
    else if ("denormalized-mdx-datasource".equals(documentInfo.getRootElement()))
    {
      return RECOGNIZED_BY_TAGNAME;
    }

    return NOT_RECOGNIZED;
  }

  public String getDefaultNamespace(final XmlDocumentInfo documentInfo)
  {
    return MondrianDataFactoryModule.NAMESPACE;
  }

  public XmlReadHandler createReadHandler(final XmlDocumentInfo documentInfo)
  {
    final DataFactoryReadHandlerFactory factory = DataFactoryReadHandlerFactory.getInstance();
    final XmlReadHandler result = factory.getHandler(MondrianDataFactoryModule.NAMESPACE, "denormalized-mdx-datasource");
    if (result == null)
    {
      throw new IllegalStateException("Failed to return a valid readhandler");
    }
    return result;
  }

}
