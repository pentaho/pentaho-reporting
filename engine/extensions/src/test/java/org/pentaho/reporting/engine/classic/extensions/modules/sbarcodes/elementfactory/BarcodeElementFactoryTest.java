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


package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.elementfactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesAttributeNames.NAMESPACE;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesAttributeNames;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesType;

public class BarcodeElementFactoryTest {

  private BarcodeElementFactory barcodeElemFactory;

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Before
  public void setUp() {
    barcodeElemFactory = new BarcodeElementFactory();
  }

  @Test
  public void testApplyStyle() {
    ElementStyleSheet style = mock( ElementStyleSheet.class );
    barcodeElemFactory.applyStyle( style );
    verify( style, never() ).setStyleProperty( any( StyleKey.class ), any() );

    barcodeElemFactory.setFontName( "Times New Roman" );
    barcodeElemFactory.setFontSize( 14 );
    barcodeElemFactory.setBold( true );
    barcodeElemFactory.setItalic( true );

    barcodeElemFactory.applyStyle( style );
    verify( style ).setStyleProperty( TextStyleKeys.FONT, barcodeElemFactory.getFontName() );
    verify( style ).setStyleProperty( TextStyleKeys.FONTSIZE, barcodeElemFactory.getFontSize() );
    verify( style ).setStyleProperty( TextStyleKeys.BOLD, barcodeElemFactory.getBold() );
    verify( style ).setStyleProperty( TextStyleKeys.ITALIC, barcodeElemFactory.getItalic() );
  }

  @Test
  public void testCreateElement() {
    Element elem = barcodeElemFactory.createElement();
    assertThat( elem, is( notNullValue() ) );
    assertThat( elem.getName(), is( equalTo( StringUtils.EMPTY ) ) );
    assertThat( elem.getElementType(), is( instanceOf( SimpleBarcodesType.class ) ) );
    assertThat( elem.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE ), is( nullValue() ) );
    assertThat( elem.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD ), is( nullValue() ) );
    assertThat( elem.getAttribute( NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE ), is( nullValue() ) );
    assertThat( (Boolean) elem.getAttribute( NAMESPACE, SimpleBarcodesAttributeNames.CHECKSUM_ATTRIBUTE ),
        is( equalTo( false ) ) );
    assertThat( elem.getAttribute( NAMESPACE, SimpleBarcodesAttributeNames.BAR_WIDTH_ATTRIBUTE ), is( nullValue() ) );
    assertThat( elem.getAttribute( NAMESPACE, SimpleBarcodesAttributeNames.BAR_HEIGHT_ATTRIBUTE ), is( nullValue() ) );
    assertThat( (Boolean) elem.getAttribute( NAMESPACE, SimpleBarcodesAttributeNames.SHOW_TEXT_ATTRIBUTE ),
        is( equalTo( true ) ) );
    assertThat( elem.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE ),
        is( nullValue() ) );

    barcodeElemFactory.setName( "elem_name" );
    barcodeElemFactory.setContent( "elem_content" );
    barcodeElemFactory.setFieldname( "field_name" );
    barcodeElemFactory.setFormula( "test_formula" );
    barcodeElemFactory.setType( "elem_type" );
    barcodeElemFactory.setChecksum( true );
    barcodeElemFactory.setBarWidth( 200 );
    barcodeElemFactory.setBarHeight( 500 );
    barcodeElemFactory.setShowText( false );

    elem = barcodeElemFactory.createElement();
    assertThat( elem, is( notNullValue() ) );
    assertThat( elem.getName(), is( equalTo( barcodeElemFactory.getName() ) ) );
    assertThat( elem.getElementType(), is( instanceOf( SimpleBarcodesType.class ) ) );
    assertThat( elem.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE ),
        is( equalTo( barcodeElemFactory.getContent() ) ) );
    assertThat( (String) elem.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD ),
        is( equalTo( barcodeElemFactory.getFieldname() ) ) );
    assertThat( (String) elem.getAttribute( NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE ),
        is( equalTo( barcodeElemFactory.getType() ) ) );
    assertThat( (Boolean) elem.getAttribute( NAMESPACE, SimpleBarcodesAttributeNames.CHECKSUM_ATTRIBUTE ),
        is( equalTo( true ) ) );
    assertThat( (Integer) elem.getAttribute( NAMESPACE, SimpleBarcodesAttributeNames.BAR_WIDTH_ATTRIBUTE ),
        is( equalTo( barcodeElemFactory.getBarWidth() ) ) );
    assertThat( (Integer) elem.getAttribute( NAMESPACE, SimpleBarcodesAttributeNames.BAR_HEIGHT_ATTRIBUTE ),
        is( equalTo( barcodeElemFactory.getBarHeight() ) ) );
    assertThat( (Boolean) elem.getAttribute( NAMESPACE, SimpleBarcodesAttributeNames.SHOW_TEXT_ATTRIBUTE ),
        is( equalTo( false ) ) );
    Object expressionObj = elem.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
    assertThat( expressionObj, is( notNullValue() ) );
    assertThat( expressionObj, is( instanceOf( FormulaExpression.class ) ) );
    assertThat( ( (FormulaExpression) expressionObj ).getFormula(), is( equalTo( barcodeElemFactory.getFormula() ) ) );
  }
}
