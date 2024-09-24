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
 * Copyright (c) 2000 - 2024 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeEvent;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.AbstractActionPlugin;

public class PaginatedUpdateListenerTest {

  @Test( expected = NullPointerException.class )
  public void testPropertyChangeException() {
    new PaginatedUpdateListener( null );
  }

  @Test
  public void testPropertyChange() {
    AbstractActionPlugin actionPlugin = mock( AbstractActionPlugin.class );
    PropertyChangeEvent evt = mock( PropertyChangeEvent.class );

    PaginatedUpdateListener listener = new PaginatedUpdateListener( actionPlugin );

    doReturn( "test" ).when( evt ).getPropertyName();
    doReturn( true ).when( evt ).getNewValue();
    listener.propertyChange( evt );
    verify( actionPlugin, never() ).setEnabled( anyBoolean() );

    doReturn( PreviewPane.PAGINATED_PROPERTY ).when( evt ).getPropertyName();
    doReturn( false ).when( evt ).getNewValue();
    listener.propertyChange( evt );
    verify( actionPlugin ).setEnabled( false );

    doReturn( true ).when( evt ).getNewValue();
    listener.propertyChange( evt );
    verify( actionPlugin ).setEnabled( true );
  }
}
