package me.Fupery.ArtMap.Utils;

import org.bukkit.entity.Entity;

public class WeakPart<T extends Entity> {
//
//    private final EaselPart partType;
//    private WeakReference<T> reference;
//    private int entityID;
//
//    public WeakPart(EaselPart partType) {
//        this.partType = partType;
//        reference = new WeakReference<T>(null);
//        entityID = -1;
//    }
//
//    public WeakPart find(Collection<Entity> nearbyEntities) {
//        for (Entity e : nearbyEntities) {
//            if (EaselPart.getPartType(e) == partType) {
//                if (e.getLocation())
//            }
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    public T spawn(Location location) {
//        T entity = (T) location.getWorld().spawnEntity(location, partType);
//        reference = new WeakReference<T>(entity);
//        entityID = entity.getEntityId();
//        this.location = location;
//        return entity;
//    }
//
//    public void remove() {
//        Entity entity = get();
//        if (entity != null) entity.remove();
//        entityID = -1;
//    }
//
//    public T get() {
//        if (location == null && !isValid()) return null;
//        return get(location.getWorld().getNearbyEntities(location, 2, 2, 2));
//    }
//
//    @SuppressWarnings("unchecked")
//    public T get(Collection<Entity> nearbyEntities) {
//        if (isValid()) {
//            return reference.get();
//        } else {
//            for (Entity e : nearbyEntities) {
//                if ((entityID == -1 || entityID == e.getEntityId()) && e.getType() == type) {
//                    T entity = ((T) e);
//                    reference = new WeakReference<T>(entity);
//                    return entity;
//                }
//            }
//        }
//        entityID = -1;
//        location = null;
//        return null;
//    }
//
//    public boolean isValid() {
//        if (reference == null) return false;
//        Entity entity = reference.get();
//        return entity != null && entity.isValid();
//    }
}
