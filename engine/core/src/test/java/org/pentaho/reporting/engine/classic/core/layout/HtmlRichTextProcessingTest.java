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
 * Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.richtext.HtmlRichTextConverter;
import org.pentaho.reporting.engine.classic.core.layout.richtext.html.RichTextHtmlStyleBuilderFactory;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;

import java.awt.Color;


public class HtmlRichTextProcessingTest extends TestCase {
  private static final String LISTTEXT =
          "<p>An ordered list:</p>"
                  +  "<ol>"
                  +  "  <li>Coffee</li>"
                  +  "  <li>"
                  +  "      <ul>"
                  +  "      <li>bean1</li>"
                  +  "      <li>bean2</li>"
                  +  "      </ul>"
                  +  "  </li>"
                  +  "  <li>Order List"
                  +  "    <ol>"
                  +  "          <li>Jokey</li>"
                  +  "      <li>"
                  +  "          <ul> "
                  +  "        <li>man</li>"
                  +  "        <li>horse</li>"
                  +  "          </ul>"
                  +  "         </li>"
                  +  "         <li>Milk</li>"
                  +  "  </ol>"
                  +  "  </li>"
                  +  "</ol>"
                  +  "<p>An unordered list:</p>"
                  +  "<ul>"
                  +  "  <li>Coffee</li>"
                  +  "  <li>Tea</li>"
                  +  "  <li>Milk</li>"
                  +  "</ul>";
  private static final String RICHTEXT =
          "<HTML><head><title></title></head><BODY>"
                  + "<form id=\"SMSFormEN\" action=\"https://heavensgate/sms/aformhandle\" method=\"post\">"
                  + "<script language=\"javascript\"> "
                  + "function validateEN()"
                  + "{ "
                  + "var message = 'Yea! Some message here'; "
                  + "var locale = -1; "
                  + "} </script>  "
                  + "<input type=\"hidden\" name=\"secretField\" value=\"__secretField__\"/>  "
                  + "<input type=\"hidden\" name=\"secretField2\" value=\"__secretField__\"/>  "
                  + "<!--__SWITCHPROXY__-->  "
                  + "<font size=\"2\">"
                  + " <p><b>Oh, a header here!</b><br>"
                  + "To register do something enter the details requested below and click &#8217;Submit&#8217;.</p> "
                  + "Is this your first or second try? "
                  + "<input type=\"radio\" name=\"gahhar\" id=\"try1\" value=\"00491\">"
                  + "<label for=\"try1\">1st</label>"
                  + "<input type=\"radio\" name=\"gahhar\" id=\"try2\" value=\"00559\">"
                  + "<label for=\"try2\">2nd</label><br/>"
                  + "Select your language "
                  + "<input type=\"radio\" id=\"en_lang_fr\" name=\"language\" value=\"fr_BE\"/><label for=\"en_lang_fr\">FR</label>"
                  + "<input type=\"radio\" id=\"en_lang_nl\" name=\"language\" value=\"nl_BE\"/><label for=\"en_lang_nl\">NL</label>"
                  + "<input type=\"radio\" id=\"en_lang_en\" name=\"language\" value=\"en_GB\"/><label for=\"en_lang_en\">EN</label>"
                  + "<br/>"
                  + "<label for=\"en_perm\">You agree to sell your soul for this service</label>"
                  + "<input id=\"en_perm\" type=\"checkbox\" name=\"permissionGiven\" value=\"1\"/>"
                  + "<b><br/>Mobile Number:&nbsp;</b>"
                  + "<input id=\"mobile\" name=\"mobile\"/>&nbsp;"
                  + "<INPUT type=\"submit\" id=\"MobileNumber\" name=\"MobileNumber\" value=\"Submit\" onclick=\"return validateEN();"
                  + "\"/>"
                  + "</p>"
                  + "</font>"
                  + "<font size=\"2\">"
                  + "<p>A pharmaceutical care effectiveness study is being carried out on compliance with Gardasil. "
                  + "To participate please click here.  "
                  + "<a href=\"http://localhost/go/die\" target=\"_blank\"> click here.</a>"
                  + "</p>"
                  + "</font>"
                  + "<font size=\"1\">"
                  + "<p>If you prefer to self-register please complete and issue an SMS Compliance Service card with the "
                  + "appropriate reference<p>For the 1st try write <b>I'M_DESPERATE</b><br/>"
                  + "For the 2nd try write <b>CANT_YOU_SEE_IM_DIEING</b><br/></p>"
                  + "<p><b>Privacy Information</b> All Information submitted remains confidential and will only be used to send "
                  + "SMS reminders as part of this program. You can opt out at any time by sending &#8220;IM_DEAD&#8221; to 12345.<br/>"
                  + "<a href=\"http://localhost/go/mad\" target=\"_blank\"> "
                  + "Click here for more information about this service</a></p></font></p>"
                  + "</form><!-- BAH --></BODY></HTML>";

