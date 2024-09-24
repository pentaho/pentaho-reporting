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

package org.pentaho.reporting.designer.core.settings.ui;

import javax.swing.*;

/**
 * Todo: Document me!
 * <p/>
 * Date: 28.04.2010 Time: 13:58:11
 *
 * @author Thomas Morgner.
 */
public interface SettingsPlugin {
  public JComponent getComponent();

  public Icon getIcon();

  public String getTitle();

  public void apply();

  public void reset();

  public ValidationResult validate( ValidationResult result );
}
