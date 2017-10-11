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
 * Copyright (c) 2017 Hitachi Vantara..  All rights reserved.
 */
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
