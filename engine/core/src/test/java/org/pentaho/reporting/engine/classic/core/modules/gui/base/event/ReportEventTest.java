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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.event;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.awt.event.MouseEvent;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

public class ReportEventTest {

  private static final String TEST_VAL = "test_action";

  @Test
  public void testActionEvent() {
    RenderNode node = mock( RenderNode.class );
    ReportAttributeMap<String> attrs = new ReportAttributeMap<String>();
    attrs.setAttribute( AttributeNames.Swing.NAMESPACE, AttributeNames.Swing.ACTION, TEST_VAL );
    doReturn( attrs ).when( node ).getAttributes();

    ReportActionEvent actionEvent = new ReportActionEvent( this, node );

    assertThat( actionEvent.getNode(), is( equalTo( node ) ) );
    assertThat( actionEvent.getActionParameter(), is( instanceOf( String.class ) ) );
    assertThat( (String) actionEvent.getActionParameter(), is( equalTo( TEST_VAL ) ) );
  }

  @Test
  public void testHyperlinkEvent() {
    RenderNode node = mock( RenderNode.class );
    String target = "target";
    String window = "window";
    String title = "title";

    ReportHyperlinkEvent event = new ReportHyperlinkEvent( this, node, target, window, title );

    assertThat( event.getSourceNode(), is( equalTo( node ) ) );
    assertThat( event.getTarget(), is( equalTo( target ) ) );
    assertThat( event.getTitle(), is( equalTo( title ) ) );
    assertThat( event.getWindow(), is( equalTo( window ) ) );
  }

  @Test
  public void testMouseEvent() {
    RenderNode node = mock( RenderNode.class );
    MouseEvent mouseEvent = mock( MouseEvent.class );

    ReportMouseEvent event = new ReportMouseEvent( node, mouseEvent );

    assertThat( event.getSourceNode(), is( equalTo( node ) ) );
    assertThat( event.getSourceEvent(), is( equalTo( mouseEvent ) ) );
  }
}
