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

package org.edgexfoundry.scheduling;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.ws.rs.NotFoundException;

import org.edgexfoundry.BaseService;
import org.edgexfoundry.controller.ScheduleClient;
import org.edgexfoundry.controller.ScheduleEventClient;
import org.edgexfoundry.controller.impl.ScheduleClientImpl;
import org.edgexfoundry.controller.impl.ScheduleEventClientImpl;
import org.edgexfoundry.domain.meta.Schedule;
import org.edgexfoundry.domain.meta.ScheduleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// TODO: Consider use of Quartz or other scheduler. Must support polyglot env, i.e can be
// implemented in Python, Go, etc.
// TODO: handle system clock update

@Component
@EnableScheduling
public class Scheduler extends BaseService {

  private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
      org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(Scheduler.class);

  private static final String ERR_SCH = "schedule ";
  private static final String ERR_FAIL_UPD = "failed to update schedule event ";
  private static final String ERR_NOT_FOUND = " not found";

  // Client to fetch schedule events
  @Autowired
  private ScheduleEventClient scheduleEventClient;

  // Client to fetch schedules
  @Autowired
  private ScheduleClient scheduleClient;

  // Schedule event executor to execute schedule events
  private ScheduleEventExecutor scheduleEventExecutor;

  // Schedule id -> Schedule Context Mapping
  // used to find the schedule context given a schedule, e.g. update/delete this schedule
  private HashMap<String, ScheduleContext> scheduleIdToScheduleContextMap;

  // Schedule Event id -> Schedule Id
  // used to find the schedule context (via schedule id) given a schedule event, e.g. update/delete
  // this schedule event
  private HashMap<String, String> scheduleEventIdToScheduleIdMap;

  // the scheduleContextQueue is prioritized based upon the next execution time of each schedule
  private PriorityQueue<ScheduleContext> scheduleContextQueue =
      new PriorityQueue<>(new Comparator<ScheduleContext>() {
        @Override
        public int compare(ScheduleContext unit1, ScheduleContext unit2) {
          int result = Long.compare(unit1.getNextTime().toEpochSecond(),
              unit2.getNextTime().toEpochSecond());
          // if the ticks are equivalent just dequeue the lhs
          result = result != 0 ? result : -1;
          return result;
        }
      });

  public Scheduler() {
    scheduleEventExecutor = new ScheduleEventExecutor();
    scheduleEventClient = new ScheduleEventClientImpl();
    scheduleClient = new ScheduleClientImpl();
    scheduleIdToScheduleContextMap = new HashMap<>();
    scheduleEventIdToScheduleIdMap = new HashMap<>();
  }

  @Scheduled(fixedRateString = "${schedule.interval}")
  public void schedule() {
    synchronized (scheduleContextQueue) {
      // Instant is in epoch time
      Instant nowInstant = Instant.now();
      long nowEpoch = nowInstant.getEpochSecond();

      while (scheduleContextQueue.peek() != null
          && scheduleContextQueue.peek().getNextTime().toEpochSecond() <= nowEpoch) {
        try {
          // pop the schedule context off the queue
          ScheduleContext scheduleContext = scheduleContextQueue.remove();

          logger.info("executing schedule " + scheduleContext.getInfo() + " at "
              + scheduleContext.getNextTime());

          // run the events for the schedule
          scheduleEventExecutor.execute(scheduleContext.getScheduleEvents());

          // update the context
          scheduleContext.updateNextTime();
          scheduleContext.updateIterations();

          // if the schedule is not complete, enqueue it.
          if (scheduleContext.isComplete()) {
            logger.info(
                ERR_SCH + scheduleContext.getInfo() + " is complete." + scheduleContext.toString());
          } else {
            logger.debug("queueing schedule " + scheduleContext.getInfo());
            scheduleContextQueue.add(scheduleContext);
          }
        } catch (Exception e) {
          logger.error("exception while scheduling schedule contects" + e);
        }
      }
    }
  }

  public boolean createScheduleContext(Schedule schedule) {
    synchronized (scheduleContextQueue) {
      if (scheduleIdToScheduleContextMap.containsKey(schedule.getId())) {
        // not intended to be an error
        logger.info("schedule context " + schedule.getId() + " '" + schedule.getName()
            + "' already exists.");
        return false;
      } else {
        // build a new schedule context
        ScheduleContext scheduleContext = new ScheduleContext(schedule);

        // store a mapping of schedule id to schedule context
        scheduleIdToScheduleContextMap.put(schedule.getId(), scheduleContext);

        // enqueue the context
        scheduleContextQueue.add(scheduleContext);
        logger.info("created schedule context " + scheduleContext.getInfo() + " initial start time "
            + scheduleContext.getNextTime().toString());
        return true;
      }
    }
  }

  public boolean updateScheduleContext(Schedule schedule) {
    synchronized (scheduleContextQueue) {
      if (!scheduleIdToScheduleContextMap.containsKey(schedule.getId())) {
        // not intended to be an error
        logger.error("failed to find schedule for " + schedule.getId() + " " + schedule.getName());
        return false;
      } else {
        // remove the schedule context from the queue
        scheduleContextQueue.remove(scheduleIdToScheduleContextMap.get(schedule.getId()));

        // update the schedule
        scheduleIdToScheduleContextMap.get(schedule.getId()).reset(schedule);

        // enqueue the context
        ScheduleContext scheduleContext = scheduleIdToScheduleContextMap.get(schedule.getId());
        scheduleContextQueue.add(scheduleContext);
        logger.info("updated schedule " + scheduleContext.getInfo() + " initial start time "
            + scheduleContext.getNextTime().toString());
        return true;
      }
    }
  }

