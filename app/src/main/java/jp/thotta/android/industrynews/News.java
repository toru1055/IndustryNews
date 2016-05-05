package jp.thotta.android.industrynews;

/**
 * Created by thotta on 2016/05/04.
 */
public class News {
    Integer id; //primary key
    String url; //not null
    String title; //not null
    String description;
    String subscriptionName;
    String industryName;
    Integer clicks;
    Boolean isStocked;


    public News(Integer id, String url, String title) {
        this.id = id;
        this.url = url;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public Integer getClicks() {
        return clicks;
    }

    public void setClicks(Integer clicks) {
        this.clicks = clicks;
    }

    public Boolean getStocked() {
        return isStocked;
    }

    public void setStocked(Boolean stocked) {
        isStocked = stocked;
    }
}
