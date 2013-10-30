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
* Copyright (c) 2006 - 2013 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula;

import java.util.ArrayList;
import java.util.HashSet;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;
import org.pentaho.reporting.libraries.base.util.DebugLog;

public class OpenFormulaConformance extends TestCase
{
  public static final String SMALL_GROUP = "ABS; ACOS; AND;"
      + "ASIN; ATAN; ATAN2; AVERAGE; CHOOSE; COLUMNS; COS; COUNT; COUNTA;"
      + "COUNTBLANK; COUNTIF; DATE; DAVERAGE; DAY; DCOUNT; DCOUNTA; DDB;"
      + "DEGREES; DGET; DMAX; DMIN; DPRODUCT; DSTDEV; DSTDEVP; DSUM; DVAR;"
      + "DVARP; EVEN; EXACT; EXP; FACT; FALSE; FIND; FV; HLOOKUP; HOUR; IF; INDEX;"
      + "INT; IRR; ISBLANK; ISERR; ISERROR; ISLOGICAL; ISNA; ISNONTEXT; ISNUMBER;"
      + "ISTEXT; LEFT; LEN; LN; LOG; LOG10; LOWER; MATCH; MAX; MID; MIN; MINUTE;"
      + "MOD; MONTH; N; NA; NOT; NOW; NPER; NPV; ODD; OR; PI; PMT; POWER;"
      + "PRODUCT; PROPER; PV; RADIANS; RATE; REPLACE; REPT; RIGHT; ROUND;"
      + "ROWS; SECOND; SIN; SLN; SQRT; STDEV; STDEVP; SUBSTITUTE; SUM; SUMIF;"
      + "SYD; T; TAN; TIME; TODAY; TRIM; TRUE; TRUNC; UPPER; VALUE; VAR; VARP;"
      + "VLOOKUP; WEEKDAY; YEAR";

  public static final String MEDIUM_GROUP = SMALL_GROUP
      + "ACCRINT; ACCRINTM; ACOSH; ACOT; ACOTH; ADDRESS; ASINH; ATANH; AVEDEV;"
      + "BESSELI; BESSELJ; BESSELK; BESSELY; BETADIST; BETAINV; BINOMDIST; CEILING;"
      + "CHAR; CHIDIST; CHIINV; CHITEST; CLEAN; CODE; COLUMN; COMBIN; CON CATENATE;"
      + "CONFIDENCE; CONVERT; CORREL; COSH; COT; COTH; COUPDAYBS; COUPDAYS;"
      + "COUPDAYSNC; COUPNCD; COUPNUM; COUPPCD; COVAR; CRITBINOM; CUMIPMT;"
      + "CUMPRINC; DATEVALUE; DAYS360; DB; DEVSQ; DISC; DOLLARDE; DOLLARFR;"
      + "DURATION; EOMONTH; ERF; ERFC; EXPONDIST; FDIST; FINV; FISHER; FISHERINV; FIXED;"
      + "FLOOR; FORECAST; FTEST; GAMMADIST; GAMMAINV; GAMMALN; GCD; GEOMEAN;"
      + "HARMEAN; HYPGEOMDIST; INTERCEPT; INTRATE; ISEVEN; ISODD; KURT; LARGE; LCM;"
      + "LINEST; LOGINV; LOGNORMDIST; LOOKUP; MDURATION; MEDIAN; MINVERSE; MIRR;"
      + "MMULT; MODE; MROUND; MULTINOMIAL; NEGBINOMDIST; NETWORKDAYS; NOMINAL;"
      + "NORMDIST; NORMINV; NORMSDIST; NORMSINV; ODDFPRICE; ODDFYIELD; ODDLPRICE;"
      + "ODDLYIELD; OFFSET; PEARSON; PERCENTILE; PERCENTRANK; PERMUT; POISSON;"
      + "PRICE; PRICEMAT; PROB; QUARTILE; QUOTIENT; RAND; RANDBETWEEN; RANK;"
      + "RECEIVED; ROMAN; ROUNDDOWN; ROUNDUP; ROW; RSQ; SERIESSUM; SIGN; SINH;"
      + "SKEW; SLOPE; SMALL; SQRTPI; STANDARDIZE; STDEVPA; STEYX; SUBTOTAL;"
      + "SUMPRODUCT; SUMSQ; SUMX2MY2; SUMX2PY2; SUMXMY2; TANH; TBILLEQ; TBILLPRICE;"
      + "TBILLYIELD; TDIST; TIMEVALUE; TINV; TRANSPOSE; TREND; TRIMMEAN; TTEST; TYPE;"
      + "VARA; VDB; WEEKNUM; WEIBULL; WORKDAY; XIRR; XNPV; YEARFRAC; YIELD;"
      + "YIELDDISC; YIELDMAT; ZTEST";

