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
package org.pentaho.reporting.engine.classic.core.layout.complextext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.text.RichTextSpec;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextDirection;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings( "HardCodedStringLiteral" )
public class RichTextSpecTest {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testSubString() throws Exception {
    RichTextSpec spec = createText();

    RichTextSpec substring = spec.substring( 0, 15 );
    Assert.assertEquals( spec, substring );
  }

  @Test
  public void testSubStringStart() throws Exception {
    RichTextSpec spec = createText();

    RichTextSpec substring = spec.substring( 0, 5 );
    Assert.assertEquals( spec.getText().substring( 0, 5 ), substring.getText() );
    Assert.assertEquals( 2, substring.getStyleChunks().size() );
    Assert.assertEquals( spec.getStyleChunks().get( 0 ), substring.getStyleChunks().get( 0 ) );
    RichTextSpec.StyledChunk actual = substring.getStyleChunks().get( 1 );
    Assert.assertEquals( "de", actual.getText() );
    Assert.assertEquals( 3, actual.getStart() );
    Assert.assertEquals( 5, actual.getEnd() );
  }

  @Test
  public void testSubStringEnd() throws Exception {
    RichTextSpec spec = createText();

    RichTextSpec substring = spec.substring( 10, 15 );
    Assert.assertEquals( spec.getText().substring( 10, 15 ), substring.getText() );
    Assert.assertEquals( 2, substring.getStyleChunks().size() );

    RichTextSpec.StyledChunk actual = substring.getStyleChunks().get( 0 );
    Assert.assertEquals( "kl", actual.getText() );
    Assert.assertEquals( 0, actual.getStart() );
    Assert.assertEquals( 2, actual.getEnd() );

    RichTextSpec.StyledChunk act2 = substring.getStyleChunks().get( 1 );
    Assert.assertEquals( "MNO", act2.getText() );
    Assert.assertEquals( 2, act2.getStart() );
    Assert.assertEquals( 5, act2.getEnd() );
  }

  @Test
  public void testSubStringMiddle() throws Exception {
    RichTextSpec spec = createText();

    RichTextSpec substring = spec.substring( 10, 14 );
    Assert.assertEquals( spec.getText().substring( 10, 14 ), substring.getText() );
    Assert.assertEquals( 2, substring.getStyleChunks().size() );

    RichTextSpec.StyledChunk actual = substring.getStyleChunks().get( 0 );
    Assert.assertEquals( "kl", actual.getText() );
    Assert.assertEquals( 0, actual.getStart() );
    Assert.assertEquals( 2, actual.getEnd() );

    RichTextSpec.StyledChunk act2 = substring.getStyleChunks().get( 1 );
    Assert.assertEquals( "MN", act2.getText() );
    Assert.assertEquals( 2, act2.getStart() );
    Assert.assertEquals( 4, act2.getEnd() );
  }

  @Test
  public void testSubStringMiddleAtBoundary() throws Exception {
    RichTextSpec spec = createText();

    RichTextSpec substring = spec.substring( 3, 6 );
    Assert.assertEquals( spec.getText().substring( 3, 6 ), substring.getText() );
    Assert.assertEquals( 1, substring.getStyleChunks().size() );

    RichTextSpec.StyledChunk act2 = substring.getStyleChunks().get( 0 );
    Assert.assertEquals( "def", act2.getText() );
    Assert.assertEquals( 0, act2.getStart() );
    Assert.assertEquals( 3, act2.getEnd() );
  }

  private RichTextSpec createText() {
    Map<AttributedCharacterIterator.Attribute, Object> attrs =
        new HashMap<AttributedCharacterIterator.Attribute, Object>();
    List<RichTextSpec.StyledChunk> chunks = new ArrayList<RichTextSpec.StyledChunk>();
    InstanceID id = new InstanceID();
    chunks.add( new RichTextSpec.StyledChunk( 0, 3, new SpacerRenderNode(), attrs, new ReportAttributeMap<Object>(),
        ElementDefaultStyleSheet.getDefaultStyle(), new InstanceID(), "ABC" ) );
    chunks.add( new RichTextSpec.StyledChunk( 3, 6, new SpacerRenderNode(), attrs, new ReportAttributeMap<Object>(),
        ElementDefaultStyleSheet.getDefaultStyle(), new InstanceID(), "def" ) );
    chunks.add( new RichTextSpec.StyledChunk( 6, 9, new SpacerRenderNode(), attrs, new ReportAttributeMap<Object>(),
        ElementDefaultStyleSheet.getDefaultStyle(), new InstanceID(), "GHI" ) );
    chunks.add( new RichTextSpec.StyledChunk( 9, 12, new SpacerRenderNode(), attrs, new ReportAttributeMap<Object>(),
        ElementDefaultStyleSheet.getDefaultStyle(), new InstanceID(), "jkl" ) );
    chunks.add( new RichTextSpec.StyledChunk( 12, 15, new SpacerRenderNode(), attrs, new ReportAttributeMap<Object>(),
        ElementDefaultStyleSheet.getDefaultStyle(), new InstanceID(), "MNO" ) );
    return new RichTextSpec( "ABCdefGHIjklMNO", TextDirection.LTR, chunks );
  }
}
