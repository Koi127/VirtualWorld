import java.util.List;
import java.util.Optional;

import processing.core.PImage;

final class Background
{
   public String id;
   public List<PImage> images;
   public int imageIndex;

   public Background(String id, List<PImage> images)
   {
      this.id = id;
      this.images = images;
   }

   public static void setBackground(WorldModel world, Point pos, Background background) //help
   {
      if (world.withinBounds(pos))
      {
         world.setBackgroundCell(pos, background);
      }
   }









}
