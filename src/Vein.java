import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Vein extends ActiveEntity{
    private static final Random rand = new Random();

    public Vein(String id, Point position, int actionPeriod, List<PImage> images){
        super(id,position,images,0,0,actionPeriod,0);

    }
    public void executeActivity(WorldModel world,
                                    ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Point> openPt = world.findOpenAround(this.position);

        if (openPt.isPresent())
        {
            Ore ore = new Ore(Ore.getOreIdPrefix() + this.id,
                    openPt.get(),
                     Ore.getOreCorruptMin() +
                            rand.nextInt(Ore.getOreCorruptMax() -Ore.getOreCorruptMin()),
                    imageStore.getImageList(ImageStore.getOreKey())
            );
            world.addEntity((Entity)ore);
            ore.scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
    }


    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
    }

//    @Override
//    public int getAnimationPeriod() {
//        throw new UnsupportedOperationException(
//                    String.format("getAnimationPeriod not supported for %s",
//                            this));
//    }

//    public void nextImage()
//    {
//        this.imageIndex = (this.imageIndex + 1) % this.images.size();
//    }
    public static Optional<Entity> findNearest(WorldModel world, Point pos)
    {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : world.entities)
        {
            if (entity instanceof Vein)
            {
                ofType.add(entity);
            }
        }
        return pos.nearestEntity(ofType);
    }
//    public void removeEntity(WorldModel world) {
//        world.removeEntityAt(this.position);
//    }
//
//    @Override
//    public PImage getCurrentImage() {
//        return (this.images.get(this.imageIndex));
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