  public static final String LARGE_GROUP = MEDIUM_GROUP
      + "AMORDEGRC; AMORLINC; ARABIC; AREAS; ASC; AVERAGEA; B; BAHTTEXT; BASE;"
      + "BIN2DEC; BIN2HEX; BIN2OCT; BITAND; BITLSHIFT; BITOR; BITRSHIFT; BITXORCEILING;"
      + "COMBINA; COMPLEX; CURRENT; DATEDIF; DAYS; DBSC; DDE; DEC2BIN; DEC2HEX;"
      + "DEC2OCT; DECIMAL; DELTA; EDATE; EFFECT; EFFECTIVE; ERROR.TYPE; FACTDOUBLE;"
      + "FINDB; FORMULA; FREQUENCY; FVSCHEDULE; GAMMA; GAUSS; GESTEP;"
      + "GETPIVOTDATA; GROWTH; HEX2BIN; HEX2DEC; HEX2OCT; HYPERLINK; HYPGEOMVERT;"
      + "IMABS; IMAGINARY; IMARGUMENT; IMCONJUGATE; IMCOS; IMDIV; IMEXP; IMLN;"
      + "IMLOG10; IMLOG2; IMPOWER; IMPRODUCT; IMREAL; IMSIN; IMSQRT; IMSUB; IMSUM;"
      + "INDIRECT; INFO; IPMT; ISFORMULA; ISPMT; ISREF; LEFTB; LENB; MAXA; MDETERM;"
      + "MUNIT; MIDB; MINA; MNORMSINV; NUMBERSTRING; OCT2BIN; OCT2DEC; OCT2HEX;"
      + "PERMUTATIONA; PHI; PHONETIC; PPMT; PRICEDISC; REPLACEB; RIGHTB; RRI; RTD;"
      + "SEARCH; SEARCHB; SHEET; SHEETS; TEXT; VARPA; XOR";

  private FormulaContext context;
  private HashSet implementedFunctions;

  public OpenFormulaConformance()
  {
  }

  public OpenFormulaConformance(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    context = new TestFormulaContext();
    LibFormulaBoot.getInstance().start();

    implementedFunctions = new HashSet();
    final String[] functionNames = context.getFunctionRegistry().getFunctionNames();

    for (int i = 0; i < functionNames.length; i++)
    {
      implementedFunctions.add(functionNames[i]);
    }
  }

  public void testIsInSmallGroup()
  {
    isInGroup("SmallGroup", SMALL_GROUP);
  }

  private void isInGroup(final String groupName, final String groupFunctions)
  {
    final ArrayList functionsNotInGroup = new ArrayList();
    final String[] split = groupFunctions.split(";");
    for (int i = 0; i < split.length; i++)
    {
      final String func = split[i].trim();

      if (!implementedFunctions.contains(func))
      {
        functionsNotInGroup.add(func);
      }
    }

    DebugLog.log ("The following "
        + functionsNotInGroup.size() + " (out of " + split.length
        + " requested) functions are not yet implemented for the " + groupName
        + " conformance: " + functionsNotInGroup.toString());
  }
}
