package me.Fupery.ArtMap.Menu.Handler;

import me.Fupery.ArtMap.Menu.Event.MenuFactory;
import org.bukkit.entity.Player;

public class ConditionalMenuFactory implements MenuFactory {
    StaticMenuFactory conditionTrue;
    StaticMenuFactory conditionFalse;
    private ConditionalGenerator generator;

    ConditionalMenuFactory(ConditionalGenerator generator) {
        this.generator = generator;
        conditionTrue = new StaticMenuFactory(generator::getConditionTrue);
        conditionFalse = new StaticMenuFactory(generator::getConditionFalse);
    }

    @Override
    public CacheableMenu get(Player viewer) {
        return generator.evaluateCondition(viewer) ? conditionTrue.get(viewer) : conditionFalse.get(viewer);
    }

    interface ConditionalGenerator {
        CacheableMenu getConditionTrue();

        CacheableMenu getConditionFalse();

        boolean evaluateCondition(Player viewer);
    }
}
