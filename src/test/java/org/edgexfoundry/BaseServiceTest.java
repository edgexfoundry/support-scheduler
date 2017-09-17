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

package org.edgexfoundry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.DeviceServiceClient;
import org.edgexfoundry.controller.ScheduleClient;
import org.edgexfoundry.controller.ScheduleEventClient;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.DeviceService;
import org.edgexfoundry.schedule.domain.SimpleSchedule;
import org.edgexfoundry.schedule.domain.SimpleScheduleEvent;
import org.edgexfoundry.schedule.test.data.SimpleScheduleData;
import org.edgexfoundry.schedule.test.data.SimpleScheduleEventData;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.AddressableData;
import org.edgexfoundry.test.data.ServiceData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@Category({RequiresNone.class})
public class BaseServiceTest {

  private static final String TEST_SERVICE_NAME = "edgex-support-scheduler-test";
  private static final String TEST_HOST = "localhost";
  private static final int TEST_PORT = 48085;
  private static final String[] TEST_LABELS = {};
  private static final String TEST_CALLBACK_URL = "/v1/callbacks";
  private static final int TEST_RETRIES = 3;
  private static final int TEST_INTERVAL = 500;

  @InjectMocks
  private BaseService baseService;

  @Mock
  private DeviceServiceClient deviceServiceClient;

  @Mock
  private AddressableClient addressableClient;

  @Mock
  private DeviceServiceClient serviceClient;

  @Mock
  private ScheduleEventClient scheduleEventClient;

  @Mock
  private ScheduleClient scheduleClient;

  @Mock
  private SimpleSchedule defaultSchedules;

  @Mock
  private SimpleScheduleEvent defaultScheduleEvents;

  @Before
  public void setup() throws IllegalAccessException {
    MockitoAnnotations.initMocks(this);
    FieldUtils.writeField(baseService, "serviceName", TEST_SERVICE_NAME, true);
    FieldUtils.writeField(baseService, "host", TEST_HOST, true);
    FieldUtils.writeField(baseService, "port", TEST_PORT, true);
    FieldUtils.writeField(baseService, "labels", TEST_LABELS, true);
    FieldUtils.writeField(baseService, "callbackUrl", TEST_CALLBACK_URL, true);
    FieldUtils.writeField(baseService, "initRetries", TEST_RETRIES, true);
    FieldUtils.writeField(baseService, "initInterval", TEST_INTERVAL, true);
  }

  @Test
  public void testGetService() {
    Mockito.when(deviceServiceClient.deviceServiceForName(TEST_SERVICE_NAME))
        .thenReturn(ServiceData.newTestInstance());
    assertNotNull("Service not retunred as expected", baseService.getService());
  }

  @Test
  public void testGetServiceNotFound() {
    Mockito.when(deviceServiceClient.deviceServiceForName(TEST_SERVICE_NAME))
        .thenThrow(new NotFoundException());
    assertNotNull("Service not retunred as expected", baseService.getService());
  }

  @Test
  public void testGetServiceAddressableNotFound() {
    Mockito.when(deviceServiceClient.deviceServiceForName(TEST_SERVICE_NAME))
        .thenThrow(new NotFoundException());
    Mockito.when(addressableClient.addressableForName(TEST_SERVICE_NAME))
        .thenThrow(new NotFoundException());
    assertNotNull("Service not retunred as expected", baseService.getService());
  }

  @Test
  public void testGetServiceException() {
    Mockito.when(deviceServiceClient.deviceServiceForName(TEST_SERVICE_NAME))
        .thenThrow(new RuntimeException());
    assertNull("Service not retunred as expected", baseService.getService());
  }

  @Test
  public void testAddDefaultSchedules() throws IllegalAccessException {
    SimpleSchedule schedule = SimpleScheduleData.newTestInstance();
    FieldUtils.writeField(baseService, "defaultSchedules", schedule, true);
    baseService.addDefaultSchedules();
  }

  @Test
  public void testAddDefaultScheduleEvents() throws IllegalAccessException {
    SimpleScheduleEvent event = SimpleScheduleEventData.newTestInstance();
    FieldUtils.writeField(baseService, "defaultScheduleEvents", event, true);
    baseService.addDefaultScheduleEvents();
  }

  @Test
  public void testAddDefaultScheduleEventsWithExistingAddressable() throws IllegalAccessException {
    Addressable addressable = AddressableData.newTestInstance();
    DeviceService service = ServiceData.newTestInstance();
    service.setAddressable(addressable);
    SimpleScheduleEvent event = SimpleScheduleEventData.newTestInstance();
    FieldUtils.writeField(baseService, "defaultScheduleEvents", event, true);
    Mockito.when(serviceClient.deviceServiceForName(event.getService()[0])).thenReturn(service);
    baseService.addDefaultScheduleEvents();
  }

  @Test
  public void testAttemptToInitialize() throws InterruptedException {
    Mockito.when(deviceServiceClient.deviceServiceForName(TEST_SERVICE_NAME))
        .thenReturn(ServiceData.newTestInstance());
    baseService.attemptToInitialize();
  }

  @Test
  public void testAttemptToInitializeNullService() throws InterruptedException {
    baseService.setInitialized(true);
    baseService.setRegistered(true);
    baseService.attemptToInitialize();
  }

  @Test(expected = RuntimeException.class)
  public void testAttemptToInitializeAndFail() throws InterruptedException {
    baseService.attemptToInitialize();
  }

}
