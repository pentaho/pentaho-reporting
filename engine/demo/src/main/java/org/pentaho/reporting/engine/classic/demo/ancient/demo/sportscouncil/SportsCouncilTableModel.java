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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.sportscouncil;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class SportsCouncilTableModel extends AbstractTableModel
{
  private static final String[] COLUMN_NAMES = {
      "recordID", "orgID",
      "council.orgName", "council.internalWebsite", "council.orgEmail",
      "council.street1", "council.street2", "council.city", "council.state",
      "council.zip", "council.phoneNumber", "council.extension", "council.faxNumber",
      "council.yearEventCount", "council.thisMonthEventCount",
      "council.lastMonthEventCount", "council.futureEventCount",
      "leader.firstName", "leader.lastName", "leader.position",
      "leader.leadershipPhoneNumber", "leader.email", "org.name",
      "org.email", "org.maleGenderCount", "org.femaleGenderCount"};

  private static final int COUNCIL_COLUMNS = 17;

  private static final Class[] COLUMN_TYPES = {
      String.class, String.class,
      String.class, String.class, String.class,
      String.class, String.class, String.class, String.class,
      String.class, String.class, String.class, String.class,
      Number.class, Number.class,
      Number.class, Number.class,
      String.class, String.class, String.class,
      String.class, String.class, String.class,
      String.class, Number.class, Number.class};

  private ArrayList columns;

  public SportsCouncilTableModel()
  {
    this.columns = new ArrayList();
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount()
  {
    return COLUMN_NAMES.length;
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display.  This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount()
  {
    return columns.size();
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param  rowIndex  the row whose value is to be queried
   * @param  columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt(final int rowIndex, final int columnIndex)
  {
    final Object[] rowData = (Object[]) columns.get(rowIndex);
    return rowData[columnIndex];
  }

  /**
   * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
   *
   * @param columnIndex the column being queried
   * @return the Object.class
   */
  public Class getColumnClass(final int columnIndex)
  {
    return COLUMN_TYPES[columnIndex];
  }

  /**
   * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc.  If
   * <code>column</code> cannot be found, returns an empty string.
   *
   * @param column the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName(final int column)
  {
    return COLUMN_NAMES[column];
  }

  public void copyInto(final Organization org)
  {
    final Object[] councilData = new Object[COUNCIL_COLUMNS];
    final CouncilRecord councilRecord = org.getCouncil();
    councilData[0] = councilRecord.getRecordType();
    councilData[1] = councilRecord.getOrgID();
    councilData[2] = councilRecord.getOrgName();
    councilData[3] = councilRecord.getInternalWebsite();
    councilData[4] = councilRecord.getOrgEmail();
    councilData[5] = councilRecord.getStreet1();
    councilData[6] = councilRecord.getStreet2();
    councilData[7] = councilRecord.getCity();
    councilData[8] = councilRecord.getState();
    councilData[9] = councilRecord.getZip();
    councilData[10] = councilRecord.getPhoneNumber();
    councilData[11] = councilRecord.getExtension();
    councilData[12] = councilRecord.getFaxNumber();
    councilData[13] = new Integer(councilRecord.getYearEventCount());
    councilData[14] = new Integer(councilRecord.getThisMonthEventCount());
    councilData[15] = new Integer(councilRecord.getLastMonthEventCount());
    councilData[16] = new Integer(councilRecord.getFutureEventCount());

    for (int i = 0; i < org.getLeaderCount(); i++)
    {
      final Object[] rowData = new Object[COLUMN_NAMES.length];
      System.arraycopy(councilData, 0, rowData, 0, councilData.length);
      final LeaderRecord leaderRecord = org.getLeader(i);
      rowData[0] = leaderRecord.getRecordType();
      rowData[17] = leaderRecord.getFirstName();
      rowData[18] = leaderRecord.getLastName();
      rowData[19] = leaderRecord.getPosition();
      rowData[20] = leaderRecord.getLeadershipPhoneNumber();
      rowData[21] = leaderRecord.getEmail();
      columns.add(rowData);
    }

    for (int i = 0; i < org.getSubOrganzationsCount(); i++)
    {
      final Object[] rowData = new Object[COLUMN_NAMES.length];
      System.arraycopy(councilData, 0, rowData, 0, councilData.length);
      final SubOrganizationRecord subOrganizationRecord = org.getSubOrganization(i);
      rowData[0] = subOrganizationRecord.getRecordType();
      rowData[22] = subOrganizationRecord.getName();
      rowData[23] = subOrganizationRecord.getEmail();
      rowData[24] = new Integer(subOrganizationRecord.getMaleGenderCount());
      rowData[25] = new Integer(subOrganizationRecord.getFemaleGenderCount());
      columns.add(rowData);
    }
  }

  public static SportsCouncilTableModel createDefaultModel()
  {
    final CouncilRecord uuCouncilRecord = new CouncilRecord
        ("1", "Unseen University Sports Council",
            "http://www.unseen-university.edu", "sportscouncil@unseen-university.edu",
            "Alberto Malich Plaza 1", "", "Ankh-Mopork", "AM", "88888",
            "(01 33) 5 85 38 56 36", "123", "(01 33) 5 85 38 99 99", 400, 35, 79, 111);
    final Organization uuOrg = new Organization(uuCouncilRecord);
    uuOrg.addLeader(new LeaderRecord
        ("1", "Mustrum", "Ridcully", "Archchancellor", "(01 33) 5 85 38 00 08",
            "big.boss@unseen-university.edu"));
    uuOrg.addLeader(new LeaderRecord
        ("1", "Dr.", "Dinwiddie", "Bursar", "(01 33) 5 85 38 53 21",
            "bursar@unseen-university.edu"));
    uuOrg.addLeader(new LeaderRecord
        ("1", "Windle", "Poons", "Wizzard", "(01 33) 5 85 38 12 36",
            "poons@unseen-university.edu"));

    final SubOrganizationRecord sailors =
        new SubOrganizationRecord("1", "Sailing Club",
            "sailing@unseen-university.edu", 24, 28);
    uuOrg.addSubOrganization(sailors);
    final SubOrganizationRecord daemonRiders =
        new SubOrganizationRecord("1", "Dungeon Dimension Explorers Club",
            "dungeons@unseen-university.edu", 44, 15);
    uuOrg.addSubOrganization(daemonRiders);
    final SubOrganizationRecord dragonBreeders =
        new SubOrganizationRecord("1", "Dragon Breeders Club",
            "dragons@unseen-university.edu", 9, 29);
    uuOrg.addSubOrganization(dragonBreeders);

    // Assasins guild record ..
    final CouncilRecord agCouncilRecord = new CouncilRecord
        ("1", "Assasins Guild Council",
            "http://www.assassins-guild.com", "info@assassins-guild.edu",
            "Grand Plaza 2a", "", "Ankh-Mopork", "AM", "88213",
            "(01 33) 6 66 55 53 36", "0", "(01 33) 6 66 66 53 39", 200, 15, 49, 31);
    final Organization org = new Organization(agCouncilRecord);
    org.addLeader(new LeaderRecord
        ("1", "Dr. MD", "Downey", "President", "(01 33) 6 66 55 53 37",
            "big.boss@assassins-guild.edu"));
    org.addLeader(new LeaderRecord
        ("1", "Zlorf", "Flannelfoot", "Vice-President", "(01 33) 6 66 55 53 32",
            "flannelfoot@assassins-guild.edu"));
    org.addLeader(new LeaderRecord
        ("1", "Jonathan", "Teatime", "Assassin", "(01 33) 6 66 55 53 34",
            "teatime@assassins-guild.edu"));

    final SubOrganizationRecord fenching =
        new SubOrganizationRecord("1", "Fencing Club",
            "fencing@assassins-guild.edu", 23, 22);
    org.addSubOrganization(fenching);
    final SubOrganizationRecord camouflage =
        new SubOrganizationRecord("1", "Mask Chamber",
            "camouflage@assassins-guild.edu", 21, 23);
    org.addSubOrganization(camouflage);
    final SubOrganizationRecord rangeWeapons =
        new SubOrganizationRecord("1", "Archer's Society",
            "bowmen@assassins-guild.edu", 13, 22);
    org.addSubOrganization(rangeWeapons);

    final SportsCouncilTableModel tableModel = new SportsCouncilTableModel();
    tableModel.copyInto(org);
    tableModel.copyInto(uuOrg);
    return tableModel;
  }
}
