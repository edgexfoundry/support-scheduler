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
package org.edgexfoundry; 
import javax.annotation.PostConstruct;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;

import org.apache.log4j.Logger;
import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.DeviceServiceClient;
import org.edgexfoundry.controller.ScheduleClient;
import org.edgexfoundry.controller.ScheduleEventClient;
import org.edgexfoundry.domain.common.HTTPMethod;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.AdminState;
import org.edgexfoundry.domain.meta.DeviceService;
import org.edgexfoundry.domain.meta.OperatingState;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.domain.meta.Schedule;
import org.edgexfoundry.domain.meta.ScheduleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

public class BaseService {
	
	private final static Logger logger = Logger.getLogger(BaseService.class);
	
	// service name
	@Value("${service.name}")
	private String serviceName;

	// service Address Info
	@Value("${service.host}")
	private String host;

	@Value("${service.port}")
	private int port;

	// service labels
	@Value("${service.labels}")
	private String[] labels;

	// service callback root
	@Value("${service.callback}")
	private String callbackUrl;

	// really a just a service, needs rework
	private DeviceService service;

	// really a base service client, needs rework
	@Autowired
	private DeviceServiceClient deviceServiceClient;
	
	@Autowired
	private AddressableClient addressableClient;

	@Autowired
	private DeviceServiceClient serviceClient;
	
	// Client to fetch schedule events
	@Autowired
	private ScheduleEventClient scheduleEventClient;

	// Client to fetch schedules
	@Autowired
	private ScheduleClient scheduleClient;

	// any schedules default to this service
	@Autowired
	private SimpleSchedule defaultSchedules;

	// any schedule events default to this service
	@Autowired
	private SimpleScheduleEvent defaultScheduleEvents;
	
	// service initialization 
	@Value("${service.connect.retries}")
	private int initRetries;

	@Value("${service.connect.interval}")
	private long initInterval;

	// track initialization attempts
	private int initAttempts;

	// track initialization success
	private boolean initialized;

	// track registration success
	private boolean registered;
	
	
	public BaseService() {
		setInitAttempts(0);
		setInitialized(false);
	}

	final public int getInitAttempts() {
		return initAttempts;
	}

	final public void setInitAttempts(int initAttempts) {
		this.initAttempts = initAttempts;
	}

	final public int getInitRetries() {
		return initRetries;
	}

	final public void setInitRetries(int initRetries) {
		this.initRetries = initRetries;
	}

	final public long getInitInterval() {
		return initInterval;
	}

	final public void setInitInterval(long initInterval) {
		this.initInterval = initInterval;
	}

	final public boolean isInitialized() {
		return initialized;
	}

	final public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	final public boolean isRegistered() {
		return registered;
	}

	final public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	// The base implementation always succeeds, derived classes customize
	public boolean initialize() {
		return true;
	}
	
	@PostConstruct
	final private void postConstructInitialize() {
		logger.debug("post construction initialization");
		attemptToInitialize();
	}

