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


package org.pentaho.reporting.designer.core.actions.global;

import mondrian.olap.CacheControl;
import mondrian.rolap.agg.AggregationManager;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ClearMondrianCacheAction extends AbstractDesignerContextAction {
  public ClearMondrianCacheAction() {
    putValue( Action.NAME, ActionMessages.getString( "ClearMondrianCacheAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ClearMondrianCacheAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ClearMondrianCacheAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ClearMondrianCacheAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final CacheControl cacheControl = AggregationManager.instance().getCacheControl( null, null );
    cacheControl.flushSchemaCache();
  }
}
