/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.extensions.drilldown.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

public class DrillDownProfileResourceFactory extends AbstractXmlResourceFactory {
  public DrillDownProfileResourceFactory() {
  }

  public void initializeDefaults() {
    registerModule( new DrillDownProfileXmlFactoryModule() );
  }

  /**
   * Returns the configuration that should be used to initialize this factory.
   *
   * @return the configuration for initializing the factory.
   */
  protected Configuration getConfiguration() {
    return ClassicEngineBoot.getInstance().getGlobalConfig();
  }

  /**
   * Returns the expected result type.
   *
   * @return the result type.
   */
  public Class getFactoryType() {
    return DrillDownProfileCollection.class;
  }
}
