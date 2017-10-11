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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * The processing context hold information about the progress of the report processing and contains global properties
 * used during the report processing.
 *
 * @author Thomas Morgner
 */
public interface ProcessingContext {
  /**
   * Returns the current progress level. The number itself has no meaning and is only used to measure the progress of
   * the report processing.
   *
   * @return the progress level.
   */
  public int getProgressLevel();

  /**
   * Returns the total number of different activities the report will process.
   *
   * @return the number of different progress levels.
   */
  public int getProgressLevelCount();

  /**
   * The processing-level is used for dependency tracking. A function that precomputes values should use this level
   * value to determine its current activity.
   *
   * @return the processing level.
   * @see Expression#getDependencyLevel()
   */
  public int getProcessingLevel();

  /**
   * Returns the formula context of this report process. The formula context is required to evaluate inline expression
   * with LibFormula.
   *
   * @return the current formula context.
   */
  public FormulaContext getFormulaContext();

  /**
   * Returns true, if the current processing run is a prepare-run. A prepare run does not generate content, but will be
   * needed to compute the layout. This flag can be used to possibly optimize the content computation. If in doubt on
   * how to interpret the flag, then please ignore this flag. The process may be slightly slower, but at least it will
   * work all the time.
   *
   * @return true, if this is a prepare-run, false if this is a content processing run.
   */
  public boolean isPrepareRun();

  /**
   * Returns the export descriptor from the output-target.
   *
   * @return the export descriptor string.
   * @see org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData#getExportDescriptor()
   */
  public String getExportDescriptor();

  public OutputProcessorMetaData getOutputProcessorMetaData();

  /**
   * The resource-bundle factory encapsulates all locale specific resources and provides a system-independent way to
   * create Resource-Bundles. This returns the initial master-report's resource-bundle factory.
   *
   * @return the report's resource-bundle factory.
   */
  public ResourceBundleFactory getResourceBundleFactory();

  /**
   * Returns the content base of the initial master-report. The content-base resource-key can be used to resolve
   * relative paths.
   *
   * @return the initial content base or null, if there is no content-base.
   */
  public ResourceKey getContentBase();

  /**
   * Returns the initial master-report's resource manager. The resource manager can be used to load external resources
   * in a system-independent way.
   *
   * @return the master-report's resourcemanager.
   */
  public ResourceManager getResourceManager();

  /**
   * Returns the initial master-report's configuration. The initial configuration is used for all subreports.
   *
   * @return the global report configuration.
   */
  public Configuration getConfiguration();

  /**
   * Returns the outermost master-report's document meta-data.
   *
   * @return the document meta-data.
   */
  public DocumentMetaData getDocumentMetaData();

  public ReportEnvironment getEnvironment();

  public long getReportProcessingStartTime();

  public int getCompatibilityLevel();
}
