/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: support-scheduler
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.schedule.domain;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.edgexfoundry.schedule.test.data.SimpleScheduleData;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresNone.class})
public class SimpleScheduleTest {

  private SimpleSchedule schedule;

  @Before
  public void setup() {
    schedule = SimpleScheduleData.newTestInstance();
  }

  @Test
  public void testGetSize() {
    assertEquals("Size not reporting as expected", 1, schedule.getSize().intValue());
  }

  @Test
  public void testGetSizeWithSplitName() {
    schedule.setName("foo,bar");
    assertEquals("Size not reporting as expected", 2, schedule.getSize().intValue());
  }

  @Test
  public void testGetSizeWithNullName() {
    schedule.setName(null);
    assertEquals("Size not reporting as expected", 0, schedule.getSize().intValue());
  }

  @Test
  public void testGetSizeWithCommaOnly() {
    schedule.setName(",");
    assertEquals("Size not reporting as expected", 0, schedule.getSize().intValue());
  }

  @Test
  public void testGetStart() {
    String[] starts = new String[] {SimpleScheduleData.TEST_START};
    assertArrayEquals("Start not reporting as expected", starts, schedule.getStart());
  }

  @Test
  public void testGetStartWithNull() {
    String[] starts = new String[1];
    schedule.setStart(null);
    assertArrayEquals("Start not reporting as expected", starts, schedule.getStart());
  }

  @Test
  public void testGetStartWithSplit() {
    String[] starts = new String[] {SimpleScheduleData.TEST_START, SimpleScheduleData.TEST_END};
    schedule.setStart(SimpleScheduleData.TEST_START + ',' + SimpleScheduleData.TEST_END);
    assertArrayEquals("Start not reporting as expected", starts, schedule.getStart());
  }

  @Test
  public void testGetEnd() {
    String[] ends = new String[] {SimpleScheduleData.TEST_END};
    assertArrayEquals("End not reporting as expected", ends, schedule.getEnd());
  }

  @Test
  public void testGetEndWithNull() {
    String[] ends = new String[1];
    schedule.setEnd(null);
    assertArrayEquals("End not reporting as expected", ends, schedule.getEnd());
  }

  @Test
  public void testGetEndWithSplit() {
    String[] ends = new String[] {SimpleScheduleData.TEST_END, SimpleScheduleData.TEST_START};
    schedule.setEnd(SimpleScheduleData.TEST_END + ',' + SimpleScheduleData.TEST_START);
    assertArrayEquals("End not reporting as expected", ends, schedule.getEnd());
  }

  @Test
  public void testGetFrequency() {
    String[] freqs = new String[] {SimpleScheduleData.TEST_FREQ};
    assertArrayEquals("Frequency not reporting as expected", freqs, schedule.getFrequency());
  }

  @Test
  public void testGetFrequencyWithNull() {
    String[] freqs = new String[1];
    schedule.setFrequency(null);
    assertArrayEquals("Frequency not reporting as expected", freqs, schedule.getFrequency());
  }

  @Test
  public void testGetFrequencyWithSplit() {
    String[] freqs = new String[] {SimpleScheduleData.TEST_FREQ, "PT1M"};
    schedule.setFrequency(SimpleScheduleData.TEST_FREQ + ',' + "PT1M");
    assertArrayEquals("Frequency not reporting as expected", freqs, schedule.getFrequency());
  }

  @Test
  public void testGetCron() {
    String[] crons = new String[] {SimpleScheduleData.TEST_CRON};
    assertArrayEquals("Crons not reporting as expected", crons, schedule.getCron());
  }

  @Test
  public void testGetCronWithNull() {
    String[] crons = new String[1];
    schedule.setCron(null);
    assertArrayEquals("Crons not reporting as expected", crons, schedule.getCron());
  }

  @Test
  public void testGetCronWithSplit() {
    String[] crons = new String[] {SimpleScheduleData.TEST_CRON, "0 0 1 * *"};
    schedule.setCron(SimpleScheduleData.TEST_CRON + ',' + "0 0 1 * *");
    assertArrayEquals("Crons not reporting as expected", crons, schedule.getCron());
  }

  @Test
  public void testGetRunOnce() {
    String[] runOnces = new String[] {SimpleScheduleData.TEST_RUN_ONCE};
    assertArrayEquals("Run once not reporting as expected", runOnces, schedule.getRunOnce());
  }

  @Test
  public void testGetRunOnceWithNull() {
    String[] runOnces = new String[1];
    schedule.setRunOnce(null);
    assertArrayEquals("Run once not reporting as expected", runOnces, schedule.getRunOnce());
  }

  @Test
  public void testGetRunOnceWithSplit() {
    String[] runOnces = new String[] {SimpleScheduleData.TEST_RUN_ONCE, "false"};
    schedule.setRunOnce(SimpleScheduleData.TEST_RUN_ONCE + ',' + "false");
    assertArrayEquals("Run once not reporting as expected", runOnces, schedule.getRunOnce());
  }

}
