/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.core.settings;

import java.util.EventListener;

/**
 * A general signal to reinitialize or to read the settings.
 *
 * @author Thomas Morgner
 */
public interface SettingsListener extends EventListener {
  public void settingsChanged();
}
