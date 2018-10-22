package basemod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.util.ArrayList;

public class TopPanelGroup {

    private static final int pageItemCount = 3;
    private static final float X_POS = 1475.0f + (pageItemCount * 64.0f);
    private static final float Y_POS = -64.0f;
    private ArrayList<TopPanelItem> topPanelItems;
    private ArrayList<TopPanelItem> activePanelItems;
    private ClickableUIElement leftArrow;
    private ClickableUIElement rightArrow;
    private int currentPage = 1;

    public TopPanelGroup(ArrayList<TopPanelItem> topPanelItems) {
        this.topPanelItems = topPanelItems;
        initArrows();
    }

    public void addPanelItem(TopPanelItem panelItem) {
        this.topPanelItems.add(panelItem);
    }

    public void removePanelItem(TopPanelItem panelItem) {
        this.topPanelItems.remove(panelItem);
    }

    public int size(){
        return this.topPanelItems.size();
    }

    public void render(SpriteBatch sb) {
        renderArrows(sb);
        renderItems(sb);
    }

    public void update() {
        int endIndex = (currentPage * pageItemCount);
        int startIndex = endIndex - pageItemCount;
        activePanelItems = new ArrayList<>(topPanelItems.subList(startIndex, endIndex > topPanelItems.size() ? topPanelItems.size() : endIndex));
        updateArrows();
        updateItems();
    }

    private void updateItems() {
        float xpos = X_POS * Settings.scale;
        for (int i = activePanelItems.size()-1; i >= 0; i--) {
            TopPanelItem temp = activePanelItems.get(i);
            xpos -= temp.hb_w;
            temp.setX(xpos);
            temp.setY(Y_POS * Settings.scale);
            temp.update();
        }
    }

    private void renderItems(SpriteBatch sb) {
        if (activePanelItems == null) {
            return;
        }
        for (int i = 0; i < activePanelItems.size(); i++) {
            activePanelItems.get(i).render(sb);
        }
    }

    private void updateArrows(){
        if(currentPage != 1) {
            leftArrow.update();
        }
        if(currentPage < (int)Math.ceil((float)topPanelItems.size() / pageItemCount)) {
            rightArrow.update();
        }
    }

    private void renderArrows(SpriteBatch sb) {
        if(currentPage != 1) {
            leftArrow.render(sb);
        }
        if(currentPage < (int)Math.ceil((float)topPanelItems.size() / pageItemCount)) {
            rightArrow.render(sb);
        }
    }

    private void initArrows(){

        float lX = X_POS - 40.0f - (pageItemCount * 64.0f);
        float rX = X_POS - 7.0f;
        float y = Y_POS + 10.0f;

        leftArrow = new TopPanelArrow(ImageMaster.loadImage("img/tinyLeftArrow.png"), lX, y, 32f, 32f) {
            @Override
            protected void onClick() {
                if(currentPage != 1) {
                    currentPage--;
                }
            }
        };

        rightArrow = new TopPanelArrow(ImageMaster.loadImage("img/tinyRightArrow.png"), rX, y, 32f, 32f) {
            @Override
            protected void onClick() {
                if(currentPage < (int)Math.ceil((float)topPanelItems.size() / pageItemCount)) {
                    currentPage++;
                }
            }
        };

    }

}
