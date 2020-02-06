import processing.core.PImage;

import java.util.*;

final class WorldModel
{
   public int numRows;
   public int numCols;
   public Background background[][];
   public Entity occupancy[][];
   public Set<Entity> entities;

   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }

   public void removeEntityAt(Point pos)
   {
      if (withinBounds(pos)
              && getOccupancyCell(pos) != null)
      {
         Entity entity = getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.position = new Point(-1, -1);
         this.entities.remove(entity);
         setOccupancyCell(pos, null);
      }
   }

   public void addEntity(Entity entity)
   {
      if (withinBounds(entity.position))
      {
         setOccupancyCell(entity.position, entity);
         this.entities.add(entity);
      }
   }

   public boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < this.numRows &&
              pos.x >= 0 && pos.x < this.numCols;
   }

   public Entity getOccupancyCell(Point pos)
   {
      return this.occupancy[pos.y][pos.x];
   }

   public void setOccupancyCell(Point pos, Entity entity)
   {
      this.occupancy[pos.y][pos.x] = entity;
   }

   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.position;
      if (withinBounds(pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt(pos);
         setOccupancyCell(pos, entity);
         entity.position = pos;
      }
   }

   public void setBackgroundCell(Point pos, Background background)
   {
      this.background[pos.y][pos.x] = background;
   }

   public Background getBackgroundCell(Point pos)
   {
      return this.background[pos.y][pos.x];
   }


   public boolean moveToOreBlob(Entity blob, Entity target, EventScheduler scheduler)
   {
      if (adjacent(blob.position, target.position))
      {
         target.removeEntity(this);
         scheduler.unscheduleAllEvents(target);
         return true;
      }
      else
      {
         Point nextPos = nextPositionOreBlob(blob, target.position);

         if (!blob.position.equals(nextPos))
         {
            Optional<Entity> occupant = getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            moveEntity(blob, nextPos);
         }
         return false;
      }
   }

   public static boolean adjacent(Point p1, Point p2)
   {
      return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) ||
              (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
   }

   public Point nextPositionOreBlob(Entity entity,
                                           Point destPos)
   {
      int horiz = Integer.signum(destPos.x - entity.position.x);
      Point newPos = new Point(entity.position.x + horiz,
              entity.position.y);

      Optional<Entity> occupant = getOccupant(newPos);

      if (horiz == 0 ||
              (occupant.isPresent() && !(occupant.get().kind == EntityKind.ORE)))
      {
         int vert = Integer.signum(destPos.y - entity.position.y);
         newPos = new Point(entity.position.x, entity.position.y + vert);
         occupant = getOccupant(newPos);

         if (vert == 0 ||
                 (occupant.isPresent() && !(occupant.get().kind == EntityKind.ORE)))
         {
            newPos = entity.position;
         }
      }

      return newPos;
   }

   public Point nextPositionMiner(Entity entity, Point destPos)
   {
      int horiz = Integer.signum(destPos.x - entity.position.x);
      Point newPos = new Point(entity.position.x + horiz,
              entity.position.y);

      if (horiz == 0 || isOccupied(newPos))
      {
         int vert = Integer.signum(destPos.y - entity.position.y);
         newPos = new Point(entity.position.x,
                 entity.position.y + vert);

         if (vert == 0 || isOccupied( newPos))
         {
            newPos = entity.position;
         }
      }

      return newPos;
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   public boolean isOccupied(Point pos)
   {
      return withinBounds(pos) &&
              getOccupancyCell(pos) != null;
   }

   public Optional<Entity> findNearest(Point pos, EntityKind kind)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : this.entities)
      {
         if (entity.kind == kind)
         {
            ofType.add(entity);
         }
      }

      return pos.nearestEntity(ofType);
   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds(pos))
      {
         return Optional.of(Entity.getCurrentImage(getBackgroundCell(pos)));
      }
      else
      {
         return Optional.empty();
      }
   }

   public boolean transformNotFull(Entity entity, EventScheduler scheduler, ImageStore imageStore)
   {
      if (entity.resourceCount >= entity.resourceLimit)
      {
         Entity miner = entity.createMinerFull(entity.id, entity.resourceLimit,
                 entity.position, entity.actionPeriod, entity.animationPeriod,
                 entity.images);

         entity.removeEntity(this);
         scheduler.unscheduleAllEvents( entity);

         addEntity(miner);
         miner.scheduleActions(scheduler, this, imageStore);

         return true;
      }

      return false;
   }

   public void transformFull(Entity entity, EventScheduler scheduler, ImageStore imageStore)
   {
      Entity miner = entity.createMinerNotFull(entity.id, entity.resourceLimit, //need help
              entity.position, entity.actionPeriod, entity.animationPeriod,
              entity.images);

      entity.removeEntity(this);
      scheduler.unscheduleAllEvents( entity);

      addEntity(miner);
      miner.scheduleActions( scheduler, this, imageStore);
   }

   public boolean moveToNotFull(Entity miner, Entity target, EventScheduler scheduler)
   {
      if (adjacent(miner.position, target.position))
      {
         miner.resourceCount += 1;
         target.removeEntity(this);
         scheduler.unscheduleAllEvents(target);

         return true;
      }
      else
      {
         Point nextPos = nextPositionMiner(miner,  target.position);

         if (!miner.position.equals(nextPos))
         {
            Optional<Entity> occupant = getOccupant( nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            moveEntity(miner, nextPos);
         }
         return false;
      }
   }

   public boolean moveToFull(Entity miner, Entity target, EventScheduler scheduler)
   {
      if (adjacent(miner.position, target.position))
      {
         return true;
      }
      else
      {
         Point nextPos = nextPositionMiner(miner, target.position);

         if (!miner.position.equals(nextPos))
         {
            Optional<Entity> occupant = getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            moveEntity(miner, nextPos);
         }
         return false;
      }
   }

   public  Optional<Point> findOpenAround( Point pos)
   {
      for (int dy = -Entity.getOreReach(); dy <= Entity.getOreReach(); dy++)
      {
         for (int dx = -Entity.getOreReach(); dx <= Entity.getOreReach(); dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (withinBounds(newPt) &&
                    !isOccupied(newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }

   public void tryAddEntity(Entity entity)
   {
      if (isOccupied(entity.position))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity( entity);
   }


}
