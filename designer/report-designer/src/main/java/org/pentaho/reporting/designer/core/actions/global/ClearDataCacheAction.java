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
* Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
* All rights reserved.
*/

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
