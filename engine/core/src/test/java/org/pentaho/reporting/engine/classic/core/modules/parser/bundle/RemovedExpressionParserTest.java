/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.modules.misc.beanshell.BSHExpression;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RemovedExpressionParserTest {
  private static final String RHINO_EXPRESSION =
      "org.pentaho.reporting.engine.classic.extensions.modules.rhino.RhinoExpression";

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @After
  public void tearDown() {
    setIgnoreRemovedExpressions( null );
  }

  private static void setIgnoreRemovedExpressions( final String value ) {
    ClassicEngineBoot.getInstance().getEditableConfig()
        .setConfigProperty( CompatibilityMapperUtil.IGNORE_REMOVED_EXPRESSIONS_KEY, value );
  }

  @Test
  public void testRemovedExpressionIsRecognizedAfterLegacyMapping() {
    setIgnoreRemovedExpressions( "true" );
    assertTrue( CompatibilityMapperUtil.isIgnoredExpression( RHINO_EXPRESSION ) );
    assertTrue( CompatibilityMapperUtil.isIgnoredExpression( CompatibilityMapperUtil.mapClassName(
        "org.jfree.report.ext.modules.rhino.RhinoExpression" ) ) );
    assertFalse( CompatibilityMapperUtil.isIgnoredExpression( BSHExpression.class.getName() ) );
    assertFalse( CompatibilityMapperUtil.isIgnoredExpression( null ) );
  }

  @Test
  public void testRemovedExpressionsAreNotIgnoredByDefault() {
    assertFalse( CompatibilityMapperUtil.isIgnoredExpression( RHINO_EXPRESSION ) );
    setIgnoreRemovedExpressions( "false" );
    assertFalse( CompatibilityMapperUtil.isIgnoredExpression( RHINO_EXPRESSION ) );
  }

  @Test
  public void testReportWithRemovedExpressionsFailsToLoadByDefault() throws Exception {
    final byte[] bundle = replaceClassName( writeReport(), BSHExpression.class.getName(), RHINO_EXPRESSION );
    try {
      load( bundle );
      fail( "Removed expressions must be rejected unless the parser has been told to ignore them" );
    } catch ( ResourceException e ) {
      // expected
    }
  }

  @Test
  public void testReportWithRemovedExpressionsLoadsWhenIgnoreIsEnabled() throws Exception {
    setIgnoreRemovedExpressions( "true" );
    final byte[] bundle = replaceClassName( writeReport(), BSHExpression.class.getName(), RHINO_EXPRESSION );
    // function, attribute expression and style expression
    assertEquals( 3, countOccurrences( bundle, RHINO_EXPRESSION ) );
    final MasterReport loaded = load( bundle );

    assertEquals( 2, loaded.getExpressions().size() );
    assertNotNull( loaded.getExpressions().get( "Kept1" ) );
    assertNotNull( loaded.getExpressions().get( "Kept2" ) );
    assertNull( loaded.getExpressions().get( "Removed" ) );

    final Element label = loaded.getReportHeader().getElement( 0 );
    assertNull( label.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE ) );
    assertNull( label.getStyleExpression( ElementStyleKeys.BACKGROUND_COLOR ) );
  }

  @Test
  public void testReportStillRendersWhenFieldReferencesRemovedFunction() throws Exception {
    setIgnoreRemovedExpressions( "true" );
    final MasterReport loaded = load( replaceClassName( writeReport(), BSHExpression.class.getName(), RHINO_EXPRESSION ) );

    final ByteArrayOutputStream html = new ByteArrayOutputStream();
    HtmlReportUtil.createStreamHTML( loaded, html );
    assertTrue( new String( html.toByteArray(), StandardCharsets.UTF_8 ).contains( "label" ) );
  }

  @Test
  public void testUnknownExpressionStillFailsToLoad() throws Exception {
    setIgnoreRemovedExpressions( "true" );
    final byte[] broken = replaceClassName( writeReport(), BSHExpression.class.getName(), "org.example.DoesNotExist" );
    try {
      load( broken );
      fail( "Expressions that were not explicitly removed must still be rejected" );
    } catch ( ResourceException e ) {
      // expected
    }
  }

  private static byte[] writeReport() throws Exception {
    final MasterReport report = new MasterReport();

    final FormulaExpression kept1 = new FormulaExpression();
    kept1.setName( "Kept1" );
    kept1.setFormula( "=1" );
    report.addExpression( kept1 );

    final BSHExpression removed = new BSHExpression();
    removed.setName( "Removed" );
    report.addExpression( removed );

    final FormulaExpression kept2 = new FormulaExpression();
    kept2.setName( "Kept2" );
    kept2.setFormula( "=2" );
    report.addExpression( kept2 );

    final LabelElementFactory factory = new LabelElementFactory();
    factory.setText( "label" );
    factory.setMinimumWidth( 100f );
    factory.setMinimumHeight( 20f );
    final Element label = factory.createElement();
    label.setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, new BSHExpression() );
    label.setStyleExpression( ElementStyleKeys.BACKGROUND_COLOR, new BSHExpression() );
    report.getReportHeader().addElement( label );

    final TextFieldElementFactory fieldFactory = new TextFieldElementFactory();
    fieldFactory.setFieldname( "Removed" );
    report.getReportHeader().addElement( fieldFactory.createElement() );

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    BundleWriter.writeReportToZipStream( report, out );
    return out.toByteArray();
  }

  private static MasterReport load( final byte[] bundle ) throws ResourceException {
    return (MasterReport) new ResourceManager().createDirectly( bundle, MasterReport.class ).getResource();
  }

  private static int countOccurrences( final byte[] bundle, final String text ) throws IOException {
    int count = 0;
    try ( ZipInputStream in = new ZipInputStream( new ByteArrayInputStream( bundle ) ) ) {
      while ( in.getNextEntry() != null ) {
        final String content = new String( in.readAllBytes(), StandardCharsets.UTF_8 );
        for ( int i = content.indexOf( text ); i >= 0; i = content.indexOf( text, i + text.length() ) ) {
          count++;
        }
      }
    }
    return count;
  }

  private static byte[] replaceClassName( final byte[] bundle, final String from, final String to )
    throws IOException {
    final ByteArrayOutputStream result = new ByteArrayOutputStream();
    try ( ZipInputStream in = new ZipInputStream( new ByteArrayInputStream( bundle ) );
          ZipOutputStream out = new ZipOutputStream( result ) ) {
      ZipEntry entry;
      while ( ( entry = in.getNextEntry() ) != null ) {
        byte[] data = in.readAllBytes();
        if ( entry.getName().endsWith( ".xml" ) ) {
          data = new String( data, StandardCharsets.UTF_8 ).replace( from, to ).getBytes( StandardCharsets.UTF_8 );
        }

        final ZipEntry copy = new ZipEntry( entry.getName() );
        if ( "mimetype".equals( entry.getName() ) ) {
          // The bundle mime-type entry has to stay uncompressed.
          final CRC32 crc = new CRC32();
          crc.update( data );
          copy.setMethod( ZipEntry.STORED );
          copy.setSize( data.length );
          copy.setCrc( crc.getValue() );
        }
        out.putNextEntry( copy );
        out.write( data );
        out.closeEntry();
      }
    }
    return result.toByteArray();
  }
}
