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


package org.pentaho.reporting.engine.classic.core.dom;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;

public class ReportStructureMatcherTest {

  @Test
  public void testMatch() throws IOException {
    MatcherContext context = mock( MatcherContext.class );
    Section base = mock( Section.class );
    Element sectionElem = mock( Element.class );

    doReturn( 1 ).when( base ).getElementCount();
    doReturn( sectionElem ).when( base ).getElement( 0 );

    ReportElement elem = ReportStructureMatcher.match( context, base, "*" );
    assertThat( elem, is( equalTo( (ReportElement) sectionElem ) ) );
    verify( context ).setSingleSelectionHint( true );
  }

  @Test
  public void testMatchAll() throws IOException {
    MatcherContext context = mock( MatcherContext.class );
    ReportElement base = mock( ReportElement.class );

    ReportElement[] elems = ReportStructureMatcher.matchAll( context, base, "*" );
    assertThat( elems.length, is( equalTo( 1 ) ) );
    assertThat( elems[0], is( equalTo( base ) ) );
  }

  @Test
  public void testParse() throws IOException {
    NodeMatcher matcher = ReportStructureMatcher.parse( "*" );
    assertThat( matcher, is( instanceOf( AnyNodeMatcher.class ) ) );

    matcher = ReportStructureMatcher.parse( "*>aaa.bbb #ccc *" );
    assertThat(
        matcher.toString(),
        is( equalTo( "AndMatcher(ElementMatcher(*);Descendant(AndMatcher(ElementMatcher(aaa);ChildMatcher(ElementMatcher(*)))))" ) ) );
  }

  @Test
  public void testFindElementByType() {
    Element element = mock( Element.class );
    ElementType type = mock( ElementType.class );
    ElementMetaData meta = mock( ElementMetaData.class );

    doReturn( meta ).when( type ).getMetaData();
    doReturn( "test_name" ).when( meta ).getName();
    doReturn( type ).when( element ).getElementType();

    ReportElement elem = ReportStructureMatcher.findElementByType( element, type );
    assertThat( elem, is( equalTo( (ReportElement) element ) ) );
  }

  @Test
  public void testFindElementsByType() {
    Element element = mock( Element.class );
    ElementType type = mock( ElementType.class );
    ElementMetaData meta = mock( ElementMetaData.class );

    doReturn( meta ).when( type ).getMetaData();
    doReturn( "test_name" ).when( meta ).getName();
    doReturn( type ).when( element ).getElementType();

    ReportElement[] elems = ReportStructureMatcher.findElementsByType( element, type );
    assertThat( elems.length, is( equalTo( 1 ) ) );
    assertThat( elems[0], is( equalTo( (ReportElement) element ) ) );
  }

  @Test
  public void testFindElementsByAttribute() {
    ReportElement element = mock( ReportElement.class );
    ReportAttributeMap<String> map = mock( ReportAttributeMap.class );

    doReturn( map ).when( element ).getAttributes();
    doReturn( "attr" ).when( map ).getAttribute( "test_namespace", "test_name" );

    ReportElement[] elems = ReportStructureMatcher.findElementsByAttribute( element, "test_namespace", "test_name" );

    assertThat( elems.length, is( equalTo( 1 ) ) );
    assertThat( elems[0], is( equalTo( (ReportElement) element ) ) );
  }

  @Test
  public void testFindElementsByName() {
    ReportElement element = mock( ReportElement.class );
    ReportAttributeMap<String> map = mock( ReportAttributeMap.class );

    doReturn( map ).when( element ).getAttributes();
    doReturn( "attr" ).when( map ).getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME );

    ReportElement[] elems = ReportStructureMatcher.findElementsByName( element, "attr" );

    assertThat( elems.length, is( equalTo( 1 ) ) );
    assertThat( elems[0], is( equalTo( (ReportElement) element ) ) );
  }
}
