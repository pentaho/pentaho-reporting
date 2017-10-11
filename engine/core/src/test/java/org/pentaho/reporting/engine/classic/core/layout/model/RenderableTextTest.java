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
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.output.GenericOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.text.DefaultRenderableTextFactory;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.fonts.encoding.ByteBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.manual.Ascii;

import static org.junit.Assert.*;

/**
 * @author Andrey Khayrutdinov
 */
public class RenderableTextTest {

  private static final String SAMPLE_LONG_WORD = "a_single_word_long_enough_to_be_broken";

  @BeforeClass
  public static void initEnvironment() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test( expected = IllegalArgumentException.class )
  public void splitBy_ThrowsException_WhenParameterIsNonPositive() throws Exception {
    RenderableText text = createText( "1" );
    text.splitBy( 0 );
  }

  @Test( expected = IllegalStateException.class )
  public void splitBy_ThrowsException_WhenParameterExceedsMinimalWidth() throws Exception {
    RenderableText text = createText( "1" );
    text.splitBy( text.getMinimumWidth() + 100 );
  }

  @Test
  public void splitBy_ReturnsNull_WhenWordBreakIsProhibited() throws Exception {
    RenderableText text = createText( SAMPLE_LONG_WORD, false );
    RenderableText[] pair = text.splitBy( text.getMinimumWidth() / 2 );
    assertNull( pair );
  }

  @Test
  public void splitBy_ReturnsNull_WhenFirstGlyphIsWiderThanParameter() throws Exception {
    RenderableText text = createText( "w" );
    RenderableText[] pair = text.splitBy( 1 );
    assertNull( pair );
  }

  @Test
  public void splitBy_ReturnsPairOfDescendants() throws Exception {
    RenderableText text = createText( SAMPLE_LONG_WORD );
    RenderableText[] pair = text.splitBy( text.getMinimumWidth() / 2 );
    assertNotNull( pair );
    assertEquals( 2, pair.length );
  }

  @Test
  public void splitBy_MakesAllReplacementsInParent() throws Exception {
    RenderBox parentBox = new InlineRenderBox();

    RenderableText text = createText( SAMPLE_LONG_WORD );
    parentBox.addChild( text );

    RenderableText[] pair = text.splitBy( text.getMinimumWidth() / 2 );
    assertNotNull( pair );
    assertEquals( 2, pair.length );

    assertEquals( 2, parentBox.getChildCount() );
    assertEquals( pair[0], parentBox.getFirstChild() );
    assertEquals( pair[1], parentBox.getLastChild() );
    assertNull( text.getParent() );
  }

  public static RenderableText createText( String content ) {
    return createText( content, true );
  }

  public static RenderableText createText( String content, boolean allowWordBreaks ) {
    // adding explicit space to force doing word break
    content += " ";

    CodePointBuffer buffer =
        new Ascii().decode( new ByteBuffer( content.getBytes() ), new CodePointBuffer( content.length() ) );
    int[] textCodepoints = buffer.getBuffer();

    ElementStyleSheet styleSheet = new ElementStyleSheet();
    styleSheet.copyFrom( ElementDefaultStyleSheet.getDefaultStyle() );
    styleSheet.setStyleProperty( TextStyleKeys.WORDBREAK, allowWordBreaks );

    DefaultRenderableTextFactory factory = new DefaultRenderableTextFactory( new GenericOutputProcessorMetaData() );
    RenderNode[] nodes =
        factory.createText( textCodepoints, 0, textCodepoints.length, new SimpleStyleSheet( styleSheet ),
            LabelType.INSTANCE, new InstanceID(), new ReportAttributeMap<Object>() );

    RenderableText text = (RenderableText) nodes[0];
    assertTrue( text.getMinimumWidth() > 0 );
    return text;
  }
}
