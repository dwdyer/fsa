// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   © Copyright 2000-2008 Daniel W. Dyer
//
//   This program is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published by
//   the Free Software Foundation, either version 3 of the License, or
//   (at your option) any later version.
//
//   This program is distributed in the hope that it will be useful,
//   but WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//   GNU General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with this program.  If not, see <http://www.gnu.org/licenses/>.
// ============================================================================
package net.footballpredictions.footballstats.model;

import org.testng.annotations.Test;
import java.util.Date;

/**
 * Unit test for the {@link FormRecord} class.
 * @author Daniel Dyer
 */
public class FormRecordTest
{
    private static final int ONE_DAY = 86400000;

    @Test
    public void testMaximumFormStars()
    {
        FormRecord record = new FormRecord(new Team("Fulham"), 3, 1, 6);
        Date today = new Date();
        record.addResult(new Result("Fulham", "Hull City", 1, 0, 0, today));
        record.addResult(new Result("Fulham", "Everton", 1, 0, 0, new Date(today.getTime() + ONE_DAY)));
        record.addResult(new Result("Fulham", "Chelsea", 1, 0, 0, new Date(today.getTime() + (ONE_DAY * 2))));
        record.addResult(new Result("Fulham", "Aston Villa", 1, 0, 0, new Date(today.getTime() + (ONE_DAY * 3))));
        record.addResult(new Result("Fulham", "Wigan Athletic", 1, 0, 0, new Date(today.getTime() + (ONE_DAY * 4))));
        record.addResult(new Result("Fulham", "Stoke City", 1, 0, 0, new Date(today.getTime() + (ONE_DAY * 5))));

        int stars = record.getFormStars();
        assert stars == 5 : "Perfect form should be rated 5 stars, not " + stars;
    }


    @Test
    public void testMinimumFormStars()
    {
        FormRecord record = new FormRecord(new Team("Fulham"), 3, 1, 6);
        Date today = new Date();
        record.addResult(new Result("Fulham", "Hull City", 0, 1, 0, today));
        record.addResult(new Result("Fulham", "Everton", 0, 1, 0, new Date(today.getTime() + ONE_DAY)));
        record.addResult(new Result("Fulham", "Chelsea", 0, 1, 0, new Date(today.getTime() + (ONE_DAY * 2))));
        record.addResult(new Result("Fulham", "Aston Villa", 0, 1, 0, new Date(today.getTime() + (ONE_DAY * 3))));
        record.addResult(new Result("Fulham", "Wigan Athletic", 0, 1, 0, new Date(today.getTime() + (ONE_DAY * 4))));
        record.addResult(new Result("Fulham", "Stoke City", 0, 1, 0, new Date(today.getTime() + (ONE_DAY * 5))));

        int stars = record.getFormStars();
        assert stars == 1 : "Abysmal form should be rated 1 star, not " + stars;
    }
}
