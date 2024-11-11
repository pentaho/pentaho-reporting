/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
