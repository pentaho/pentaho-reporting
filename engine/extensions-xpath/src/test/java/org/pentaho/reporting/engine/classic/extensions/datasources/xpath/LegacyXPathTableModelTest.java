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