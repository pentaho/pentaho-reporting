/*
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata.propertyeditors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.TimeZone;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.BoxSizing;
import org.pentaho.reporting.engine.classic.core.style.FontSmooth;
import org.pentaho.reporting.engine.classic.core.style.TextDirection;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.engine.classic.core.util.StagingMode;

public class PropertyEditorTest {

  @Test
  public void testWhitespaceCollapsePropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new WhitespaceCollapsePropertyEditor(), new Object[] { WhitespaceCollapse.COLLAPSE,
          WhitespaceCollapse.PRESERVE, WhitespaceCollapse.DISCARD, WhitespaceCollapse.PRESERVE_BREAKS } );
    checker.checkAll();
  }

  @Test
  public void testVerticalTextAlignmentPropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new VerticalTextAlignmentPropertyEditor(), new Object[] {
          VerticalTextAlign.USE_SCRIPT, VerticalTextAlign.BASELINE, VerticalTextAlign.SUB, VerticalTextAlign.SUPER,
          VerticalTextAlign.TOP, VerticalTextAlign.TEXT_TOP, VerticalTextAlign.CENTRAL, VerticalTextAlign.MIDDLE,
          VerticalTextAlign.BOTTOM, VerticalTextAlign.TEXT_BOTTOM } );
    checker.checkAll();
  }

  @Test
  public void testVerticalAlignmentPropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new VerticalAlignmentPropertyEditor(), new Object[] { ElementAlignment.TOP,
          ElementAlignment.MIDDLE, ElementAlignment.BOTTOM } );
    checker.checkAll();
  }

  @Test
  public void testTimeZonePropertyEditor() {
    TimeZonePropertyEditor editor = new TimeZonePropertyEditor();
    PropertyEditorChecker checker = new PropertyEditorChecker( editor, new Object[] { TimeZone.getDefault() } );
    checker.checkSettingValue();

    editor.setValue( null );
    assertThat( editor.getAsText(), is( nullValue() ) );
    assertThat( editor.getJavaInitializationString(), is( equalTo( "null" ) ) );

    editor.setAsText( "GMT" );
    assertThat( editor.getAsText(), is( equalTo( "GMT" ) ) );
    assertThat( editor.getJavaInitializationString(),
        is( equalTo( TimeZone.class.getName() + ".getTimeZone(\"GMT\")" ) ) );

    assertThat( editor.getTags(), is( arrayContainingInAnyOrder( TimeZone.getAvailableIDs() ) ) );

    checker.checkIsPaintable();
    checker.checkGetCustomEditor();
    checker.checkSupportsCustomEditor();
  }

  @Test
  public void testTextWrapPropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new TextWrapPropertyEditor(), new Object[] { TextWrap.NONE, TextWrap.WRAP } );
    checker.checkAll();
  }

  @Test
  public void testTextDirectionPropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new TextDirectionPropertyEditor(), new Object[] { TextDirection.LTR,
          TextDirection.RTL } );
    checker.checkAll();
  }

  @Test
  public void testStagingModePropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new StagingModePropertyEditor(), new Object[] { StagingMode.MEMORY,
          StagingMode.TMPFILE, StagingMode.THRU } );
    checker.checkSettingValue();
    checker.checkSettingValueAsText();
    checker.checkGetTags();
    checker.checkIsPaintable();
    checker.checkGetCustomEditor();
    checker.checkGetJavaInitializationStringWithNull();
    checker.checkSupportsCustomEditor();
  }

  @Test
  public void testRichTextTypePropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new RichTextTypePropertyEditor(), new Object[] { "text/plain", "text/html",
          "text/rtf" } );
    checker.checkSettingValue();
    checker.checkSettingValueAsTextWithoutException();
    checker.checkGetTags();
    checker.checkIsPaintable();
    checker.checkGetCustomEditor();
    checker.checkGetJavaInitializationStringWithNull();
    checker.checkSupportsCustomEditor();
  }

  @Test
  public void testParameterLayoutPropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new ParameterLayoutPropertyEditor(),
            new Object[] { "horizontal", "flow", "vertical" } );
    checker.checkSettingValue();
    checker.checkSettingValueAsTextWithoutException();
    checker.checkGetTags();
    checker.checkIsPaintable();
    checker.checkGetCustomEditor();
    checker.checkGetJavaInitializationStringWithNull();
    checker.checkSupportsCustomEditor();
  }

  @Test
  public void testLayoutPropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new LayoutPropertyEditor(), new Object[] { BandStyleKeys.LAYOUT_CANVAS,
          BandStyleKeys.LAYOUT_BLOCK, BandStyleKeys.LAYOUT_INLINE, BandStyleKeys.LAYOUT_ROW, BandStyleKeys.LAYOUT_AUTO,
          BandStyleKeys.LAYOUT_TABLE, BandStyleKeys.LAYOUT_TABLE_BODY, BandStyleKeys.LAYOUT_TABLE_HEADER,
          BandStyleKeys.LAYOUT_TABLE_FOOTER, BandStyleKeys.LAYOUT_TABLE_ROW, BandStyleKeys.LAYOUT_TABLE_CELL,
          BandStyleKeys.LAYOUT_TABLE_COL, BandStyleKeys.LAYOUT_TABLE_COL_GROUP } );
    checker.checkSettingValue();
    checker.checkSettingValueAsTextWithoutException();
    checker.checkGetTags();
    checker.checkIsPaintable();
    checker.checkGetCustomEditor();
    checker.checkGetJavaInitializationStringWithNull();
    checker.checkSupportsCustomEditor();
  }

  @Test
  public void testHorizontalAlignmentPropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new HorizontalAlignmentPropertyEditor(), new Object[] { ElementAlignment.LEFT,
          ElementAlignment.CENTER, ElementAlignment.RIGHT, ElementAlignment.JUSTIFY } );
    checker.checkAll();
  }

  @Test
  public void testFontSmoothPropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new FontSmoothPropertyEditor(), new Object[] { FontSmooth.AUTO, FontSmooth.ALWAYS,
          FontSmooth.NEVER } );
    checker.checkAll();
  }

  @Test
  public void testBoxSizingPropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new BoxSizingPropertyEditor(), new Object[] { BoxSizing.BORDER_BOX,
          BoxSizing.CONTENT_BOX } );
    checker.checkAll();
  }

  @Test
  public void testBorderStylePropertyEditor() {
    PropertyEditorChecker checker =
        new PropertyEditorChecker( new BorderStylePropertyEditor(), new Object[] { BorderStyle.SOLID,
          BorderStyle.DASHED, BorderStyle.DOT_DASH, BorderStyle.DOT_DOT_DASH, BorderStyle.DOTTED, BorderStyle.DOUBLE,
          BorderStyle.HIDDEN, BorderStyle.NONE, BorderStyle.GROOVE, BorderStyle.RIDGE, BorderStyle.INSET,
          BorderStyle.OUTSET } );
    checker.checkSettingValue();
    checker.checkSettingValueAsText();
    checker.checkGetTagsWithNull();
    checker.checkIsPaintable();
    checker.checkGetCustomEditor();
    checker.checkGetJavaInitializationString();
    checker.checkSupportsCustomEditor();
  }
}
