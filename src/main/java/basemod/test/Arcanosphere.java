package basemod.test;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.abstracts.CustomRelic;

public class Arcanosphere extends CustomRelic {
	public static final String ID = "Arcanosphere";
	private static final int CARDS_TO_RETAIN = 1;
	
	public Arcanosphere() {
		super(ID, TestMod.getArcanoSphereTexture(),
				RelicTier.STARTER, LandingSound.MAGICAL);
	}
	
	@Override
	public void onPlayerEndTurn() {
		if (AbstractDungeon.player.hand.isEmpty()) {
			return;
		}
		AbstractDungeon.actionManager.addToBottom(
				new ArcanosphereAction(AbstractDungeon.player,
						CARDS_TO_RETAIN));
	}
	
	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	@Override
	public AbstractRelic makeCopy() {
		return new Arcanosphere();
	}	
}
