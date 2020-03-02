import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MinerNotFull extends ActiveEntity{
//    public String id;
//    public Point position;
//    public List<PImage> images;
//    public int imageIndex;
//    public int resourceLimit;
//    public int resourceCount;
//    public int actionPeriod;
//    public int animationPeriod;

    public MinerNotFull(String id, int resourceLimit,
                     Point position, int actionPeriod,
                     int animationPeriod,
                     List<PImage> images)
    {
        super(id,position,images,resourceLimit,0,actionPeriod,animationPeriod);
//        this.imageIndex = 0;
//        this.id = id;
//        this.position =position;
//        this.images = images;
//        this.resourceLimit=resourceLimit;
//        this.resourceCount=0;
//        this.actionPeriod = actionPeriod;
//        this.animationPeriod=animationPeriod;
    }


    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this,
                new Animation(this, 0), getAnimationPeriod());
    }

//    public void removeEntity(WorldModel world) {
//        world.removeEntityAt(this.position);
//    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> notFullTarget = Ore.findNearest(world,this.position);

        if (!notFullTarget.isPresent() ||
                !world.moveToNotFull(this, notFullTarget.get(), scheduler) ||
                !world.transformNotFull(this, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore),
                    super.actionPeriod);
        }
    }
//    public Point getPosition(){
//        return this.position;
//    }
//    public void setPosition(Point point){
//        this.position = point;
//    }
//    public int getAnimationPeriod(){
//        return this.animationPeriod;
//    }


//    public void nextImage()
//    {
//        this.imageIndex = (this.imageIndex + 1) % this.images.size();
//    }
//
//    @Override
//    public PImage getCurrentImage() {
//        {
//                return (this.images.get(this.imageIndex));
//
//        }
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
//
//    public void setResourceCount(int x) {
//        this.resourceCount = x;
//    }
//

}
