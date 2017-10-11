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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;

public class ExitActionPluginTest extends ActionTestingUtil {

  private ExitActionPlugin plugin;

  @Before
  public void setUp() {
    super.setUp();
    plugin = new ExitActionPlugin();
  }

  @Test
  public void testConfigure() {
    plugin.initialize( context );
    PreviewPane reportPane = mock( PreviewPane.class );

    boolean result = plugin.configure( reportPane );

    verify( reportPane ).setClosed( true );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Override
  protected String getSmallIconKey() {
    return "action.close.small-icon";
  }

  @Override
  protected String getNameValue() {
    return "test close name";
  }

  @Override
  protected String getDescriptionValue() {
    return "test close description";
  }

  @Override
  protected String getLargeIconKey() {
    return "action.close.icon";
  }

  @Override
  protected ControlActionPlugin getPlugin() {
    return plugin;
  }

  @Override
  protected String getExpectedPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.close.";
  }

  @Override
  protected String getPrefix() {
    return plugin.getConfigurationPrefix();
  }
}
