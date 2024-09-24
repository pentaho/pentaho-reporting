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

package org.pentaho.reporting.designer.core.xul;

import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.swing.SwingXulLoader;

public class ActionSwingXulLoader extends SwingXulLoader {
  public ActionSwingXulLoader()
    throws XulException {
    parser.registerHandler( "MENU", PrdSwingMenu.class.getName() );//NON-NLS
    parser.registerHandler( "MENUITEM", "org.pentaho.reporting.designer.core.xul.ActionSwingMenuitem" );//NON-NLS
    parser.registerHandler( "BUTTON", "org.pentaho.reporting.designer.core.xul.ActionSwingButton" );//NON-NLS
    parser.registerHandler( "POPUP", "org.pentaho.reporting.designer.core.xul.SwingXulPopupMenu" );//NON-NLS

    parser.registerHandler( "radio-menuitem", "org.pentaho.reporting.designer.core.xul.ActionSwingMenuitem" );//NON-NLS
    parser
      .registerHandler( "checkbox-menuitem", "org.pentaho.reporting.designer.core.xul.ActionSwingMenuitem" );//NON-NLS
    parser.registerHandler( "-x-SwingXulToolbar", "org.pentaho.reporting.designer.core.xul.SwingXulToolbar" );//NON-NLS
  }
}
