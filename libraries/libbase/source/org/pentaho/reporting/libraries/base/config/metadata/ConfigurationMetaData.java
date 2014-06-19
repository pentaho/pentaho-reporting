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

package org.pentaho.reporting.libraries.base.config.metadata;

import java.util.HashMap;

public class ConfigurationMetaData
{
  private HashMap<String, ConfigurationDomain> domains;
  private static ConfigurationMetaData instance;

  public static synchronized ConfigurationMetaData getInstance()
  {
    if (instance == null)
    {
      instance = new ConfigurationMetaData();
    }
    return instance;
  }

  private ConfigurationMetaData()
  {
    domains = new HashMap<String, ConfigurationDomain>();
  }

  public ConfigurationDomain getDomain(final String key)
  {
    return domains.get(key);
  }

  public ConfigurationDomain createDomain(final String key)
  {
    final ConfigurationDomain configurationDomain = domains.get(key);
    if (configurationDomain != null)
    {
      return configurationDomain;
    }

    final ConfigurationDomain newDomain = new ConfigurationDomain();
    domains.put(key, newDomain);
    return newDomain;
  }
}
