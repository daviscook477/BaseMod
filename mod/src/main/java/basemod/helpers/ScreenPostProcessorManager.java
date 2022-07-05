package basemod.helpers;

import basemod.interfaces.ScreenPostProcessor;
import basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame.ApplyScreenPostProcessor;

import java.util.Comparator;

public class ScreenPostProcessorManager {
    public static void addPostProcessor(ScreenPostProcessor postProcessor) {
        ApplyScreenPostProcessor.postProcessors.add(postProcessor);
        ApplyScreenPostProcessor.postProcessors.sort(Comparator.comparingInt(ScreenPostProcessor::priority));
    }

    public static boolean removePostProcessor(ScreenPostProcessor postProcessor) {
        return ApplyScreenPostProcessor.postProcessors.remove(postProcessor);
    }
}
