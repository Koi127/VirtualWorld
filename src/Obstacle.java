import processing.core.PImage;

import java.util.List;

public class Obstacle extends Entity{
//    public String id;
//    public Point position;
//    public List<PImage> images;
//    public int imageIndex;
//    public int resourceLimit;
//    public int resourceCount;
//    public int actionPeriod;
//    public int animationPeriod;

    public Obstacle(String id, Point position,
                    List<PImage> images)
    {
        super(id,position,images,0,0,0,0);

    }

//    public void nextImage()
//    {
//        this.imageIndex = (this.imageIndex + 1) % this.images.size();
//    }
//
//    @Override
//    public PImage getCurrentImage() {
//        return this.images.get(this.imageIndex);
//    }
//
//    public Point getPosition(){
//        return this.position;
//    }
//
//    public void removeEntity(WorldModel world) {
//        world.removeEntityAt(this.position);
//    }
//
//
//    @Override
//    public int getAnimationPeriod() {
//        return this.animationPeriod;
//    }
//
//    public void setPosition(Point point){
//        this.position = point;
//    }
//
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
