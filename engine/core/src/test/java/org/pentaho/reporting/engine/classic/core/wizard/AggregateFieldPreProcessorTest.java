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

package org.pentaho.reporting.engine.classic.core.wizard;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.function.AggregationFunction;
import org.pentaho.reporting.engine.classic.core.function.ItemSumFunction;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

public class AggregateFieldPreProcessorTest {

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void configureRelationalAggreation() {
    DefaultFlowController fc = Mockito.mock( DefaultFlowController.class );
    DataSchema schema = Mockito.mock( DataSchema.class );
    SubReport section = new SubReport();
    AggregateFieldPreProcessor afpp = Mockito.spy( AggregateFieldPreProcessor.class );
    Element element = new Element();
    RelationalGroup group = Mockito.spy( new RelationalGroup() );
    group.setName( "a" );
    GroupDataBody groupBody = new GroupDataBody();
    ItemBand ib = new ItemBand();
    Mockito.doReturn( schema ).when( fc ).getDataSchema();
    Mockito.doReturn( new String[] { "a" } ).when( schema ).getNames();

    AggregationFunction o = Mockito.mock( AggregationFunction.class );
    Object o1 = ItemSumFunction.class;
    try {
      element.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_TYPE, o1 );
      ib.addElement( element );
      groupBody.setElementAt(1, ib );
      group.setElementAt(1, groupBody );
      section.setElementAt( 2, group );
      SubReport result = afpp.performPreProcessing( section, fc );
      Mockito.verify( group, Mockito.times( 1 ) ).getGeneratedName();
      String name = ( (ItemSumFunction) result.getExpressions().getExpression( 0 ) ).getGroup();
      Assert.assertEquals( name, group.getGeneratedName() );
    } catch ( Exception e ) {
      e.printStackTrace();
      Assert.fail();
    }
  }
}
