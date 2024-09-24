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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.StyleDefinitionWriter;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class Prd4581IT extends TestCase {
  public Prd4581IT() {
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPrptStyleParsing() throws ResourceException {
    final URL resource = getClass().getResource( "Prd-4581.prptstyle" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource loaded = mgr.createDirectly( resource, ElementStyleDefinition.class );
    final ElementStyleDefinition style = (ElementStyleDefinition) loaded.getResource();
    assertEquals( 1, style.getRuleCount() );
    final ElementStyleSheet rule = style.getRule( 0 );
    assertNotNull( rule );

    assertEquals( "Arial", rule.getStyleProperty( TextStyleKeys.FONT ) );
  }

  public void testPrptStyleReParsing() throws ResourceException, IOException {
    final URL resource = getClass().getResource( "Prd-4581.prptstyle" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource loaded = mgr.createDirectly( resource, ElementStyleDefinition.class );
    final ElementStyleDefinition style = (ElementStyleDefinition) loaded.getResource();
    assertEquals( 1, style.getRuleCount() );
    final ElementStyleSheet rule = style.getRule( 0 );
    assertNotNull( rule );

    rule.setStyleProperty( TextStyleKeys.ITALIC, Boolean.TRUE );

    final ByteArrayOutputStream bout = new ByteArrayOutputStream();

    final StyleDefinitionWriter writer = new StyleDefinitionWriter();
    writer.write( bout, style );

    Resource parsed = mgr.createDirectly( bout.toByteArray(), ElementStyleDefinition.class );
    final ElementStyleDefinition parsedStyle = (ElementStyleDefinition) parsed.getResource();

    assertEquals( 1, parsedStyle.getRuleCount() );
    final ElementStyleSheet parsedRule = parsedStyle.getRule( 0 );
    assertNotNull( parsedRule );
    assertEquals( Boolean.TRUE, rule.getStyleProperty( TextStyleKeys.ITALIC ) );
  }

}
