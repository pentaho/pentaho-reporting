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
* Copyright (c) 2001 - 2018 Object Refinery Ltd, Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.states.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.filter.types.ExternalElementType;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.function.ProcessingDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.function.sys.AttributeExpressionsEvaluator;
import org.pentaho.reporting.engine.classic.core.function.sys.CellFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.sys.MetaDataStyleEvaluator;
import org.pentaho.reporting.engine.classic.core.function.sys.SheetNameFunction;
import org.pentaho.reporting.engine.classic.core.function.sys.StyleExpressionsEvaluator;
import org.pentaho.reporting.engine.classic.core.function.sys.StyleResolvingEvaluator;
import org.pentaho.reporting.engine.classic.core.function.sys.WizardItemHideFunction;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;
import org.pentaho.reporting.engine.classic.core.sorting.SortingDataFactory;
import org.pentaho.reporting.engine.classic.core.states.DataFactoryManager;
import org.pentaho.reporting.engine.classic.core.states.DefaultGroupSizeRecorder;
import org.pentaho.reporting.engine.classic.core.states.DefaultGroupingState;
import org.pentaho.reporting.engine.classic.core.states.DesignTimeDataFactory;
import org.pentaho.reporting.engine.classic.core.states.EmptyDataFactory;
import org.pentaho.reporting.engine.classic.core.states.EmptyGroupSizeRecorder;
import org.pentaho.reporting.engine.classic.core.states.FunctionStorage;
import org.pentaho.reporting.engine.classic.core.states.FunctionStorageKey;
import org.pentaho.reporting.engine.classic.core.states.GroupSizeRecorder;
import org.pentaho.reporting.engine.classic.core.states.GroupStartRecord;
import org.pentaho.reporting.engine.classic.core.states.GroupingState;
import org.pentaho.reporting.engine.classic.core.states.IgnoreEverythingReportErrorHandler;
import org.pentaho.reporting.engine.classic.core.states.InitialLayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.ProcessStateHandle;
import org.pentaho.reporting.engine.classic.core.states.ReportDefinitionImpl;
import org.pentaho.reporting.engine.classic.core.states.ReportProcessingErrorHandler;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.states.StateUtilities;
import org.pentaho.reporting.engine.classic.core.states.StructureFunctionComparator;
import org.pentaho.reporting.engine.classic.core.states.SubLayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.SubReportStorage;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabProcessorFunction;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.states.datarow.InlineDataRowRuntime;
import org.pentaho.reporting.engine.classic.core.states.datarow.MasterDataRow;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.util.LongSequence;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.wizard.ProxyDataSchemaDefinition;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings( "HardCodedStringLiteral" )
public class ProcessState implements ReportState {
  private static class InternalProcessHandle implements ProcessStateHandle {
    private PerformanceMonitorContext monitorContext;
    private DataFactoryManager manager;

    private InternalProcessHandle( final DataFactoryManager manager,
                                   final PerformanceMonitorContext monitorContext ) {
      this.manager = manager;
      this.monitorContext = monitorContext;
    }

    public void close() {
      // close the data-factory manager ...
      monitorContext.close();
      manager.close();
    }
  }

  private static class InternalPerformanceMonitorContext implements PerformanceMonitorContext {
    private PerformanceMonitorContext parent;
    private EventListenerList listeners;

    private InternalPerformanceMonitorContext( final PerformanceMonitorContext parent ) {
      this.parent = parent;
      this.listeners = new EventListenerList();
    }

    public PerformanceLoggingStopWatch createStopWatch( final String tag ) {
      return parent.createStopWatch( tag );
    }

    public PerformanceLoggingStopWatch createStopWatch( final String tag, final Object message ) {
      return parent.createStopWatch( tag, message );
    }

    public void addChangeListener( final ChangeListener listener ) {
      listeners.add( ChangeListener.class, listener );
    }

    public void removeChangeListener( final ChangeListener listener ) {
      listeners.remove( ChangeListener.class, listener );
    }

    public void close() {
      ChangeEvent event = new ChangeEvent( this );
      for ( final ChangeListener changeListener : listeners.getListeners( ChangeListener.class ) ) {
        changeListener.stateChanged( event );
      }
    }
  }

  public static final int ARTIFICIAL_EVENT_CODE = ReportEvent.ARTIFICIAL_EVENT_CODE;
  private static final Log logger = LogFactory.getLog( ProcessState.class );

  private int currentGroupIndex;
  private int currentPresentationGroupIndex;
  private ReportDefinitionImpl report;

  private int currentSubReport;
  private InlineSubreportMarker[] subReports;
  private ProcessState suspendedState;
  private ProcessState parentSubReportState;
  private FunctionStorage functionStorage;
  private FunctionStorage structureFunctionStorage;
  private DataFactoryManager dataFactoryManager;
  private InternalProcessHandle processHandle;
  private DefaultFlowController flowController;
  private LayoutProcess layoutProcess;
  private ReportStateKey processKey;
  private AdvanceHandler advanceHandler;
  private ReportProcessingErrorHandler errorHandler;
  private int sequenceCounter;
  private boolean inItemGroup;
  private InlineSubreportMarker currentSubReportMarker;
  private boolean inlineProcess;
  private FastStack<GroupStartRecord> groupStarts;
  private boolean structuralPreprocessingNeeded;
  private HashSet<Integer> processLevels;
  private SubReportStorage subReportStorage;
  private String query;
  private Integer queryLimit;
  private Integer queryTimeout;
  private GroupSizeRecorder recorder;
  private boolean reportInstancesShareConnection;
  private AdvanceHandler postSummaryRowAdvanceHandler;
  private int replayStoredCrosstabGroup;
  private LongSequence groupSequenceCounter;
  private LongSequence crosstabColumnSequenceCounter;
  private boolean designtime;
  private PerformanceMonitorContext performanceMonitorContext;
  private ReportProcessStore processStore;

  public ProcessState() {

  }

