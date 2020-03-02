import com.sun.org.apache.xpath.internal.operations.Or;
import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class OreBlob extends ActiveEntity {
    private static final String BLOB_KEY = "blob";
    private static final String BLOB_ID_SUFFIX = " -- blob";
    private static final int BLOB_PERIOD_SCALE = 4;
    private static final int BLOB_ANIMATION_MIN = 50;
    private static final int BLOB_ANIMATION_MAX = 150;

    public OreBlob(String id, Point position,
                    int actionPeriod, int animationPeriod,List<PImage> images){
        super(id,position, images,0,0,actionPeriod,animationPeriod);

    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> blobTarget =Vein.findNearest(world, this.position);
        long nextPeriod = this.actionPeriod;

        if (blobTarget.isPresent())
        {
            Point tgtPos = blobTarget.get().getPosition();

            if (world.moveToOreBlob(this,blobTarget.get(), scheduler))
            {
                Quake quake = new Quake(tgtPos,
                        imageStore.getImageList(Quake.getQuakeKey()));

                world.addEntity(quake);
                nextPeriod += this.actionPeriod;
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                nextPeriod);
    }

    public static String getBlobKey(){
        return BLOB_KEY;
    }
    public static String getBlobIdSuffix (){
        return BLOB_ID_SUFFIX;
    }

    public static int getBlobPeriodScale(){
        return BLOB_PERIOD_SCALE;
    }
    public static int getBlobAnimationMin(){
        return BLOB_ANIMATION_MIN;
    }
    public static int getBlobAnimationMax(){
        return  BLOB_ANIMATION_MAX;
    }



    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this,
                new Animation(this, 0), getAnimationPeriod());
    }

//    public int getAnimationPeriod(){
//        return this.animationPeriod;
//    }
//
//    public void nextImage()
//    {
//        this.imageIndex = (this.imageIndex + 1) % this.images.size();
//    }
//
//    @Override
//    public PImage getCurrentImage() {
//        return (this.images.get(this.imageIndex));
//    }
//
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
//    public List<PImage> getImages(){
//        return this.images;
//    }
//    public void setResourceCount(int x) {
//        this.resourceCount = x;
//    }
//    public void removeEntity(WorldModel world) {
//        world.removeEntityAt(this.position);
//    }

}
