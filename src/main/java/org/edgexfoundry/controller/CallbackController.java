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
package org.edgexfoundry.controller;

import org.apache.log4j.Logger;
import org.edgexfoundry.domain.meta.CallbackAlert;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.scheduling.SchedulerCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${service.callback}")
public class CallbackController {
	
	@Autowired
	private SchedulerCallbackHandler callbackHandler;
	
	private static final Logger logger = Logger.getLogger(CallbackController.class);

	@RequestMapping(method = RequestMethod.PUT)
	public String handlePUT(@RequestBody CallbackAlert alert) {
		try {
			logger.debug("put callback : '" + alert.toString() + "'");
			return (callbackHandler.handlePUT(alert) == true) ? "true" : "false";
		} catch (Exception e) {
			logger.error("put error : " + e.getMessage());
			throw new ServiceException(e);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String handlePOST(@RequestBody CallbackAlert alert) {
		try {
			logger.debug("post callback : '" + alert.toString() + "'");
			return (callbackHandler.handlePOST(alert) == true) ? "true" : "false";
		} catch (Exception e) {
			logger.error("post error : " + e.getMessage());
			throw new ServiceException(e);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public String handleDELETE(@RequestBody CallbackAlert alert) {
		try {
			logger.debug("delete callback : '" + alert.toString() + "'" );
			return (callbackHandler.handleDELETE(alert) == true) ? "true" : "false";
		} catch (Exception e) {
			logger.error("delete error : " + e.getMessage());
			throw new ServiceException(e);
		}
	}
}