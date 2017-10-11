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

package org.pentaho.reporting.engine.classic.demo.ancient.demo;

import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.demo.ClassicEngineDemoBoot;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.bookstore.BookstoreDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.cards.CardDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.chartdemo.ChartDemos;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.conditionalgroup.ConditionalGroupDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.fonts.FontDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.form.SimplePatientFormDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.functions.FunctionsDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.groups.GroupsDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.groups.LogEventDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.groups.RowbandingDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.groups.TrafficLightingDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.huge.VeryLargeReportDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.internationalisation.I18nDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.InvoiceDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.largetext.LGPLTextDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts.LayoutDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.multireport.MultiReportDemoCollection;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.onetomany.PeopleReportDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.opensource.OpenSourceDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.sportscouncil.SportsCouncilDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.subreport.SubReportDemoCollection;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.surveyscale.SurveyScaleDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.swingicons.SwingIconsDemo;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.world.WorldDemo;
import org.pentaho.reporting.engine.classic.demo.elements.sbarcodes.SimpleBarcodesXMLDemo;
import org.pentaho.reporting.engine.classic.demo.elements.sparklines.SparklineXMLDemo;
import org.pentaho.reporting.engine.classic.demo.features.datasource.SQLDataSourceDemo;
import org.pentaho.reporting.engine.classic.demo.features.interactivity.InteractiveSwingDemo;
import org.pentaho.reporting.engine.classic.demo.features.loading.FileLoadingDemo;
import org.pentaho.reporting.engine.classic.demo.features.parameters.ParameterDemo;
import org.pentaho.reporting.engine.classic.demo.features.subreport.SQLSubReportDemo;
import org.pentaho.reporting.engine.classic.demo.util.CompoundDemoFrame;
import org.pentaho.reporting.engine.classic.demo.util.DefaultDemoSelector;
import org.pentaho.reporting.engine.classic.demo.util.DemoSelector;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class DemoFrontend extends CompoundDemoFrame
{
  private JComponent infoPane;

  public DemoFrontend(final DemoSelector demoSelector)
  {
    super(demoSelector);
    setIgnoreEmbeddedConfig(true);
    final ModifiableConfiguration editableConfig =
        ClassicEngineDemoBoot.getInstance().getEditableConfig();
    editableConfig.setConfigProperty(EMBEDDED_KEY, "true");
    init();
  }

  public static DemoSelector createDemoInfo()
  {
    final DefaultDemoSelector rootSelector = new DefaultDemoSelector
        ("All JFreeReport Demos");

    // the most important demos first: the ones that get you started
    rootSelector.addDemo(new HelloWorld());
    rootSelector.addDemo(new GroupsDemo());
    rootSelector.addDemo(new LogEventDemo());
    rootSelector.addDemo(new SwingIconsDemo());
    rootSelector.addDemo(new RowbandingDemo());
    rootSelector.addDemo(new TrafficLightingDemo());
    //
    rootSelector.addChild(OpenSourceDemo.createDemoInfo());
    rootSelector.addChild(WorldDemo.createDemoInfo());
    rootSelector.addChild(InvoiceDemo.createDemoInfo());
    rootSelector.addChild(PeopleReportDemo.createDemoInfo());
    rootSelector.addChild(SurveyScaleDemo.createDemoInfo());
    rootSelector.addChild(FunctionsDemo.createDemoInfo());
    rootSelector.addChild(LayoutDemo.createDemoInfo());
    rootSelector.addChild(CardDemo.createDemoInfo());
    rootSelector.addChild(MultiReportDemoCollection.createDemoInfo());
    rootSelector.addChild(SubReportDemoCollection.createDemoInfo());
    rootSelector.addChild(ChartDemos.createDemoInfo());

    rootSelector.addDemo(new ConditionalGroupDemo());
    rootSelector.addDemo(new SimplePatientFormDemo());
    rootSelector.addDemo(new SportsCouncilDemo());
    rootSelector.addDemo(new LGPLTextDemo());
    rootSelector.addDemo(new I18nDemo());
    rootSelector.addDemo(new VeryLargeReportDemo());
    rootSelector.addDemo(new BookstoreDemo());
    rootSelector.addDemo(new FontDemo());

    rootSelector.addDemo(new SQLSubReportDemo());
    rootSelector.addDemo(new SQLDataSourceDemo());
    rootSelector.addDemo(new InteractiveSwingDemo());
    rootSelector.addDemo(new ParameterDemo());
    rootSelector.addDemo(new SparklineXMLDemo());
    rootSelector.addDemo(new SimpleBarcodesXMLDemo());
    rootSelector.addDemo(new FileLoadingDemo());
    return rootSelector;
  }

  protected JComponent getNoHandlerInfoPane()
  {
    if (infoPane == null)
    {
      final URL url = ObjectUtilities.getResource
          ("org/pentaho/reporting/engine/classic/demo/demo-introduction.html", CompoundDemoFrame.class);

      infoPane = createDescriptionTextPane(url);
    }
    return infoPane;
  }

  public static void main(final String[] args)
  {
    ClassicEngineDemoBoot.getInstance().start();

    final DemoFrontend frontend = new DemoFrontend(createDemoInfo());
    frontend.pack();
    LibSwingUtil.centerFrameOnScreen(frontend);
    frontend.setVisible(true);
  }
}
