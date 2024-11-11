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


package org.pentaho.reporting.engine.classic.core.metadata.propertyeditors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskRegistry;

public class ReportProcessTaskPropertyEditorTest {

  private ReportProcessTaskPropertyEditor editor;
  private static ReportProcessTaskMetaData exportTask;
  private static String[] correctValues;

  @BeforeClass
  public static void init() {
    exportTask = mock( ReportProcessTaskMetaData.class );
    doReturn( "test_export_type" ).when( exportTask ).getName();
    ReportProcessTaskRegistry.getInstance().registerExportType( exportTask );

    correctValues = ReportProcessTaskRegistry.getInstance().getExportTypes();
  }

  @Before
  public void setUp() {
    editor = new ReportProcessTaskPropertyEditor();
  }

  @Test
  public void testSettingValue() {
    editor.setValue( null );
    assertThat( editor.getValue(), is( nullValue() ) );

    editor.setValue( exportTask.getName() );
    assertThat( (String) editor.getValue(), is( equalTo( exportTask.getName() ) ) );

    editor.setValue( "incorrect" );
    assertThat( editor.getValue(), is( nullValue() ) );
  }

  @Test
  public void testSettingValueAsText() {
    editor.setAsText( null );
    assertThat( editor.getAsText(), is( nullValue() ) );

    editor.setAsText( exportTask.getName() );
    assertThat( editor.getAsText(), is( equalTo( exportTask.getName() ) ) );
  }

  @Test
  public void testGetTags() {
    assertThat( editor.getTags(), is( arrayContainingInAnyOrder( correctValues ) ) );
  }

  @Test
  public void testIsPaintable() {
    assertThat( editor.isPaintable(), is( equalTo( false ) ) );
  }

  @Test
  public void testGetJavaInitializationStringWithNull() {
    assertThat( editor.getJavaInitializationString(), is( nullValue() ) );
  }

  @Test
  public void testGetCustomEditor() {
    assertThat( editor.getCustomEditor(), is( nullValue() ) );
  }

  @Test
  public void testSupportsCustomEditor() {
    assertThat( editor.supportsCustomEditor(), is( equalTo( false ) ) );
  }
}