  public HtmlRichTextProcessingTest() {
  }

  public HtmlRichTextProcessingTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testRichTextParsing() {
    HtmlRichTextConverter richTextConverter = new HtmlRichTextConverter();
    final Element element = new Element();
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, RICHTEXT );
    element.setElementType( new LabelType() );
    final Object o = richTextConverter.convert( element, RICHTEXT );
    final Element elementlist = new Element();
    elementlist.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, RICHTEXT );
    elementlist.setElementType( new LabelType() );

    final SimpleStyleResolver styleResolver = new SimpleStyleResolver( true );
    final ResolverStyleSheet resolverTarget = new ResolverStyleSheet();
    styleResolver.resolve( element, resolverTarget );
    elementlist.setComputedStyle( new SimpleStyleSheet( resolverTarget ) );

    final Band o2 = (Band) richTextConverter.convert( elementlist, LISTTEXT );
    ReportElement[] re = o2.getChildElementsByName( "point" );

    assertTrue( re.length == 13 );
    System.out.println( re.length );
    System.out.println( o );
  }

  @Test
  public void testCSSConversionDefault() {
    final Element element = new Element();
    final Element elementlist = new Element();
    elementlist.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, RICHTEXT );
    elementlist.setElementType( new LabelType() );

    final SimpleStyleResolver styleResolver = new SimpleStyleResolver( true );
    final ResolverStyleSheet resolverTarget = new ResolverStyleSheet();
    styleResolver.resolve( element, resolverTarget );

    RichTextHtmlStyleBuilderFactory richTextBuilder = new RichTextHtmlStyleBuilderFactory();
    String codeCss = richTextBuilder.produceTextStyle( null, new SimpleStyleSheet( resolverTarget ) ).toString();

    String expected = "color: black; font-size: 10pt; font-family: serif; font-weight: normal; font-style: normal; "
            + "text-decoration: none; text-align: left; word-spacing: 0pt; letter-spacing: 0pt; white-space: pre";
    assertEquals( expected, codeCss );
  }

  @Test
  public void testCSSConversionChangesGeneral() {
    final Element element = new Element();
    final Element elementlist = new Element();
    elementlist.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, RICHTEXT );
    elementlist.setElementType( new LabelType() );

    final SimpleStyleResolver styleResolver = new SimpleStyleResolver( true );
    final ResolverStyleSheet resolverTarget = new ResolverStyleSheet();

    elementlist.getStyle().setStyleProperty( ElementStyleKeys.PAINT, Color.red );
    elementlist.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.green );
    elementlist.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.RIGHT );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.WORD_SPACING, 2f );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.FONT, "arial" );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, 32 );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.BOLD, true );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.ITALIC, true );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.UNDERLINED, true );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.STRIKETHROUGH, true );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.X_MIN_LETTER_SPACING, 2f );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.X_MAX_LETTER_SPACING, 4f );

    styleResolver.resolve( elementlist, resolverTarget );

    RichTextHtmlStyleBuilderFactory richTextBuilder = new RichTextHtmlStyleBuilderFactory();
    String codeCss = richTextBuilder.produceTextStyle( null, new SimpleStyleSheet( resolverTarget ) ).toString();

    String expected = "color: #ff0000; font-size: 32pt; font-family: \"arial\"; font-weight: bold; font-style: italic;"
            + " text-decoration: underline line-through; text-align: right; word-spacing: 2pt; letter-spacing: 2pt; white-space:"
            + " pre; background-color: lime";
    assertEquals( expected, codeCss );
  }

  @Test
  public void testCSSConversionChangesVar1() {
    final Element element = new Element();
    final Element elementlist = new Element();
    elementlist.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, RICHTEXT );
    elementlist.setElementType( new LabelType() );

    final SimpleStyleResolver styleResolver = new SimpleStyleResolver( true );
    final ResolverStyleSheet resolverTarget = new ResolverStyleSheet();

    elementlist.getStyle().setStyleProperty( ElementStyleKeys.PAINT, Color.red );
    elementlist.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.green );
    elementlist.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.RIGHT );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.WORD_SPACING, 2f );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.FONT, "sans-serif" );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, 42 );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.BOLD, true );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.ITALIC, false );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.UNDERLINED, true );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.STRIKETHROUGH, false );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.X_MIN_LETTER_SPACING, 22f );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.X_MAX_LETTER_SPACING, 41f );

    styleResolver.resolve( elementlist, resolverTarget );

    RichTextHtmlStyleBuilderFactory richTextBuilder = new RichTextHtmlStyleBuilderFactory();
    String codeCss = richTextBuilder.produceTextStyle( null, new SimpleStyleSheet( resolverTarget ) ).toString();

    String expected = "color: #ff0000; font-size: 42pt; font-family: sans-serif; font-weight: bold; font-style: normal; "
            + "text-decoration: underline; text-align: right; word-spacing: 2pt; letter-spacing: 22pt; white-space: pre; background-color: lime";

    assertEquals( expected, codeCss );
  }

  @Test
  public void testCSSConversionChangesVar2() {
    final Element element = new Element();
    final Element elementlist = new Element();
    elementlist.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, RICHTEXT );
    elementlist.setElementType( new LabelType() );

    final SimpleStyleResolver styleResolver = new SimpleStyleResolver( true );
    final ResolverStyleSheet resolverTarget = new ResolverStyleSheet();

    elementlist.getStyle().setStyleProperty( ElementStyleKeys.PAINT, Color.blue );
    elementlist.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.blue );
    elementlist.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.CENTER );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.WORD_SPACING, 4f );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.FONT, "verdana" );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, 42 );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.BOLD, false );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.ITALIC, false );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.UNDERLINED, false );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.STRIKETHROUGH, false );

    elementlist.getStyle().setStyleProperty( TextStyleKeys.X_MIN_LETTER_SPACING, 22f );
    elementlist.getStyle().setStyleProperty( TextStyleKeys.X_MAX_LETTER_SPACING, 41f );

    styleResolver.resolve( elementlist, resolverTarget );

    RichTextHtmlStyleBuilderFactory richTextBuilder = new RichTextHtmlStyleBuilderFactory();
    String codeCss = richTextBuilder.produceTextStyle( null, new SimpleStyleSheet( resolverTarget ) ).toString();

    String expected = "color: blue; font-size: 42pt; font-family: \"verdana\"; font-weight: normal; font-style: normal; "
            + "text-decoration: none; text-align: center; word-spacing: 4pt; letter-spacing: 22pt; white-space: pre; background-color: blue";

    assertEquals( expected, codeCss );
  }

  @Test
  public void testScenarioPRD6160() {
    testConversionPxToPt(
            "<span style=\"font-size:13px\">13px high text here.</span>",
            "13px high text here.",
            10
    );
    testConversionPxToPt(
            "<span style=\"font-size:26px\">this 26px text has 20pt size.</span>",
            "this 26px text has 20pt size.",
            20
    );
  }

  private void testConversionPxToPt(String aHtml, String aExpectedPlainText, double aExpectedFontSize) {
    HtmlRichTextConverter converter = new HtmlRichTextConverter();
    ReportElement source = new Element();
    source.setComputedStyle(new SimpleStyleSheet(BandDefaultStyleSheet.getBandDefaultStyle()));
    Object result = converter.convert(source, aHtml);
    assertTrue(result instanceof Band);
    Band band = (Band) result;
    Element content = (Element) band.getChildElementsByName("content")[0];
    Object elementAttributeValue = content.getAttribute("http://reporting.pentaho.org/namespaces/engine/attributes/core", "value");
    assertEquals(aExpectedPlainText, elementAttributeValue);
    double fontSize = content.getStyle().getDoubleStyleProperty(TextStyleKeys.FONTSIZE,0.0);
    assertEquals(aExpectedFontSize, fontSize);
  }
}
