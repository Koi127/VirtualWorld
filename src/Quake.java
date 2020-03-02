import processing.core.PImage;

import java.util.List;

public class Quake extends ActiveEntity{

    private static final String QUAKE_KEY = "quake";
    private static final String QUAKE_ID = "quake";
    private static final int QUAKE_ACTION_PERIOD = 1100;
    private static final int QUAKE_ANIMATION_PERIOD = 100;
    private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

    public Quake(Point position, List<PImage> images){
        super(QUAKE_ID,position,images,0,0,QUAKE_ACTION_PERIOD,QUAKE_ANIMATION_PERIOD);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        scheduler.unscheduleAllEvents(this);
        removeEntity(world);
    }

    public static String getQuakeKey(){
        return QUAKE_KEY;
    }

//    public void removeEntity(WorldModel world)
//    {
//        world.removeEntityAt(this.position);
//    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                new Activity( this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent((Entity)this,
                new Animation((Entity)this, QUAKE_ANIMATION_REPEAT_COUNT),
                getAnimationPeriod());
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
}