  public boolean removeScheduleById(String id) {
    synchronized (scheduleContextQueue) {
      if (!scheduleIdToScheduleContextMap.containsKey(id)) {
        logger.error(ERR_SCH + id + " not found.");
        return false;
      } else {
        // look up the schedule context
        ScheduleContext sc = scheduleIdToScheduleContextMap.get(id);

        // remove all event id to schedule id mappings
        for (Map.Entry<String, ScheduleEvent> entry : sc.getScheduleEvents().entrySet()) {
          scheduleEventIdToScheduleIdMap.remove(entry.getValue().getId());
        }

        // remove the schedule context from the queue
        scheduleContextQueue.remove(sc);

        // remove the schedule context from the map (which contains schedule events)
        scheduleIdToScheduleContextMap.remove(id);

        logger.info("removed schedule " + id);
        return true;
      }
    }
  }

  public boolean addScheduleEventToScheduleContext(ScheduleEvent scheduleEvent) {
    synchronized (scheduleContextQueue) {
      // get the schedule for the event
      Schedule schedule = null;
      try {
        schedule = scheduleClient.scheduleForName(scheduleEvent.getSchedule());
      } catch (NotFoundException nfE) {
        logger.info("Schedule event not found with");
      }

      if (schedule == null) {
        logger.error(
            "failed to add schedule event " + scheduleEvent.getId() + " '" + scheduleEvent.getName()
                + "' " + "schedule '" + scheduleEvent.getSchedule() + "' not found");
      } else {
        // ensure a schedule context exists
        createScheduleContext(schedule);

        // add the schedule event to the context
        scheduleIdToScheduleContextMap.get(schedule.getId()).addScheduleEvent(scheduleEvent);

        // add to the schedule event id to schedule id map
        scheduleEventIdToScheduleIdMap.put(scheduleEvent.getId(), schedule.getId());
        return true;
      }
      return false;
    }
  }

  public boolean updateScheduleEventInScheduleContext(ScheduleEvent scheduleEvent) {
    synchronized (scheduleContextQueue) {
      // get the schedule for the event
      String scheduleId = scheduleEventIdToScheduleIdMap.get(scheduleEvent.getId());
      if (scheduleId == null) {
        logger.error(ERR_FAIL_UPD + scheduleEvent.getName() + " current schedule "
            + scheduleEvent.getId() + ERR_NOT_FOUND);
      } else {
        Schedule schedule = scheduleClient.scheduleForName(scheduleEvent.getSchedule());
        if (schedule == null) {
          logger.error(ERR_FAIL_UPD + scheduleEvent.getName() + " schedule "
              + scheduleEvent.getSchedule() + ERR_NOT_FOUND);
        } else {
          // see if the event switched schedules
          if (scheduleId != schedule.getId()) {
            if (!scheduleIdToScheduleContextMap.containsKey(scheduleId)) {
              logger.error("failed to switch schedule event " + scheduleEvent.getId()
                  + ", schedule " + scheduleId + ERR_NOT_FOUND);
            } else {
              // remove the schedule event from the old schedule
              removeScheduleEventById(scheduleEvent.getId());
              // add the schedule event to the new schedule
              addScheduleEventToScheduleContext(scheduleEvent);
              return true;
            }
          } else {
            // update the schedule event in place
            if (!scheduleIdToScheduleContextMap.containsKey(schedule.getId())) {
              logger.error(ERR_FAIL_UPD + scheduleEvent.getId() + ", schedule " + schedule.getId()
                  + ERR_NOT_FOUND);
            } else {
              // update the schedule event in the context
              scheduleIdToScheduleContextMap.get(schedule.getId())
                  .updateScheduleEvent(scheduleEvent);
              return true;
            }
          }
        }
      }
      return false;
    }
  }

  public boolean removeScheduleEventById(String id) {
    synchronized (scheduleContextQueue) {
      String scheduleId;
      scheduleId = scheduleEventIdToScheduleIdMap.get(id);
      if (scheduleId == null) {
        // check for schedule event id to schedule id mapping
        logger.error("failed to remove schedule event, schedule event " + id + ERR_NOT_FOUND);
      } else {
        // check for schedule event id to schedule context mapping
        if (!scheduleIdToScheduleContextMap.containsKey(scheduleId)) {
          logger.error("failed to remove schedule event, schedule " + scheduleId + ERR_NOT_FOUND);
        } else {
          // remove the schedule event from the schedule context
          scheduleIdToScheduleContextMap.get(scheduleId).removeScheduleEventById(id);

          // if there are no more events for the schedule remove the schedule context
          if (scheduleIdToScheduleContextMap.get(scheduleId).getScheduleEvents().isEmpty()) {
            logger.info(ERR_SCH + scheduleId + " event list is empty, removing.");
            removeScheduleById(scheduleId);
            return true;
          }
        }
      }
      return false;
    }
  }

  // Scheduler implementation of initialize
  @Override
  public boolean initialize() {
    boolean loaded = true;

    logger.info("loading schedules");
    synchronized (scheduleContextQueue) {
      // get all the schedule events for this service
      List<ScheduleEvent> scheduleEventList = null;
      try {
        scheduleEventList = scheduleEventClient.scheduleEventsForServiceByName(getServiceName());
        for (ScheduleEvent se : scheduleEventList)
          addScheduleEventToScheduleContext(se);
      } catch (Exception e) {
        logger.error("failed to load schedule events for service " + getServiceName() + " " + e);
        loaded = false;
      }

    }
    logger.info("loaded schedules");
    return loaded;
  }

}
