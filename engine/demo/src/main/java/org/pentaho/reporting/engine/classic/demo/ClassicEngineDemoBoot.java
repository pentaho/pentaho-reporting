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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo;

import java.net.URL;

import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;


/**
 * Creation-Date: 02.12.2006, 19:20:26
 *
 * @author Thomas Morgner
 */
public class ClassicEngineDemoBoot extends AbstractBoot
{
  private static ClassicEngineDemoBoot singleton;

  public static synchronized ClassicEngineDemoBoot getInstance()
  {
    if (singleton == null)
    {
      singleton = new ClassicEngineDemoBoot();
    }
    return singleton;
  }

  private ClassicEngineDemoBoot()
  {
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo()
  {
    return ClassicEngineDemoInfo.getInstance();
  }

  /**
   * Loads the configuration.
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration()
  {
    return createDefaultHierarchicalConfiguration
        ("/org/pentaho/reporting/engine/classic/demo/demo.properties",
            "/jfreereport-demo.properties", true, ClassicEngineDemoBoot.class);
  }

  /**
   * Returns the current global configuration as modifiable instance. This is exactly the same as casting the global
   * configuration into a ModifableConfiguration instance.
   * <p/>
   * This is a convinience function, as all programmers are lazy.
   *
   * @return the global config as modifiable configuration.
   */
  public ModifiableConfiguration getEditableConfig()
  {
    return (ModifiableConfiguration) getGlobalConfig();
  }

  /**
   * Performs the boot.
   */
  protected void performBoot()
  {
    final URL expressionMetaSource = ObjectUtilities.getResource
        ("org/pentaho/reporting/engine/classic/demo/ancient/demo/meta-expressions.xml", ClassicEngineDemoBoot.class);
    if (expressionMetaSource == null)
    {
      throw new IllegalStateException("Error: Could not find the expression meta-data description file");
    }
    try
    {
      ExpressionRegistry.getInstance().registerFromXml(expressionMetaSource);
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Error: Could not parse the element meta-data description file: " + e);
    }

  }
}
