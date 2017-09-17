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

import org.edgexfoundry.schedule.domain.SimpleSchedule;

public interface SimpleScheduleData {

  static final String TEST_NAME = "midnight";
  static final String TEST_START = "20170101T000000";
  static final String TEST_END = "20171212T000000";
  static final String TEST_CRON = "0 0 12 * * ?";
  static final String TEST_FREQ = "P1D";
  static final String TEST_RUN_ONCE = "true";

  static SimpleSchedule newTestInstance() {
    SimpleSchedule schedule = new SimpleSchedule();
    schedule.setName(TEST_NAME);
    schedule.setCron(TEST_CRON);
    schedule.setStart(TEST_START);
    schedule.setEnd(TEST_END);
    schedule.setFrequency(TEST_FREQ);
    schedule.setRunOnce(TEST_RUN_ONCE);
    return schedule;
  }

}
