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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
