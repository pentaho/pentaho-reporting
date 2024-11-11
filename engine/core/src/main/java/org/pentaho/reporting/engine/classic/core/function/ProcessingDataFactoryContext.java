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


package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class ProcessingDataFactoryContext implements DataFactoryContext {
  private ProcessingContext processingContext;
  private DataFactory contextDataFactory;

  public ProcessingDataFactoryContext( final ProcessingContext processingContext, final DataFactory contextDataFactory ) {
    this.processingContext = processingContext;
    this.contextDataFactory = contextDataFactory;
  }

  public Configuration getConfiguration() {
    return processingContext.getConfiguration();
  }

  public ResourceManager getResourceManager() {
    return processingContext.getResourceManager();
  }

  public ResourceKey getContextKey() {
    return processingContext.getContentBase();
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return processingContext.getResourceBundleFactory();
  }

  public DataFactory getContextDataFactory() {
    return contextDataFactory;
  }

  public FormulaContext getFormulaContext() {
    return processingContext.getFormulaContext();
  }
}
