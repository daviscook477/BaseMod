package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.BaseMod;
import basemod.customacts.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

public class GetDungeonCustomAct {
    @SpirePatch(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class})
    public static class getDungeonThroughProgression {
        public static AbstractDungeon Postfix(AbstractDungeon dungeon, CardCrawlGame self, String key, AbstractPlayer p) {
            if (dungeon == null) {
                CustomDungeon cd = CustomDungeon.dungeons.get(key);
                try {
                    dungeon = cd.fromProgression(p);
                }catch (Exception ex) {
                    BaseMod.logger.error("Failed to instantiate act " + key);
                    ex.printStackTrace();
                }
            }
            return dungeon;
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class, SaveFile.class})
    public static class getDungeonThroughSavefile {
        public static AbstractDungeon Postfix(AbstractDungeon dungeon, CardCrawlGame self, String key, AbstractPlayer p, SaveFile save) {
            if (dungeon == null) {
                CustomDungeon cd = CustomDungeon.dungeons.get(key);
                try {
                    dungeon = cd.fromSaveFile(p, save);
                }catch (Exception ex) {
                    BaseMod.logger.error("Failed to instantiate act " + key);
                    ex.printStackTrace();
                }
            }
            return dungeon;
        }
    }
}
