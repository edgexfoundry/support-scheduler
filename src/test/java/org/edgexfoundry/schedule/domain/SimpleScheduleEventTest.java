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

import static org.junit.Assert.*;

import org.edgexfoundry.schedule.test.data.SimpleScheduleEventData;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresNone.class})
public class SimpleScheduleEventTest {

  private SimpleScheduleEvent event;

  @Before
  public void setup() {
    event = SimpleScheduleEventData.newTestInstance();
  }

  @Test
  public void testGetSize() {
    assertEquals("Size not as expected", 2, event.getSize().intValue());
  }

  @Test
  public void testGetSizeWithNull() {
    event.setName(null);
    assertEquals("Size not as expected", 0, event.getSize().intValue());
  }

  @Test
  public void testGetSchedule() {
    assertEquals("Schedule not reporting as expected", 2, event.getSchedule().length);
  }

  @Test
  public void testGetScheduleWithNull() {
    String[] empty = new String[2];
    event.setSchedule(null);
    assertArrayEquals("Schedule not reporting as expected", empty, event.getSchedule());
  }

  @Test
  public void testGetParams() {
    assertEquals("Parameters not reporting as expected", 2, event.getParameters().length);
  }

  @Test
  public void testGetParamsWithNull() {
    String[] empty = new String[2];
    event.setParameters(null);
    assertArrayEquals("Parameters not reporting as expected", empty, event.getParameters());
  }

  @Test
  public void testGetService() {
    assertEquals("Service not reporting as expected", 2, event.getService().length);
  }

  @Test
  public void testGetServiceWithNull() {
    String[] empty = new String[2];
    event.setService(null);
    assertArrayEquals("Service not reporting as expected", empty, event.getService());
  }

  @Test
  public void testGetPath() {
    assertEquals("Path not reporting as expected", 2, event.getPath().length);
  }

  @Test
  public void testGetPathWithNull() {
    String[] empty = new String[2];
    event.setPath(null);
    assertArrayEquals("Path not reporting as expected", empty, event.getPath());
  }

  @Test
  public void testGetScheduler() {
    assertEquals("Scheduler not reporting as expected", 2, event.getScheduler().length);
  }

  @Test
  public void testGetSchedulerWithNull() {
    String[] service = event.getService();
    event.setScheduler(null);
    assertArrayEquals("Scheduler not reporting as expected", service, event.getService());
  }

}
