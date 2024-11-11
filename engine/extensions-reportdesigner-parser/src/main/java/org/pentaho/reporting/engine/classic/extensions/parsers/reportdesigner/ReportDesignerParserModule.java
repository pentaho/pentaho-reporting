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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner;

import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * The report-designer format has no DTD and uses the same root element as the simple report format. Therefore this
 * parser only gets active if explicitly specified.
 *
 * @author Thomas Morgner
 */
public class ReportDesignerParserModule extends AbstractModule {
  public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/report-designer/2.0";

  public static final String VERTICAL_GUIDE_LINES_ATTRIBUTE = "VerticalGuideLines";
  public static final String HORIZONTAL_GUIDE_LINES_ATTRIBUTE = "HorizontalGuideLines";
  public static final String HIDE_IN_LAYOUT_GUI_ATTRIBUTE = "hideInLayoutGUI";

  public ReportDesignerParserModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws ModuleInitializeException if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    ElementTypeRegistry.getInstance().registerNamespacePrefix( NAMESPACE, "report-designer" );

  }
}
