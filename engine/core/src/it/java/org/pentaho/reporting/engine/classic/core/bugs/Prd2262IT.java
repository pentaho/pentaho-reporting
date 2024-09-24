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
import org.junit.Ignore;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.process.ParagraphLineBreakStep;
import org.pentaho.reporting.engine.classic.core.layout.richtext.HtmlRichTextConverter;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class Prd2262IT extends TestCase {
  private static final String htmlText = "<html><body><p>one<br />\n" + "    two<br /><br />\n"
      + "    three<br /></p><p>Paragraph2</p></body></html>";
  private static final String rtfText =
      "{\\rtf1\\ansi\\deff0\\adeflang1025\n"
          + "{\\fonttbl{\\f0\\froman\\fprq2\\fcharset0 Times New Roman;}{\\f1\\froman\\fprq2\\fcharset0 Times New Roman;"
          + "}{\\f2\\fswiss\\fprq2\\fcharset0 Arial;}{\\f3\\fnil\\fprq2\\fcharset0 Arial Unicode MS;"
          + "}{\\f4\\fnil\\fprq2\\fcharset0 Tahoma;}{\\f5\\fnil\\fprq0\\fcharset0 Tahoma;}}\n"
          + "{\\colortbl;\\red0\\green0\\blue0;\\red128\\green128\\blue128;}\n"
          + "{\\stylesheet{\\s1\\cf0{\\*\\hyphen2\\hyphlead2\\hyphtrail2\\hyphmax0}\\rtlch\\af4\\afs24\\lang255\\ltrch\\dbch"
          + "\\af3\\langfe255\\hich\\f0\\fs24\\lang1031\\loch\\f0\\fs24\\lang1031\\snext1 Normal;}\n"
          + "{\\s2\\sb240\\sa120\\keepn\\cf0{\\*\\hyphen2\\hyphlead2\\hyphtrail2\\hyphmax0}\\rtlch\\afs28\\lang255\\ltrch"
          + "\\dbch\\langfe255\\hich\\f2\\fs28\\lang1031\\loch\\f2\\fs28\\lang1031\\sbasedon1\\snext3 Heading;}\n"
          + "{\\s3\\sa120\\cf0{\\*\\hyphen2\\hyphlead2\\hyphtrail2\\hyphmax0}\\rtlch\\af4\\afs24\\lang255\\ltrch\\dbch\\af3"
          + "\\langfe255\\hich\\f0\\fs24\\lang1031\\loch\\f0\\fs24\\lang1031\\sbasedon1\\snext3 Body Text;}\n"
          + "{\\s4\\sa120\\cf0{\\*\\hyphen2\\hyphlead2\\hyphtrail2\\hyphmax0}\\rtlch\\af5\\afs24\\lang255\\ltrch\\dbch\\af3"
          + "\\langfe255\\hich\\f0\\fs24\\lang1031\\loch\\f0\\fs24\\lang1031\\sbasedon3\\snext4 List;}\n"
          + "{\\s5\\sb120\\sa120\\cf0{\\*\\hyphen2\\hyphlead2\\hyphtrail2\\hyphmax0}\\rtlch\\af5\\afs24\\lang255\\ai\\ltrch"
          + "\\dbch\\af3\\langfe255\\hich\\f0\\fs24\\lang1031\\i\\loch\\f0\\fs24\\lang1031\\i\\sbasedon1\\snext5 caption;}\n"
          + "{\\s6\\cf0{\\*\\hyphen2\\hyphlead2\\hyphtrail2\\hyphmax0}\\rtlch\\af5\\afs24\\lang255\\ltrch\\dbch\\af3"
          + "\\langfe255\\hich\\f0\\fs24\\lang1031\\loch\\f0\\fs24\\lang1031\\sbasedon1\\snext6 Index;}\n"
          + "}\n"
          + "{\\info{\\creatim\\yr2009\\mo11\\dy24\\hr17\\min0}{\\revtim\\yr0\\mo0\\dy0\\hr0\\min0}{\\printim\\yr0\\mo0\\dy0"
          + "\\hr0\\min0}{\\comment StarWriter}{\\vern3000}}\\deftab709\n"
          + "{\\*\\pgdsctbl\n"
          + "{\\pgdsc0\\pgdscuse195\\pgwsxn11905\\pghsxn16837\\marglsxn1134\\margrsxn1134\\margtsxn1134\\margbsxn1134"
          + "\\pgdscnxt0 Standard;}}\n"
          + "\\paperh16837\\paperw11905\\margl1134\\margr1134\\margt1134\\margb1134\\sectd\\sbknone\\pgwsxn11905\\pghsxn16837"
          + "\\marglsxn1134\\margrsxn1134\\margtsxn1134\\margbsxn1134\\ftnbj\\ftnstart1\\ftnrstcont\\ftnnar\\aenddoc"
          + "\\aftnrstcont\\aftnstart1\\aftnnrlc\n"
          + "\\pard\\plain \\ltrpar\\s1\\cf0{\\*\\hyphen2\\hyphlead2\\hyphtrail2\\hyphmax0}\\rtlch\\af4\\afs24\\lang255"
          + "\\ltrch\\dbch\\af3\\langfe255\\hich\\f0\\fs24\\lang1031\\loch\\f0\\fs24\\lang1031 {\\rtlch "
          + "\\ltrch\\loch\\f0\\fs24\\lang1031\\i0\\b0 Paragraph1}\n"
          + "\\par \\pard\\plain \\ltrpar\\s1\\cf0{\\*\\hyphen2\\hyphlead2\\hyphtrail2\\hyphmax0}\\rtlch\\af4\\afs24\\lang255"
          + "\\ltrch\\dbch\\af3\\langfe255\\hich\\f0\\fs24\\lang1031\\loch\\f0\\fs24\\lang1031 {\\rtlch "
          + "\\ltrch\\loch\\f0\\fs24\\lang1031\\i0\\b0 Paragraph2}\n"
          + "\\par \\pard\\plain \\ltrpar\\s1\\cf0{\\*\\hyphen2\\hyphlead2\\hyphtrail2\\hyphmax0}\\rtlch\\af4\\afs24\\lang255"
          + "\\ltrch\\dbch\\af3\\langfe255\\hich\\f0\\fs24\\lang1031\\loch\\f0\\fs24\\lang1031 \n" + "\\par }";

  public Prd2262IT() {
  }

  public Prd2262IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

//  @Ignore
//  public void testHtmlParsing() throws ReportProcessingException, ContentProcessingException {
//    final Element e = new Element();
//    e.setElementType( LabelType.INSTANCE );
//    e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, htmlText );
//
//    final HtmlRichTextConverter htmlRichTextConverter = new HtmlRichTextConverter();
//    final Object o = htmlRichTextConverter.convert( e, htmlText );
//    if ( o instanceof Band == false ) {
//      fail();
//      return;
//    }
//    final Band containerBand = (Band) o;
//    assertEquals( "Container Band is block: ", "block", containerBand.getStyle()
//        .getStyleProperty( BandStyleKeys.LAYOUT ) );
//    assertEquals( 1, containerBand.getElementCount() );
//
//    final Band htmlBand = (Band) containerBand.getElement( 0 );
//    assertEquals( "HTML Band is block: ", "block", htmlBand.getStyle().getStyleProperty( BandStyleKeys.LAYOUT ) );
//    assertEquals( 1, htmlBand.getElementCount() );
//
//    final Band bodyBand = (Band) htmlBand.getElement( 0 );
//    assertEquals( "BODY Band is block: ", "block", bodyBand.getStyle().getStyleProperty( BandStyleKeys.LAYOUT ) );
//    assertEquals( 2, bodyBand.getElementCount() );
//
//    final Band p1Band = (Band) bodyBand.getElement( 0 );
//    assertEquals( "P[0] Band is block: ", "block", p1Band.getStyle().getStyleProperty( BandStyleKeys.LAYOUT ) );
//    assertEquals( 1, p1Band.getElementCount() );
//    translateToRenderableElements( p1Band );
//
//    final Band p2Band = (Band) bodyBand.getElement( 0 );
//    assertEquals( "P[1] Band is block: ", "block", p2Band.getStyle().getStyleProperty( BandStyleKeys.LAYOUT ) );
//    assertEquals( 1, p2Band.getElementCount() );
//  }

  private void translateToRenderableElements( final Band b ) throws ReportProcessingException,
    ContentProcessingException {
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( new MasterReport(), b );

    final ParagraphLineBreakStep step = new ParagraphLineBreakStep();
    step.compute( logicalPageBox );
    System.out.println();
  }
}
