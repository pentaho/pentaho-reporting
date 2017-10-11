/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements;

import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.filter.templates.Template;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ArrayClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.URLClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DataSourceCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DefaultDataSourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements.DefaultElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.BandLayoutClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.DefaultClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.DefaultStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.PageableLayoutStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.DefaultTemplateCollection;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.TemplateCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.TemplateDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataSourceWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.TemplateWriter;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class LegacyElementWriteHandler extends AbstractElementWriteHandler {
  public LegacyElementWriteHandler() {
  }

  /**
   * Writes a single element as XML structure.
   *
   * @param bundle
   *          the bundle to which to write to.
   * @param state
   *          the current write-state.
   * @param xmlWriter
   *          the xml writer.
   * @param element
   *          the element.
   * @throws IOException
   *           if an IO error occured.
   * @throws BundleWriterException
   *           if an Bundle writer.
   */
  @SuppressWarnings( "deprecation" )
  public void writeElement( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final XmlWriter xmlWriter, final Element element ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( element == null ) {
      throw new NullPointerException();
    }

    final AttributeList attList = createMainAttributes( element, xmlWriter );
    attList.addNamespaceDeclaration( "ext", ExtParserModule.NAMESPACE );

    xmlWriter.writeTag( BundleNamespaces.LAYOUT, "legacy", attList, XmlWriterSupport.OPEN );
    writeElementBody( bundle, state, element, xmlWriter );

    final ReportWriter writer = new ReportWriter( state.getMasterReport(), "UTF-8" );
    writer.addClassFactoryFactory( new URLClassFactory() );
    writer.addClassFactoryFactory( new DefaultClassFactory() );
    writer.addClassFactoryFactory( new BandLayoutClassFactory() );
    writer.addClassFactoryFactory( new ArrayClassFactory() );

    writer.addStyleKeyFactory( new DefaultStyleKeyFactory() );
    writer.addStyleKeyFactory( new PageableLayoutStyleKeyFactory() );
    writer.addTemplateCollection( new DefaultTemplateCollection() );
    writer.addElementFactory( new DefaultElementFactory() );
    writer.addDataSourceFactory( new DefaultDataSourceFactory() );
    final DataSource datasource = element.getDataSource();

    if ( datasource instanceof Template ) {
      writeLegacyTemplate( xmlWriter, writer, (Template) datasource );
    } else {
      writeLegacyDataSource( xmlWriter, writer, datasource );
    }

    xmlWriter.writeCloseTag();

  }

  private void writeLegacyTemplate( final XmlWriter xmlWriter, final ReportWriterContext writerContext,
      final Template template ) throws BundleWriterException, IOException {

    final TemplateCollector tc = writerContext.getTemplateCollector();

    // the template description of the element template will get the
    // template name as its name.
    final TemplateDescription templateDescription = tc.getDescription( template );

    if ( templateDescription == null ) {
      throw new BundleWriterException( "Unknown template type: " + template );
    }

    // create the parent description before the template description is filled.
    final TemplateDescription parentTemplate = (TemplateDescription) templateDescription.getInstance();

    try {
      templateDescription.setParameterFromObject( template );

      final TemplateWriter templateWriter =
          new TemplateWriter( writerContext, xmlWriter, templateDescription, parentTemplate );
      templateWriter.write();
    } catch ( ObjectFactoryException ofe ) {
      throw new BundleWriterException( "Error while preparing the template", ofe );
    } catch ( ReportWriterException e ) {
      throw new BundleWriterException( "Failed to write legacy template " + template, e );
    }
  }

  private void writeLegacyDataSource( final XmlWriter xmlWriter, final ReportWriterContext writerContext,
      final DataSource datasource ) throws BundleWriterException, IOException {
    final ClassFactoryCollector classFactoryCollector = writerContext.getClassFactoryCollector();
    ObjectDescription od = classFactoryCollector.getDescriptionForClass( datasource.getClass() );
    if ( od == null ) {
      od = classFactoryCollector.getSuperClassObjectDescription( datasource.getClass(), null );
    }

    if ( od == null ) {
      throw new BundleWriterException( "Unable to resolve DataSource: " + datasource.getClass() );
    }

    final DataSourceCollector dataSourceCollector = writerContext.getDataSourceCollector();
    final String dsname = dataSourceCollector.getDataSourceName( od );
    if ( dsname == null ) {
      throw new BundleWriterException( "No name for DataSource " + datasource );
    }

    xmlWriter.writeTag( ExtParserModule.NAMESPACE, "datasource", "type", dsname, XmlWriterSupport.OPEN );

    try {
      final DataSourceWriter dsWriter = new DataSourceWriter( writerContext, datasource, od, xmlWriter );
      dsWriter.write();
    } catch ( ReportWriterException e ) {
      throw new BundleWriterException( "Failed to write legacy DataSource " + datasource, e );
    }

    xmlWriter.writeCloseTag();
  }
}