  public boolean initializeForMasterReport( final MasterReport report,
                                         final ProcessingContext processingContext,
                                         final OutputFunction outputFunction )
    throws ReportProcessingException {
    ArgumentNullException.validate( "report", report );
    ArgumentNullException.validate( "processingContext", processingContext );
    ArgumentNullException.validate( "outputFunction", outputFunction );

    final ReportParameterDefinition parameters = report.getParameterDefinition();
    final DefaultParameterContext parameterContext = new DefaultParameterContext( report );

    // pre-init the output-processor-metadata.
    initializeProcessingContext( processingContext, report );

    this.designtime =
      processingContext.getOutputProcessorMetaData().isFeatureSupported( OutputProcessorFeature.DESIGNTIME );
    final ReportParameterValues parameterValues;
    if ( designtime == false ) {
      try {
        final ReportParameterValidator reportParameterValidator = parameters.getValidator();
        final ValidationResult validationResult =
          reportParameterValidator.validate( new ValidationResult(), parameters, parameterContext );
        if ( validationResult.isEmpty() == false ) {
          throw new ReportParameterValidationException( "The parameters provided for this report are not valid.",
            validationResult );
        }
        parameterValues = validationResult.getParameterValues();
      } finally {
        parameterContext.close();
      }
    } else {
      parameterValues = new ReportParameterValues();
    }

    final PerformanceMonitorContext rawPerformanceMonitorContext =
      ClassicEngineBoot.getInstance().getObjectFactory().get( PerformanceMonitorContext.class );
    this.performanceMonitorContext = new InternalPerformanceMonitorContext( rawPerformanceMonitorContext );
    final InitialLayoutProcess layoutProcess = new InitialLayoutProcess( outputFunction, performanceMonitorContext );

    this.reportInstancesShareConnection = "true".equals( processingContext.getConfiguration()
      .getConfigProperty( "org.pentaho.reporting.engine.classic.core.ReportInstancesShareConnections" ) );
    this.processLevels = new HashSet<Integer>();
    this.groupStarts = new FastStack<GroupStartRecord>();
    this.errorHandler = IgnoreEverythingReportErrorHandler.INSTANCE;
    this.advanceHandler = BeginReportHandler.HANDLER;
    this.currentSubReport = -1;
    this.currentGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    this.currentPresentationGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    this.processStore = new ReportProcessStore();
    this.functionStorage = new FunctionStorage();
    this.structureFunctionStorage = new FunctionStorage();
    this.sequenceCounter = 0;
    this.processKey =
      new ReportStateKey( null, ReportState.BEFORE_FIRST_ROW, 0, ReportState.BEFORE_FIRST_GROUP, -1, sequenceCounter,
        false, false );
    this.dataFactoryManager = new DataFactoryManager();
    this.subReportStorage = new SubReportStorage();
    this.processHandle = new InternalProcessHandle( dataFactoryManager, performanceMonitorContext );
    this.crosstabColumnSequenceCounter = new LongSequence( 10, -1 );
    this.groupSequenceCounter = new LongSequence( 10, -1 );
    this.groupSequenceCounter.set( 0, -1 );

    final DefaultFlowController startFlowController =
      new DefaultFlowController( processingContext, report.getDataSchemaDefinition(),
        StateUtilities.computeParameterValueSet( report, parameterValues ), performanceMonitorContext );

    final MasterReportProcessPreprocessor processPreprocessor =
      new MasterReportProcessPreprocessor( startFlowController );
    final MasterReport processedReport = processPreprocessor.invokePreDataProcessing( report );
    final DefaultFlowController flowController = processPreprocessor.getFlowController();

    final Object dataCacheEnabledRaw =
      processedReport.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.DATA_CACHE );
    final boolean dataCacheEnabled = designtime == false && Boolean.FALSE.equals( dataCacheEnabledRaw ) == false;

    final DataFactory sortingDataFactory =
      new SortingDataFactory( lookupDataFactory( processedReport ), performanceMonitorContext );
    final CachingDataFactory dataFactory = new CachingDataFactory( sortingDataFactory, dataCacheEnabled );
    dataFactory.initialize( new ProcessingDataFactoryContext( processingContext, dataFactory ) );

    final FunctionStorageKey functionStorageKey = FunctionStorageKey.createKey( null, processedReport );
    this.dataFactoryManager.store( functionStorageKey, dataFactory, true );
    // eval query, query-limit and query-timeout
    this.flowController = flowController;
    final Integer queryLimitDefault = IntegerCache.getInteger( processedReport.getQueryLimit() );
    final Integer queryTimeoutDefault = IntegerCache.getInteger( processedReport.getQueryTimeout() );

    final String queryDefined = designtime ? "design-time-query" : processedReport.getQuery();
    final Object queryRaw =
      evaluateExpression( processedReport.getAttributeExpression( AttributeNames.Internal.NAMESPACE,
        AttributeNames.Internal.QUERY ), queryDefined );
    final Object queryLimitRaw =
      evaluateExpression( processedReport.getAttributeExpression( AttributeNames.Internal.NAMESPACE,
        AttributeNames.Internal.QUERY_LIMIT ), queryLimitDefault );
    final Object queryTimeoutRaw =
      evaluateExpression( processedReport.getAttributeExpression( AttributeNames.Internal.NAMESPACE,
        AttributeNames.Internal.QUERY_TIMEOUT ), queryTimeoutDefault );
    final List<SortConstraint> sortOrder = lookupSortOrder( processedReport );

    this.query = (String) ConverterRegistry.convert( queryRaw, String.class, processedReport.getQuery() );
    this.queryLimit = (Integer) ConverterRegistry.convert( queryLimitRaw, Integer.class, queryLimitDefault );
    this.queryTimeout = (Integer) ConverterRegistry.convert( queryTimeoutRaw, Integer.class, queryTimeoutDefault );

    DefaultFlowController postQueryFlowController =
      flowController.performQuery( dataFactory, query, queryLimit.intValue(), queryTimeout.intValue(),
        processingContext.getResourceBundleFactory(), sortOrder );

    final MasterReportProcessPreprocessor postProcessor =
      new MasterReportProcessPreprocessor( postQueryFlowController );
    final MasterReport fullReport = postProcessor.invokePreProcessing( processedReport );
    postQueryFlowController = postProcessor.getFlowController();

