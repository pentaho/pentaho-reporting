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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;

/**
 * Carries the global state while writing.
 *
 * @author Thomas Morgner
 */
public class BundleWriterState {
  private MasterReport masterReport;
  private DocumentBundle globalBundle;
  private BundleWriter bundleWriter;
  private AbstractReportDefinition report;
  private String bundleFileName;

  public BundleWriterState( final MasterReport masterReport, final DocumentBundle globalBundle,
      final BundleWriter writer ) {
    if ( masterReport == null ) {
      throw new NullPointerException();
    }
    if ( globalBundle == null ) {
      throw new NullPointerException();
    }
    if ( writer == null ) {
      throw new NullPointerException();
    }

    this.report = masterReport;
    this.masterReport = masterReport;
    this.globalBundle = globalBundle;
    this.bundleWriter = writer;
    this.bundleFileName = "";
  }

  public BundleWriterState( final BundleWriterState parent, final String bundleFileName ) {
    this( parent, parent.getReport(), bundleFileName );
  }

  public BundleWriterState( final BundleWriterState parent, final AbstractReportDefinition report,
      final String bundleFileName ) {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( parent == null ) {
      throw new NullPointerException();
    }
    if ( bundleFileName == null ) {
      throw new NullPointerException();
    }

    this.masterReport = parent.masterReport;
    this.globalBundle = parent.globalBundle;
    this.bundleWriter = parent.bundleWriter;
    this.report = report;
    this.bundleFileName = IOUtils.getInstance().getAbsolutePath( bundleFileName, parent.getFileName() );
  }

  public MasterReport getMasterReport() {
    return masterReport;
  }

  public DocumentBundle getGlobalBundle() {
    return globalBundle;
  }

  // Either subreport or jfreereport object
  public AbstractReportDefinition getReport() {
    return report;
  }

  public String getFileName() {
    return bundleFileName;
  }

  public BundleWriter getBundleWriter() {
    return bundleWriter;
  }
}
