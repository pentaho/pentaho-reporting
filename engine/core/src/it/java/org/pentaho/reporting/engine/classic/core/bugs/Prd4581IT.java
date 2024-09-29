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