    if ( isStructureRunNeeded( processedReport ) == false ) {
      // Perform a static analysis on whether there is an External-element or Inline-Subreports or Crosstabs
      // if none, return unchanged
      this.structuralPreprocessingNeeded = false;
    } else {
      // otherwise process the report one time to walk through all eligible states. Record all subreports,
      // and then compute the runlevels based on what we have in the caches.
      this.structuralPreprocessingNeeded = true;
      this.processLevels.add( LayoutProcess.LEVEL_STRUCTURAL_PREPROCESSING );
      postQueryFlowController.requireStructuralProcessing();
    }

    final Expression[] expressions;
    if ( designtime ) {
      expressions = new Expression[ 0 ];
    } else {
      expressions = fullReport.getExpressions().getExpressions();
    }

    this.flowController = postQueryFlowController.activateExpressions( expressions, false );
    this.report = new ReportDefinitionImpl( fullReport, fullReport.getPageDefinition() );
    this.layoutProcess = new SubLayoutProcess( layoutProcess,
      computeStructureFunctions( fullReport.getStructureFunctions(),
        getFlowController().getReportContext().getOutputProcessorMetaData() ), fullReport.getObjectID() );

    if ( StateUtilities.computeLevels( this.flowController, this.layoutProcess, processLevels ) ) {
      this.recorder = new DefaultGroupSizeRecorder();
    } else {
      this.recorder = new EmptyGroupSizeRecorder();
    }
    this.processKey = createKey();

