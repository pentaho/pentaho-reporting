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


package org.pentaho.reporting.engine.classic.extensions.datasources.xpath;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LegacyXPathTableModelTest {

    @Test
    public void testCalculateDocumentBuilderFactory() throws ReportDataFactoryException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, ParserConfigurationException {

        Method method =  LegacyXPathTableModel.class.getDeclaredMethod( "calculateDocumentBuilderFactory", Configuration.class );
        method.setAccessible( true );
        LegacyXPathTableModel legacyXPathTableModel = Mockito.mock( LegacyXPathTableModel.class );

        //disable DTDs
        ModifiableConfiguration disableDTDsPropertyConfig = ClassicEngineBoot.getInstance().getEditableConfig();
        disableDTDsPropertyConfig.setConfigProperty( LegacyXPathTableModel.XPATH_ENABLE_DTDS, "false" );
        DocumentBuilderFactory documentBuilderFactoryDisableDTDs = ( DocumentBuilderFactory ) method.invoke( legacyXPathTableModel, disableDTDsPropertyConfig );
        Assert.assertTrue( documentBuilderFactoryDisableDTDs.getFeature( LegacyXPathTableModel.DISALLOW_DOCTYPE_DECL ) );

        //disable DTDs missing config
        ModifiableConfiguration missingPropertyConfig = ClassicEngineBoot.getInstance().getEditableConfig();
        DocumentBuilderFactory documentBuilderFactoryMissingProperty = ( DocumentBuilderFactory ) method.invoke( legacyXPathTableModel, missingPropertyConfig );
        Assert.assertTrue( documentBuilderFactoryMissingProperty.getFeature( LegacyXPathTableModel.DISALLOW_DOCTYPE_DECL ) );

        //enable DTDs
        ModifiableConfiguration enableDTDsPropertyConfig = ClassicEngineBoot.getInstance().getEditableConfig();
        enableDTDsPropertyConfig.setConfigProperty( LegacyXPathTableModel.XPATH_ENABLE_DTDS, "true" );
        DocumentBuilderFactory documentBuilderFactoryEnableDTDs = ( DocumentBuilderFactory ) method.invoke( legacyXPathTableModel, enableDTDsPropertyConfig );
        Assert.assertFalse( documentBuilderFactoryEnableDTDs.getFeature( LegacyXPathTableModel.DISALLOW_DOCTYPE_DECL ) );

    }

}