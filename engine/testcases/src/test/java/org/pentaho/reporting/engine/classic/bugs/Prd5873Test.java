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


package org.pentaho.reporting.engine.classic.bugs;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfContentReaderTool;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

/**
 * @author Thomas Morgner
 */
public class Prd5873Test {

    @Before
    public void setUp() throws Exception {
        ClassicEngineBoot.getInstance().start();
    }

    @Test
    public void validateWithUnderline()  throws ResourceException, ReportProcessingException, IOException {
        final URL resource = getClass().getResource("Prd-5873.prpt");
        final MasterReport report = (MasterReport)
                new ResourceManager().createDirectly(resource, MasterReport.class).getResource();
        report.getPageFooter().getElement(0).getStyle().setBooleanStyleProperty(TextStyleKeys.UNDERLINED, false);
        runTest(report);
    }

    @Test
    public void validateCorrectness()
            throws ResourceException, ReportProcessingException, IOException {
        final URL resource = getClass().getResource("Prd-5873.prpt");
        final MasterReport report = (MasterReport)
                new ResourceManager().createDirectly(resource, MasterReport.class).getResource();
        runTest(report);
    }

    private void runTest(MasterReport report)
            throws ResourceException, ReportProcessingException, IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PdfReportUtil.createPDF(report, bout);

        PdfReader reader = new PdfReader(bout.toByteArray());

        printPdfPage(reader);

        final PdfDictionary pageN = reader.getPageN(1);
        final PdfDictionary asDict = pageN.getAsDict(PdfName.RESOURCES);
        final byte[] pageContent = reader.getPageContent(1);
    }

    private void printPdfPage(PdfReader reader) throws IOException {
        final StringWriter out = new StringWriter();
        PdfContentReaderTool.listContentStreamForPage(reader, 1, new PrintWriter(out));
        System.out.println(out);
    }

}
