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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.MultiStreamReportProcessTask;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.core.ReportProcessTask;
import org.pentaho.reporting.engine.classic.core.ReportProcessTaskUtil;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskRegistry;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.email.EmailRepository;

import jakarta.activation.DataHandler;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * The mail-processor performs the bursting operation.
 *
 * @author Thomas Morgner
 * @noinspection ThrowableResultOfMethodCallIgnored
 */
public class MailProcessor {

  private static class WrapperTableModel implements TableModel {
    private TableModel parent;
    private DataRow parameters;
    private String[] parameterNames;

    private WrapperTableModel( final DataRow parameters, final TableModel parent ) {
      this.parent = parent;
      this.parameters = parameters;

      this.parameterNames = parameters.getColumnNames();
    }

    /**
     * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
     * should display. This method should be quick, as it is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
      return parent.getRowCount();
    }

    /**
     * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns
     * it should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
      return parameterNames.length + parent.getColumnCount();
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
     *
     * @param rowIndex
     *          the row whose value is to be queried
     * @param columnIndex
     *          the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt( final int rowIndex, final int columnIndex ) {
      if ( columnIndex < parameterNames.length ) {
        return parameters.get( parameterNames[columnIndex] );
      }
      return parent.getValueAt( rowIndex, columnIndex - parameterNames.length );
    }

    /**
     * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc. If
     * <code>column</code> cannot be found, returns an empty string.
     *
     * @param columnIndex
     *          the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public String getColumnName( final int columnIndex ) {
      if ( columnIndex < parameterNames.length ) {
        return parameterNames[columnIndex];
      }
      return parent.getColumnName( columnIndex - parameterNames.length );
    }

    /**
     * Returns the most specific superclass for all the cell values in the column. This is used by the
     * <code>JTable</code> to set up a default renderer and editor for the column.
     *
     * @param columnIndex
     *          the index of the column
     * @return the common ancestor class of the object values in the model.
     */
    public Class getColumnClass( final int columnIndex ) {
      if ( columnIndex < parameterNames.length ) {
        return Object.class;
      }
      return parent.getColumnClass( columnIndex - parameterNames.length );
    }

    /**
     * Returns true if the cell at <code>rowIndex</code> and <code>columnIndex</code> is editable. Otherwise,
     * <code>setValueAt</code> on the cell will not change the value of that cell.
     *
     * @param rowIndex
     *          the row whose value to be queried
     * @param columnIndex
     *          the column whose value to be queried
     * @return true if the cell is editable
     * @see #setValueAt
     */
    public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
      return false;
    }

    /**
     * Sets the value in the cell at <code>columnIndex</code> and <code>rowIndex</code> to <code>aValue</code>.
     *
     * @param aValue
     *          the new value
     * @param rowIndex
     *          the row whose value is to be changed
     * @param columnIndex
     *          the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {

    }

    /**
     * Adds a listener to the list that is notified each time a change to the data model occurs.
     *
     * @param l
     *          the TableModelListener
     */
    public void addTableModelListener( final TableModelListener l ) {

    }

