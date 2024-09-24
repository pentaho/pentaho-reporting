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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Locale;

import javax.swing.Icon;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.IconTheme;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.libraries.base.config.Configuration;

public abstract class ActionTestingUtil {

  protected SwingGuiContext context;
  private Icon smallIcon;
  private Icon largeIcon;

  @Before
  public void setUp() {
    context = mock( SwingGuiContext.class );
    Configuration conf = mock( Configuration.class );
    IconTheme theme = mock( IconTheme.class );
    smallIcon = mock( Icon.class );
    largeIcon = mock( Icon.class );

    Locale locale = new Locale( "test_test" );

    doReturn( conf ).when( context ).getConfiguration();
    doReturn( locale ).when( context ).getLocale();
    doReturn( theme ).when( context ).getIconTheme();
    doReturn( smallIcon ).when( theme ).getSmallIcon( locale, getSmallIconKey() );
    doReturn( largeIcon ).when( theme ).getLargeIcon( locale, getLargeIconKey() );
  }

  protected abstract String getSmallIconKey();

  protected abstract String getNameValue();

  protected abstract String getDescriptionValue();

  protected abstract String getLargeIconKey();

  protected abstract String getExpectedPrefix();

  protected abstract String getPrefix();

  protected abstract ControlActionPlugin getPlugin();

  @Test
  public void testResources() {
    assertThat( getPrefix(), is( equalTo( getExpectedPrefix() ) ) );

    boolean result = getPlugin().initialize( context );

    assertThat( result, is( equalTo( true ) ) );
    assertThat( getPlugin().getDisplayName(), is( equalTo( getNameValue() ) ) );
    assertThat( getPlugin().getShortDescription(), is( equalTo( getDescriptionValue() ) ) );
    assertThat( getPlugin().getSmallIcon(), is( equalTo( smallIcon ) ) );
    assertThat( getPlugin().getLargeIcon(), is( equalTo( largeIcon ) ) );
    assertThat( getPlugin().getMnemonicKey(), is( notNullValue() ) );
    assertThat( getPlugin().getAcceleratorKey(), is( nullValue() ) );
  }
}
