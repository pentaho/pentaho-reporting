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


package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.ValidationSequence;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

public class TableTestSpecXmlResourceFactory extends AbstractXmlResourceFactory {
  public TableTestSpecXmlResourceFactory() {
    registerModule( new TableTestSpecXmlFactoryModule() );
  }

  public void initializeDefaults() {
  }

  protected Configuration getConfiguration() {
    return ClassicEngineBoot.getInstance().getGlobalConfig();
  }

  public Class getFactoryType() {
    return ValidationSequence.class;
  }
}
