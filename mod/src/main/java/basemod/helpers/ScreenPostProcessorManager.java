package basemod.helpers;

import basemod.interfaces.ScreenPostProcessor;
import basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame.ApplyScreenPostProcessor;

public class ScreenPostProcessorManager {
    public static void addPostProcessor(ScreenPostProcessor postProcessor) {
        ApplyScreenPostProcessor.postProcessors.add(postProcessor);
    }

    public static boolean removePostProcessor(ScreenPostProcessor postProcessor) {
        return ApplyScreenPostProcessor.postProcessors.remove(postProcessor);
    }
}
