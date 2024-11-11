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

package org.pentaho.reporting.designer.actions.elements;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.ProtectedMethodHelper;
import org.pentaho.reporting.designer.core.actions.elements.MorphAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;

import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class MorphActionTest {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ReportDesignerBoot.getInstance().start();
  }

  @Test
  public void testActionPerformed() {
    TextFieldType tf = Mockito.spy( new TextFieldType() );
    ElementMetaData md = Mockito.mock( ElementMetaData.class );
    Mockito.doReturn( md ).when( tf ).getMetaData();
    Mockito.doReturn( null ).when( md ).getIcon( Locale.getDefault(), BeanInfo.ICON_COLOR_16x16 );
    Mockito.doReturn( "name1" ).when( md ).getName();
    Mockito.doReturn( ElementMetaData.TypeClassification.SUBREPORT ).when( md ).getReportElementType();
    Mockito.doReturn( MaturityLevel.Development ).when( md ).getFeatureMaturityLevel();
    MorphAction action = new MorphAction( tf );
    final MasterReport report = Mockito.mock( MasterReport.class );
    final DocumentBundle documentBundle = Mockito.mock( DocumentBundle.class );
    final DocumentMetaData documentMetaData = Mockito.mock( DocumentMetaData.class );
    Mockito.doReturn( documentMetaData ).when( documentBundle ).getMetaData();
    Mockito.doReturn( documentBundle ).when( report ).getBundle();
    Mockito.doReturn( tf ).when( report ).getElementType();
    ReportRenderContext new1 = Mockito.spy( new ReportRenderContext( report ) );
    DocumentContextSelectionModel selModel = Mockito.mock( DocumentContextSelectionModel.class );

    Mockito.doReturn( selModel ).when( new1 ).getSelectionModel();
    List<Element> visualElements = new ArrayList<Element>();
    Element elem = new Element();
    ElementMetaData emd = Mockito.mock( ElementMetaData.class );
    ElementType et = Mockito.mock( ElementType.class );
    Mockito.doReturn( new AttributeMetaData[ 0 ] ).when( emd ).getAttributeDescriptions();
    Mockito.doReturn( emd ).when( et ).getMetaData();
    Mockito.doReturn( ElementMetaData.TypeClassification.DATA ).when( emd ).getReportElementType();
    visualElements.add( elem );
    Mockito.doReturn( visualElements ).when( selModel ).getSelectedElementsOfType( Element.class );
    ProtectedMethodHelper.callUpdate( action, null, new1 );
    ReportDesignerContext reportDesignerContext = Mockito.mock( ReportDesignerContext.class );
    ReportDocumentContext reportDocumentContext = Mockito.mock( ReportDocumentContext.class );
    UndoManager undoManager = Mockito.mock( UndoManager.class );
    Mockito.doReturn( undoManager ).when( reportDocumentContext ).getUndo();
    Mockito.doReturn( new1 ).when( reportDesignerContext ).getActiveContext();
    action.setReportDesignerContext( reportDesignerContext );
    action.actionPerformed( new ActionEvent( this, 0, "" ) );
    List<Element> visualElements2 = ProtectedMethodHelper.getListElements( action );
    Element element = visualElements2.get( 0 );
    Assert.assertTrue( visualElements2.size() > 0 );
    Assert.assertFalse( element.getElementType().equals( tf ) );
  }
}
