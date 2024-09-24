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
