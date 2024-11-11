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
