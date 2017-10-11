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

package org.pentaho.reporting.engine.classic.core;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeChange;
import org.pentaho.reporting.engine.classic.core.designtime.StyleChange;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.MasterReportType;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.LegacyBundleResourceRegistry;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ModifiableReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;
import org.pentaho.reporting.engine.classic.core.util.LibLoaderResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * A JFreeReport instance is used as report template to define the visual layout of a report and to collect all data
 * sources for the reporting. Possible data sources are the {@link javax.swing.table.TableModel}, {@link Expression}s or
 * {@link ReportParameterValues}. The report is made up of 'bands', which are used repeatedly as necessary to generate
 * small sections of the report.
 * <p/>
 * <h2>Accessing the bands and the elements:</h2>
 * <p/>
 * The different bands can be accessed using the main report definition (this class):
 * <p/>
 * <ul>
 * <li>the report header and footer can be reached by using <code>getReportHeader()</code> and
 * <code>getReportFooter()</code>
 * <p/>
 * <li>the page header and page footer can be reached by using <code>getPageHeader()</code> and
 * <code>getPageFooter()</code>
 * <p/>
 * <li>the item band is reachable with <code>getItemBand()</code>
 * <p/>
 * <li>the no-data band is reachable with <code>getNoDataBand()</code>
 * <p/>
 * <li>the watermark band is reachable with <code>getWaterMark()</code>
 * </ul>
 * <p/>
 * Groups can be queried using <code>getGroup(int groupLevel)</code>. The group header and footer are accessible through
 * the group object, so use <code>getGroup(int groupLevel).getGroupHeader()<code> and <code>getGroup(int
 * groupLevel).getGroupFooter()<code>.
 * <p/>
 * All report elements share the same stylesheet collection. Report elements cannot be shared between two different
 * report instances. Adding a report element to one band will remove it from the other one.
 * <p/>
 * For dynamic computation of content you can add {@link Expression}s and {@link org.pentaho.reporting.engine.classic
 * .core.function.Function}s
 * to the report.
 * <p/>
 * Creating a new instance of JFreeReport seems to lock down the JDK on some Windows Systems, where no printer driver is
 * installed. To prevent that behaviour on these systems, you can set the {@link Configuration} key
 * "org.pentaho.reporting.engine.classic.core.NoPrinterAvailable" to "false" and JFreeReport will use a hardcoded
 * default page format instead.
 * <p/>
 * A JFreeReport object always acts as Master-Report. The JFreeReport object defines the global report-configuration,
 * the report's datasource (through the DataFactory property) and the ResourceBundleFactory (for localization).
 *
 * @author David Gilbert
 * @author Thomas Morgner
 */
public class MasterReport extends AbstractReportDefinition {

  /**
   * Listens for changes to the DocumentBundle being used by a report and will update the ResourceManager to use that
   * DocumentBundle.
   */
  private static class ResourceBundleChangeHandler implements ReportModelListener {
    private ResourceBundleChangeHandler() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      if ( event.isNodeStructureChanged() ) {
        return;
      }
      if ( event.getParameter() instanceof StyleChange ) {
        return;
      }

