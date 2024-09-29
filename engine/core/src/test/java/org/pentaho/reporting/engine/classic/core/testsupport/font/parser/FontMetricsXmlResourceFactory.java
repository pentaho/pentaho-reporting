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


package org.pentaho.reporting.engine.classic.core.testsupport.font.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

public class FontMetricsXmlResourceFactory extends AbstractXmlResourceFactory {
  public FontMetricsXmlResourceFactory() {
    registerModule( new FontMetricsXmlFactoryModule() );
  }

  public void initializeDefaults() {
  }

  protected Configuration getConfiguration() {
    return ClassicEngineBoot.getInstance().getGlobalConfig();
  }

  public Class getFactoryType() {
    return FontMetricsCollection.class;
  }
}
