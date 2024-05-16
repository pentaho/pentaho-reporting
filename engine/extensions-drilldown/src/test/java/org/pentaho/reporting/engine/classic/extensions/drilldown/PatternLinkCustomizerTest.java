/*!
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
 * Copyright (c) 2019 - 2024 Hitachi Vantara..  All rights reserved.
 */
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
