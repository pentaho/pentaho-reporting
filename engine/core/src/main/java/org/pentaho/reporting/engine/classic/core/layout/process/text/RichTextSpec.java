/*
 *
 *  * This program is free software; you can redistribute it and/or modify it under the
 *  * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  * Foundation.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public License along with this
 *  * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  * or from the Free Software Foundation, Inc.,
 *  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *  *
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  * See the GNU Lesser General Public License for more details.
 *  *
 *  * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 *
 */

package org.pentaho.reporting.engine.classic.core.layout.process.text;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextDirection;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RichTextSpec {
  public static class StyledChunk {
    private int start;
    private int end;
    private RenderNode originatingTextNode;
    private String text;
    private Map<AttributedCharacterIterator.Attribute, Object> attributes;
    private ReportAttributeMap<Object> originalAttributes;
    private StyleSheet styleSheet;
    private InstanceID instanceID;

    public StyledChunk( final int start, final int end, final RenderNode originatingTextNode,
        final Map<AttributedCharacterIterator.Attribute, Object> attributes,
        final ReportAttributeMap<Object> originalAttributes, final StyleSheet styleSheet, final InstanceID instanceID,
        final String text ) {
      ArgumentNullException.validate( "originatingTextNode", originatingTextNode );
      ArgumentNullException.validate( "attributes", attributes );
      ArgumentNullException.validate( "text", text );
      ArgumentNullException.validate( "originalAttributes", originalAttributes );
      ArgumentNullException.validate( "styleSheet", styleSheet );
      ArgumentNullException.validate( "instanceID", instanceID );

      this.instanceID = instanceID;
      this.start = start;
      this.end = end;
      this.originatingTextNode = originatingTextNode;
      this.attributes = Collections.unmodifiableMap( attributes );
      this.originalAttributes = originalAttributes;
      this.styleSheet = styleSheet;
      this.text = text;
    }

    public Map<AttributedCharacterIterator.Attribute, Object> getAttributes() {
      return attributes;
    }

    public ReportAttributeMap<Object> getOriginalAttributes() {
      return originalAttributes;
    }

    public StyleSheet getStyleSheet() {
      return styleSheet;
    }

    public String getText() {
      return text;
    }

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }

    public RenderNode getOriginatingTextNode() {
      return originatingTextNode;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final StyledChunk that = (StyledChunk) o;

      if ( instanceID != that.instanceID ) {
        return false;
      }
      if ( end != that.end ) {
        return false;
      }
      if ( start != that.start ) {
        return false;
      }
      if ( !attributes.equals( that.attributes ) ) {
        return false;
      }
      if ( !originatingTextNode.equals( that.originatingTextNode ) ) {
        return false;
      }
      if ( !text.equals( that.text ) ) {
        return false;
      }

      return true;
    }

    public InstanceID getInstanceID() {
      return instanceID;
    }

    public int hashCode() {
      int result = start;
      result = 31 * result + end;
      result = 31 * result + instanceID.hashCode();
      result = 31 * result + originatingTextNode.hashCode();
      result = 31 * result + text.hashCode();
      result = 31 * result + attributes.hashCode();
      return result;
    }
  }

  private String text;
  private AttributedString attributedString;
  private List<StyledChunk> styleChunks;
  private TextDirection runDirection;

  public RichTextSpec( final String text, final TextDirection runDirection, final List<StyledChunk> styleChunks ) {
    ArgumentNullException.validate( "text", text );
    ArgumentNullException.validate( "styleChunks", styleChunks );
    ArgumentNullException.validate( "runDirection", runDirection );

    if ( styleChunks.isEmpty() ) {
      throw new IllegalStateException( "Need at least one style chunk" );
    }
    if ( text.length() == 0 ) {
      throw new IllegalStateException( "Text must not be empty." );
    }

    this.runDirection = runDirection;
    this.text = text;
    this.styleChunks = Collections.unmodifiableList( new ArrayList<StyledChunk>( styleChunks ) );
    this.attributedString = createText();
  }

  private AttributedString createText() {
    AttributedString str = new AttributedString( text );
    str.addAttribute( TextAttribute.RUN_DIRECTION, TextDirection.RTL.equals( runDirection ) );
    int startPosition = 0;
    for ( final StyledChunk chunk : styleChunks ) {
      int length = chunk.getText().length();
      int endIndex = startPosition + length;
      str.addAttributes( chunk.getAttributes(), startPosition, endIndex );
      startPosition = endIndex;
    }
    return str;
  }

  public String getText() {
    return text;
  }

  public AttributedString getAttributedString() {
    return attributedString;
  }

  public AttributedCharacterIterator createAttributedCharacterIterator() {
    return attributedString.getIterator();
  }

  public List<StyledChunk> getStyleChunks() {
    return styleChunks;
  }

  public RichTextSpec substring( final int start, final int end ) {
    List<StyledChunk> clippedChunks = new ArrayList<StyledChunk>();
    for ( final StyledChunk chunk : styleChunks ) {
      if ( chunk.end <= start ) {
        continue;
      }
      if ( chunk.start >= end ) {
        continue;
      }

      int chunkStart;
      int textStart;
      if ( chunk.start < start ) {
        chunkStart = 0;
        textStart = start;
      } else {
        chunkStart = chunk.start - start;
        textStart = chunk.start;
      }
      int chunkEnd;
      int textEnd;
      if ( chunk.end < end ) {
        chunkEnd = chunk.end - start;
        textEnd = chunk.end;
      } else {
        chunkEnd = end - start;
        textEnd = end;
      }

      String clippedText = text.substring( textStart, textEnd );
      clippedChunks.add( new StyledChunk( chunkStart, chunkEnd, chunk.originatingTextNode, chunk.attributes,
          chunk.originalAttributes, chunk.styleSheet, chunk.instanceID, clippedText ) );
    }
    return new RichTextSpec( text.substring( start, end ), runDirection, clippedChunks );
  }

  public RenderableComplexText create( final RenderBox lineBoxContainer ) {
    AttributedCharacterIterator ci = createAttributedCharacterIterator();
    return create( lineBoxContainer, ci.getBeginIndex(), ci.getEndIndex() );
  }

  public RenderableComplexText create( final RenderBox lineBoxContainer, final int start, final int end ) {
    return new RenderableComplexText( lineBoxContainer.getStyleSheet(), lineBoxContainer.getInstanceId(),
        lineBoxContainer.getElementType(), lineBoxContainer.getAttributes(), this.substring( start, end ) );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final RichTextSpec that = (RichTextSpec) o;
    if ( !styleChunks.equals( that.styleChunks ) ) {
      return false;
    }
    if ( !text.equals( that.text ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = text.hashCode();
    result = 31 * result + styleChunks.hashCode();
    return result;
  }

  public int length() {
    return text.length();
  }

  public boolean isEmpty() {
    return styleChunks.isEmpty();
  }
}