    return flowController.isQueryLimitReached();
  }

  private List<SortConstraint> lookupSortOrder( final ReportDefinition report ) {
    Object attribute = report.getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMPUTED_SORT_CONSTRAINTS );
    if ( attribute instanceof List<?> ) {
      return (List<SortConstraint>) attribute;
    }
    return Collections.emptyList();
  }

  private void initializeProcessingContext( final ProcessingContext processingContext, final MasterReport report ) {
    Configuration configuration = wrapForCompatibility( processingContext );
    processingContext.getOutputProcessorMetaData().initialize( mapStaticMetaData( configuration, report ) );
  }

  private Configuration mapStaticMetaData( final Configuration configuration, final MasterReport report ) {
    HierarchicalConfiguration hc = new HierarchicalConfiguration( configuration );

    setConfigurationIfDefined( hc,
      "org.pentaho.reporting.engine.classic.core.layout.fontrenderer.ComplexTextLayout",
      report.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.COMPLEX_TEXT ) );
    setConfigurationIfDefined( hc,
      "org.pentaho.reporting.engine.classic.core.WatermarkPrintedOnTopOfContent",
      report.getWatermark()
        .getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.WATERMARK_PRINTED_ON_TOP ) );

    return hc;
  }

  private void setConfigurationIfDefined( final ModifiableConfiguration config, final String configKey,
                                          final Object value ) {
    if ( value == null ) {
      return;
    }
    try {
      String valueText = ConverterRegistry.toAttributeValue( value );
      config.setConfigProperty( configKey, valueText );
    } catch ( final BeanException e ) {
      logger.info( String
        .format( "Ignoring invalid attribute-value override for configuration '%s' with value '%s'", configKey,
          value ) );
    }
  }

  private Configuration wrapForCompatibility( final ProcessingContext processingContext ) {
    final int compatibilityLevel = processingContext.getCompatibilityLevel();
    if ( compatibilityLevel < 0 ) {
      return processingContext.getConfiguration();
    }

    if ( compatibilityLevel < ClassicEngineBoot.computeVersionId( 3, 999, 999 ) ) {
      // enable strict compatibility mode for reports older than 4.0.
      final HierarchicalConfiguration config = new HierarchicalConfiguration( processingContext.getConfiguration() );
      config
        .setConfigProperty( "org.pentaho.reporting.engine.classic.core.legacy.WrapProgressMarkerInSection", "true" );
      config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.legacy.StrictCompatibility", "true" );
      return config;
    }

    // this is a trunk or 4.0 or newer report.
    return processingContext.getConfiguration();
  }

  private boolean isDesigntime() {
    return designtime;
  }

  private boolean isReportsShareConnections( final ReportDefinition report ) {
    final Object attribute =
      report.getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.SHARED_CONNECTIONS );
    if ( Boolean.TRUE.equals( attribute ) ) {
      return true;
    }
    if ( Boolean.FALSE.equals( attribute ) ) {
      return false;
    }
    return reportInstancesShareConnection;
  }

  public void initializeForSubreport( final InlineSubreportMarker[] subReports,
                                      final int subReportIndex,
                                      final ProcessState parentState ) throws ReportProcessingException {
    if ( parentState == null ) {
      throw new NullPointerException();
    }

    this.designtime = parentState.designtime;
    this.crosstabColumnSequenceCounter = new LongSequence( 10, -1 );
    this.groupSequenceCounter = new LongSequence( 10, -1 );
    this.groupSequenceCounter.set( 0, -1 );
    this.recorder = (GroupSizeRecorder) parentState.recorder.clone();
    this.recorder.reset();
    this.performanceMonitorContext = parentState.performanceMonitorContext;
    this.reportInstancesShareConnection = parentState.reportInstancesShareConnection;
    this.groupStarts = new FastStack<GroupStartRecord>();
    this.parentSubReportState = parentState;
    this.advanceHandler = BeginReportHandler.HANDLER;
    this.errorHandler = parentState.errorHandler;
    this.functionStorage = parentState.functionStorage;
    this.structureFunctionStorage = parentState.structureFunctionStorage;
    this.processStore = parentState.processStore;
    this.currentGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    this.currentPresentationGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    this.currentSubReport = subReportIndex;
    this.subReports = subReports.clone();
    this.dataFactoryManager = parentState.dataFactoryManager;
    this.subReportStorage = parentState.subReportStorage;
    this.structuralPreprocessingNeeded = parentState.structuralPreprocessingNeeded;
    this.processLevels = parentState.processLevels;
    this.sequenceCounter = parentState.getSequenceCounter() + 1;

    this.currentSubReportMarker = subReports[ subReportIndex ];
    this.inlineProcess =
      parentState.isInlineProcess() || currentSubReportMarker.getProcessType() == SubReportProcessType.INLINE;

    final SubReport subreportFromMarker = currentSubReportMarker.getSubreport();
    final FunctionStorageKey functionStorageKey =
      FunctionStorageKey.createKey( parentSubReportState.getProcessKey(), subreportFromMarker );
    final boolean needPreProcessing;
    final SubReport initialSubReport;
    if ( subReportStorage.contains( functionStorageKey ) ) {
      initialSubReport = subReportStorage.restore( functionStorageKey );
      initialSubReport.reconnectParent( subreportFromMarker.getParentSection() );
      applyCurrentStyleAndAttributes( subreportFromMarker, initialSubReport );
      needPreProcessing = false;
    } else {
      initialSubReport = subreportFromMarker.derive( true );
      initialSubReport.reconnectParent( subreportFromMarker.getParentSection() );
      needPreProcessing = true;
    }

    final DefaultFlowController parentStateFlowController = parentState.getFlowController();
    final ResourceBundleFactory resourceBundleFactory = parentState.getResourceBundleFactory();

    if ( isSubReportInvisible( initialSubReport, parentStateFlowController ) ) {
      // make it a minimum effort report, but still enter the loop.
      final ReportDefinition parentReport = parentState.getReport();
      final SubReport dummyReport = new SubReport( functionStorageKey.getReportId() );
      this.report = new ReportDefinitionImpl( dummyReport, parentReport.getPageDefinition(),
        subreportFromMarker.getParentSection() );
      this.flowController = parentStateFlowController.derive();
      this.advanceHandler = EndSubReportHandler.HANDLER;
      this.layoutProcess = new SubLayoutProcess( parentState.layoutProcess,
        computeStructureFunctions( initialSubReport.getStructureFunctions(),
          flowController.getReportContext().getOutputProcessorMetaData() ), this.report.getObjectID() );
    } else {
      DataFactory dataFactory =
        dataFactoryManager.restore( functionStorageKey, isReportsShareConnections( initialSubReport ) );

      final DefaultFlowController postPreProcessingFlowController;
      final SubReport preDataSubReport;
      if ( dataFactory == null ) {
        final SubReportProcessPreprocessor preprocessor = new SubReportProcessPreprocessor( parentStateFlowController );
        preDataSubReport = preprocessor.invokePreDataProcessing( initialSubReport );
        postPreProcessingFlowController = preprocessor.getFlowController();

        final DataFactory subreportDf = lookupDataFactory( preDataSubReport );
        final boolean dataCacheEnabled = isCacheEnabled( preDataSubReport );
        if ( subreportDf == null ) {
          // subreport does not define a own factory, we reuse the parent's data-factory in the master-row.
          dataFactory = new EmptyDataFactory();
        } else {
          // subreport comes with an own factory, so open the gates ..
          final DataFactory sortingDataFactory = new SortingDataFactory( subreportDf, performanceMonitorContext );
          final CachingDataFactory cdataFactory = new CachingDataFactory( sortingDataFactory, dataCacheEnabled );
          final ProcessingContext context = postPreProcessingFlowController.getReportContext();
          cdataFactory.initialize( new ProcessingDataFactoryContext( context, cdataFactory ) );
          dataFactoryManager.store( functionStorageKey, cdataFactory, isReportsShareConnections( preDataSubReport ) );
          dataFactory = cdataFactory;
        }
      } else {
        preDataSubReport = initialSubReport;
        postPreProcessingFlowController = parentStateFlowController;
      }

      // And now initialize the sub-report.
      final ParameterMapping[] inputMappings = preDataSubReport.getInputMappings();
      final ParameterMapping[] exportMappings = preDataSubReport.getExportMappings();

      // eval query, query-limit and query-timeout
      this.flowController = postPreProcessingFlowController.performInitSubreport( dataFactory, inputMappings, resourceBundleFactory );
      final Integer queryLimitDefault = IntegerCache.getInteger( preDataSubReport.getQueryLimit() );
      final Integer queryTimeoutDefault = IntegerCache.getInteger( preDataSubReport.getQueryTimeout() );

      final Object queryRaw =
        evaluateExpression( preDataSubReport.getAttributeExpression( AttributeNames.Internal.NAMESPACE,
          AttributeNames.Internal.QUERY ), preDataSubReport.getQuery() );
      final String queryDefined = designtime ? "design-time-query" : preDataSubReport.getQuery();
      this.query = (String) ConverterRegistry.convert( queryRaw, String.class, queryDefined );

      if ( this.currentSubReportMarker.getSubreport().isQueryLimitInherited() && parentState.queryLimit != null ) {
        this.queryLimit = parentState.queryLimit;
      } else {
        final Object queryLimitRaw =
          evaluateExpression( preDataSubReport.getAttributeExpression( AttributeNames.Internal.NAMESPACE,
            AttributeNames.Internal.QUERY_LIMIT ), queryLimitDefault );
        this.queryLimit = (Integer) ConverterRegistry.convert( queryLimitRaw, Integer.class, queryLimitDefault );
      }

      final Object queryTimeoutRaw =
        evaluateExpression( preDataSubReport.getAttributeExpression( AttributeNames.Internal.NAMESPACE,
          AttributeNames.Internal.QUERY_TIMEOUT ), queryTimeoutDefault );
      this.queryTimeout = (Integer) ConverterRegistry.convert( queryTimeoutRaw, Integer.class, queryTimeoutDefault );

      final List<SortConstraint> sortOrder = lookupSortOrder( preDataSubReport );

      DefaultFlowController postQueryFlowController = flowController
        .performSubReportQuery( query, queryLimit, queryTimeout, exportMappings, sortOrder );
      final ProxyDataSchemaDefinition schemaDefinition =
        new ProxyDataSchemaDefinition( preDataSubReport.getDataSchemaDefinition(),
          postQueryFlowController.getMasterRow().getDataSchemaDefinition() );
      postQueryFlowController = postQueryFlowController.updateDataSchema( schemaDefinition );

      SubReport fullReport = preDataSubReport;
      DefaultFlowController fullFlowController = postQueryFlowController;
      if ( needPreProcessing ) {
        final SubReportProcessPreprocessor preprocessor = new SubReportProcessPreprocessor( postQueryFlowController );
        fullReport = preprocessor.invokePreProcessing( preDataSubReport );
        fullFlowController = preprocessor.getFlowController();
        subReportStorage.store( functionStorageKey, fullReport );
      }

      this.report =
        new ReportDefinitionImpl( fullReport, fullReport.getPageDefinition(), subreportFromMarker.getParentSection() );


      final Expression[] structureFunctions = getStructureFunctionStorage().restore( functionStorageKey );
      if ( structureFunctions != null ) {
        final StructureFunction[] functions = new StructureFunction[ structureFunctions.length ];
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy( structureFunctions, 0, functions, 0, structureFunctions.length );
        this.layoutProcess = new SubLayoutProcess( parentState.layoutProcess, functions, this.report.getObjectID() );
      } else {
        final StructureFunction[] functions = computeStructureFunctions( fullReport.getStructureFunctions(),
          fullFlowController.getReportContext().getOutputProcessorMetaData() );
        this.layoutProcess = new SubLayoutProcess( parentState.layoutProcess, functions, this.report.getObjectID() );
      }

      boolean preserve = true;
      Expression[] expressions = getFunctionStorage().restore( functionStorageKey );
      if ( expressions == null ) {
        // ok, it seems we have entered a new subreport ..
        // we use the expressions from the report itself ..
        if ( designtime ) {
          expressions = new Expression[ 0 ];
        } else {
          expressions = fullReport.getExpressions().getExpressions();
        }
        preserve = false;
      }

      this.flowController = fullFlowController.activateExpressions( expressions, preserve );
      this.flowController = this.flowController.refreshDataRow();

      // now a bunch of paranoid assertions, just in case I missed something.
      if ( this.report.getParentSection() == null ) {
        throw new InvalidReportStateException();
      }
      if ( this.report.getParentSection().getReportDefinition() != this.parentSubReportState.getReport() ) {
        throw new InvalidReportStateException();
      }
      final int processingLevel = flowController.getReportContext().getProcessingLevel();
      if ( processingLevel == LayoutProcess.LEVEL_PAGINATE ) {
        if ( this.parentSubReportState.isInItemGroup() ) {
          if ( this.parentSubReportState.getReport().getDetailsFooter().getComputedStyle() == null ) {
            throw new InvalidReportStateException();
          }
        }
      }
    }

    StateUtilities.computeLevels( this.flowController, this.layoutProcess, processLevels );
    this.processKey = createKey();
  }

  public ReportProcessStore getProcessStore() {
    return processStore;
  }

  private DataFactory lookupDataFactory( final AbstractReportDefinition report ) {
    if ( designtime ) {
      return new DesignTimeDataFactory();
    }
    return report.getDataFactory();
  }

  private void applyCurrentStyleAndAttributes( final SubReport subreportFromMarker, final SubReport report ) {
    // derive would regenerate instance-IDs, which is not advisable.
    report.getStyle().copyFrom( subreportFromMarker.getStyle() );
    report.copyAttributes( subreportFromMarker.getAttributes() );
  }

  private boolean isSubReportInvisible( final SubReport report,
                                        final DefaultFlowController flowController ) {
    final int processingLevel = flowController.getReportContext().getProcessingLevel();
    if ( processingLevel != LayoutProcess.LEVEL_PAGINATE ) {
      // outside
      return false;
    }

    if ( designtime ) {
      return false;
    }

    OutputProcessorMetaData metaData = flowController.getReportContext().getOutputProcessorMetaData();
    if ( metaData.isFeatureSupported( OutputProcessorFeature.DESIGNTIME ) ) {
      final Object attribute = report.getAttribute( AttributeNames.Designtime.NAMESPACE,
        AttributeNames.Designtime.HIDE_IN_LAYOUT_GUI_ATTRIBUTE );
      if ( Boolean.TRUE.equals( attribute ) ) {
        return true;
      }
      return false;
    } else {
      final StyleSheet computedStyle = report.getComputedStyle();
      if ( computedStyle.getBooleanStyleProperty( ElementStyleKeys.VISIBLE ) ) {
        return false;
      }
      return true;
    }
  }

  private static boolean isCacheEnabled( ReportDefinition reportDefinition ) {
    while ( reportDefinition != null ) {
      final Object dataCacheEnabledRaw =
        reportDefinition.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.DATA_CACHE );
      if ( Boolean.FALSE.equals( dataCacheEnabledRaw ) ) {
        return false;
      }
      final Section parentSection = reportDefinition.getParentSection();
      if ( parentSection == null ) {
        break;
      }
      reportDefinition = parentSection.getReportDefinition();
    }
    return true;
  }


  private Object evaluateExpression( final Expression expression, final Object defaultValue ) {
    if ( expression == null ) {
      return defaultValue;
    }
    if ( designtime ) {
      return defaultValue;
    }

    final Expression evalExpression = expression.getInstance();

    final InlineDataRowRuntime runtime = new InlineDataRowRuntime();
    runtime.setState( this );
    final ExpressionRuntime oldRuntime = evalExpression.getRuntime();
    try {
      evalExpression.setRuntime( runtime );
      return evalExpression.getValue();
    } catch ( final Exception e ) {
      logger.debug( "Failed to evaluate expression " + expression, e );
      return defaultValue;
    } finally {
      evalExpression.setRuntime( oldRuntime );
    }
  }

  public int[] getRequiredRuntimeLevels() {
    processLevels.add( IntegerCache.getInteger( LayoutProcess.LEVEL_PAGINATE ) );

    final int[] retval = new int[ processLevels.size() ];
    final Integer[] levels = processLevels.toArray( new Integer[ processLevels.size() ] );
    Arrays.sort( levels, new StateUtilities.DescendingComparator<Integer>() );
    for ( int i = 0; i < levels.length; i++ ) {
      final Integer level = levels[ i ];
      retval[ i ] = level.intValue();
    }

    return retval;
  }

  private StructureFunction[] computeStructureFunctions( final StructureFunction[] fromReport,
                                                         final OutputProcessorMetaData metaData ) {
    final ArrayList<StructureFunction> e = new ArrayList<StructureFunction>( Arrays.asList( fromReport ) );
    if ( structuralPreprocessingNeeded ) {
      e.add( new CrosstabProcessorFunction() );
    }
    if ( isDesigntime() == false ) {
      e.add( new AttributeExpressionsEvaluator() );
      e.add( new SheetNameFunction() );
      e.add( new StyleExpressionsEvaluator() );
      e.add( new CellFormatFunction() );
      e.add( new WizardItemHideFunction() );
    }

    e.add( new MetaDataStyleEvaluator() );
    e.add( new StyleResolvingEvaluator() );

    Collections.sort( e, new StructureFunctionComparator() );
    return e.toArray( new StructureFunction[ e.size() ] );
  }

  public boolean isSubReportExecutable() {
    final Expression expression =
      getReport().getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SUBREPORT_ACTIVE );
    if ( expression != null ) {
      // the master-report state will only be non-null for subreports.
      final InlineDataRowRuntime dataRowRuntime = new InlineDataRowRuntime();
      dataRowRuntime.setState( this );
      expression.setRuntime( dataRowRuntime );
      try {
        final Object value = expression.getValue();
        // the expression has to explicitly return false as a value to disable the report processing of
        // subreports. Just returning null or a non-boolean value is not enough. This is a safety measure
        // so that if in doubt we print more data than to little.
        if ( Boolean.FALSE.equals( value ) ) {
          return false;
        }
        if ( "false".equals( String.valueOf( value ) ) ) {
          return false;
        }
        return true;
      } finally {
        expression.setRuntime( null );
      }
    }
    return true;
  }

  public ProcessState returnFromSubReport( final LayoutProcess layoutProcess ) throws ReportProcessingException {
    final ProcessState state = deriveForAdvance();
    state.layoutProcess = (LayoutProcess) layoutProcess.clone();
    return state;
  }

  public ProcessState restart() throws ReportProcessingException {
    if ( getParentState() != null ) {
      throw new IllegalStateException( "Cannot reset a state that is a subreport state" );
    }

    final ProcessState state = this.deriveForStorage();
    state.crosstabColumnSequenceCounter.clear();
    state.groupSequenceCounter.clear();
    state.groupSequenceCounter.set( 0, -1 );
    state.recorder.reset();
    state.currentSubReport = -1;
    state.currentGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    state.currentPresentationGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    if ( state.groupStarts.isEmpty() == false ) {
      throw new IllegalStateException();
    }
    state.setAdvanceHandler( BeginReportHandler.HANDLER );

    final ReportStateKey parentStateKey;
    final ReportState parentState = this.getParentSubReportState();
    if ( parentState == null ) {
      parentStateKey = null;
    } else {
      parentStateKey = parentState.getProcessKey();
    }

    final CachingDataFactory dataFactory = state.dataFactoryManager
      .restore( FunctionStorageKey.createKey( parentStateKey, state.getReport() ),
        isReportsShareConnections( report ) );
    if ( dataFactory == null ) {
      throw new ReportProcessingException( "No data factory on restart()? Somewhere we went wrong." );
    }

    final DefaultFlowController fc = state.getFlowController();
    final DefaultFlowController cfc = fc.restart();
    final DefaultFlowController qfc =
      cfc.performQuery( dataFactory, query, queryLimit.intValue(), queryTimeout.intValue(),
        fc.getMasterRow().getResourceBundleFactory(), lookupSortOrder( state.report ) );
    final Expression[] expressions =
      getFunctionStorage().restore( FunctionStorageKey.createKey( null, state.getReport() ) );
    final DefaultFlowController efc = qfc.activateExpressions( expressions, true );
    state.setFlowController( efc );
    state.sequenceCounter += 1;
    state.processKey = state.createKey();
    return state;
  }

  public ReportProcessingErrorHandler getErrorHandler() {
    return errorHandler;
  }

  public void setErrorHandler( final ReportProcessingErrorHandler errorHandler ) {
    this.errorHandler = errorHandler;
  }

  public void setSequenceCounter( final int sequenceCounter ) {
    this.sequenceCounter = sequenceCounter;
    this.processKey = this.createKey();
  }

  public int getSequenceCounter() {
    return sequenceCounter;
  }

  public InlineSubreportMarker getCurrentSubReportMarker() {
    return currentSubReportMarker;
  }

  public boolean isInlineProcess() {
    return inlineProcess;
  }

  public SubReportProcessType getSubreportProcessingType() {
    InlineSubreportMarker cm = getCurrentSubReportMarker();
    if ( cm == null ) {
      return SubReportProcessType.BANDED;
    }
    return cm.getProcessType();
  }

  /**
   * This is a more expensive version of the ordinary derive. This method creates a separate copy of the layout-process
   * so that this operation is expensive in memory and CPU usage.
   *
   * @return the derived state.
   */
  public ProcessState deriveForPagebreak() {
    try {
      final ProcessState processState = clone();
      processState.flowController = flowController.derive();
      processState.report = report.clone();
      processState.layoutProcess = layoutProcess.deriveForPagebreak();
      return processState;
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException( "Clone failed but I dont know why .." );
    }
  }

  public ProcessState deriveForAdvance() {
    try {
      final ProcessState processState = clone();
      processState.sequenceCounter += 1;
      processState.processKey = processState.createKey();
      return processState;
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException( "Clone failed but I dont know why .." );
    }
  }

  public ProcessState deriveForStorage() {
    try {
      final ProcessState result = clone();
      result.flowController = flowController.derive();
      result.report = report.clone();
      result.layoutProcess = layoutProcess.deriveForStorage();
      return result;
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException( "Clone failed but I dont know why .." );
    }
  }

  public ProcessState clone() throws CloneNotSupportedException {
    final ProcessState result = (ProcessState) super.clone();
    result.groupSequenceCounter = (LongSequence) groupSequenceCounter.clone();
    result.crosstabColumnSequenceCounter = (LongSequence) crosstabColumnSequenceCounter.clone();
    result.groupStarts = groupStarts.clone();
    result.processKey = result.createKey();
    result.recorder = (GroupSizeRecorder) recorder.clone();
    return result;
  }

  public AdvanceHandler getAdvanceHandler() {
    return advanceHandler;
  }

  private ReportStateKey createKey() {
    final ProcessState parent = (ProcessState) getParentState();
    if ( parent != null ) {
      return new ReportStateKey( parent.createKey(),
        getCurrentRow(), getEventCode(),
        getCurrentGroupIndex(), getCurrentSubReport(),
        sequenceCounter, advanceHandler.isRestoreHandler(),
        isInlineProcess() );
    }

    return new ReportStateKey( null, getCurrentRow(),
      getEventCode(), getCurrentGroupIndex(), getCurrentSubReport(),
      sequenceCounter, advanceHandler.isRestoreHandler(), false );
  }

  public void setAdvanceHandler( final AdvanceHandler advanceHandler ) {
    if ( advanceHandler == null ) {
      throw new NullPointerException();
    }
    this.advanceHandler = advanceHandler;
    this.processKey = null;
  }

  public final ProcessState advance() throws ReportProcessingException {
    return advanceHandler.advance( this );
  }

  public final ProcessState commit() throws ReportProcessingException {
    final ProcessState commit = advanceHandler.commit( this );
    commit.processKey = commit.createKey();
    return commit;
  }

  public int getCurrentRow() {
    return this.flowController.getMasterRow().getCursor();
  }

  public int getCurrentDataItem() {
    return this.flowController.getMasterRow().getRawDataCursor();
  }

  public int getProgressLevel() {
    return flowController.getReportContext().getProgressLevel();
  }

  public int getProgressLevelCount() {
    return flowController.getReportContext().getProgressLevelCount();
  }

  public boolean isPrepareRun() {
    return flowController.getReportContext().isPrepareRun();
  }

  public int getLevel() {
    return flowController.getReportContext().getProcessingLevel();
  }

  public boolean isFinish() {
    return advanceHandler.isFinish();
  }

  public int getEventCode() {
    return advanceHandler.getEventCode();
  }

  public int getCurrentGroupIndex() {
    return currentGroupIndex;
  }

  public void enterGroup() {
    recorder.enterGroup();
    currentGroupIndex += 1;
    final Group group = report.getGroup( currentGroupIndex );
    groupStarts.push( new GroupStartRecord( getCurrentRow(), group.getName(), group.getGeneratedName() ) );
    groupSequenceCounter.increment( currentGroupIndex );
    groupSequenceCounter.set( currentGroupIndex + 1, 0 );

    if ( groupStarts.size() != currentGroupIndex + 1 ) {
      throw new IllegalStateException();
    }
  }

  public void leaveGroup() {
    recorder.leaveGroup();
    if ( groupStarts.size() != currentGroupIndex + 1 ) {
      throw new IllegalStateException();
    }

    currentGroupIndex -= 1;
    groupStarts.pop();
  }

  public int getPresentationGroupIndex() {
    return currentPresentationGroupIndex;
  }

  public void enterPresentationGroup() {
    currentPresentationGroupIndex += 1;
  }

  public void leavePresentationGroup() {
    currentPresentationGroupIndex -= 1;
  }

  public ReportDefinition getReport() {
    return report;
  }

  public int getCurrentSubReport() {
    return currentSubReport;
  }

  public ReportState getParentState() {
    if ( suspendedState != null ) {
      return suspendedState;
    }
    if ( parentSubReportState != null ) {
      return parentSubReportState;
    }
    return null;
  }

  public ReportState getParentSubReportState() {
    return parentSubReportState;
  }

  public FunctionStorage getStructureFunctionStorage() {
    return structureFunctionStorage;
  }

  public FunctionStorage getFunctionStorage() {
    return functionStorage;
  }

  public DefaultFlowController getFlowController() {
    return flowController;
  }

  public void setFlowController( final DefaultFlowController flowController ) {
    if ( flowController == null ) {
      throw new NullPointerException();
    }
    this.flowController = flowController;
    this.processKey = null;
  }

  public LayoutProcess getLayoutProcess() {
    return layoutProcess;
  }

  public ReportStateKey getProcessKey() {
    if ( processKey == null ) {
      processKey = createKey();
    }
    return processKey;
  }

  public DataRow getDataRow() {
    return flowController.getMasterRow().getGlobalView();
  }

  public int getNumberOfRows() {
    final MasterDataRow masterRow = flowController.getMasterRow();
    return masterRow.getRowCount();
  }

  /**
   * Fires a 'page-started' event.
   *
   * @param baseEvent the type of the base event which caused the page start to be triggered.
   */
  public void firePageStartedEvent( final int baseEvent ) {
    final ReportEvent event = new ReportEvent( this, ReportEvent.PAGE_STARTED | baseEvent );
    flowController = flowController.fireReportEvent( event );
    layoutProcess.fireReportEvent( event );
  }

  /**
   * Fires a '<code>page-finished</code>' event.  The <code>pageFinished(...)</code> method is called for every report
   * function.
   */
  public void firePageFinishedEvent( final boolean noParentPassing ) {
    final int eventCode = ReportEvent.PAGE_FINISHED | ( noParentPassing ? ReportEvent.NO_PARENT_PASSING_EVENT : 0 );
    final ReportEvent event = new ReportEvent( this, eventCode );
    flowController = flowController.fireReportEvent( event );
    layoutProcess.fireReportEvent( event );
  }

  protected void fireReportEvent() {
    final int eventCode = advanceHandler.getEventCode();
    if ( ( eventCode & ProcessState.ARTIFICIAL_EVENT_CODE ) == ProcessState.ARTIFICIAL_EVENT_CODE ) {
      throw new IllegalStateException( "Cannot fire artificial events." );
    }

    final ReportEvent event = new ReportEvent( this, eventCode );
    flowController = flowController.fireReportEvent( event );
    layoutProcess.fireReportEvent( event );
  }

  /**
   * Returns true if this is the last item in the group, and false otherwise. This checks the group condition and all
   * conditions of all subgroups.
   *
   * @param rootGroup      the root group that should be checked.
   * @param currentDataRow the current data row.
   * @param nextDataRow    the next data row, or null, if this is the last datarow.
   * @return A flag indicating whether or not the current item is the last in its group.
   */
  public static boolean isLastItemInGroup( final Group rootGroup,
                                           final MasterDataRow currentDataRow,
                                           final MasterDataRow nextDataRow ) {
    // return true if this is the last row in the model.
    if ( currentDataRow.isAdvanceable() == false || nextDataRow == null ) {
      return true;
    }

    final DataRow nextView = nextDataRow.getGlobalView();
    Group g = rootGroup;
    while ( g != null ) {
      if ( g.isGroupChange( nextView ) ) {
        return true;
      }

      // groups are never directly nested into each other. They always have a group-body between each group instance.
      final Section parentSection = g.getParentSection();
      if ( parentSection == null ) {
        return false;
      }

      final Section maybeGroup = parentSection.getParentSection();
      if ( maybeGroup instanceof Group ) {
        g = (Group) maybeGroup;
      } else {
        g = null;
      }
    }
    return false;
  }

  public boolean isSubReportEvent() {
    return getParentSubReportState() != null;
  }

  public InlineSubreportMarker[] getSubReports() {
    return subReports.clone();
  }

  public ProcessStateHandle getProcessHandle() {
    return processHandle;
  }

  public void setInItemGroup( final boolean inItemGroup ) {
    if ( inItemGroup ) {
      recorder.enterItems();
    } else {
      recorder.leaveItems();
    }
    this.inItemGroup = inItemGroup;
  }

  public boolean isInItemGroup() {
    return inItemGroup;
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return flowController.getMasterRow().getResourceBundleFactory();
  }

  public boolean isArtifcialState() {
    return ( advanceHandler.getEventCode() & ReportEvent.ARTIFICIAL_EVENT_CODE ) != 0;
  }

  public GroupingState createGroupingState() {
    return new DefaultGroupingState( currentGroupIndex, groupStarts.clone() );
  }

  private boolean isStructureRunNeeded( final Section section ) {
    final int count = section.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = section.getElement( i );
      final Object type = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE );
      if ( type instanceof ExternalElementType ) {
        return true;
      }

      if ( element instanceof CrosstabGroup ) {
        return true;
      } else if ( element instanceof SubReport ) {
        return true;
      } else if ( element instanceof RootLevelBand ) {
        final RootLevelBand band = (RootLevelBand) element;
        if ( band.getSubReportCount() > 0 ) {
          return true;
        }
        if ( isStructureRunNeeded( (Section) element ) ) {
          return true;
        }
      } else if ( element instanceof Section ) {
        if ( isStructureRunNeeded( (Section) element ) ) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isStructuralPreprocessingNeeded() {
    return structuralPreprocessingNeeded;
  }

  public void advanceCursor() {
    recorder.advanceItems();
  }

  public Integer getPredictedStateCount() {
    return recorder.getPredictedStateCount();
  }

  public boolean isCrosstabActive() {
    return flowController.isCrosstabActive();
  }

  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append( "ProcessState={" );
    b.append( "runLevel=" ).append( getLevel() );
    b.append( ", key=" ).append( getProcessKey() );
    b.append( "}" );
    return b.toString();
  }

  public ProcessState recordCrosstabRowState() {
    // record the flow controller and all expressions for a later temporary rollback.
    final ProcessState next = deriveForAdvance();
    next.flowController = flowController.recordCrosstabRowState();
    next.crosstabColumnSequenceCounter.clear();
    next.crosstabColumnSequenceCounter.fill( -1 );
    return next;
  }

  /**
   * Reset the state to use the stored flow-controller for the summary calculation.
   *
   * @return
   */
  public ProcessState replayStoredCrosstabRowState() {
    final ProcessState next = deriveForAdvance();
    next.replayStoredCrosstabGroup = currentGroupIndex + 1;
    next.suspendedState = this;
    next.flowController = flowController.replayStoredCrosstabRowState();
    next.processKey = next.createKey();
    //    DebugLog.log("ProcessState:replay " + processKey);
    return next;
  }

  public int getReplayStoredCrosstabGroup() {
    return replayStoredCrosstabGroup;
  }

  public AdvanceHandler getPostSummaryRowAdvanceHandler() {
    return postSummaryRowAdvanceHandler;
  }

  public void setPostSummaryRowAdvanceHandler( final AdvanceHandler postSummaryRowAdvanceHandler ) {
    this.postSummaryRowAdvanceHandler = postSummaryRowAdvanceHandler;
  }

  public ProcessState finishReplayingStoredCrosstabRowState() throws ReportProcessingException {
    final ProcessState next = this.suspendedState.deriveForAdvance();
    next.layoutProcess = (LayoutProcess) this.layoutProcess.clone();
    next.flowController = flowController.derive();
    next.advanceHandler = this.advanceHandler;
    next.flowController.getMasterRow().validateReplayFinished();
    //    DebugLog.log("ProcessState:finish-replay " + processKey);
    return next;
  }

  public void clearStoredCrosstabRowState() {
    this.flowController = flowController.clearRecordedCrosstabRowState();
  }

  public long getGroupSequenceCounter( final int groupIndex ) {
    return groupSequenceCounter.get( groupIndex );
  }

  public long getCrosstabColumnSequenceCounter( final int groupIndex ) {
    return crosstabColumnSequenceCounter.get( groupIndex );
  }

  public void crosstabResetColumnIndices() {
    crosstabColumnSequenceCounter.clear();
    crosstabColumnSequenceCounter.fill( -1 );
  }

  public void crosstabIncrementColumnCounter() {
    crosstabColumnSequenceCounter.increment( currentGroupIndex );
  }

  public PerformanceMonitorContext getPerformanceMonitorContext() {
    return performanceMonitorContext;
  }

  Integer getQueryLimit() {
    return queryLimit;
  }
}
