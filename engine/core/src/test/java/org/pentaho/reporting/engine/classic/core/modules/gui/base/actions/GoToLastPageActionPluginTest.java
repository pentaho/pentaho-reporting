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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;

public class GoToLastPageActionPluginTest extends GoToActionPluginTest {

  private GoToLastPageActionPlugin plugin;

  @Before
  public void setUp() {
    super.setUp();
    plugin = new GoToLastPageActionPlugin();
  }

  @Override
  protected String getSmallIconKey() {
    return "action.lastpage.small-icon";
  }

  @Override
  protected String getNameValue() {
    return "test lastpage name";
  }

  @Override
  protected String getDescriptionValue() {
    return "test lastpage description";
  }

  @Override
  protected String getLargeIconKey() {
    return "action.lastpage.icon";
  }

  @Override
  protected String getExpectedPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.go-to-last.";
  }

  @Override
  protected String getPrefix() {
    return getPlugin().getConfigurationPrefix();
  }

  @Override
  protected GoToLastPageActionPlugin getPlugin() {
    return plugin;
  }

  @Test
  public void testRevalidate() {
    doReturn( false ).when( source ).isPaginated();
    boolean result = plugin.initialize( context );
    assertThat( result, is( equalTo( true ) ) );
    assertThat( plugin.isEnabled(), is( equalTo( false ) ) );

    doReturn( true ).when( source ).isPaginated();
    doReturn( 0 ).when( source ).getPageNumber();
    result = plugin.initialize( context );
    assertThat( result, is( equalTo( true ) ) );
    assertThat( plugin.isEnabled(), is( equalTo( false ) ) );

    doReturn( true ).when( source ).isPaginated();
    doReturn( 5 ).when( source ).getPageNumber();
    doReturn( 5 ).when( source ).getNumberOfPages();
    result = plugin.initialize( context );
    assertThat( result, is( equalTo( true ) ) );
    assertThat( plugin.isEnabled(), is( equalTo( false ) ) );

    doReturn( true ).when( source ).isPaginated();
    doReturn( 5 ).when( source ).getPageNumber();
    doReturn( 15 ).when( source ).getNumberOfPages();
    result = plugin.initialize( context );
    assertThat( result, is( equalTo( true ) ) );
    assertThat( plugin.isEnabled(), is( equalTo( true ) ) );
  }

  @Test
  public void testConfigure() {
    PreviewPane reportPane = mock( PreviewPane.class );
    doReturn( 2 ).when( reportPane ).getNumberOfPages();

    boolean result = plugin.configure( reportPane );

    verify( reportPane ).setPageNumber( 2 );
    assertThat( result, is( equalTo( true ) ) );
  }
}