      final Object element = event.getElement();
      if ( element instanceof MasterReport == false ) {
        return;
      }
      final MasterReport report = (MasterReport) element;
      report.updateResourceBundleFactoryInternal();
    }
  }

  /**
   * Listens for changes to the DocumentBundle being used by a report and will update the ResourceManager to use that
   * DocumentBundle.
   */
  private static class DocumentBundleChangeHandler implements ReportModelListener {
    private static final Log log = LogFactory.getLog( DocumentBundleChangeHandler.class );

    private DocumentBundleChangeHandler() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      if ( event.getElement() instanceof MasterReport == false ) {
        return;
      }
      final MasterReport report = (MasterReport) event.getElement();

      if ( event.getParameter() instanceof AttributeChange ) {
        final AttributeChange attributeChange = (AttributeChange) event.getParameter();

        // This is an attribute change event on the master report ... see if it is one we are concerned about
        if ( AttributeNames.Core.NAMESPACE.equals( attributeChange.getNamespace() )
            && AttributeNames.Core.BUNDLE.equals( attributeChange.getName() ) ) {
          final Object value = attributeChange.getNewValue();
          if ( ( value instanceof DocumentBundle ) == false ) {
            return;
          }

          // Insert the DocumentBundle's ResourceManager as the MasterReports resource manager
          log.debug( "DocumentBundle change detected - changing the ResourceManager for the MasterReport" );
          final DocumentBundle newDocumentBundle = (DocumentBundle) value;
          final ResourceManager resourceManager = newDocumentBundle.getResourceManager();
          report.setContentBase( newDocumentBundle.getBundleKey() );
          report.setResourceManager( resourceManager );
        }
      } else if ( event.getParameter() instanceof ResourceManager ) {
        final ResourceManager mgr = report.getResourceManager();
        final ResourceBundleFactory resourceBundleFactory = report.getResourceBundleFactory();
        if ( resourceBundleFactory instanceof LibLoaderResourceBundleFactory ) {
          LibLoaderResourceBundleFactory ll = (LibLoaderResourceBundleFactory) resourceBundleFactory;
          ll.setResourceLoader( mgr, report.getContentBase() );
        }
      }
    }
  }

  /**
   * Key for the 'report date' property.
   */
  public static final String REPORT_DATE_PROPERTY = "report.date";
  /**
   * The data factory is used to query data for the reporting.
   */
  private DataFactory dataFactory;
  /**
   * The report configuration.
   */
  private HierarchicalConfiguration reportConfiguration;
  /**
   * The resource manager is used to load the report resources.
   */
  private transient ResourceManager resourceManager;
  private ReportParameterDefinition parameterDefinition;
  private ReportEnvironment reportEnvironment;
  private ReportParameterValues parameterValues;
  /**
   * The resource bundle factory is used when generating localized reports.
   */
  private ResourceBundleFactory resourceBundleFactory;

  /**
   * The default constructor. Creates an empty but fully initialized report.
   */
  public MasterReport() {
    setElementType( new MasterReportType() );
    setResourceBundleFactory( new LibLoaderResourceBundleFactory() );

    this.reportConfiguration = new HierarchicalConfiguration( ClassicEngineBoot.getInstance().getGlobalConfig() );
    this.parameterValues = new ReportParameterValues();

    setPageDefinition( null );

    final TableDataFactory dataFactory = new TableDataFactory();
    dataFactory.addTable( "default", new DefaultTableModel() );
    this.dataFactory = dataFactory;
    setQuery( "default" );

    // Add a listener that will handle keeping the ResourceManager in sync with changes to the Document Bundle
    addReportModelListener( new DocumentBundleChangeHandler() );

    this.reportEnvironment = new DefaultReportEnvironment( getConfiguration() );
    this.parameterDefinition = new DefaultParameterDefinition();
    final MemoryDocumentBundle documentBundle = new MemoryDocumentBundle();
    documentBundle.getWriteableDocumentMetaData().setBundleType( ClassicEngineBoot.BUNDLE_TYPE );
    documentBundle.getWriteableDocumentMetaData().setBundleAttribute( ODFMetaAttributeNames.Meta.NAMESPACE,
        ODFMetaAttributeNames.Meta.CREATION_DATE, new Date() );
    setBundle( documentBundle );

    setContentBase( documentBundle.getBundleMainKey() );

    addReportModelListener( new ResourceBundleChangeHandler() );
    updateResourceBundleFactoryInternal();
  }

  public static ResourceBundleFactory computeAndInitResourceBundleFactory(
      final ResourceBundleFactory resourceBundleFactory, final ReportEnvironment environment ) {
    if ( resourceBundleFactory instanceof ExtendedResourceBundleFactory == false ) {
      return resourceBundleFactory;
    }
    final ExtendedResourceBundleFactory rawResourceBundleFactory =
        (ExtendedResourceBundleFactory) resourceBundleFactory;
    try {
      final ExtendedResourceBundleFactory extendedResourceBundleFactory =
          (ExtendedResourceBundleFactory) rawResourceBundleFactory.clone();
      if ( extendedResourceBundleFactory.getLocale() == null ) {
        extendedResourceBundleFactory.setLocale( environment.getLocale() );
      }
      if ( extendedResourceBundleFactory.getTimeZone() == null ) {
        extendedResourceBundleFactory.setTimeZone( environment.getTimeZone() );
      }
      return extendedResourceBundleFactory;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( "Cannot clone resource-bundle factory" );
    }
  }

  /**
   * Returns the resource bundle factory for this report definition. The {@link ResourceBundleFactory} is used in
   * internationalized reports to create the resourcebundles holding the localized resources.
   *
   * @return the assigned resource bundle factory.
   */
  public ResourceBundleFactory getResourceBundleFactory() {
    return resourceBundleFactory;
  }

  /**
   * Redefines the resource bundle factory for the report.
   *
   * @param resourceBundleFactory
   *          the new resource bundle factory, never null.
   * @throws NullPointerException
   *           if the given ResourceBundleFactory is null.
   */
  public void setResourceBundleFactory( final ResourceBundleFactory resourceBundleFactory ) {
    ArgumentNullException.validate( "resourceBundleFactory", resourceBundleFactory );

    this.resourceBundleFactory = resourceBundleFactory;
    this.notifyNodePropertiesChanged();
  }

  public DocumentBundle getBundle() {
    final Object o = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.BUNDLE );
    if ( o instanceof DocumentBundle ) {
      return (DocumentBundle) o;
    }
    return null;
  }

  public void setBundle( final DocumentBundle bundle ) {
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.BUNDLE, bundle );
  }

  public ReportParameterDefinition getParameterDefinition() {
    return parameterDefinition;
  }

  public void setParameterDefinition( final ReportParameterDefinition parameterDefinition ) {
    if ( parameterDefinition == null ) {
      throw new NullPointerException();
    }
    this.parameterDefinition = parameterDefinition;
    notifyNodePropertiesChanged();
  }

  public ModifiableReportParameterDefinition getModifiableParameterDefinition() {
    if ( this.parameterDefinition instanceof ModifiableReportParameterDefinition ) {
      return (ModifiableReportParameterDefinition) this.parameterDefinition;
    }
    return null;
  }

  public ReportEnvironment getReportEnvironment() {
    return reportEnvironment;
  }

  public void setReportEnvironment( final ReportEnvironment reportEnvironment ) {
    if ( reportEnvironment == null ) {
      throw new NullPointerException();
    }
    this.reportEnvironment = reportEnvironment;
    notifyNodePropertiesChanged();
  }

  public String getTitle() {
    final DocumentBundle bundle = getBundle();
    if ( bundle != null ) {
      final Object o =
          bundle.getMetaData().getBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
              ODFMetaAttributeNames.DublinCore.TITLE );
      if ( o != null ) {
        return o.toString();
      }
    }
    return null;
  }

  /**
   * Returns the logical page definition for this report.
   *
   * @return the page definition.
   */
  public PageDefinition getPageDefinition() {
    final PageDefinition pageDefinition =
        (PageDefinition) getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.PAGE_DEFINITION );
    if ( pageDefinition == null ) {
      return createDefaultPageDefinition();
    }
    return pageDefinition;
  }

  /**
   * Defines the logical page definition for this report. If no format is defined the system's default page format is
   * used.
   * <p/>
   * If there is no printer available and the JDK blocks during the printer discovery, you can set the
   * {@link Configuration} key "org.pentaho.reporting.engine.classic.core.NoPrinterAvailable" to "false" and JFreeReport
   * will use a hardcoded default page format instead.
   *
   * @param format
   *          the default format (<code>null</code> permitted).
   */
  public void setPageDefinition( PageDefinition format ) {
    if ( format == null ) {
      format = createDefaultPageDefinition();
    }
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.PAGE_DEFINITION, format );
    notifyNodePropertiesChanged();
  }

  private PageDefinition createDefaultPageDefinition() {
    final PageDefinition format;
    final ExtendedConfiguration config = ClassicEngineBoot.getInstance().getExtendedConfig();
    if ( config.getBoolProperty( ClassicEngineCoreModule.NO_PRINTER_AVAILABLE_KEY ) ) {
      format = new SimplePageDefinition( new PageFormat() );
    } else {
      format = new SimplePageDefinition( PrinterJob.getPrinterJob().defaultPage() );
    }
    return format;
  }

  /**
   * Returns the data factory that has been assigned to this report. The data factory will never be null.
   *
   * @return the data factory.
   */
  public DataFactory getDataFactory() {
    return dataFactory;
  }

  /**
   * Sets the data factory for the report.
   *
   * @param dataFactory
   *          the data factory for the report, never null.
   */
  public void setDataFactory( final DataFactory dataFactory ) {
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }

    final DataFactory old = this.dataFactory;
    this.dataFactory = dataFactory;
    notifyNodeChildRemoved( old );
    notifyNodeChildAdded( dataFactory );
  }

  /**
   * Clones the report.
   *
   * @return the clone.
   */
  public MasterReport clone() {
    final MasterReport report = (MasterReport) super.clone();
    report.reportConfiguration = (HierarchicalConfiguration) reportConfiguration.clone();
    report.reportEnvironment = (ReportEnvironment) reportEnvironment.clone();
    if ( report.reportEnvironment instanceof DefaultReportEnvironment ) {
      // this is a ugly hack. Needs to be addressed in Sugar
      final DefaultReportEnvironment dre = (DefaultReportEnvironment) report.reportEnvironment;
      dre.update( report.reportConfiguration );
    }
    report.parameterDefinition = (ReportParameterDefinition) parameterDefinition.clone();
    report.parameterValues = (ReportParameterValues) parameterValues.clone();
    report.dataFactory = dataFactory.derive();

    // Add a listener that will handle keeping the ResourceManager in sync with changes to the Document Bundle
    report.addReportModelListener( new DocumentBundleChangeHandler() );
    report.addReportModelListener( new ResourceBundleChangeHandler() );

    return report;
  }

  public MasterReport derive( final boolean preserveElementInstanceIds ) {
    final MasterReport report = (MasterReport) super.derive( preserveElementInstanceIds );
    report.reportConfiguration = (HierarchicalConfiguration) reportConfiguration.clone();
    report.reportEnvironment = (ReportEnvironment) reportEnvironment.clone();
    if ( report.reportEnvironment instanceof DefaultReportEnvironment ) {
      // this is a ugly hack. Needs to be addressed in Sugar
      final DefaultReportEnvironment dre = (DefaultReportEnvironment) report.reportEnvironment;
      dre.update( report.reportConfiguration );
    }
    report.parameterDefinition = (ReportParameterDefinition) parameterDefinition.clone();
    report.parameterValues = (ReportParameterValues) parameterValues.clone();
    report.dataFactory = dataFactory.derive();

    // Add a listener that will handle keeping the ResourceManager in sync with changes to the Document Bundle
    report.addReportModelListener( new DocumentBundleChangeHandler() );
    report.addReportModelListener( new ResourceBundleChangeHandler() );

    return report;
  }

  /**
   * Returns the report configuration.
   * <p/>
   * The report configuration is automatically set up when the report is first created, and uses the global JFreeReport
   * configuration as its parent.
   *
   * @return the report configuration.
   */
  public ModifiableConfiguration getReportConfiguration() {
    return reportConfiguration;
  }

  /**
   * Returns the report's configuration.
   *
   * @return the configuration.
   */
  public Configuration getConfiguration() {
    return reportConfiguration;
  }

  /**
   * Returns the resource manager that was responsible for loading the report. This method will return a default manager
   * if the report had been constructed otherwise.
   * <p/>
   * The resource manager of the report should be used for all resource loading activities during the report processing.
   *
   * @return the resource manager, never null.
   */
  public ResourceManager getResourceManager() {
    if ( resourceManager == null ) {
      resourceManager = new ResourceManager();
      updateResourceBundleFactoryInternal();
    }
    return resourceManager;
  }

  /**
   * Assigns a new resource manager or clears the current one. If no resource manager is set anymore, the next call to
   * 'getResourceManager' will recreate one.
   *
   * @param resourceManager
   *          the new resource manager or null.
   */
  public void setResourceManager( final ResourceManager resourceManager ) {
    this.resourceManager = resourceManager;
    notifyNodePropertiesChanged( resourceManager );
  }

  public ReportParameterValues getParameterValues() {
    return parameterValues;
  }

  protected void updateChangedFlagInternal( final ReportElement element, final int type, final Object parameter ) {
    fireModelLayoutChanged( element, type, parameter );
  }

  /**
   * A helper method that serializes the element object.
   *
   * @param stream
   *          the stream to which the element should be serialized.
   * @throws IOException
   *           if an IO error occured or a property was not serializable.
   */
  private void writeObject( final ObjectOutputStream stream ) throws IOException {
    stream.defaultWriteObject();
    try {
      final DocumentBundle bundle = getBundle();
      stream.writeObject( bundle.getMetaData().getBundleType() );

      final MemoryDocumentBundle mem = new MemoryDocumentBundle();
      BundleUtilities.copyStickyInto( mem, bundle );
      BundleUtilities.copyInto( mem, bundle, LegacyBundleResourceRegistry.getInstance().getRegisteredFiles(), true );
      BundleUtilities.copyMetaData( mem, bundle );
      mem.getWriteableDocumentMetaData().setBundleType( "application/vnd.pentaho.serialized-bundle" );
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      BundleUtilities.writeAsZip( outputStream, mem );
      stream.writeObject( outputStream.toByteArray() );
    } catch ( ContentIOException e ) {
      throw new IOException( "Unable to serialize the bundle", e );
    }
  }

  /**
   * A helper method that deserializes a object from the given stream.
   *
   * @param stream
   *          the stream from which to read the object data.
   * @throws IOException
   *           if an IO error occured.
   * @throws ClassNotFoundException
   *           if an referenced class cannot be found.
   */
  private void readObject( final ObjectInputStream stream ) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();

    updateResourceBundleFactoryInternal();

    reportConfiguration.reconnectConfiguration( ClassicEngineBoot.getInstance().getGlobalConfig() );
    addReportModelListener( new DocumentBundleChangeHandler() );

    try {
      final String bundleType = (String) stream.readObject();

      final byte[] bundleRawZip = (byte[]) stream.readObject();
      final ResourceManager mgr = getResourceManager();
      final Resource bundleResource = mgr.createDirectly( bundleRawZip, DocumentBundle.class );
      final DocumentBundle bundle = (DocumentBundle) bundleResource.getResource();

      final MemoryDocumentBundle mem = new MemoryDocumentBundle( getContentBase() );
      BundleUtilities.copyStickyInto( mem, bundle );
      BundleUtilities.copyInto( mem, bundle, LegacyBundleResourceRegistry.getInstance().getRegisteredFiles(), true );
      BundleUtilities.copyMetaData( mem, bundle );
      mem.getWriteableDocumentMetaData().setBundleType( bundleType );
      setBundle( mem );
    } catch ( ResourceException e ) {
      throw new IOException( e );
    }
  }

  private void updateResourceBundleFactoryInternal() {
    if ( resourceBundleFactory instanceof ExtendedResourceBundleFactory ) {
      final ExtendedResourceBundleFactory erbf = (ExtendedResourceBundleFactory) resourceBundleFactory;
      erbf.setResourceLoader( getResourceManager(), getContentBase() );
    }
  }

  public Integer getCompatibilityLevel() {
    final Object definedCompatLevel =
        getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMAPTIBILITY_LEVEL );
    if ( definedCompatLevel instanceof Integer ) {
      return (Integer) definedCompatLevel;
    }
    return null;
  }

  public void setCompatibilityLevel( final Integer level ) {
    setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMAPTIBILITY_LEVEL, level );
  }

  public void updateLegacyConfiguration() {
  }

  public ElementStyleDefinition getStyleDefinition() {
    return (ElementStyleDefinition) getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_SHEET );
  }

  public void setStyleDefinition( final ElementStyleDefinition styleDefinition ) {
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_SHEET, styleDefinition );
  }

  public ResourceKey getStyleSheetReference() {
    return (ResourceKey) getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_SHEET_REFERENCE );
  }

  public void setStyleSheetReference( final ResourceKey styleSheetReference ) {
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_SHEET_REFERENCE, styleSheetReference );
  }

  public String getContentCacheKey() {
    return (String) getAttribute( AttributeNames.Pentaho.NAMESPACE, AttributeNames.Pentaho.CONTENT_CACHE_KEY );
  }

  public void setContentCacheKey( final String contentCacheKey ) {
    setAttribute( AttributeNames.Pentaho.NAMESPACE, AttributeNames.Pentaho.CONTENT_CACHE_KEY, contentCacheKey );
  }

  public boolean isStrictLegacyMode() {
    return "true".equals( getReportConfiguration().getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.legacy.StrictCompatibility" ) );
  }

  public void setStrictLegacyMode( final boolean strict ) {
    getReportConfiguration().setConfigProperty( "org.pentaho.reporting.engine.classic.core.legacy.StrictCompatibility",
        String.valueOf( strict ) );
  }

  public ReportDefinition getMasterReport() {
    return this;
  }
}
