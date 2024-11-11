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


package org.pentaho.reporting.engine.classic.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;

import java.util.Locale;

public class DefaultReportEnvironmentTest {

  private static final String LOCALE_OVERRIDE =
      "org.pentaho.reporting.engine.classic.core.environment.designtime.Locale";

  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void normally_locale_defaults_to_system_locale() {
    ReportEnvironment re = new DefaultReportEnvironment( ClassicEngineBoot.getInstance().getGlobalConfig() );
    Assert.assertEquals( re.getLocale(), Locale.getDefault() );
  }

  @Test
  public void locale_can_be_explicitly_overridden() {
    DefaultReportEnvironment re = new DefaultReportEnvironment( ClassicEngineBoot.getInstance().getGlobalConfig() );
    re.setLocale( Locale.GERMAN );
    Assert.assertEquals( re.getLocale(), Locale.GERMAN );
  }

  @Test
  public void locale_can_be_changed_via_designtime_locale() {
    HierarchicalConfiguration conf =
        new HierarchicalConfiguration( ClassicEngineBoot.getInstance().getGlobalConfig() );
    conf.setConfigProperty( LOCALE_OVERRIDE, Locale.FRENCH.toString() );
    DefaultReportEnvironment re = new DefaultReportEnvironment( conf );
    Assert.assertEquals( re.getLocale(), Locale.FRENCH );
  }

  @Test
  public void locale_explicit_definitions_override_design_time_behaviours() {
    HierarchicalConfiguration conf =
        new HierarchicalConfiguration( ClassicEngineBoot.getInstance().getGlobalConfig() );
    conf.setConfigProperty( LOCALE_OVERRIDE, Locale.FRENCH.toString() );
    DefaultReportEnvironment re = new DefaultReportEnvironment( conf );
    re.setLocale( Locale.GERMAN );
    Assert.assertEquals( re.getLocale(), Locale.GERMAN );
  }

  @Test
  public void locale_properties_are_correct() {
    DefaultReportEnvironment re = new DefaultReportEnvironment( ClassicEngineBoot.getInstance().getGlobalConfig() );
    Locale weirdGerman = new Locale( "de", "DE", "WeirdLocalAccent" );
    Assert.assertEquals( weirdGerman.getCountry(), "DE" );
    Assert.assertEquals( weirdGerman.getLanguage(), "de" );
    Assert.assertEquals( weirdGerman.getVariant(), "WeirdLocalAccent" );

    re.setLocale( weirdGerman );
    Assert.assertEquals( re.getEnvironmentProperty( "locale" ), weirdGerman.toString() );
    Assert.assertEquals( re.getEnvironmentProperty( "locale-language" ), weirdGerman.getLanguage() );
    Assert.assertEquals( re.getEnvironmentProperty( "locale-short" ), "de_DE" );
  }

  @Test
  public void locale_without_country_is_handled() {
    DefaultReportEnvironment re = new DefaultReportEnvironment( ClassicEngineBoot.getInstance().getGlobalConfig() );
    Locale weirdGerman = new Locale( "de", "", "WeirdLocalAccent" );
    Assert.assertEquals( weirdGerman.getCountry(), "" );
    Assert.assertEquals( weirdGerman.getLanguage(), "de" );
    Assert.assertEquals( weirdGerman.getVariant(), "WeirdLocalAccent" );

    re.setLocale( weirdGerman );
    Assert.assertEquals( re.getEnvironmentProperty( "locale" ), weirdGerman.toString() );
    Assert.assertEquals( re.getEnvironmentProperty( "locale-language" ), weirdGerman.getLanguage() );
    Assert.assertEquals( re.getEnvironmentProperty( "locale-short" ), "de" );
  }
}
