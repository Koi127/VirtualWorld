import java.util.List;

final class Event
{
   public Action action;
   public long time;
   public Entity entity;

   public Event(Action action, long time, Entity entity)
   {
      this.action = action;
      this.time = time;
      this.entity = entity;
   }

   public static void updateOnTime(EventScheduler scheduler, long time)
   {
      while (!scheduler.eventQueue.isEmpty() &&
              scheduler.eventQueue.peek().time < time)
      {
         Event next = scheduler.eventQueue.poll();

         next.removePendingEvent(scheduler);

         next.action.executeAction(scheduler);
      }
   }

   public void removePendingEvent(EventScheduler scheduler)
   {
      List<Event> pending = scheduler.pendingEvents.get(this.entity);

      if (pending != null)
      {
         pending.remove(this);
      }
   }

}
