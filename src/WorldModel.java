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
         entity.setPosition(new Point(-1, -1));
         this.entities.remove(entity);
         setOccupancyCell(pos, null);
      }
   }

   public void addEntity(Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
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
      Point oldPos = entity.getPosition();
      if (withinBounds(pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt(pos);
         setOccupancyCell(pos, entity);
         entity.setPosition(pos);
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
      if (adjacent(blob.getPosition(), target.getPosition()))
      {
         target.removeEntity(this);
         scheduler.unscheduleAllEvents(target);
         return true;
      }
      else
      {
         Point nextPos = nextPositionOreBlob(blob, target.getPosition());

         if (!blob.getPosition().equals(nextPos))
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
      int horiz = Integer.signum(destPos.x - entity.getPosition().x);
      Point newPos = new Point(entity.getPosition().x + horiz,
              entity.getPosition().y);

      Optional<Entity> occupant = getOccupant(newPos);

      if (horiz == 0 ||
              (occupant.isPresent() && !(occupant.getClass().equals(Ore.class))))
      {
         int vert = Integer.signum(destPos.y - entity.getPosition().y);
         newPos = new Point(entity.getPosition().x, entity.getPosition().y + vert);
         occupant = getOccupant(newPos);

         if (vert == 0 ||
                 (occupant.isPresent() && !((occupant.getClass().equals(Ore.class)))))
         {
            newPos = entity.getPosition();
         }
      }

      return newPos;
   }

   public Point nextPositionMiner(Entity entity, Point destPos)
   {
      int horiz = Integer.signum(destPos.x - entity.getPosition().x);
      Point newPos = new Point(entity.getPosition().x + horiz,
              entity.getPosition().y);

      if (horiz == 0 || isOccupied(newPos))
      {
         int vert = Integer.signum(destPos.y - entity.getPosition().y);
         newPos = new Point(entity.getPosition().x,
                 entity.getPosition().y + vert);

         if (vert == 0 || isOccupied( newPos))
         {
            newPos = entity.getPosition();
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

//   public Optional<Entity> findNearest(Point pos, EntityKind kind)
//   {
//      List<Entity> ofType = new LinkedList<>();
//      for (Entity entity : this.entities)
//      {
//         if (entity.kind == kind)
//         {
//            ofType.add(entity);
//         }
//      }
//
//      return pos.nearestEntity(ofType);
//   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds(pos))
      {
         return Optional.of(getBackgroundCell(pos).getCurrentImage());
      }
      else
      {
         return Optional.empty();
      }
   }

   public boolean transformNotFull(Entity entity, EventScheduler scheduler, ImageStore imageStore)
   {
      if (entity.getResourceCount() >= entity.getResourceLimit())
      {
         MinerFull miner = new MinerFull(entity.getId(), entity.getResourceLimit(),
                 entity.getPosition(), entity.getActionPeriod(), entity.getAnimationPeriod(),
                 entity.getImages());

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
      MinerNotFull miner =new MinerNotFull(entity.getId(), entity.getResourceLimit(), //need help
              entity.getPosition(), entity.getActionPeriod(), entity.getAnimationPeriod(),
              entity.getImages());

      entity.removeEntity(this);
      scheduler.unscheduleAllEvents( entity);

      addEntity(miner);
      miner.scheduleActions( scheduler, this, imageStore);
   }

   public boolean moveToNotFull(Entity miner, Entity target, EventScheduler scheduler)
   {
      if (adjacent(miner.getPosition(), target.getPosition()))
      {
         miner.setResourceCount(miner.getResourceCount()+1);
         target.removeEntity(this);
         scheduler.unscheduleAllEvents(target);

         return true;
      }
      else
      {
         Point nextPos = nextPositionMiner(miner,  target.getPosition());

         if (!miner.getPosition().equals(nextPos))
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
      if (adjacent(miner.getPosition(), target.getPosition()))
      {
         return true;
      }
      else
      {
         Point nextPos = nextPositionMiner(miner, target.getPosition());

         if (!miner.getPosition().equals(nextPos))
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
      for (int dy = -Ore.getOreReach(); dy <= Ore.getOreReach(); dy++)
      {
         for (int dx = -Ore.getOreReach(); dx <= Ore.getOreReach(); dx++)
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
      if (isOccupied(entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity( entity);
   }


}
