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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ActionPlugin;

/**
 * An ActionPlugin that configures the Preview-Component in some way. Usually these implementations are tightly bound to
 * the PreviewPane implementation details and assume a specific behavior of that class.
 *
 * @author Thomas Morgner
 */
public interface ControlActionPlugin extends ActionPlugin {
  public boolean configure( PreviewPane pane );
}
