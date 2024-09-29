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


package org.pentaho.reporting.designer.core.editor.styles.styleeditor;

import java.util.EventListener;

public interface ElementStyleDefinitionChangeListener extends EventListener {
  public void styleRuleAdded( ElementStyleDefinitionChangeEvent event );

  public void styleRuleRemoved( ElementStyleDefinitionChangeEvent event );

  public void styleRulesChanged( ElementStyleDefinitionChangeEvent event );
}
