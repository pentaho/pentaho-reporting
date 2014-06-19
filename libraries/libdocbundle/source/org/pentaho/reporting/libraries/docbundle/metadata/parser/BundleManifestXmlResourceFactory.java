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
* Copyright (c) 2008 - 2009 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.docbundle.metadata.parser;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.docbundle.LibDocBundleBoot;
import org.pentaho.reporting.libraries.docbundle.metadata.BundleManifest;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModuleRegistry;

public class BundleManifestXmlResourceFactory extends AbstractXmlResourceFactory
{
  private static final XmlFactoryModuleRegistry registry = new XmlFactoryModuleRegistry();

  public static void register(final Class<? extends XmlFactoryModule> readHandler)
  {
    registry.register(readHandler);
  }

  public BundleManifestXmlResourceFactory()
  {
  }

  public void initializeDefaults()
  {
    super.initializeDefaults();
    final XmlFactoryModule[] registeredHandlers = registry.getRegisteredHandlers();
    for (int i = 0; i < registeredHandlers.length; i++)
    {
      registerModule(registeredHandlers[i]);
    }
  }

  protected Configuration getConfiguration()
  {
    return LibDocBundleBoot.getInstance().getGlobalConfig();
  }

  public Class getFactoryType()
  {
    return BundleManifest.class;
  }
}
