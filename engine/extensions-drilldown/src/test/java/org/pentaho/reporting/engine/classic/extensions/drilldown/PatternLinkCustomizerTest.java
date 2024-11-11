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

package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LocalizationContext;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class PatternLinkCustomizerTest {

  private PatternLinkCustomizer patternLinkCustomizer;
  private FormulaContext formulaContext;
  private Configuration config;
  private DrillDownProfileMetaData profileMeta;
  private DrillDownProfile profile;
  private LocalizationContext localContext;

  @Test
  public void testFormatWithoutParams() throws EvaluationException {
    try ( MockedStatic<DrillDownProfileMetaData> mockedStatic = mockStatic( DrillDownProfileMetaData.class ) ) {
      formulaContext = mock(FormulaContext.class);
      config = mock(Configuration.class);
      profileMeta = mock(DrillDownProfileMetaData.class);
      profile = mock(DrillDownProfile.class);
      localContext = mock(LocalizationContext.class);
      mockedStatic.when( () -> DrillDownProfileMetaData.getInstance() ).thenReturn(profileMeta);
      when(formulaContext.getConfiguration()).thenReturn(config);
      when(formulaContext.getLocalizationContext()).thenReturn(localContext);
      when(localContext.getLocale()).thenReturn(Locale.ENGLISH);
      when(config.getConfigProperty("org.pentaho.reporting.libraries.formula.URLEncoding", "UTF-8")).thenReturn("UTF-8");
      when( profileMeta.getDrillDownProfile( anyString() ) ).thenReturn( profile );
      when( profile.getAttribute( "patternNoAttributes" ) ).thenReturn( "{0}"  );
      patternLinkCustomizer = new PatternLinkCustomizer();
      ParameterEntry[] paramEntries = {  };
      String result = patternLinkCustomizer.format( formulaContext, "generic-url", "http://www.google.com", paramEntries );
      assertEquals( "http://www.google.com", result );
    }
  }

  @Test
  public void testFormatWithParams() throws EvaluationException {
    try ( MockedStatic<DrillDownProfileMetaData> mockedStatic = mockStatic( DrillDownProfileMetaData.class ) ) {
      formulaContext = mock( FormulaContext.class );
      config = mock( Configuration.class );
      profileMeta = mock( DrillDownProfileMetaData.class );
      profile = mock( DrillDownProfile.class );
      localContext = mock( LocalizationContext.class );
      mockedStatic.when( () -> DrillDownProfileMetaData.getInstance() ).thenReturn( profileMeta );
      when( formulaContext.getConfiguration() ).thenReturn( config );
      when( formulaContext.getLocalizationContext() ).thenReturn( localContext );
      when( localContext.getLocale() ).thenReturn( Locale.ENGLISH );
      when( config.getConfigProperty( "org.pentaho.reporting.libraries.formula.URLEncoding", "UTF-8" ) ).thenReturn(
        "UTF-8" );
      when( profileMeta.getDrillDownProfile( anyString() ) ).thenReturn( profile );
      when( profile.getAttribute( "pattern" ) ).thenReturn( "{0}?{1}"  );
      patternLinkCustomizer = new PatternLinkCustomizer();
      ParameterEntry[] paramEntries = { new ParameterEntry( "param", "value" ) };
      String result =
        patternLinkCustomizer.format( formulaContext, "generic-url", "http://www.google.com", paramEntries );
      assertEquals( "http://www.google.com?param=value", result );
    }
  }

}
