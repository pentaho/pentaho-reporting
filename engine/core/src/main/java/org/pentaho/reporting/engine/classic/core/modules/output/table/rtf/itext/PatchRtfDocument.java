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

package org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.itext;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Meta;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Section;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.rtf.RtfBasicElement;
import com.lowagie.text.rtf.RtfMapper;
import com.lowagie.text.rtf.document.RtfDocument;
import com.lowagie.text.rtf.document.RtfInfoElement;
import com.lowagie.text.rtf.field.RtfAnchor;
import com.lowagie.text.rtf.graphic.RtfImage;
import com.lowagie.text.rtf.list.RtfList;
import com.lowagie.text.rtf.list.RtfListItem;
import com.lowagie.text.rtf.text.RtfAnnotation;
import com.lowagie.text.rtf.text.RtfChapter;
import com.lowagie.text.rtf.text.RtfChunk;
import com.lowagie.text.rtf.text.RtfNewPage;
import com.lowagie.text.rtf.text.RtfParagraph;
import com.lowagie.text.rtf.text.RtfPhrase;
import com.lowagie.text.rtf.text.RtfSection;
import com.lowagie.text.rtf.text.RtfTab;

import java.util.ArrayList;

public class PatchRtfDocument extends RtfDocument {
  private static class PatchRtfMapper extends RtfMapper {
    private RtfDocument rtfDoc;

    private PatchRtfMapper( final RtfDocument doc ) {
      super( doc );
      this.rtfDoc = doc;
    }

    public RtfBasicElement[] mapElement( Element element ) throws DocumentException {
      ArrayList<RtfBasicElement> rtfElements = new ArrayList<RtfBasicElement>();
      if ( element instanceof RtfBasicElement ) {
        RtfBasicElement rtfElement = (RtfBasicElement) element;
        rtfElement.setRtfDocument( rtfDoc );
        return new RtfBasicElement[] { rtfElement };
      }
      switch ( element.type() ) {
        case Element.CHUNK:
          Chunk chunk = (Chunk) element;
          if ( chunk.hasAttributes() ) {
            if ( chunk.getAttributes().containsKey( Chunk.IMAGE ) ) {
              rtfElements.add( new RtfImage( rtfDoc, chunk.getImage() ) );
            } else if ( chunk.getAttributes().containsKey( Chunk.NEWPAGE ) ) {
              rtfElements.add( new RtfNewPage( rtfDoc ) );
            } else if ( chunk.getAttributes().containsKey( Chunk.TAB ) ) {
              Float tabPos = (Float) ( (Object[]) chunk.getAttributes().get( Chunk.TAB ) )[1];
              RtfTab tab = new RtfTab( tabPos.floatValue(), RtfTab.TAB_LEFT_ALIGN );
              tab.setRtfDocument( rtfDoc );
              rtfElements.add( tab );
              rtfElements.add( new RtfChunk( rtfDoc, new Chunk( "\t" ) ) );
            } else {
              rtfElements.add( new RtfChunk( rtfDoc, (Chunk) element ) );
            }
          } else {
            rtfElements.add( new RtfChunk( rtfDoc, (Chunk) element ) );
          }
          break;
        case Element.PHRASE:
          rtfElements.add( new RtfPhrase( rtfDoc, (Phrase) element ) );
          break;
        case Element.PARAGRAPH:
          rtfElements.add( new RtfParagraph( rtfDoc, (Paragraph) element ) );
          break;
        case Element.ANCHOR:
          rtfElements.add( new RtfAnchor( rtfDoc, (Anchor) element ) );
          break;
        case Element.ANNOTATION:
          rtfElements.add( new RtfAnnotation( rtfDoc, (Annotation) element ) );
          break;
        case Element.IMGRAW:
        case Element.IMGTEMPLATE:
        case Element.JPEG:
          rtfElements.add( new RtfImage( rtfDoc, (Image) element ) );
          break;
        case Element.AUTHOR:
        case Element.SUBJECT:
        case Element.KEYWORDS:
        case Element.TITLE:
        case Element.PRODUCER:
        case Element.CREATIONDATE:
          rtfElements.add( new RtfInfoElement( rtfDoc, (Meta) element ) );
          break;
        case Element.LIST:
          rtfElements.add( new RtfList( rtfDoc, (List) element ) ); // TODO: Testing
          break;
        case Element.LISTITEM:
          rtfElements.add( new RtfListItem( rtfDoc, (ListItem) element ) ); // TODO: Testing
          break;
        case Element.SECTION:
          rtfElements.add( new RtfSection( rtfDoc, (Section) element ) );
          break;
        case Element.CHAPTER:
          rtfElements.add( new RtfChapter( rtfDoc, (Chapter) element ) );
          break;
        case Element.TABLE:
          if ( element instanceof Table ) {
            rtfElements.add( new PatchRtfTable( rtfDoc, (Table) element ) );
          } else {
            rtfElements.add( new PatchRtfTable( rtfDoc, ( (SimpleTable) element ).createTable() ) );
          }
          break;
        case Element.PTABLE:
          if ( element instanceof PdfPTable ) {
            rtfElements.add( new PatchRtfTable( rtfDoc, (PdfPTable) element ) );
          } else {
            rtfElements.add( new PatchRtfTable( rtfDoc, ( (SimpleTable) element ).createTable() ) );
          }
          break;
      }

      return rtfElements.toArray( new RtfBasicElement[rtfElements.size()] );
    }
  }

  private RtfMapper mapper;

  public PatchRtfDocument() {
    this.mapper = new PatchRtfMapper( this );
  }

  public RtfMapper getMapper() {
    return mapper;
  }
}