    /**
     * Removes a listener from the list that is notified each time a change to the data model occurs.
     *
     * @param l
     *          the TableModelListener
     */
    public void removeTableModelListener( final TableModelListener l ) {

    }
  }

  private static final Log logger = LogFactory.getLog( MailProcessor.class );

  private MailProcessor() {
  }

  public static MimeMessage createReport( final MailDefinition mailDefinition, final Session session )
    throws ReportProcessingException, ContentIOException, MessagingException {
    return createReport( mailDefinition, session, new StaticDataRow() );
  }

  public static MimeMessage createReport( final MailDefinition mailDefinition, final Session session,
      final DataRow parameters ) throws ReportProcessingException, ContentIOException, MessagingException {
    final MasterReport bodyReport = mailDefinition.getBodyReport();
    final String[] paramNames = parameters.getColumnNames();
    final ReportParameterValues parameterValues = bodyReport.getParameterValues();
    for ( int i = 0; i < paramNames.length; i++ ) {
      final String paramName = paramNames[i];
      if ( isParameterDefined( bodyReport, paramName ) ) {
        parameterValues.put( paramName, parameters.get( paramName ) );
      }
    }

    final ReportProcessTaskRegistry registry = ReportProcessTaskRegistry.getInstance();
    final String bodyType = mailDefinition.getBodyType();
    final ReportProcessTask processTask = registry.createProcessTask( bodyType );
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ReportProcessTaskUtil.configureBodyStream( processTask, bout, "report", null );
    processTask.setReport( bodyReport );
    if ( processTask instanceof MultiStreamReportProcessTask ) {
      final MultiStreamReportProcessTask mtask = (MultiStreamReportProcessTask) processTask;
      mtask.setBulkLocation( mtask.getBodyContentLocation() );
      mtask.setBulkNameGenerator( new DefaultNameGenerator( mtask.getBodyContentLocation(), "data" ) );
      mtask.setUrlRewriter( new MailURLRewriter() );
    }
    processTask.run();
    if ( processTask.isTaskSuccessful() == false ) {
      if ( processTask.isTaskAborted() ) {
        logger.info( "EMail Task received interrupt." );
        return null;
      } else {
        logger.info( "EMail Task failed:", processTask.getError() );
        throw new ReportProcessingException( "EMail Task failed", processTask.getError() );
      }
    }

    final EmailRepository repository = new EmailRepository( session );
    final MimeBodyPart messageBodyPart = repository.getBodypart();
    final ByteArrayDataSource dataSource =
        new ByteArrayDataSource( bout.toByteArray(), processTask.getReportMimeType() );
    messageBodyPart.setDataHandler( new DataHandler( dataSource ) );

    final int attachmentsSize = mailDefinition.getAttachmentCount();
    for ( int i = 0; i < attachmentsSize; i++ ) {
      final MasterReport report = mailDefinition.getAttachmentReport( i );
      final String type = mailDefinition.getAttachmentType( i );
      final ContentLocation location = repository.getRoot();
      final ContentLocation bulkLocation = location.createLocation( "attachment-" + i );

      final ReportProcessTask attachmentProcessTask = registry.createProcessTask( type );
      attachmentProcessTask.setBodyContentLocation( bulkLocation );
      attachmentProcessTask.setBodyNameGenerator( new DefaultNameGenerator( bulkLocation, "report" ) );
      attachmentProcessTask.setReport( report );
      if ( attachmentProcessTask instanceof MultiStreamReportProcessTask ) {
        final MultiStreamReportProcessTask mtask = (MultiStreamReportProcessTask) attachmentProcessTask;
        mtask.setBulkLocation( bulkLocation );
        mtask.setBulkNameGenerator( new DefaultNameGenerator( bulkLocation, "data" ) );
        mtask.setUrlRewriter( new MailURLRewriter() );
      }
      attachmentProcessTask.run();

      if ( attachmentProcessTask.isTaskSuccessful() == false ) {
        if ( attachmentProcessTask.isTaskAborted() ) {
          logger.info( "EMail Task received interrupt." );
        } else {
          logger.info( "EMail Task failed:", attachmentProcessTask.getError() );
          throw new ReportProcessingException( "EMail Task failed", attachmentProcessTask.getError() );
        }
      }
    }

    return repository.getEmail();
  }

  private static boolean isParameterDefined( final MasterReport bodyReport, final String paramName ) {
    final ParameterDefinitionEntry[] definitionEntries = bodyReport.getParameterDefinition().getParameterDefinitions();
    for ( int i = 0; i < definitionEntries.length; i++ ) {
      final ParameterDefinitionEntry definitionEntry = definitionEntries[i];
      if ( definitionEntry.getName().equals( paramName ) ) {
        return true;
      }
    }
    return false;
  }

  public static void performBursting( final MailDefinition definition ) throws ReportProcessingException,
    MessagingException, ContentIOException {
    final Session session = Session.getInstance( definition.getSessionProperties(), definition.getAuthenticator() );
    performBursting( definition, session );
  }

  public static void performBursting( final MailDefinition definition, final Session session )
    throws MessagingException, ReportProcessingException, ContentIOException {
    if ( session == null ) {
      throw new NullPointerException();
    }

    // process parameters - validate!
    final ReportParameterValues parameterValues = definition.getParameterValues();
    final DefaultParameterContext parameterContext =
        new DefaultParameterContext( definition.getDataFactory(), parameterValues, ClassicEngineBoot.getInstance()
            .getGlobalConfig(), definition.getResourceBundleFactory(), definition.getResourceManager(), definition
            .getContextKey(), definition.getReportEnvironment() );

    try {
      final ReportParameterDefinition parameterDefinition = definition.getParameterDefinition();
      final ReportParameterValidator reportParameterValidator = parameterDefinition.getValidator();
      final ValidationResult validationResult =
          reportParameterValidator.validate( new ValidationResult(), parameterDefinition, parameterContext );
      if ( validationResult.isEmpty() == false ) {
        throw new ReportParameterValidationException( "The parameters provided for this report are not valid.",
            validationResult );
      }
    } finally {
      parameterContext.close();
    }

    // definition: Single mail or multi-mail
    final TableModel burstingData;
    final DataFactory dataFactory = definition.getDataFactory();
    if ( definition.getBurstQuery() != null
        && dataFactory.isQueryExecutable( definition.getBurstQuery(), parameterValues ) ) {
      burstingData =
          wrapWithParameters( dataFactory.queryData( definition.getBurstQuery(), parameterValues ), parameterValues );
    } else {
      burstingData = wrapWithParameters( new DefaultTableModel( 1, 0 ), parameterValues );
    }

    if ( burstingData.getRowCount() > 0 ) {
      // final Transport transport = session.getTransport();
      // transport.connect();
      for ( int i = 0; i < burstingData.getRowCount(); i++ ) {
        final DataRow parameterDataRow = createReportParameterDataRow( burstingData, i );
        final MimeMessage message = createReport( definition, session, parameterDataRow );

        parameterContext.setParameterValues( parameterDataRow );

        final MailHeader[] headers = definition.getHeaders();
        for ( int j = 0; j < headers.length; j++ ) {
          final MailHeader header = headers[j];
          message.addHeader( header.getName(), header.getValue( parameterContext ) );
        }

        processRecipients( definition, message, dataFactory, parameterDataRow );

        // transport.sendMessage(message, message.getAllRecipients());
      }
      // transport.close();
    }
  }

  private static void processRecipients( final MailDefinition definition, final MimeMessage message,
      final DataFactory dataFactory, final DataRow parameterDataRow ) throws ReportDataFactoryException,
    MessagingException {
    if ( definition.getRecipientsQuery() != null
        && dataFactory.isQueryExecutable( definition.getRecipientsQuery(), parameterDataRow ) ) {
      final TableModel model =
          wrapWithParameters( dataFactory.queryData( definition.getRecipientsQuery(), parameterDataRow ),
              parameterDataRow );

      for ( int r = 0; r < model.getRowCount(); r++ ) {
        String address = null;
        String name = null;
        String type = "TO";
        if ( model.getColumnCount() >= 3 ) {
          type = (String) model.getValueAt( 0, 2 );
        }
        if ( model.getColumnCount() >= 2 ) {
          name = (String) model.getValueAt( 0, 1 );
        }
        if ( model.getColumnCount() >= 1 ) {
          address = (String) model.getValueAt( 0, 0 );
        }
        if ( address == null ) {
          continue;
        }

        if ( name == null ) {
          message.addRecipient( parseType( type ), new InternetAddress( address, true ) );
        } else {
          try {
            message.addRecipient( parseType( type ), new InternetAddress( address, name, "UTF-8" ) );
          } catch ( UnsupportedEncodingException e ) {
            // Should not happen - UTF-8 is safe to use
            throw new MessagingException( "Failed to encode recipient", e );
          }
        }
      }
    }
  }

  private static Message.RecipientType parseType( final String type ) {
    if ( "TO".equalsIgnoreCase( type ) ) {
      return MimeMessage.RecipientType.TO;
    }
    if ( "CC".equalsIgnoreCase( type ) ) {
      return MimeMessage.RecipientType.CC;
    }
    if ( "BCC".equalsIgnoreCase( type ) ) {
      return MimeMessage.RecipientType.BCC;
    }
    return MimeMessage.RecipientType.TO;
  }

  private static DataRow createReportParameterDataRow( final TableModel burstingData, final int row ) {
    final int columnCount = burstingData.getColumnCount();
    final String[] columnNames = new String[columnCount];
    final Object[] columnValues = new Object[columnCount];
    for ( int i = 0; i < columnCount; i++ ) {
      columnValues[i] = burstingData.getValueAt( row, i );
      columnNames[i] = burstingData.getColumnName( i );
    }
    return new StaticDataRow( columnNames, columnValues );
  }

  private static TableModel wrapWithParameters( final TableModel model, final DataRow parameter ) {
    return new WrapperTableModel( parameter, model );
  }
}
