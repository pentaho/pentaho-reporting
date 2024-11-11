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


package org.pentaho.reporting.designer.core.editor.drilldown;

import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010 Time: 13:49:17
 *
 * @author Thomas Morgner.
 */
public interface DrillDownSelector {
  public DrillDownUiProfile getSelectedProfile();

  public void setSelectedProfile( DrillDownUiProfile profile );

  public JComponent getComponent();

  public void addChangeListener( ChangeListener changeListener );

  public void removeChangeListener( ChangeListener changeListener );
}
