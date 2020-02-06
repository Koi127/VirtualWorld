import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

final class Entity
{
   public EntityKind kind;
   public String id;
   public Point position;
   public List<PImage> images;
   public int imageIndex;
   public int resourceLimit;
   public int resourceCount;
   public int actionPeriod;
   public int animationPeriod;

   private static final Random rand = new Random();

   private static final String BLOB_KEY = "blob";
   private static final String BLOB_ID_SUFFIX = " -- blob";
   private static final int BLOB_PERIOD_SCALE = 4;
   private static final int BLOB_ANIMATION_MIN = 50;
   private static final int BLOB_ANIMATION_MAX = 150;

   private static final String ORE_ID_PREFIX = "ore -- ";
   private static final int ORE_CORRUPT_MIN = 20000;
   private static final int ORE_CORRUPT_MAX = 30000;
   private static final int ORE_REACH = 1;

   private static final String QUAKE_KEY = "quake";
   private static final String QUAKE_ID = "quake";
   private static final int QUAKE_ACTION_PERIOD = 1100;
   private static final int QUAKE_ANIMATION_PERIOD = 100;
   private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;


   public Entity(EntityKind kind, String id, Point position,
      List<PImage> images, int resourceLimit, int resourceCount,
      int actionPeriod, int animationPeriod)
   {
      this.kind = kind;
      this.id = id;
      this.position = position;
      this.images = images;
      this.imageIndex = 0;
      this.resourceLimit = resourceLimit;
      this.resourceCount = resourceCount;
      this.actionPeriod = actionPeriod;
      this.animationPeriod = animationPeriod;
   }

   public static Entity createBlacksmith(String id, Point position,
                                         List<PImage> images)
   {
      return new Entity(EntityKind.BLACKSMITH, id, position, images,
              0, 0, 0, 0);
   }

   public static Entity createQuake(Point position, List<PImage> images)
   {
      return new Entity(EntityKind.QUAKE, QUAKE_ID, position, images,
              0, 0, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);
   }

   public static Entity createVein(String id, Point position, int actionPeriod,
                                   List<PImage> images)
   {
      return new Entity(EntityKind.VEIN, id, position, images, 0, 0,
              actionPeriod, 0);
   }

   public static Entity createOreBlob(String id, Point position,
                                      int actionPeriod, int animationPeriod, List<PImage> images)
   {
      return new Entity(EntityKind.ORE_BLOB, id, position, images,
              0, 0, actionPeriod, animationPeriod);
   }

   public static Entity createOre(String id, Point position, int actionPeriod,
                                  List<PImage> images)
   {
      return new Entity(EntityKind.ORE, id, position, images, 0, 0,
              actionPeriod, 0);
   }

   public static Entity createMinerFull(String id, int resourceLimit,
                                        Point position, int actionPeriod, int animationPeriod,
                                        List<PImage> images)
   {
      return new Entity(EntityKind.MINER_FULL, id, position, images,
              resourceLimit, resourceLimit, actionPeriod, animationPeriod);
   }

   public static Entity createMinerNotFull(String id, int resourceLimit,
                                           Point position, int actionPeriod, int animationPeriod,
                                           List<PImage> images)
   {
      return new Entity(EntityKind.MINER_NOT_FULL, id, position, images,
              resourceLimit, 0, actionPeriod, animationPeriod);
   }

