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


package org.pentaho.reporting.engine.classic.extensions.modules.mailer;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.DefaultResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskRegistry;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import jakarta.mail.Authenticator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

public class MailDefinition implements Serializable, Cloneable {
  private ArrayList headers;
  private Properties sessionProperties;

  private CompoundDataFactory dataFactory;
  private ReportParameterValues parameterValues;
  private ReportParameterDefinition parameterDefinition;

  private String burstQuery;
  private String recipientsQuery;

  private String bodyType;
  private MasterReport bodyReport;
  private ReportEnvironment reportEnvironment;

  private ArrayList attachmentReports;
  private ArrayList attachmentTypes;
  private ResourceKey contextKey;
  private ResourceManager resourceManager;
  private ResourceBundleFactory resourceBundleFactory;

  public MailDefinition() {
    this.parameterDefinition = new DefaultParameterDefinition();
    this.resourceBundleFactory = new DefaultResourceBundleFactory();
    this.resourceManager = new ResourceManager();
    this.parameterValues = new ReportParameterValues();
    this.dataFactory = new CompoundDataFactory();
    this.headers = new ArrayList();
    this.attachmentReports = new ArrayList();
    this.attachmentTypes = new ArrayList();
    this.sessionProperties = new Properties();
    this.reportEnvironment = new DefaultReportEnvironment( ClassicEngineBoot.getInstance().getGlobalConfig() );
  }

  public MailDefinition( final String bodyType, final MasterReport bodyReport ) {
    this();
    setBodyReport( bodyType, bodyReport );
  }

  public ReportEnvironment getReportEnvironment() {
    return reportEnvironment;
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return resourceBundleFactory;
  }

  public ResourceKey getContextKey() {
    return contextKey;
  }

  public void setContextKey( final ResourceKey contextKey ) {
    this.contextKey = contextKey;
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public void setResourceManager( final ResourceManager resourceManager ) {
    this.resourceManager = resourceManager;
    if ( this.resourceManager == null ) {
      this.resourceManager = new ResourceManager();
    }
  }

  public Properties getSessionProperties() {
    return (Properties) sessionProperties.clone();
  }

  public void setSessionProperties( final Properties sessionProperties ) {
    this.sessionProperties.clear();
    this.sessionProperties.putAll( sessionProperties );
  }

  public void addStaticHeader( final String name, final String value ) {
    addHeader( new StaticHeader( name, value ) );
  }

  public void addFormulaHeader( final String name, final String formula ) {
    addHeader( new FormulaHeader( name, formula ) );
  }

  public void addHeader( final MailHeader header ) {
    if ( header == null ) {
      throw new NullPointerException();
    }
    this.headers.add( header );
  }

  public MailHeader getHeader( int i ) {
    return (MailHeader) this.headers.get( i );
  }

  public int getHeaderCount() {
    return headers.size();
  }

  public MailHeader[] getHeaders() {
    return (MailHeader[]) headers.toArray( new MailHeader[headers.size()] );
  }

  public void addAttachmentReport( final String type, final MasterReport attachmentReport ) {
    if ( type == null ) {
      throw new NullPointerException();
    }
    if ( attachmentReport == null ) {
      throw new NullPointerException();
    }
    if ( ReportProcessTaskRegistry.getInstance().isExportTypeRegistered( type ) == false ) {
      throw new IllegalArgumentException( "The export type " + type + " is not defined." );
    }

    this.attachmentTypes.add( type );
    this.attachmentReports.add( attachmentReport );
  }

  public String getBodyType() {
    return bodyType;
  }

  public MasterReport getBodyReport() {
    return bodyReport;
  }

  public void setBodyReport( final String bodyType, final MasterReport bodyReport ) {
    this.bodyType = bodyType;
    this.bodyReport = bodyReport;
  }

  public int getAttachmentCount() {
    return attachmentReports.size();
  }

  public MasterReport getAttachmentReport( final int index ) {
    return (MasterReport) attachmentReports.get( index );
  }

  public String getAttachmentType( final int index ) {
    return (String) attachmentTypes.get( index );
  }

  public void removeAttachment( final int index ) {
    attachmentReports.remove( index );
    attachmentTypes.remove( index );
  }

  public CompoundDataFactory getDataFactory() {
    return dataFactory;
  }

  public ReportParameterValues getParameterValues() {
    return parameterValues;
  }

  public ReportParameterDefinition getParameterDefinition() {
    return parameterDefinition;
  }

  public void setParameterDefinition( final ReportParameterDefinition parameterDefinition ) {
    if ( parameterDefinition == null ) {
      throw new NullPointerException();
    }
    this.parameterDefinition = parameterDefinition;
  }

  public String getBurstQuery() {
    return burstQuery;
  }

  public void setBurstQuery( final String burstQuery ) {
    this.burstQuery = burstQuery;
  }

  public String getRecipientsQuery() {
    return recipientsQuery;
  }

  public void setRecipientsQuery( final String recipientsQuery ) {
    this.recipientsQuery = recipientsQuery;
  }

  public Object clone() throws CloneNotSupportedException {
    final MailDefinition mailDefinition = (MailDefinition) super.clone();
    mailDefinition.bodyReport = (MasterReport) bodyReport.clone();
    mailDefinition.attachmentTypes = (ArrayList) attachmentTypes.clone();
    mailDefinition.attachmentReports = (ArrayList) attachmentReports.clone();
    mailDefinition.attachmentReports.clear();
    for ( int i = 0; i < attachmentReports.size(); i++ ) {
      final MasterReport report = (MasterReport) attachmentReports.get( i );
      mailDefinition.attachmentReports.add( report.clone() );
    }
    return mailDefinition;
  }

  public Authenticator getAuthenticator() {
    return new DefaultAuthenticator( sessionProperties );
  }
}
