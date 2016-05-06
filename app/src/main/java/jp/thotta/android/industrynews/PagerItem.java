package jp.thotta.android.industrynews;

import java.util.List;

/**
 * Created by thotta on 2016/05/06.
 */
public class PagerItem {
    String pageTitle;
    String sortMode;
    List<Industry> industries;
    int indicatorColor;
    int dividerColor;

    public PagerItem(String pageTitle, String sortMode,
                     List<Industry> industries,
                     int indicatorColor, int dividerColor) {
        this.pageTitle = pageTitle;
        this.sortMode = sortMode;
        this.industries = industries;
        this.indicatorColor = indicatorColor;
        this.dividerColor = dividerColor;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getIndustriesQuery() {
        String q = "";
        String s = "";
        for(Industry industry : industries) {
            q += s + industry.getId();
            s = "-";
        }
        return q;
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public String getQuery() {
        return String.format("?industries=%s&sort=%s",
                getIndustriesQuery(),
                sortMode);
    }
}