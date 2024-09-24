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

package org.pentaho.reporting.engine.classic.extensions.drilldown;

/**
 * @author Thomas Morgner.
 */
public class SugarDrillDownFunctionTest extends FormulaTestBase {
  public SugarDrillDownFunctionTest() {
  }

  protected Object[][] createDataTest() {
    return new Object[][] {
      { "DRILLDOWN(\"local-sugar\"; 0; {\"::pentaho-path\" ; \"/public/steel-wheels/test.prpt\" | \"test\" ; "
        + "\"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "http://localhost:8080/pentaho/api/repos/:public:steel-wheels:test"
          + ".prpt/viewer?test=value&mtest=v1&mtest=v2&mtest=v3" },
      { "DRILLDOWN(\"remote-sugar\"; \"ssh://domain.example\"; {\"::pentaho-path\" ; \"/public/steel-wheels/test"
        + ".prpt\" | \"test\" ; \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "ssh://domain.example/api/repos/:public:steel-wheels:test.prpt/viewer?test=value&mtest=v1&mtest=v2&mtest=v3" },
      { "DRILLDOWN(\"remote-sugar\"; \"ssh://domain.example/\"; {\"::pentaho-path\" ; \"/public/steel-wheels/test"
        + ".prpt\" | \"test\" ; \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "ssh://domain.example/api/repos/:public:steel-wheels:test.prpt/viewer?test=value&mtest=v1&mtest=v2&mtest=v3" },
      { "DRILLDOWN(\"local-sugar-no-parameter\"; \"ssh://domain.example\"; {\"::pentaho-path\" ; "
        + "\"/public/steel-wheels/test.prpt\" | \"test\" ; \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "http://localhost:8080/pentaho/api/repos/:public:steel-wheels:test"
          + ".prpt/viewer?showParameters=false&test=value&mtest=v1&mtest=v2&mtest=v3" },
      { "DRILLDOWN(\"remote-sugar-no-parameter\"; \"ssh://domain.example\"; {\"::pentaho-path\" ; "
        + "\"/public/steel-wheels/test.prpt\" | \"test\" ; \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "ssh://domain.example/api/repos/:public:steel-wheels:test"
          + ".prpt/viewer?showParameters=false&test=value&mtest=v1&mtest=v2&mtest=v3" },
      { "DRILLDOWN(\"remote-sugar-no-parameter\"; \"ssh://domain.example/\"; {\"::pentaho-path\" ; "
        + "\"/public/steel-wheels/test.prpt\" | \"test\" ; \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "ssh://domain.example/api/repos/:public:steel-wheels:test"
          + ".prpt/viewer?showParameters=false&test=value&mtest=v1&mtest=v2&mtest=v3" },
      { "DRILLDOWN(\"remote-sugar-prpti\"; \"http://domain.example/\"; {\"::pentaho-path\" ; "
        + "\"/public/steel-wheels/test.prpt\" | \"test\" ; \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "http://domain.example/api/repos/:public:steel-wheels:test.prpt/prpti"
          + ".view?test=value&mtest=v1&mtest=v2&mtest=v3" },
      { "DRILLDOWN(\"local-sugar-prpti\"; 0; {\"::pentaho-path\" ; \"/public/steel-wheels/test.prpt\" | \"test\" ; "
        + "\"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "http://localhost:8080/pentaho/api/repos/:public:steel-wheels:test.prpt/prpti"
          + ".view?test=value&mtest=v1&mtest=v2&mtest=v3" },
      { "DRILLDOWN(\"local-sugar-analyzer\"; 0; {\"::pentaho-path\" ; \"/public/steel-wheels/test.prpt\" | \"test\" ;"
        + " \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "http://localhost:8080/pentaho/api/repos/:public:steel-wheels:test"
          + ".prpt/viewer?test=value&mtest=v1&mtest=v2&mtest=v3" },
      { "DRILLDOWN(\"local-sugar-xaction\"; 0; {\"::pentaho-path\" ; \"/public/steel-wheels/test.prpt\" | \"test\" ; "
        + "\"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "http://localhost:8080/pentaho/api/repos/:public:steel-wheels:test"
          + ".prpt/generatedContent?test=value&mtest=v1&mtest=v2&mtest=v3" },
      { "DRILLDOWN(\"remote-sugar-analyzer\"; \"http://domain.example/\"; {\"::pentaho-path\" ; "
        + "\"/public/steel-wheels/test.prpt\" | \"test\" ; \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "http://domain.example/api/repos/:public:steel-wheels:test.prpt/viewer?test=value&mtest=v1&mtest=v2&mtest=v3" },
      { "DRILLDOWN(\"remote-sugar-xaction\"; \"http://domain.example/\"; {\"::pentaho-path\" ; "
        + "\"/public/steel-wheels/test.prpt\" | \"test\" ; \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
        "http://domain.example/api/repos/:public:steel-wheels:test"
          + ".prpt/generatedContent?test=value&mtest=v1&mtest=v2&mtest=v3" },
    };

  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

}
