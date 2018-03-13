package basemod.patches.com.megacrit.cardcrawl.ui.panels.DrawPilePanel;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.DrawPilePanel;
import com.megacrit.cardcrawl.vfx.BobEffect;

import basemod.abstracts.CustomPlayer;

@SpirePatch(cls = "com.megacrit.cardcrawl.ui.panels.DrawPilePanel", method = "render")
public class DrawPilePanelRenderPatch {
	
	@SpireInsertPatch(
			loc=190,
			localvars = {"DECK_X", "DECK_Y", "scale", "bob"}
	)
	public static void Insert(DrawPilePanel __instance, SpriteBatch sb, float DECK_X, float DECK_Y, float scale, BobEffect bob) {
		if(AbstractDungeon.player instanceof CustomPlayer) {
			
			CustomPlayer player = (CustomPlayer) AbstractDungeon.player;
			
			if(player.deckTexture != null) {
				sb.draw( player.deckTexture,
						__instance.current_x + DECK_X, 
						__instance.current_y + DECK_Y + bob.y / 2.0F,
						64.0F, 
						64.0F,
						128.0F,
						128.0F, 
						scale * Settings.scale, 
						scale * Settings.scale, 
						0.0F, 
						0, 
						0, 
						128,
						128, 
						false, 
						false);
			}
		}
	}
}
