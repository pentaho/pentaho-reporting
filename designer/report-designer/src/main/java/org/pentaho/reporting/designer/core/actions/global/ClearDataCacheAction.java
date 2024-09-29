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


package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.engine.classic.core.cache.DataCache;
import org.pentaho.reporting.engine.classic.core.cache.DataCacheFactory;
import org.pentaho.reporting.engine.classic.core.cache.DataCacheManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ClearDataCacheAction extends AbstractDesignerContextAction {
  public ClearDataCacheAction() {
    putValue( Action.NAME, ActionMessages.getString( "ClearDataCacheAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ClearDataCacheAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ClearDataCacheAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ClearDataCacheAction.Accelerator" ) );
    final DataCache cache = DataCacheFactory.getCache();
    if ( cache == null ) {
      setEnabled( false );
    }
  }

  public void actionPerformed( final ActionEvent e ) {
    final DataCache cache = DataCacheFactory.getCache();
    if ( cache == null ) {
      return;
    }
    final DataCacheManager cacheManager = cache.getCacheManager();
    if ( cacheManager == null ) {
      return;
    }
    cacheManager.clearAll();
  }
}
