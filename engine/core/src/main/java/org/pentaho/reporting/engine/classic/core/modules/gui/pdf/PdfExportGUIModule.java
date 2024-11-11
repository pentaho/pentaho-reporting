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


package org.pentaho.reporting.engine.classic.core.modules.gui.pdf;

import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * The module definition for the PDF export gui module.
 *
 * @author Thomas Morgner
 */
public class PdfExportGUIModule extends AbstractModule {
  /**
   * A constant for the encryption type (40 bit).
   */
  public static final String SECURITY_ENCRYPTION_NONE = "none"; //$NON-NLS-1$

  /**
   * A constant for the encryption type (40 bit).
   */
  public static final String SECURITY_ENCRYPTION_40BIT = "40bit"; //$NON-NLS-1$

  /**
   * A constant for the encryption type (128 bit).
   */
  public static final String SECURITY_ENCRYPTION_128BIT = "128bit"; //$NON-NLS-1$

  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public PdfExportGUIModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initalizes the module and registes it with the export plugin factory.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public void initialize( final SubSystem s ) throws ModuleInitializeException {
  }
}
