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

package org.pentaho.reporting.engine.classic.core.designtime.datafactory;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DefaultResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DesignTimeDataFactoryContext implements DataFactoryContext {
  private Configuration configuration;
  private ResourceManager resourceManager;
  private ResourceKey contextKey;
  private ResourceBundleFactory resourceBundleFactory;
  private DataFactory contextFactory;
  private FormulaContext formulaContext;

  public DesignTimeDataFactoryContext() {
    configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    resourceManager = new ResourceManager();
    resourceBundleFactory = new DefaultResourceBundleFactory();
    formulaContext = new DefaultFormulaContext();
  }

  public DesignTimeDataFactoryContext( final MasterReport report ) {
    this( report.getConfiguration(), report.getResourceManager(), report.getContentBase(), MasterReport
        .computeAndInitResourceBundleFactory( report.getResourceBundleFactory(), report.getReportEnvironment() ),
        report.getDataFactory() );
  }

  public DesignTimeDataFactoryContext( final Configuration configuration, final ResourceManager resourceManager,
      final ResourceKey contextKey, final ResourceBundleFactory resourceBundleFactory ) {
    this( configuration, resourceManager, contextKey, resourceBundleFactory, null );
  }

  public DesignTimeDataFactoryContext( final Configuration configuration, final ResourceManager resourceManager,
      final ResourceKey contextKey, final ResourceBundleFactory resourceBundleFactory, final DataFactory contextFactory ) {
    if ( configuration == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }
    if ( resourceBundleFactory == null ) {
      throw new NullPointerException();
    }
    this.contextFactory = contextFactory;
    this.configuration = configuration;
    this.resourceManager = resourceManager;
    this.contextKey = contextKey;
    this.resourceBundleFactory = resourceBundleFactory;
    this.formulaContext = new DefaultFormulaContext();
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public ResourceKey getContextKey() {
    return contextKey;
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return resourceBundleFactory;
  }

  public DataFactory getContextDataFactory() {
    return contextFactory;
  }

  public FormulaContext getFormulaContext() {
    return formulaContext;
  }
}
