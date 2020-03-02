import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Ore extends ActiveEntity{
    private static final String ORE_ID_PREFIX = "ore -- ";
    private static final int ORE_CORRUPT_MIN = 20000;
    private static final int ORE_CORRUPT_MAX = 30000;
    private static final int ORE_REACH = 1;

    private static final Random rand = new Random();

    public Ore( String id, Point position,
                int actionPeriod,
                List<PImage> images
                )
    {
        super(id,position,images,0,0,actionPeriod,0);

    }

    public void executeActivity(WorldModel world,
                                   ImageStore imageStore, EventScheduler scheduler)
    {
        Point pos = this.position;  // store current position before removing

        removeEntity(world);
        scheduler.unscheduleAllEvents(this);

        OreBlob blob = new OreBlob(this.id + OreBlob.getBlobIdSuffix(),
                pos,
                this.actionPeriod / OreBlob.getBlobPeriodScale(),
                OreBlob.getBlobAnimationMin() +
                        rand.nextInt(OreBlob.getBlobAnimationMax() - OreBlob.getBlobAnimationMin()),
                imageStore.getImageList(OreBlob.getBlobKey())
                );

        world.addEntity(blob);
        blob.scheduleActions(scheduler, world, imageStore);
    }

    public static Optional<Entity> findNearest(WorldModel world, Point pos)
    {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : world.entities)
        {
            if (entity instanceof Ore)
            {
                ofType.add(entity);
            }
        }
        return pos.nearestEntity(ofType);
    }

//    public void removeEntity(WorldModel world) {
//        world.removeEntityAt(this.position);
//    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
    }

    public int getAnimationPeriod(){
        throw new UnsupportedOperationException(
                String.format("getAnimationPeriod not supported for %s",
                        this));
    }

//    public void nextImage()
//    {
//        this.imageIndex = (this.imageIndex + 1) % this.images.size();
//    }
//
//    @Override
//    public PImage getCurrentImage() {
//
//         return (this.images.get(this.imageIndex));
//
//    }

    public static int getOreReach(){
        return ORE_REACH;
    }

    public static String getOreIdPrefix(){
        return ORE_ID_PREFIX;
    }

    public static int getOreCorruptMin(){
        return ORE_CORRUPT_MIN;
    }

    public static int getOreCorruptMax(){
        return ORE_CORRUPT_MAX;
    }
//    public Point getPosition(){
//        return this.position;
//    }
//    public void setPosition(Point point){
//        this.position = point;
//    }
//    public String getId(){
//        return this.id;
//    }
//    public int getImageIndex(){
//        return this.getImageIndex();
//    }
//    public int getResourceLimit(){
//        return this.resourceLimit;
//    }
//    public int getResourceCount(){
//        return this.resourceCount;
//    }
//    public int getActionPeriod(){
//        return this.actionPeriod;
//    }
//
//    public List<PImage> getImages(){
//        return this.images;
//    }
//    public void setResourceCount(int x) {
//        this.resourceCount = x;
//    }
}
