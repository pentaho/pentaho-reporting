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


package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.LegacyType;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.text.DefaultRenderableTextFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.manual.Utf16LE;
import org.pentaho.reporting.libraries.fonts.text.GraphemeClusterProducer;

public class TextTest extends TestCase {
  public TextTest() {
  }

  public TextTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testTextNewline() {
    final DefaultRenderableTextFactory textFactory =
        new DefaultRenderableTextFactory( new HtmlOutputProcessorMetaData( HtmlOutputProcessorMetaData.PAGINATION_NONE ) );
    textFactory.startText();

    CodePointBuffer buffer = Utf16LE.getInstance().decodeString( "Test\n\n\nTest", null ); //$NON-NLS-1$
    final int[] data = buffer.getBuffer();

    final int length = buffer.getLength();
    final ElementDefaultStyleSheet defaultStyle = ElementDefaultStyleSheet.getDefaultStyle();
    final RenderNode[] renderNodes =
        textFactory.createText( data, 0, length, new SimpleStyleSheet( defaultStyle ), LegacyType.INSTANCE,
            new InstanceID(), ReportAttributeMap.EMPTY_MAP );
    final RenderNode[] finishNodes = textFactory.finishText();

    assertNotNull( renderNodes );
    assertEquals( renderNodes.length, 3 );
    assertTrue( renderNodes[0].getMinimumChunkWidth() > 0 );
    assertTrue( renderNodes[1].getMinimumChunkWidth() == 0 );
    assertTrue( renderNodes[2].getMinimumChunkWidth() == 0 );

    assertNotNull( finishNodes );
    assertEquals( finishNodes.length, 1 );
    assertTrue( finishNodes[0].getMinimumChunkWidth() > 0 );
  }

  // // Broken ..
  public void testTextRNewline() {
    final DefaultRenderableTextFactory textFactory =
        new DefaultRenderableTextFactory( new HtmlOutputProcessorMetaData( HtmlOutputProcessorMetaData.PAGINATION_NONE ) );
    textFactory.startText();

    CodePointBuffer buffer = Utf16LE.getInstance().decodeString( "T\r\n\r\n\r\nT", null ); //$NON-NLS-1$
    final int[] data = buffer.getBuffer();

    final int length = buffer.getLength();
    final ElementDefaultStyleSheet defaultStyle = ElementDefaultStyleSheet.getDefaultStyle();
    final RenderNode[] renderNodes =
        textFactory.createText( data, 0, length, new SimpleStyleSheet( defaultStyle ), LegacyType.INSTANCE,
            new InstanceID(), ReportAttributeMap.EMPTY_MAP );
    final RenderNode[] finishNodes = textFactory.finishText();

    assertNotNull( renderNodes );
    assertEquals( renderNodes.length, 3 );
    assertTrue( renderNodes[0].getMinimumChunkWidth() > 0 );
    assertTrue( renderNodes[1].getMinimumChunkWidth() == 0 );
    assertTrue( renderNodes[2].getMinimumChunkWidth() == 0 );

    assertNotNull( finishNodes );
    assertEquals( finishNodes.length, 1 );
    assertTrue( finishNodes[0].getMinimumChunkWidth() > 0 );
  }

  public void testGraphemeClusterGenerationWindows() {
    CodePointBuffer buffer = Utf16LE.getInstance().decodeString( "T\r\n\r\n\r\nT", null ); //$NON-NLS-1$
    final int[] data = buffer.getBuffer();
    final boolean[] result = new boolean[] { true, true, false, true, false, true, false, true };
    GraphemeClusterProducer prod = new GraphemeClusterProducer();

    for ( int i = 0; i < buffer.getLength(); i++ ) {
      final int codepoint = data[i];
      if ( prod.createGraphemeCluster( codepoint ) != result[i] ) {
        TestCase.fail();
      }
    }
  }

  public void testGraphemeClusterGenerationUnix() {
    CodePointBuffer buffer = Utf16LE.getInstance().decodeString( "T\n\n\nT", null ); //$NON-NLS-1$
    final int[] data = buffer.getBuffer();
    final boolean[] result = new boolean[] { true, true, true, true, true };
    GraphemeClusterProducer prod = new GraphemeClusterProducer();

    for ( int i = 0; i < buffer.getLength(); i++ ) {
      final int codepoint = data[i];
      if ( prod.createGraphemeCluster( codepoint ) != result[i] ) {
        TestCase.fail();
      }
    }
  }

  public void testGraphemeClusterGenerationOldMac() {
    CodePointBuffer buffer = Utf16LE.getInstance().decodeString( "T\r\r\rT", null ); //$NON-NLS-1$
    final int[] data = buffer.getBuffer();
    final boolean[] result = new boolean[] { true, true, true, true, true };
    GraphemeClusterProducer prod = new GraphemeClusterProducer();

    for ( int i = 0; i < buffer.getLength(); i++ ) {
      final int codepoint = data[i];
      if ( prod.createGraphemeCluster( codepoint ) != result[i] ) {
        TestCase.fail();
      }
    }
  }

  public void testWhitespaceCollapse() {
    final DefaultRenderableTextFactory textFactory =
        new DefaultRenderableTextFactory( new HtmlOutputProcessorMetaData( HtmlOutputProcessorMetaData.PAGINATION_NONE ) );
    textFactory.startText();

    CodePointBuffer buffer = Utf16LE.getInstance().decodeString( "T T T T", null ); //$NON-NLS-1$
    final int[] data = buffer.getBuffer();

    final Element element = new Element();
    final int length = buffer.getLength();
    element.getStyle().setStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE, WhitespaceCollapse.DISCARD );
    final RenderNode[] renderNodes =
        textFactory.createText( data, 0, length, SimpleStyleResolver.resolveOneTime( element ), LegacyType.INSTANCE,
            new InstanceID(), ReportAttributeMap.EMPTY_MAP );
    final RenderNode[] finishNodes = textFactory.finishText();

  }

  public void testTextWithFirstSpetialSimbol() throws Exception {
    final DefaultRenderableTextFactory textFactory =
        new DefaultRenderableTextFactory( new HtmlOutputProcessorMetaData( HtmlOutputProcessorMetaData.PAGINATION_NONE ) );
    textFactory.startText();

    String sourceText = new String( new char[] { 768, 768, 69, 114, 114, 111, 114 } ); // String sourceText = "?Error";
    CodePointBuffer buffer = Utf16LE.getInstance().decodeString( sourceText, null );
    final int[] data = buffer.getBuffer();

    textFactory.createText( data, 0, buffer.getLength(), new SimpleStyleSheet( ElementDefaultStyleSheet
        .getDefaultStyle() ), TextFieldType.INSTANCE, new InstanceID(), ReportAttributeMap.EMPTY_MAP );
    final RenderNode[] finishNodes = textFactory.finishText();

    assertNotNull( finishNodes );
    assertEquals( finishNodes.length, 1 );
    assertTrue( finishNodes[0] instanceof RenderableText );
    RenderableText textNode = (RenderableText) finishNodes[0];
    assertEquals( textNode.getRawText(), Utf16LE.getInstance().encodeString( buffer ) );
  }
}
