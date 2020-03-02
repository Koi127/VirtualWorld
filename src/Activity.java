public class Activity implements Action{
    public ActiveEntity activeEntity;
    public WorldModel world;
    public ImageStore imageStore;
    public int repeatCount;

    public Activity(ActiveEntity activeEntity, WorldModel world, ImageStore imageStore){
        this.activeEntity = activeEntity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = 0;
    }

    public void executeAction(EventScheduler scheduler){
        this.activeEntity.executeActivity(this.world, this.imageStore, scheduler);
//        switch (this.entity.kind)
//        {
//            case MINER_FULL:
//                this.entity.executeMinerFullActivity(this.world,
//                        this.imageStore, scheduler);
//                break;
//
//            case MINER_NOT_FULL:
//                this.entity.executeMinerNotFullActivity(this.world,
//                        this.imageStore, scheduler);
//                break;
//
//            case ORE:
//                this.entity.executeOreActivity( this.world, this.imageStore, scheduler);
//                break;
//
//            case ORE_BLOB:
//                this.entity.executeOreBlobActivity(this.world,
//                        this.imageStore, scheduler);
//                break;
//
//            case QUAKE:
//                this.entity.executeQuakeActivity(this.world, this.imageStore,
//                        scheduler);
//                break;
//
//            case VEIN:
//                this.entity.executeVeinActivity(this.world, this.imageStore,
//                        scheduler);
//                break;
//
//            default:
//                throw new UnsupportedOperationException(
//                        String.format("executeActivityAction not supported for %s",
//                                this.entity.kind));
//        }
    }
}