   public static Entity createObstacle(String id, Point position,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.OBSTACLE, id, position, images,
              0, 0, 0, 0);
   }


   public void executeOreActivity(WorldModel world,
                                         ImageStore imageStore, EventScheduler scheduler)
   {
      Point pos = this.position;  // store current position before removing

      removeEntity(world);
      scheduler.unscheduleAllEvents(this);

      Entity blob = createOreBlob(this.id + BLOB_ID_SUFFIX,
              pos, this.actionPeriod / BLOB_PERIOD_SCALE,
              BLOB_ANIMATION_MIN +
                      rand.nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN),
              imageStore.getImageList(BLOB_KEY));

      world.addEntity(blob);
      blob.scheduleActions(scheduler, world, imageStore);
   }

   public void executeOreBlobActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> blobTarget = world.findNearest(
              this.position, EntityKind.VEIN);
      long nextPeriod = this.actionPeriod;

      if (blobTarget.isPresent())
      {
         Point tgtPos = blobTarget.get().position;

         if (world.moveToOreBlob(this,blobTarget.get(), scheduler))
         {
            Entity quake = createQuake(tgtPos,
                    imageStore.getImageList(QUAKE_KEY));

            world.addEntity(quake);
            nextPeriod += this.actionPeriod;
            quake.scheduleActions(scheduler, world, imageStore);
         }
      }

      scheduler.scheduleEvent(this,
              Action.createActivityAction(this, world, imageStore),
              nextPeriod);
   }

   public void executeMinerFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> fullTarget = world.findNearest(this.position,
              EntityKind.BLACKSMITH);

      if (fullTarget.isPresent() &&
              world.moveToFull(this, fullTarget.get(), scheduler))
      {
         world.transformFull(this, scheduler, imageStore);
      }
      else
      {
         scheduler.scheduleEvent(this,
                 Action.createActivityAction(this, world, imageStore),
                 this.actionPeriod);
      }
   }

   public void executeMinerNotFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> notFullTarget = world.findNearest(this.position,
              EntityKind.ORE);

      if (!notFullTarget.isPresent() ||
              !world.moveToNotFull(this, notFullTarget.get(), scheduler) ||
              !world.transformNotFull(this, scheduler, imageStore))
      {
         scheduler.scheduleEvent(this,
                 Action.createActivityAction(this, world, imageStore),
                 this.actionPeriod);
      }
   }

   public void executeQuakeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
   {
      scheduler.unscheduleAllEvents(this);
      removeEntity(world);
   }

   public void executeVeinActivity(WorldModel world,
                                          ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Point> openPt = world.findOpenAround(this.position);

      if (openPt.isPresent())
      {
         Entity ore = createOre(ORE_ID_PREFIX + this.id,
                 openPt.get(), ORE_CORRUPT_MIN +
                         rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                 imageStore.getImageList(ImageStore.getOreKey()));
         world.addEntity(ore);
         ore.scheduleActions(scheduler, world, imageStore);
      }

      scheduler.scheduleEvent(this,
              Action.createActivityAction(this, world, imageStore),
              this.actionPeriod);
   }

   public void removeEntity(WorldModel world)
   {
      world.removeEntityAt(this.position);
   }

   public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
   {
      switch (this.kind)
      {
         case MINER_FULL:
            scheduler.scheduleEvent(this,
                    Action.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
            scheduler.scheduleEvent(this, Action.createAnimationAction(this, 0),
                    getAnimationPeriod());
            break;

         case MINER_NOT_FULL:
            scheduler.scheduleEvent(this,
                    Action.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
            scheduler.scheduleEvent(this,
                    Action.createAnimationAction(this, 0), getAnimationPeriod());
            break;

         case ORE:
            scheduler.scheduleEvent(this,
                    Action.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
            break;

         case ORE_BLOB:
            scheduler.scheduleEvent(this,
                    Action.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
            scheduler.scheduleEvent(this,
                    Action.createAnimationAction(this, 0), getAnimationPeriod());
            break;

         case QUAKE:
            scheduler.scheduleEvent(this,
                    Action.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
            scheduler.scheduleEvent(this,
                    Action.createAnimationAction(this, QUAKE_ANIMATION_REPEAT_COUNT),
                    getAnimationPeriod());
            break;

         case VEIN:
            scheduler.scheduleEvent(this,
                    Action.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
            break;

         default:
      }
   }

   public int getAnimationPeriod()
   {
      switch (this.kind)
      {
         case MINER_FULL:
         case MINER_NOT_FULL:
         case ORE_BLOB:
         case QUAKE:
            return this.animationPeriod;
         default:
            throw new UnsupportedOperationException(
                    String.format("getAnimationPeriod not supported for %s",
                            this.kind));
      }
   }

   public void nextImage()
   {
      this.imageIndex = (this.imageIndex + 1) % this.images.size();
   }


   public static PImage getCurrentImage(Object entity)
   {
      if (entity instanceof Background)
      {
         return ((Background)entity).images
                 .get(((Background)entity).imageIndex);
      }
      else if (entity instanceof Entity)
      {
         return ((Entity)entity).images.get(((Entity)entity).imageIndex);
      }
      else
      {
         throw new UnsupportedOperationException(
                 String.format("getCurrentImage not supported for %s",
                         entity));
      }
   }

   public static int getOreReach(){
      return ORE_REACH;
   }



}
