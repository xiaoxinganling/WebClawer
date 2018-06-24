/**
 * 视频实体类
 */
package entity;

public class Video {
    private String id;//av号
    private String title;//标题
    private String author;//作者
    private String category;//类别
    private String coin;//硬币
    private String favorite;//收藏
    private String play;//播放量
    private String barrage;//弹幕
    private String comment;//评论
    private double fluency;//影响力

    public Video(String id, String title, String author, String category, String coin,
                 String favorite, String play, String barrage, String comment) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.coin = coin;
        this.favorite = favorite;
        this.play = play;
        this.barrage = barrage;
        this.comment = comment;
        this.fluency = 0;//此处计算影响力
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }

    public String getBarrage() {
        return barrage;
    }

    public void setBarrage(String barrage) {
        this.barrage = barrage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getFluency() {
        return fluency;
    }

    public void setFluency(double fluency) {
        this.fluency = fluency;
    }
}
