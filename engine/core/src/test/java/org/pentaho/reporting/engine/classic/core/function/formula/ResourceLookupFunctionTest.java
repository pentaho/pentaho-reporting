/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.function.formula;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.DefaultTypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Created by dima.prokopenko@gmail.com on 10/3/2016.
 */
public class ResourceLookupFunctionTest {

  private ReportFormulaContext context = mock( ReportFormulaContext.class );
  private ParameterCallback parameters = mock( ParameterCallback.class );
  private ProcessingContext pcontext = mock( ProcessingContext.class );
  private ResourceBundleFactory rbfactory = mock( ResourceBundleFactory.class );

  private static final String resourceId = "bundleId";

  @Before
  public void before() throws IOException, EvaluationException {
    rbfactory = mock( ResourceBundleFactory.class );
    InputStream stream = new ByteArrayInputStream( ( "key=TRANSLATED" ).getBytes( StandardCharsets.UTF_8 ) );
    ResourceBundle rb = new PropertyResourceBundle( stream );
    when( rbfactory.getResourceBundle( eq( resourceId ) ) ).thenReturn( rb );

    when( parameters.getParameterCount() ).thenReturn( 2 );
    when( parameters.getType( eq( 0 ) ) ).thenReturn( TextType.TYPE );
    when( parameters.getValue( eq( 0 ) ) ).thenReturn( resourceId );

    when( parameters.getType( eq( 1 ) ) ).thenReturn( TextType.TYPE );
    when( parameters.getValue( eq( 1 ) ) ).thenReturn( "key" );

    when( context.getTypeRegistry() ).thenReturn( new DefaultTypeRegistry() );

    when( context.getProcessingContext() ).thenReturn( pcontext );
    when( pcontext.getResourceBundleFactory() ).thenReturn( rbfactory );
  }


  @Test
  public void testResourceLookup() throws EvaluationException {
    ResourceLookupFunction function = new ResourceLookupFunction();
    TypeValuePair result = function.evaluate( context, parameters );
    String actual = String.valueOf( result.getValue() );
    assertEquals( "TRANSLATED", actual );
  }

  @Test( expected = EvaluationException.class )
  public void testIncorrectParameterCount() throws EvaluationException {
    when( parameters.getParameterCount() ).thenReturn( 1 );
    ResourceLookupFunction function = new ResourceLookupFunction();
    function.evaluate( context, parameters );
  }

  @Test( expected = EvaluationException.class )
  public void testNullKey() throws EvaluationException {
    when( parameters.getValue( eq( 0 ) ) ).thenReturn( null );
    ResourceLookupFunction function = new ResourceLookupFunction();
    function.evaluate( context, parameters );
  }

  @Test( expected = EvaluationException.class )
  public void testNullValue() throws EvaluationException {
    when( parameters.getValue( eq( 1 ) ) ).thenReturn( null );
    ResourceLookupFunction function = new ResourceLookupFunction();
    function.evaluate( context, parameters );
  }

  @Test
  public void testNoResourceFound() throws EvaluationException {
    when( parameters.getValue( eq( 0 ) ) ).thenReturn( "not found" );
    ResourceLookupFunction function = new ResourceLookupFunction();
    TypeValuePair result = function.evaluate( context, parameters );
    String actual = String.valueOf( result.getValue() );
    assertEquals( "key", actual );
  }

}
