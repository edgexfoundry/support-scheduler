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
 * @author: Marc Hammons, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.controller.impl;

import org.edgexfoundry.controller.CallbackController;
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
public class CallbackControllerImpl implements CallbackController {

  @Autowired
  private SchedulerCallbackHandler callbackHandler;

  private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
      org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
          .getEdgeXLogger(CallbackControllerImpl.class);
  private static final String TRUE = "true";
  private static final String FALSE = "false";

  @RequestMapping(method = RequestMethod.PUT)
  @Override
  public String handlePut(@RequestBody CallbackAlert alert) {
    try {
      logger.debug("put callback : '" + alert.toString() + "'");
      return (callbackHandler.handlePut(alert)) ? TRUE : FALSE;
    } catch (Exception e) {
      logger.error("put error : " + e.getMessage());
      throw new ServiceException(e);
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  @Override
  public String handlePost(@RequestBody CallbackAlert alert) {
    try {
      logger.debug("post callback : '" + alert.toString() + "'");
      return (callbackHandler.handlePost(alert)) ? TRUE : FALSE;
    } catch (Exception e) {
      logger.error("post error : " + e.getMessage());
      throw new ServiceException(e);
    }
  }

  @RequestMapping(method = RequestMethod.DELETE)
  @Override
  public String handleDelete(@RequestBody CallbackAlert alert) {
    try {
      logger.debug("delete callback : '" + alert.toString() + "'");
      return (callbackHandler.handleDelete(alert)) ? TRUE : FALSE;
    } catch (Exception e) {
      logger.error("delete error : " + e.getMessage());
      throw new ServiceException(e);
    }
  }
}
