/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2016 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.parameters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;

/**
 * Created by dima.prokopenko@gmail.com on 9/30/2016.
 */
public class ParameterDefinitionEntryTest {

  private final String paramNamesp = "anyNamespace";
  private final String paramName = "paranName";
  private final String paramValue = "anyValue";

  AbstractParameter entry;
  ParameterContext context;

  @Before
  public void before() {
    entry = new PlainParameter( "name" );
    entry.setParameterAttribute( paramNamesp, paramName, paramValue );

    context = mock( ParameterContext.class );
  }

  @Test
  public void translatedParameterAttributeNullTest() {
    entry.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID, null );
    String actual = entry.getTranslatedParameterAttribute( paramNamesp, paramName, context );
    assertEquals( paramValue, actual );
  }

  @Test
  public void translatedParameterAttributeTest() throws IOException {
    entry.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID, "rid" );

    ResourceBundleFactory factory = mock( ResourceBundleFactory.class );
    InputStream stream = new ByteArrayInputStream( ( paramValue + "=TRANSLATED" ).getBytes( StandardCharsets.UTF_8 ) );
    ResourceBundle rb = new PropertyResourceBundle( stream );
    when( factory.getResourceBundle( eq( "rid" ) ) ).thenReturn( rb );
    when( context.getResourceBundleFactory() ).thenReturn( factory );

    String actual = entry.getTranslatedParameterAttribute( paramNamesp, paramName, context );

    assertEquals( "TRANSLATED", actual );
  }
}
