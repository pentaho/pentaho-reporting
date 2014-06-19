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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.parser.data.sequence;

import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

public class SequenceResourceXmlFactoryModule implements XmlFactoryModule
{
  public SequenceResourceXmlFactoryModule()
  {
  }

  public int getDocumentSupport(final XmlDocumentInfo documentInfo)
  {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if (rootNamespace != null && rootNamespace.length() > 0)
    {
      if (SequenceDataFactoryModule.NAMESPACE.equals(rootNamespace) == false)
      {
        return XmlFactoryModule.NOT_RECOGNIZED;
      }
      else if ("sequence-datasource".equals(documentInfo.getRootElement()))
      {
        return XmlFactoryModule.RECOGNIZED_BY_NAMESPACE;
      }
    }
    else if ("sequence-datasource".equals(documentInfo.getRootElement()))
    {
      return XmlFactoryModule.RECOGNIZED_BY_TAGNAME;
    }

    return XmlFactoryModule.NOT_RECOGNIZED;
  }

  public String getDefaultNamespace(final XmlDocumentInfo documentInfo)
  {
    return SequenceDataFactoryModule.NAMESPACE;
  }

  public XmlReadHandler createReadHandler(final XmlDocumentInfo documentInfo)
  {
    return new SequenceDataSourceReadHandler();
  }
}
