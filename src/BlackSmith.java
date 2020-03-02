import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BlackSmith extends Entity{

    public BlackSmith(String id, Point position, List<PImage> images){
        super(id, position, images, 0, 0,0 ,0);
    }

    public static Optional<Entity> findNearest(WorldModel world, Point pos)
    {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : world.entities)
        {
            if (entity instanceof BlackSmith)
            {
                ofType.add(entity);
            }
        }
        return pos.nearestEntity(ofType);
    }
//    public void removeEntity(WorldModel world) {
//        world.removeEntityAt(this.position);
//    }

//    public void nextImage()
//    {
//        this.imageIndex = (this.imageIndex + 1) % this.images.size();
//    }

//    public PImage getCurrentImage()
//    {
//        return (this.images.get((this.imageIndex)));
//    }


//    public String getId(){
//        return this.id;
//    }
//
//    public int getImageIndex(){
//        return this.getImageIndex();
//    }
//
//    public int getResourceLimit(){
//        return this.resourceLimit;
//    }
//
//    public int getResourceCount(){
//        return this.resourceCount;
//    }
//
//    public int getActionPeriod(){
//        return this.actionPeriod;
//    }
//
//    public int getAnimationPeriod() {
//        return this.animationPeriod;
//    }
//
//    public List<PImage> getImages(){
//        return this.images;
//    }
//
//    public void setResourceCount(int x) {
//        this.resourceCount = x;
//    }

}
