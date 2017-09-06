/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @microservice:  support-scheduler
 * @author: Marc Hammons, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.scheduling;

import static org.edgexfoundry.test.data.ScheduleData.*;
import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.edgexfoundry.domain.meta.Schedule;
import org.edgexfoundry.domain.meta.ScheduleEvent;
import org.edgexfoundry.scheduling.ScheduleContext;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ScheduleData;
import org.edgexfoundry.test.data.ScheduleEventData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RequiresNone.class)
public class ScheduleContextTest {

	private boolean print = false;

	@Before
	public void createTestData() {
	}

	@After
	public void cleanup() {
	}

	// IS EQUAL
	@Test
	public void testIsEqualTrue() {
		Schedule s1 = ScheduleData.newTestInstance();
		s1.setId(TEST_SCHEDULE_ID1);
		ScheduleContext sc1 = new ScheduleContext(s1);
		Schedule s2 = ScheduleData.newTestInstance();
		s2.setId(TEST_SCHEDULE_ID1);
		ScheduleContext sc2 = new ScheduleContext(s2);
		assertTrue("schedule contexts are not equal.", sc1.equals(sc2));
	}

	@Test
	public void testIsEqualFalse() {
		Schedule s1 = ScheduleData.newTestInstance();
		s1.setId(TEST_SCHEDULE_ID1);
		ScheduleContext sc1 = new ScheduleContext(s1);
		Schedule s2 = ScheduleData.newTestInstance();
		s2.setId(TEST_SCHEDULE_ID2);
		ScheduleContext sc2 = new ScheduleContext(s2);
		assertTrue("schedule contexts are equal.", !sc1.equals(sc2));
	}

	// GET NAME
	@Test
	public void testGetScheduleName() {
		Schedule s1 = ScheduleData.newTestInstance();
		ScheduleContext sc1 = new ScheduleContext(s1);
		assertTrue("schedule context name is incorrect.", sc1.getName() == TEST_SCHEDULE_NAME);
	}

