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

package org.edgexfoundry.schedule.test.data;

import org.edgexfoundry.schedule.domain.SimpleScheduleEvent;

public interface SimpleScheduleEventData {

  static final String TEST_NAME = "scrub-pushed-events,scrub-aged-events";
  static final String TEST_METHOD = "DELETE,DELETE";
  static final String TEST_SERVICE = "core-data,core-data";
  static final String TEST_PATH = "/api/v1/event/scrub,/api/v1/event/removeold/age/604800000";
  static final String TEST_SCHEDULE = "midnight,midnight";
  static final String TEST_SCHEDULER = "support-scheduler,support-scheduler";
  static final String TEST_PARMS = "param1,param2";

  static SimpleScheduleEvent newTestInstance() {
    SimpleScheduleEvent event = new SimpleScheduleEvent();
    event.setMethod(TEST_METHOD);
    event.setName(TEST_NAME);
    event.setPath(TEST_PATH);
    event.setSchedule(TEST_SCHEDULE);
    event.setScheduler(TEST_SCHEDULER);
    event.setService(TEST_SERVICE);
    event.setParameters(TEST_PARMS);
    return event;
  }

}
