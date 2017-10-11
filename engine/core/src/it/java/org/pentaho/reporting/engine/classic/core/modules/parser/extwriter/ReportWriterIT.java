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

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.DataRowDataSource;
import org.pentaho.reporting.engine.classic.core.filter.StaticDataSource;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ArrayClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.URLClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DefaultDataSourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements.DefaultElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.BandLayoutClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.DefaultClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.DefaultStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.PageableLayoutStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.DefaultTemplateCollection;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.awt.geom.Line2D;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class ReportWriterIT extends TestCase {
  public ReportWriterIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private ReportWriter createWriter() {
    final MasterReport report = new MasterReport();
    final ModifiableConfiguration repConf = new HierarchicalConfiguration( report.getReportConfiguration() );
    repConf.setConfigProperty( AbstractXmlResourceFactory.CONTENTBASE_KEY, "file://tmp/" );

    final ReportWriter writer = new ReportWriter( report, "UTF-16", repConf );
    writer.addClassFactoryFactory( new URLClassFactory() );
    writer.addClassFactoryFactory( new DefaultClassFactory() );
    writer.addClassFactoryFactory( new BandLayoutClassFactory() );
    writer.addClassFactoryFactory( new ArrayClassFactory() );

    writer.addStyleKeyFactory( new DefaultStyleKeyFactory() );
    writer.addStyleKeyFactory( new PageableLayoutStyleKeyFactory() );
    writer.addTemplateCollection( new DefaultTemplateCollection() );
    writer.addElementFactory( new DefaultElementFactory() );
    writer.addDataSourceFactory( new DefaultDataSourceFactory() );
    return writer;
  }

  public void testFactories() {
    final ReportWriter writer = createWriter();
    final ClassFactory cc = writer.getClassFactoryCollector();
    assertNotNull( cc.getDescriptionForClass( DataRowDataSource.class ) );
    assertEquals( cc.getDescriptionForClass( DataRowDataSource.class ).getObjectClass(), DataRowDataSource.class );
    System.out.println( cc.getDescriptionForClass( DataRowDataSource.class ) );
  }

  public void testDataSourceWriter() throws Exception {
    final ReportWriter writer = createWriter();
    final StaticDataSource ds = new StaticDataSource( new Line2D.Float() );
    final ClassFactory cc = writer.getClassFactoryCollector();
    final Writer w = new OutputStreamWriter( new NullOutputStream(), "UTF-16" );
    final XmlWriter xmlWriter = new XmlWriter( w );

    final AttributeList attList = new AttributeList();
    attList.addNamespaceDeclaration( "", ExtParserModule.NAMESPACE );
    xmlWriter.writeTag( ExtParserModule.NAMESPACE, "testcase", attList, XmlWriter.OPEN );
    final DataSourceWriter dsW =
        new DataSourceWriter( writer, ds, cc.getDescriptionForClass( ds.getClass() ), xmlWriter );
    dsW.write();
    xmlWriter.writeCloseTag();
    w.flush();
  }
}