	@Async
	final public void attemptToInitialize() {
		
		// count the attempt
		setInitAttempts(getInitAttempts() + 1);
		logger.debug("initialization attempt " + getInitAttempts());

		// first - get the service information or register service with metadata
		if(getService() != null) {
			// if we were able to get the service data we're registered
			setRegistered(true);
			// second - add any default schedules before calling initialize()
			addDefaultSchedules();
			addDefaultScheduleEvents();

			//  third - invoke any custom initialization method 
			setInitialized(initialize());
		}

		// if both are successful, then we're done
		if(isRegistered() && isInitialized()) {
			logger.info("initialization successful.");
		} else {
			// otherwise see if we need to keep going
			if((getInitRetries() == 0) || (getInitAttempts() < getInitRetries())) {
				logger.debug("initialization unsuccessful. sleeping " + getInitInterval());
				try {
					Thread.sleep(getInitInterval());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// start up the next thread
				attemptToInitialize();

			} else {
				// here, we've failed and run out of retries, so just be done.
				logger.info("initialization unsuccessful after " + getInitAttempts() + " attempts.  Giving up.");
				// what do we do here? exit?
				Application.exit(-1);
			}
		} 
	}
	
	final public String getHost() {
		return host;
	}

	final public void setHost(String host) {
		this.host = host;
	}

	final public int getPort() {
		return port;
	}

	final public void setPort(int port) {
		this.port = port;
	}

	final public String[] getLabels() {
		return labels;
	}

	final public void setLabels(String[] labels) {
		this.labels = labels;
	}

	final public String getCallbackUrl() {
		return callbackUrl;
	}

	final public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	final public String getServiceName() {
		return serviceName;
	}

	final public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public DeviceService getService() {
		if(service == null) {
			try {
				service = deviceServiceClient.deviceServiceForName(serviceName);
			} catch (NotFoundException nfe) {
				setService();
				if(service == null) {
					logger.info("failed to create service " + serviceName + " in metadata");
				}
			} catch (Exception e) {
				logger.error("unable to establish connection to metadata" + e.getCause() + " " + e.getMessage());
				service = null;
			}
			if(service != null) {
				logger.info("service " + serviceName + " has service id " + service.getId());
			}
		}
		return service;
	}

	private void setService() {
		logger.info("creating service " + serviceName + " in metadata");
		Addressable addressable = null;
		service = new DeviceService();
		
		// Check for an addressable
		try {
			addressable = addressableClient.addressableForName(serviceName);
		} catch (NotFoundException nfe) {
			addressable = new Addressable(serviceName, Protocol.HTTP, host, callbackUrl, port);
			addressable.setOrigin(System.currentTimeMillis());
			addressableClient.add(addressable);
		}
		
		if(service != null && addressable != null) {
			// Setup the service
			service.setAddressable(addressable);
			service.setOrigin(System.currentTimeMillis());
			service.setAdminState(AdminState.unlocked);
			service.setOperatingState(OperatingState.enabled);
			service.setLabels(labels);
			service.setName(serviceName);
			String id = deviceServiceClient.add(service);
			service.setId(id);
		}
	}

	public boolean isServiceLocked(){
		return getService().getAdminState().equals(AdminState.locked);
	}
	
	public void addDefaultSchedules() {		
		
		logger.info("adding default schedules");
		for (int i = 0; i < defaultSchedules.getSize(); i++) {
			String name = defaultSchedules.getName()[i];
			String start = defaultSchedules.getStart()[i];
			String end = defaultSchedules.getEnd()[i];
			String frequency = defaultSchedules.getFrequency()[i];
			String cron = defaultSchedules.getCron()[i];
			Boolean runOnce = Boolean.valueOf(defaultSchedules.getRunOnce()[i]);
			
			Schedule schedule = new Schedule(name, start, end, frequency, cron, runOnce);
				
			try {
				scheduleClient.add(schedule);
				logger.info("added default schedule " + name);
			} catch (ClientErrorException e) {
				// Ignore if the schedule is already present
				// schedule.setId(scheduleClient.scheduleForName(name).getId());
				// scheduleClient.update(schedule);
			}
		}
		
	}
		
	public void addDefaultScheduleEvents() {
		logger.info("adding default schedule events");
		for (int i = 0; i < defaultScheduleEvents.getSize(); i++) {	
			String name = defaultScheduleEvents.getName()[i];
			String schedule = defaultScheduleEvents.getSchedule()[i];
			String parameters = defaultScheduleEvents.getParameters()[i];
			String service = defaultScheduleEvents.getService()[i];
			String path = defaultScheduleEvents.getPath()[i];
			String method = defaultScheduleEvents.getMethod()[i];
			String scheduler = defaultScheduleEvents.getScheduler()[i];
			
			Addressable addressable;
			try {
				addressable = serviceClient.deviceServiceForName(service).getAddressable();
				addressable.setName("Schedule-" + name);
				addressable.setPath(path);
			} catch (Exception e) {
				// some services are not registering an addressable, construct one for them
				addressable = new Addressable(name, Protocol.HTTP, service, path, 48080);
				addressable.setMethod(HTTPMethod.valueOf(method));
			}
			addressable.setId(null);
			
			try {
				addressable.setId(addressableClient.add(addressable));
			} catch (ClientErrorException e) {
				// Ignore if the addressable is already present
				// addressable.setId(addressableClient.addressableForName(addressable.getName()).getId());
				// addressableClient.update(addressable);
			}
			
			ScheduleEvent scheduleEvent = new ScheduleEvent(name, addressable, parameters, schedule, scheduler);
			
			try {
				scheduleEventClient.add(scheduleEvent);
				logger.info("added default schedule event " + name);
			} catch (ClientErrorException e) {
				// Ignore if the event is already present
				// scheduleEvent.setId(scheduleEventClient.scheduleEventForName(name).getId());
				// scheduleEventClient.update(scheduleEvent);
			}
		}
	}
}