	// GET STARTIME
	@Test
	public void testGetStartTime() {
		Schedule s1 = ScheduleData.newTestInstance();
		s1.setStart(ScheduleData.TEST_TIME_2015);
		s1.setFrequency(TEST_FREQUENCY_1M); // speed up next time computation
		ScheduleContext sc1 = new ScheduleContext(s1);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Schedule.DATETIME_FORMATS[0])
				.withZone(ZoneId.systemDefault());
		ZonedDateTime zdt = ZonedDateTime.parse(ScheduleData.TEST_TIME_2015, dtf);
		assertTrue("schedule context starttime is incorrect.", zdt.isEqual(sc1.getStartTime()));
	}

	// EMPTY STARTTIME
	@Test
	public void testEmptyStartTime() {
		Schedule s = ScheduleData.newTestInstance();
		// TODO: there's a race condition here that might cause this test to
		// fail
		ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now().withNano(0), ZoneId.systemDefault());
		s.setStart("");
		ScheduleContext sc = new ScheduleContext(s);
		if (print) {
			System.out.println("schedule " + s);
			System.out.println("schedule context " + sc);
		}
		assertTrue("start time should be now. " + sc.toString(),
				sc.getStartTime().compareTo(zdt) == 0 && !sc.isComplete());
	}

	// GET ENDTIME
	@Test
	public void testGetEndTime() {
		Schedule s1 = ScheduleData.newTestInstance();
		s1.setEnd(ScheduleData.TEST_TIME_2015);
		s1.setFrequency(TEST_FREQUENCY_1M); // speed up next time computation
		ScheduleContext sc1 = new ScheduleContext(s1);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Schedule.DATETIME_FORMATS[0])
				.withZone(ZoneId.systemDefault());
		ZonedDateTime zdt = ZonedDateTime.parse(ScheduleData.TEST_TIME_2015, dtf);
		assertTrue("schedule context endtime is incorrect. Is " + sc1.getEndTime() + " should be " + zdt,
				zdt.equals(sc1.getEndTime()));
	}

	// EMPTY ENDTIME
	@Test
	public void testEmptyEndTime() {
		Schedule s = ScheduleData.newTestInstance();
		ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.MAX.withNano(0), ZoneId.systemDefault());
		s.setEnd("");
		ScheduleContext sc = new ScheduleContext(s);
		if (print) {
			System.out.println("zdt " + zdt);
			System.out.println("schedule " + s);
			System.out.println("schedule context " + sc);
		}
		assertTrue("end time should be max. " + sc.getEndTime(),
				sc.getEndTime().compareTo(zdt) == 0 && !sc.isComplete());
	}

	// GET NEXTTIME - P1D
	@Test
	public void testFrequencyP1D() {
		Schedule s = ScheduleData.newTestInstance();
		s.setStart(ScheduleData.TEST_TIME_2015);
		s.setEnd("");
		s.setFrequency(TEST_FREQUENCY_1D);
		ScheduleContext sc = new ScheduleContext(s);
		ZonedDateTime zdt = ZonedDateTime.now();
		zdt = zdt.withHour(0).withMinute(0).withSecond(0).withNano(0);
		zdt = zdt.plusDays(1);
		if (print) {
			System.out.println("schedule " + s);
			System.out.println("schedule context " + sc);
		}
		assertTrue("next time does not match", sc.getNextTime().compareTo(zdt) == 0 && !sc.isComplete());
	}

	// GET NEXTTIME - P1H
	@Test
	public void testFrequencyPT1H() {
		Schedule s = ScheduleData.newTestInstance();
		s.setStart(ScheduleData.TEST_TIME_2015);
		s.setEnd("");
		s.setFrequency(TEST_FREQUENCY_1H);
		ScheduleContext sc = new ScheduleContext(s);
		ZonedDateTime zdt = ZonedDateTime.now();
		zdt = zdt.withMinute(0).withSecond(0).withNano(0);
		zdt = zdt.plusHours(1);
		if (print) {
			System.out.println("schedule " + s);
			System.out.println("schedule context " + sc);
		}
		assertTrue("next time does not match", sc.getNextTime().compareTo(zdt) == 0 && !sc.isComplete());
	}

	// GET NEXTTIME - PT1D1H
	@Test
	public void testFrequencyPT1D1H() {
		Schedule s = ScheduleData.newTestInstance();
		s.setStart(ScheduleData.TEST_TIME_2015);
		s.setEnd("");
		s.setFrequency(TEST_FREQUENCY_1D1H);
		ScheduleContext sc = new ScheduleContext(s);
		ZonedDateTime zdt1 = ZonedDateTime.now();
		zdt1 = zdt1.withHour(0).withMinute(0).withSecond(0).withNano(0);
		ZonedDateTime zdt2 = zdt1;
		// should be within 2 days
		zdt2 = zdt2.plusDays(2);
		if (print) {
			System.out.println("schedule " + s);
			System.out.println("schedule context " + sc);
		}
		assertTrue("next time does not match",
				sc.getNextTime().compareTo(zdt1) > 0 && sc.getNextTime().compareTo(zdt2) <= 0 && !sc.isComplete());
	}

	// IS COMPLETE
	@Test
	public void testIsCompleteTrue() {
		Schedule s = ScheduleData.newTestInstance();
		s.setStart(ScheduleData.TEST_TIME_2015);
		s.setRunOnce(true);
		ScheduleContext sc = new ScheduleContext(s);
		if (print) {
			System.out.println("schedule " + s);
			System.out.println("schedule context " + sc);
		}
		assertTrue("schedule is not complete but should be.", sc.isComplete() == true);
	}

	@Test
	public void testIsCompleteFalse() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Schedule.DATETIME_FORMATS[0])
				.withZone(ZoneId.systemDefault());
		String st = formatter.format(Instant.now().plusSeconds(86400));
		Schedule s = ScheduleData.newTestInstance();
		s.setStart(st);
		s.setEnd("");
		s.setRunOnce(true);
		ScheduleContext sc = new ScheduleContext(s);
		if (print) {
			System.out.println("schedule " + s);
			System.out.println("schedule context " + sc);
		}
		assertTrue("schedule is complete but should not be.", sc.isComplete() == false);
	}

	// GET ITERATIONS
	@Test
	public void testGetIterationsZero() {
		Schedule s = ScheduleData.newTestInstance();
		ScheduleContext sc = new ScheduleContext(s);
		assertTrue("schedule iterations should be zero.", sc.getIterations() == 0);
	}

	@Test
	public void testGetIterationsOne() {
		Schedule s = ScheduleData.newTestInstance();
		ScheduleContext sc = new ScheduleContext(s);
		sc.updateIterations();
		assertTrue("schedule iterations should be one.", sc.getIterations() == 1);
	}

	@Test
	public void testGetIterationsNoUpdate() {
		Schedule s = ScheduleData.newTestInstance();
		s.setStart(ScheduleData.TEST_TIME_2015);
		s.setRunOnce(true);
		ScheduleContext sc = new ScheduleContext(s);
		sc.updateIterations();
		assertTrue("schedule iterations should be zero (schedule completed).", sc.getIterations() == 0);
	}

	// GET MAXITERATIONS
	@Test
	public void testGetMaxIterationsZero() {
		Schedule s = ScheduleData.newTestInstance();
		ScheduleContext sc = new ScheduleContext(s);
		assertTrue("max iterations should be zero.", sc.getMaxIterations() == 0);
	}

	@Test
	public void testGetMaxIterationsOne() {
		Schedule s = ScheduleData.newTestInstance();
		s.setRunOnce(true);
		ScheduleContext sc = new ScheduleContext(s);
		assertTrue("max iterations should be one.", sc.getMaxIterations() == 1);
	}

	// ADD SCHEDULE EVENT
	@Test
	public void testAddOneScheduleEvent() {
		Schedule s = ScheduleData.newTestInstance();
		ScheduleContext sc = new ScheduleContext(s);
		ScheduleEvent se = ScheduleEventData.newTestInstance();
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
		sc.addScheduleEvent(se);
		assertTrue("schedule event count should be one.", sc.getScheduleEvents().size() == 1);
		ScheduleEventData.checkTestData(sc.getScheduleEvents().get(se.getId()), se.getId());
	}

	// GET SCHEDULE EVENTS
	// EMPTY
	@Test
	public void testGetEmptyScheduleEvents() {
		Schedule s = ScheduleData.newTestInstance();
		ScheduleContext sc = new ScheduleContext(s);
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
	}

	// NON-EMPTY
	@Test
	public void testGetNonEmptyScheduleEvents() {
		Schedule s = ScheduleData.newTestInstance();
		ScheduleContext sc = new ScheduleContext(s);
		ScheduleEvent se = ScheduleEventData.newTestInstance();
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
		sc.addScheduleEvent(se);
		assertTrue("schedule event count should be one.", sc.getScheduleEvents().size() == 1);
	}

	// UPDATE SCHEDULE EVENT
	// Present
	@Test
	public void testUpdateExistentScheduleEvent() {
		Schedule s = ScheduleData.newTestInstance();
		ScheduleContext sc = new ScheduleContext(s);
		ScheduleEvent se = ScheduleEventData.newTestInstance();
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
		sc.addScheduleEvent(se);
		assertTrue("schedule event count should be one.", sc.getScheduleEvents().size() == 1);
		se.setName(TEST_SCHEDULE_NAME_NONE);
		assertTrue("schedule event did not update.", sc.updateScheduleEvent(se) == true);
		assertTrue("schedule event name should be '" + TEST_SCHEDULE_NAME_NONE + "'",
				sc.getScheduleEvents().get(se.getId()).getName() == TEST_SCHEDULE_NAME_NONE);
	}

	@Test
	public void testUpdateNonExistentScheduleEvent() {
		Schedule s = ScheduleData.newTestInstance();
		ScheduleContext sc = new ScheduleContext(s);
		ScheduleEvent se = ScheduleEventData.newTestInstance();
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
		se.setName(TEST_SCHEDULE_NAME_NONE);
		// This should throw 404 eventually
		assertTrue("scheudle event was found", sc.updateScheduleEvent(se) == false);
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
	}

	// REMOVE SCHEDULE EVENT
	@Test
	public void testRemoveExistentScheduleEvent() {
		Schedule s = ScheduleData.newTestInstance();
		ScheduleContext sc = new ScheduleContext(s);
		ScheduleEvent se = ScheduleEventData.newTestInstance();
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
		sc.addScheduleEvent(se);
		assertTrue("schedule event count should be one.", sc.getScheduleEvents().size() == 1);
		assertTrue("schedule event not found", sc.removeScheduleEventById(se.getId()) == true);
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
	}

	@Test
	public void testRemoveNonExistentScheduleEvent() {
		Schedule s = ScheduleData.newTestInstance();
		ScheduleContext sc = new ScheduleContext(s);
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
		assertTrue("schedule event found", sc.removeScheduleEventById("yaddayadda") == false);
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
	}

	// RESET
	@Test
	public void testReset() {
		Schedule s1 = ScheduleData.newTestInstance();
		Schedule s2 = ScheduleData.newTestInstance();
		ScheduleContext sc = new ScheduleContext(s1);
		ScheduleEvent se = ScheduleEventData.newTestInstance();
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
		sc.addScheduleEvent(se);
		assertTrue("schedule event count should be one.", sc.getScheduleEvents().size() == 1);
		ScheduleEventData.checkTestData(sc.getScheduleEvents().get(se.getId()), se.getId());
		s2.setEnd(TEST_TIME_2015);
		s2.setName("yaddayadda");
		sc.reset(s2);
		assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Schedule.DATETIME_FORMATS[0])
				.withZone(ZoneId.systemDefault());
		ZonedDateTime zdt = ZonedDateTime.parse(ScheduleData.TEST_TIME_2015, dtf);
		assertTrue("schedule context starttime is incorrect.", zdt.isEqual(sc.getEndTime()));
		assertTrue("schedule context name is incorrect.", sc.getName() == s2.getName());
	}

}
