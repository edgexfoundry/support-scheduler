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

package org.edgexfoundry.scheduling;

import static org.junit.Assert.assertTrue;

import org.edgexfoundry.domain.meta.Schedule;
import org.edgexfoundry.domain.meta.ScheduleEvent;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ScheduleData;
import org.edgexfoundry.test.data.ScheduleEventData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@Category({RequiresNone.class})
public class ScheduleEventHTTPExecutorTest {

  @InjectMocks
  private ScheduleEventHTTPExecutor executor;


  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testExcecute() {
    Schedule s = ScheduleData.newTestInstance();
    ScheduleContext sc = new ScheduleContext(s);
    ScheduleEvent se = ScheduleEventData.newTestInstance();
    assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
    executor.execute(se);
  }
  
  @Test
  public void testExcecuteWithNullParams() {
    Schedule s = ScheduleData.newTestInstance();
    ScheduleContext sc = new ScheduleContext(s);
    ScheduleEvent se = ScheduleEventData.newTestInstance();
    se.setParameters(null);
    assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
    executor.execute(se);
  }
  
  @Test
  public void testExcecuteWithNullAddressable() {
    Schedule s = ScheduleData.newTestInstance();
    ScheduleContext sc = new ScheduleContext(s);
    ScheduleEvent se = ScheduleEventData.newTestInstance();
    se.setAddressable(null);
    assertTrue("schedule event count should be zero.", sc.getScheduleEvents().size() == 0);
    executor.execute(se);
  }

//  @Test
//  public void testExcecuteWithNull() {
//    Map<String, ScheduleEvent> events = null;
//    executor.execute(events);
//  }

}
