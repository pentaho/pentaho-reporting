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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.swt.commonSWT;

/**
 * =========================================================
 * Pentaho-Reporting-Classic : a free Java reporting library
 * =========================================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * SwingCommonModule.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.util.Enumeration;
import java.util.ResourceBundle;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * Creation-Date: 17.11.2006, 14:40:24
 *
 * @author Thomas Morgner
 */
public class SwtCommonModule extends AbstractModule
{
  public static final String BUNDLE_NAME =
      "org.pentaho.reporting.engine.classic.extensions.swt.commonSWT.messages.messages"; //$NON-NLS-1$
  public static final String LARGE_ICON_PROPERTY = "Icon24"; //$NON-NLS-1$

  public SwtCommonModule() throws ModuleInitializeException
  {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup
   * operations. This method is called only once in a modules lifetime. If the
   * initializing cannot be completed, throw a ModuleInitializeException to
   * indicate the error,. The module will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws ModuleInitializeException
   *          if an error ocurred while initializing the module.
   */
  public void initialize(final SubSystem subSystem) throws ModuleInitializeException
  {
    if (subSystem.getExtendedConfig().getBoolProperty("org.pentaho.reporting.engine.classic.core.modules.gui.base.SwingDialogTranslation")) //$NON-NLS-1$
    {
      final ResourceBundle resources = ResourceBundle.getBundle(SwtCommonModule.BUNDLE_NAME);
      final UIDefaults defaults = UIManager.getDefaults();
      final Enumeration en = resources.getKeys();
      while (en.hasMoreElements())
      {
        try
        {
          final String keyName = (String) en.nextElement();
          defaults.put(keyName, resources.getObject(keyName));
        }
        catch(Exception e)
        {
          // Ignored; if it happens, we would not care that much ..
        }
      }
    }
  }
}
